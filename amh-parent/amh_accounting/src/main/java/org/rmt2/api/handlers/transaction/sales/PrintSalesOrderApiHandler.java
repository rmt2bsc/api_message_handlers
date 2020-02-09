package org.rmt2.api.handlers.transaction.sales;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dao.mapping.orm.rmt2.Customer;
import org.dao.mapping.orm.rmt2.Xact;
import org.dto.ContactDto;
import org.dto.CustomerDto;
import org.dto.SalesInvoiceDto;
import org.dto.SalesOrderItemDto;
import org.dto.XactDto;
import org.dto.adapter.orm.Rmt2AddressBookDtoFactory;
import org.dto.adapter.orm.account.subsidiary.Rmt2SubsidiaryDtoFactory;
import org.dto.adapter.orm.transaction.Rmt2XactDtoFactory;
import org.modules.contacts.ContactsApi;
import org.modules.contacts.ContactsApiFactory;
import org.modules.subsidiary.CustomerApi;
import org.modules.subsidiary.SubsidiaryApiFactory;
import org.modules.transaction.XactApi;
import org.modules.transaction.XactApiFactory;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.api.handlers.XmlReportUtility;
import org.rmt2.api.handlers.transaction.TransactionJaxbDtoFactory;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionRequest;
import org.rmt2.jaxb.BusinessType;
import org.rmt2.jaxb.CustomerCriteriaType;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ReplyStatusType;
import org.rmt2.jaxb.SalesInvoiceType;
import org.rmt2.jaxb.SalesOrderCriteria;
import org.rmt2.jaxb.SalesOrderItemType;
import org.rmt2.jaxb.SalesOrderType;
import org.rmt2.jaxb.TransactionDetailGroup;
import org.rmt2.jaxb.XactCriteriaType;
import org.rmt2.jaxb.XactType;
import org.rmt2.util.addressbook.BusinessTypeBuilder;

import com.InvalidDataException;
import com.RMT2Exception;
import com.SystemException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.RMT2Date;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes messages pertaining to the query of Sales Orders in the
 * Accounting API.
 * 
 * @author rterrell
 *
 */
public class PrintSalesOrderApiHandler extends SalesOrderApiHandler {
    private static final Logger logger = Logger.getLogger(PrintSalesOrderApiHandler.class);
    private static final String REPORT_NAME = "SalesOrderInvoiceReport.xsl";

    /**
     * 
     */
    public PrintSalesOrderApiHandler() {
        super();
        logger.info(PrintSalesOrderApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Processes requests pertaining to the creation of a sales order
     * transaction.
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
            case ApiTransactionCodes.ACCOUNTING_SALESORDER_PRINT:
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
        SalesOrderCriteria jaxbSalesOrderCriteria = req.getCriteria().getSalesCriteria();
        XactCriteriaType jaxbXactCriteria = req.getCriteria().getXactCriteria();
        CustomerCriteriaType jaxbCustomerCriteria = req.getCriteria().getCustomerCriteria();
        List<SalesOrderType> jaxbResults = new ArrayList<>();
        List<SalesInvoiceDto> salesOrders = new ArrayList<>();
        Map<Integer, List<SalesOrderItemDto>> itemsMap = new HashMap<>();
        Map<Integer, List<XactDto>> xactMap = new HashMap<>();
        Map<Integer, CustomerDto> custMap = new HashMap<>();
        int recCount = 0;
        boolean error = false;

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);

            // Use SalesInvoiceDto instance instead of SalesOrderDto for the
            // purpose of obtaining extra sales order data
            SalesInvoiceDto criteriaDto = SalesOrderJaxbDtoFactory.createSalesInvoiceCriteriaDtoInstance(jaxbSalesOrderCriteria);
            salesOrders = api.getInvoice(criteriaDto);
            
            // Get customer info
            CustomerApi custApi = SubsidiaryApiFactory.createCustomerApi();
            Customer cust = new Customer();
            cust.setCustomerId(jaxbCustomerCriteria.getCustomer().getCustomerId().intValue());
            CustomerDto custDto = Rmt2SubsidiaryDtoFactory.createCustomerInstance(cust, null);
            List<CustomerDto> customer = custApi.getExt(custDto);
            if (customer != null && customer.size() == 1) {
                // Get contact info and assign to customer object
                ContactsApi contactApi = ContactsApiFactory.createApi();
                ContactDto criteria = Rmt2AddressBookDtoFactory.getNewContactInstance();
                criteria.setContactId(customer.get(0).getContactId());
                List<ContactDto> contacts = contactApi.getContact(criteria);
                if (contacts != null && contacts.size() == 1) {
                    customer.get(0).setContactId(contacts.get(0).getContactId());
                    customer.get(0).setContactName(contacts.get(0).getContactName());
                }
                custMap.put(criteriaDto.getSalesOrderId(), customer.get(0));
            }

            // Organize query results as a Map since we are dealing with sales
            // orders and their items
            if (salesOrders != null) {
                recCount = salesOrders.size();
                XactApi xactApi = XactApiFactory.createDefaultXactApi();
                for (SalesInvoiceDto header : salesOrders) {
                    List<SalesOrderItemDto> items = api.getLineItems(header.getSalesOrderId());
                    itemsMap.put(header.getSalesOrderId(), items);
                    Xact orm = new Xact();
                    orm.setXactId(jaxbXactCriteria.getBasicCriteria().getXactId().intValue());
                    XactDto xactDto = Rmt2XactDtoFactory.createXactBaseInstance(orm);
                    List<XactDto> xact = xactApi.getXact(xactDto);
                    xactMap.put(header.getSalesOrderId(), xact);
                }
            }

            // Convert query results to JAXB objects
            jaxbResults = this.createJaxbResultSet(salesOrders, custMap, itemsMap, xactMap);

            // Assign messages to the reply status that apply to the outcome of
            // this operation
            rs.setMessage(SalesOrderHandlerConst.MSG_PRINT_SUCCESS);
            rs.setRecordCount(recCount);

            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage(SalesOrderHandlerConst.MSG_CREATE_FAILURE);
            rs.setExtMessage(e.getMessage());
            error = true;
        } finally {
//             jaxbResults.add(reqSalesOrder);
            this.api.close();
            String xml = this.buildResponse(jaxbResults, rs);

            // Create PDF file from JAXB XML.
            if (!error) {
                try {
                    this.generatePdf(xml);
                } catch (Exception e) {
                    rs.setExtMessage(rs.getExtMessage() + "; " + e.getMessage());
                    xml = this.buildResponse(jaxbResults, rs);
                }
            }
            results.setPayload(xml);
        }
        return results;
    }

    private List<SalesOrderType> createJaxbResultSet(List<SalesInvoiceDto> salesOrders, Map<Integer, CustomerDto> custMap,
            Map<Integer, List<SalesOrderItemDto>> itemMap, Map<Integer, List<XactDto>> xactMap) {
        List<SalesOrderType> jaxbResults = new ArrayList<>();
        if (itemMap == null) {
            return jaxbResults;
        }

        ObjectFactory f = new ObjectFactory();
        for (SalesInvoiceDto header : salesOrders) {
            SalesOrderType sot = SalesOrderJaxbDtoFactory.createSalesOrderHeaderJaxbInstance(header);

            // Check if we need to add sales order items.
            List<SalesOrderItemDto> itemDtoList = itemMap.get(header.getSalesOrderId());
            if (itemDtoList != null) {
                sot.setSalesOrderItems(f.createSalesOrderItemListType());
                List<SalesOrderItemType> soitList = SalesOrderJaxbDtoFactory.createSalesOrderItemJaxbInstance(itemDtoList);
                sot.getSalesOrderItems().getSalesOrderItem().addAll(soitList);
            }

            // Add customer data
            CustomerDto custDto = custMap.get(header.getSalesOrderId());
            sot.setCustomerId(BigInteger.valueOf(custDto.getCustomerId()));
            sot.setCustomerName(custDto.getContactName());

            // Add transaction info
            SalesInvoiceType sit = f.createSalesInvoiceType();
            sit.setInvoiceId(BigInteger.valueOf(header.getInvoiceId()));
            sit.setInvoiceDate(RMT2Date.toXmlDate(header.getInvoiceDate()));
            sit.setInvoiceNo(header.getInvoiceNo());
            sot.setInvoiceDetails(sit);
            XactDto xactDto = xactMap.get(header.getSalesOrderId()).get(0);
            XactType xact = TransactionJaxbDtoFactory.createXactJaxbInstance(xactDto, 0, null);
            sot.getInvoiceDetails().setTransaction(xact);

            jaxbResults.add(sot);
        }
        return jaxbResults;
    }

    private void generatePdf(String jaxbXml) {
        XmlReportUtility xform = new XmlReportUtility(REPORT_NAME, jaxbXml, true);
        try {
            xform.buildReport();
        } catch (RMT2Exception e) {
            e.printStackTrace();
            throw new SystemException("Failed to generate PDF file", e);
        }
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

    }


    @Override
    protected String buildResponse(List<SalesOrderType> payload, MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);
        }

        if (payload != null) {
            TransactionDetailGroup profile = this.jaxbObjFactory.createTransactionDetailGroup();
            // Add Company data
            int busId = Integer.valueOf(System.getProperty("CompContactId"));
            BusinessType bt = BusinessTypeBuilder.Builder.create()
                    .withBusinessId(busId)
                    .withContactFirstname(System.getProperty("CompContactFirstname"))
                    .withContactLastname(System.getProperty("CompContactLastname"))
                    .withContactPhone(System.getProperty("CompContactPhone"))
                    .withLongname(System.getProperty("CompanyName"))
                    .withTaxId(System.getProperty("CompTaxId"))
                    .withWebsite(System.getProperty("CompWebsite"))
                    .withContactEmail(System.getProperty("CompContactEmail"))
                    .build();
            profile.setCompany(bt);
            profile.getCompany().setBusinessId(BigInteger.valueOf(Long.valueOf(System.getProperty("CompContactId"))));
            profile.getCompany().setLongName(System.getProperty("CompContactId"));
            profile.getCompany().setContactFirstname(System.getProperty("CompContactFirstname"));
            profile.getCompany().setContactLastname(System.getProperty("CompContactLastname"));
            profile.getCompany().setContactPhone(System.getProperty("CompContactPhone"));
            profile.getCompany().setContactEmail(System.getProperty("CompContactEmail"));
            profile.getCompany().setTaxId(System.getProperty("CompTaxId"));
            profile.getCompany().setWebsite(System.getProperty("CompWebsite"));

            profile.setSalesOrders(jaxbObjFactory.createSalesOrderListType());
            profile.getSalesOrders().getSalesOrder().addAll(payload);
            this.responseObj.setProfile(profile);
        }

        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}
