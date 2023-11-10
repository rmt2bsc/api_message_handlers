package org.rmt2.api.handlers.transaction.sales;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dto.SalesInvoiceDto;
import org.dto.SalesOrderItemDto;
import org.modules.transaction.sales.SalesApi;
import org.modules.transaction.sales.SalesApiFactory;
import org.rmt2.api.ApiMessageHandlerConst;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionRequest;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.SalesOrderCriteria;
import org.rmt2.jaxb.SalesOrderItemType;
import org.rmt2.jaxb.SalesOrderType;
import org.rmt2.jaxb.XactCustomCriteriaTargetType;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.RMT2String;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes messages pertaining to the query of Sales Orders in the
 * Accounting API. The result set should contain a list of sales orders that
 * could potentially span one or more customers.
 * 
 * @author rterrell
 *
 */
public class QuerySalesOrderApiHandler extends SalesOrderApiHandler {
    private static final Logger logger = Logger.getLogger(QuerySalesOrderApiHandler.class);

    /**
     * 
     */
    public QuerySalesOrderApiHandler() {
        super();
        logger.info(QuerySalesOrderApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Processes requests pertaining to the query of a sales order transaction.
     * 
     * @param command
     *            The name of the operation.
     * @param payload
     *            The XML message that is to be processed.
     * @return MessageHandlerResults
     * @throws MessageHandlerCommandException
     *             <i>payload</i> is deemed invalid.
     */
    @Override
    public MessageHandlerResults processMessage(String command, Serializable payload) throws MessageHandlerCommandException {
        MessageHandlerResults r = super.processMessage(command, payload);

        if (r != null) {
            String errMsg = ERROR_MSG_TRANS_NOT_FOUND + command;
            if (r.getErrorMsg() != null && r.getErrorMsg().equalsIgnoreCase(errMsg)) {
                // Ancestor was not able to find command. Continue processing.
            }
            else {
                // This means an error occurred.
                return r;
            }
        }
        switch (command) {
            case ApiTransactionCodes.ACCOUNTING_SALESORDER_GET:
                r = this.doOperation(this.requestObj);
                break;

            default:
                r = this.createErrorReply(MessagingConstants.RETURN_CODE_FAILURE, MessagingConstants.RETURN_STATUS_BAD_REQUEST,
                        ERROR_MSG_TRANS_NOT_FOUND + command);
        }
        return r;
    }

    /**
     * Handler for invoking the appropriate API in order to query one or more
     * sales order accounting transaction objects. The only supported target
     * levels are HEADER and FULL.
     * 
     * @param req
     *            an instance of {@link AccountingTransactionRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults doOperation(AccountingTransactionRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        SalesOrderCriteria criteriaJaxb = req.getCriteria().getSalesCriteria();
        List<SalesOrderType> jaxbResults = new ArrayList<>();
        List<SalesInvoiceDto> headerResults = new ArrayList<>();
        Map<Integer, List<SalesOrderItemDto>> resultsMap = new HashMap<>();
        int recCount = 0;

        // IS-71: Changed the scope to local to prevent memory leaks as a result
        // of sharing the API instance that was once contained in ancestor
        // class, SalesORderApiHandler.
        SalesApi api = SalesApiFactory.createApi();

        // UI-37: Added for capturing the update user id
        api.setApiUser(this.userId);

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);

            // Use SalesInvoiceDto instance instead of SalesOrderDto for the
            // purpose of obtaining extra sales order data
            SalesInvoiceDto criteriaDto = SalesOrderJaxbDtoFactory.createSalesInvoiceCriteriaDtoInstance(criteriaJaxb);
            headerResults = api.getInvoice(criteriaDto);

            // Organize query results as a Map since we are dealing with sales
            // orders and their items
            if (headerResults != null) {
                recCount = headerResults.size();
                for (SalesInvoiceDto header : headerResults) {
                    List<SalesOrderItemDto> items = null;
                    if (criteriaJaxb.getTargetLevel() == XactCustomCriteriaTargetType.FULL) {
                        items = api.getLineItems(header.getSalesOrderId());
                    }
                    resultsMap.put(header.getSalesOrderId(), items);
                }

                // Convert query results to JAXB objects
                jaxbResults = this.createJaxbResultSet(headerResults, resultsMap);
            }

            // Assign messages to the reply status that apply to the outcome of
            // this operation
            String msg = RMT2String.replace(SalesOrderHandlerConst.MSG_GET_SUCCESS, String.valueOf(recCount), "%s");
            rs.setMessage(msg);
            rs.setRecordCount(recCount);

            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage(SalesOrderHandlerConst.MSG_CREATE_FAILURE);
            rs.setExtMessage(e.getMessage());
        } finally {
//             jaxbResults.add(reqSalesOrder);
            api.close();
        }

        String xml = this.buildResponse(jaxbResults, rs);
        results.setPayload(xml);
        return results;
    }

    private List<SalesOrderType> createJaxbResultSet(List<SalesInvoiceDto> headerResults,
            Map<Integer, List<SalesOrderItemDto>> resultsMap) {
        List<SalesOrderType> jaxbResults = new ArrayList<>();
        if (resultsMap == null) {
            return jaxbResults;
        }

        ObjectFactory f = new ObjectFactory();
        for (SalesInvoiceDto header : headerResults) {
            List<SalesOrderItemDto> itemDtoList = resultsMap.get(header.getSalesOrderId());
            SalesOrderType sot = SalesOrderJaxbDtoFactory.createSalesOrderHeaderJaxbInstance(header);
            sot.setSalesOrderItems(f.createSalesOrderItemListType());

            // Check if we need to add sales order items.
            if (itemDtoList != null) {
                List<SalesOrderItemType> soitList = SalesOrderJaxbDtoFactory.createSalesOrderItemJaxbInstance(itemDtoList);
                sot.getSalesOrderItems().getSalesOrderItem().addAll(soitList);
            }
            jaxbResults.add(sot);
        }
        return jaxbResults;
    }

    /**
     * @see org.rmt2.api.handlers.transaction.XactApiHandler#validateRequest(org.
     *      rmt2.jaxb.AccountingTransactionRequest)
     */
    @Override
    protected void validateRequest(AccountingTransactionRequest req) throws InvalidDataException {
        super.validateRequest(req);
        try {
            Verifier.verifyNotNull(req.getCriteria());
            Verifier.verifyNotNull(req.getCriteria().getSalesCriteria());
        } catch (VerifyException e) {
            throw new InvalidRequestException(SalesOrderHandlerConst.MSG_MISSING_GENERAL_CRITERIA);
        }

        // Must contain flag that indicates what level of the transaction object
        // to populate with data
        try {
            Verifier.verifyNotNull(req.getCriteria().getSalesCriteria().getTargetLevel());
        } catch (VerifyException e) {
            throw new InvalidRequestException(SalesOrderHandlerConst.MSG_MISSING_TARGET_LEVEL, e);
        }

        // Target level "DETAILS" is not supported.
        try {
            Verifier.verifyFalse(req.getCriteria().getSalesCriteria()
                    .getTargetLevel().name()
                    .equalsIgnoreCase(ApiMessageHandlerConst.TARGET_LEVEL_DETAILS));
        } catch (VerifyException e) {
            throw new InvalidRequestException(SalesOrderHandlerConst.MSG_TARGET_LEVEL_DETAILS_NOT_SUPPORTED, e);
        }
    }

}
