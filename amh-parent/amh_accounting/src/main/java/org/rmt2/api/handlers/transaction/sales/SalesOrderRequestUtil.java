package org.rmt2.api.handlers.transaction.sales;

import java.math.BigInteger;
import java.util.List;

import org.ApiMessageHandlerConst;
import org.dto.SalesOrderDto;
import org.dto.SalesOrderItemDto;
import org.dto.SalesOrderStatusDto;
import org.dto.SalesOrderStatusHistDto;
import org.modules.transaction.sales.SalesApi;
import org.modules.transaction.sales.SalesApiException;
import org.rmt2.jaxb.AccountingTransactionRequest;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.SalesInvoiceType;
import org.rmt2.jaxb.SalesOrderItemType;
import org.rmt2.jaxb.SalesOrderStatusType;
import org.rmt2.jaxb.SalesOrderType;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Utility class for managing sales orders that are targeted for creation.
 * 
 * @author roy.terrell
 *
 */
public class SalesOrderRequestUtil {

    /**
     * Validates the accounting transaction request in regards to createing
     * sales orders.
     * 
     * @param req
     *            instance of {@link AccountingTransactionRequest}
     * @throws InvalidDataException
     *             when profile data is missing, sales order structure is
     *             missing, sales order list is empty, or more that one sale
     *             order element exists in the sales order structure.
     */
    protected static final void doBaseValidation(AccountingTransactionRequest req) throws InvalidDataException {
        try {
            Verifier.verifyNotNull(req.getProfile());
        } catch (VerifyException e) {
            throw new InvalidRequestException(ApiMessageHandlerConst.MSG_MISSING_PROFILE_DATA);
        }

        try {
            Verifier.verifyNotNull(req.getProfile().getSalesOrders());
        } catch (VerifyException e) {
            throw new InvalidRequestException(SalesOrderHandlerConst.MSG_MISSING_SALESORDER_STRUCTURE);
        }

        try {
            Verifier.verifyNotEmpty(req.getProfile().getSalesOrders().getSalesOrder());
        } catch (VerifyException e) {
            throw new InvalidRequestException(SalesOrderHandlerConst.MSG_SALESORDER_LIST_EMPTY);
        }

        try {
            Verifier.verifyTrue(req.getProfile().getSalesOrders().getSalesOrder().size() == 1);
        } catch (VerifyException e) {
            throw new InvalidRequestException(SalesOrderHandlerConst.MSG_SALESORDER_LIST_CONTAINS_TOO_MANY);
        }
    }

    /**
     * Makes various calls to the Sales Order API in order to fulfill creating a
     * sales order.
     * 
     * @param api
     *            an instance of {@link SalesApi}
     * @param salesOrderDto
     *            An instance of {@link SalesOrderDto}
     * @param itemsDtoList
     *            A list of {@link SalesOrderItemDto}
     * @param reqSalesOrder
     *            an instance of {@link SalesOrderType}
     * @throws SalesApiException
     *             for sales order API error
     * @throws SystemException
     *             for data conversion errors.
     */
    protected static final void createSalesOrder(SalesApi api, SalesOrderDto salesOrderDto, List<SalesOrderItemDto> itemsDtoList,
            SalesOrderType reqSalesOrder) throws SalesApiException {
        int newXactId = api.updateSalesOrder(salesOrderDto, itemsDtoList);

        // Update XML with new sales order id
        reqSalesOrder.setSalesOrderId(BigInteger.valueOf(newXactId));

        // Ensure that each sales order item is associated with the sales
        // order.
        for (SalesOrderItemType item : reqSalesOrder.getSalesOrderItems().getSalesOrderItem()) {
            item.setSalesOrderId(BigInteger.valueOf(newXactId));
        }
        return;
    }

    /**
     * Initiates the Sales Order API invoicing process.
     * 
     * @param api
     *            an instance of {@link SalesApi}
     * @param salesOrderDto
     *            An instance of {@link SalesOrderDto}
     * @param itemsDtoList
     *            A list of {@link SalesOrderItemDto}
     * @param applyPayment
     *            apply cash receipt transaction once invoiced.
     * @param reqSalesOrder
     *            an instance of {@link SalesOrderType}
     * @throws SalesApiException
     *             for sales order API error
     */
    protected static final void invoiceSalesOrder(SalesApi api, SalesOrderDto salesOrderDto, List<SalesOrderItemDto> itemsDtoList,
            boolean applyPayment, SalesOrderType reqSalesOrder) throws SalesApiException {

        int newInvoiceId = api.invoiceSalesOrder(salesOrderDto, itemsDtoList, applyPayment);
        ObjectFactory fact = new ObjectFactory();
        SalesInvoiceType sit = fact.createSalesInvoiceType();
        sit.setInvoiceId(BigInteger.valueOf(newInvoiceId));
        reqSalesOrder.setInvoiceDetails(sit);
        reqSalesOrder.setInvoiced(newInvoiceId > 0);

        return;
    }

    /**
     * Assigns the current status to the sales order request.
     * 
     * @param api
     *            an instance of {@link SalesApi}
     * @param reqSalesOrder
     *            an instance of {@link SalesOrderType}
     * @throws SalesApiException
     *             for sales order API error
     * @throws SystemException
     *             for data conversion errors.
     */
    protected static final void assignCurrentStatus(SalesApi api, SalesOrderType reqSalesOrder) throws SalesApiException {
        // Update XML with current sales order status
        SalesOrderStatusHistDto statusHist = api.getCurrentStatus(reqSalesOrder.getSalesOrderId().intValue());
        SalesOrderStatusDto status = api.getStatus(statusHist.getSoStatusId());
        ObjectFactory fact = new ObjectFactory();
        SalesOrderStatusType salesOrdStatusType = fact.createSalesOrderStatusType();
        salesOrdStatusType.setStatusId(BigInteger.valueOf(status.getSoStatusId()));
        salesOrdStatusType.setDescription(status.getSoStatusDescription());
        reqSalesOrder.setStatus(salesOrdStatusType);
        return;
    }
}
