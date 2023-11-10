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
import org.dto.BusinessContactDto;
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
import org.modules.transaction.sales.SalesApi;
import org.modules.transaction.sales.SalesApiFactory;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.api.handlers.transaction.TransactionJaxbDtoFactory;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionRequest;
import org.rmt2.jaxb.AddressType;
import org.rmt2.jaxb.BusinessType;
import org.rmt2.jaxb.CustomerCriteriaType;
import org.rmt2.jaxb.CustomerListType;
import org.rmt2.jaxb.CustomerType;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ReplyStatusType;
import org.rmt2.jaxb.SalesInvoiceType;
import org.rmt2.jaxb.SalesOrderCriteria;
import org.rmt2.jaxb.SalesOrderItemType;
import org.rmt2.jaxb.SalesOrderType;
import org.rmt2.jaxb.TransactionDetailGroup;
import org.rmt2.jaxb.XactCriteriaType;
import org.rmt2.jaxb.XactType;
import org.rmt2.jaxb.ZipcodeType;
import org.rmt2.util.accounting.subsidiary.CustomerTypeBuilder;
import org.rmt2.util.addressbook.AddressTypeBuilder;
import org.rmt2.util.addressbook.BusinessTypeBuilder;
import org.rmt2.util.addressbook.ZipcodeTypeBuilder;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.RMT2Date;
import com.api.util.RMT2String;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes messages pertaining to the querying of a customer's aales
 * order in the Accounting API.
 * 
 * @author rterrell
 *
 */
public class QueryCustomerSalesOrderApiHandler extends SalesOrderApiHandler {
    private static final Logger logger = Logger.getLogger(QueryCustomerSalesOrderApiHandler.class);

    /**
     * 
     */
    public QueryCustomerSalesOrderApiHandler() {
        super();
        logger.info(QueryCustomerSalesOrderApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Processes requests pertaining to the query of a customer's sales order
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
            case ApiTransactionCodes.ACCOUNTING_SALESORDER_GET_CUSTOMER_SPECIFIC:
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
     * customer's sales order accounting transaction objects. The only supported
     * target levels are HEADER and FULL.
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
        Map<Integer, ContactDto> contactMap = new HashMap<>();
        int recCount = 0;

        // IS-71: Changed the scope to local to prevent memory leaks as a result
        // of sharing the API instance that was once contained in ancestor
        // class, SalesORderApiHandler.
        SalesApi api = SalesApiFactory.createApi();

        // UI-37: Added for capturing the update user id
        api.setApiUser(this.userId);
        
        // Setup other API's for data access.
        CustomerApi custApi = SubsidiaryApiFactory.createCustomerApi();
        ContactsApi contactApi = ContactsApiFactory.createApi();
        XactApi xactApi = XactApiFactory.createDefaultXactApi();
        
        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);

            // Use SalesInvoiceDto instance instead of SalesOrderDto for the
            // purpose of obtaining extra sales order data
            SalesInvoiceDto criteriaDto = SalesOrderJaxbDtoFactory.createSalesInvoiceCriteriaDtoInstance(jaxbSalesOrderCriteria);
            salesOrders = api.getInvoice(criteriaDto);
            
            // Get customer info            
            Customer cust = new Customer();
            cust.setCustomerId(jaxbCustomerCriteria.getCustomer().getCustomerId().intValue());
            CustomerDto custDto = Rmt2SubsidiaryDtoFactory.createCustomerInstance(cust, null);
            List<CustomerDto> customer = custApi.getExt(custDto);

            // Get contact info and assign to customer object
            if (customer != null && customer.size() == 1) {
                // Get contact info for customer
                BusinessContactDto criteria = Rmt2AddressBookDtoFactory.getBusinessInstance(null);
                criteria.setContactId(customer.get(0).getContactId());
                List<ContactDto> custContacts = contactApi.getContact(criteria);
                contactMap.put(customer.get(0).getContactId(), custContacts.get(0));
                custMap.put(criteriaDto.getSalesOrderId(), customer.get(0));
            }

            // Get contact info for main company
            int compContactId = Integer.valueOf(System.getProperty("CompContactId")).intValue();
            BusinessContactDto criteria = Rmt2AddressBookDtoFactory.getBusinessInstance(null);
            criteria.setContactId(compContactId);
            List<ContactDto> compContacts = contactApi.getContact(criteria);
            contactMap.put(compContactId, compContacts.get(0));

            // Organize query results as a Map since we are dealing with sales
            // orders and their items
            if (salesOrders != null) {
                recCount = salesOrders.size();                
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
            jaxbResults = this.setupJaxbSalesOrderReportXml(salesOrders, itemsMap, xactMap);

            // Assign messages to the reply status that apply to the outcome of
            // this operation
            String message = RMT2String.replace(SalesOrderHandlerConst.MSG_GET_CUSTOMER_SPECIFIC_SUCCESS,
                    String.valueOf(recCount), "%s");
            rs.setMessage(message);
            rs.setRecordCount(recCount);

            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage(SalesOrderHandlerConst.MSG_GET_CUSTOMER_SPECIFIC_FAILURE);
            rs.setExtMessage(e.getMessage());
        } finally {
//             jaxbResults.add(reqSalesOrder);
            api.close();
            custApi.close();
            contactApi.close();
            xactApi.close();
            String xml = this.buildResponse(jaxbResults, custMap, contactMap, rs);
            results.setPayload(xml);
        }
        return results;
    }

    private List<SalesOrderType> setupJaxbSalesOrderReportXml(List<SalesInvoiceDto> salesOrders,
            Map<Integer, List<SalesOrderItemDto>> itemMap, Map<Integer, List<XactDto>> xactMap) {
        List<SalesOrderType> jaxbResults = new ArrayList<>();
        if (itemMap == null) {
            return jaxbResults;
        }

        ObjectFactory f = new ObjectFactory();
        // Really just expecting one element in the salesOrders List
        for (SalesInvoiceDto header : salesOrders) {
            SalesOrderType sot = SalesOrderJaxbDtoFactory.createSalesOrderHeaderJaxbInstance(header);

            // Check if we need to add sales order items.
            List<SalesOrderItemDto> itemDtoList = itemMap.get(header.getSalesOrderId());
            if (itemDtoList != null) {
                sot.setSalesOrderItems(f.createSalesOrderItemListType());
                List<SalesOrderItemType> soitList = SalesOrderJaxbDtoFactory.createSalesOrderItemJaxbInstance(itemDtoList);
                sot.getSalesOrderItems().getSalesOrderItem().addAll(soitList);
            }

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



    /**
     * @see org.rmt2.api.handlers.transaction.XactApiHandler#validateRequest(org.
     *      rmt2.jaxb.AccountingTransactionRequest)
     */
    @Override
    protected void validateRequest(AccountingTransactionRequest req) throws InvalidDataException {
        super.validateRequest(req);
        try {
            Verifier.verifyNotNull(req.getCriteria());
        } catch (VerifyException e) {
            throw new InvalidRequestException(SalesOrderHandlerConst.MSG_MISSING_GENERAL_CRITERIA);
        }

        try {
            Verifier.verifyNotNull(req.getCriteria().getSalesCriteria());
        } catch (VerifyException e) {
            throw new InvalidRequestException(SalesOrderHandlerConst.MSG_MISSING_SALESORDER_STRUCTURE);
        }

        try {
            Verifier.verifyNotNull(req.getCriteria().getCustomerCriteria());
            Verifier.verifyNotNull(req.getCriteria().getCustomerCriteria().getCustomer());
        } catch (VerifyException e) {
            throw new InvalidRequestException(SalesOrderHandlerConst.MSG_MISSING_CUSTOMER_STRUCTURE);
        }
        
        try {
            Verifier.verifyNotNull(req.getCriteria().getXactCriteria());
            Verifier.verifyNotNull(req.getCriteria().getXactCriteria().getBasicCriteria());
        } catch (VerifyException e) {
            throw new InvalidRequestException(SalesOrderHandlerConst.MSG_MISSING_XACT_STRUCTURE);
        }

        try {
            Verifier.verifyNotNull(req.getCriteria().getXactCriteria().getBasicCriteria().getXactId());
            Verifier.verifyNotNull(req.getCriteria().getCustomerCriteria().getCustomer().getCustomerId());
            Verifier.verifyPositive(req.getCriteria().getSalesCriteria().getSalesOrderId());
        } catch (VerifyException e) {
            throw new InvalidRequestException(SalesOrderHandlerConst.MSG_MISSING_PRINT_PARAMETERS);
        }
    }


    protected String buildResponse(List<SalesOrderType> payload, Map<Integer, CustomerDto> custMap,
            Map<Integer, ContactDto> contactMap, MessageHandlerCommonReplyStatus replyStatus) {

        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);
        }

        if (payload != null) {
            TransactionDetailGroup profile = this.jaxbObjFactory.createTransactionDetailGroup();

            // Add Company data
            int busId = Integer.valueOf(System.getProperty("CompContactId"));
            BusinessContactDto mainCompContactDto = (BusinessContactDto) contactMap.get(busId);
            if (mainCompContactDto != null) {
                ZipcodeType zt = ZipcodeTypeBuilder.Builder.create()
                        .withCity(mainCompContactDto.getCity())
                        .withState(mainCompContactDto.getState())
                        .withZipcode(mainCompContactDto.getZip())
                        .build();
                AddressType at = AddressTypeBuilder.Builder.create()
                        .withAddrId(mainCompContactDto.getAddrId())
                        .withAddressLine1(mainCompContactDto.getAddr1())
                        .withAddressLine2(mainCompContactDto.getAddr2())
                        .withAddressLine3(mainCompContactDto.getAddr3())
                        .withAddressLine4(mainCompContactDto.getAddr4())
                        .withZipcode(zt)
                        .build();
                BusinessType bt = BusinessTypeBuilder.Builder.create()
                        .withBusinessId(busId)
                        .withContactFirstname(mainCompContactDto.getContactFirstname())
                        .withContactLastname(mainCompContactDto.getContactLastname())
                        .withContactPhone(mainCompContactDto.getContactPhone())
                        .withLongname(mainCompContactDto.getContactName())
                        .withTaxId(mainCompContactDto.getTaxId())
                        .withWebsite(mainCompContactDto.getWebsite())
                        .withContactEmail(mainCompContactDto.getContactEmail())
                        .withAddress(at)
                        .build();
                profile.setCompany(bt);
            }

            // Add Customer data
            if (payload != null && payload.size() > 0) {
                CustomerDto custDto = custMap.get(payload.get(0).getSalesOrderId().intValue());
                BusinessContactDto custContactDto = (BusinessContactDto) contactMap.get(custDto.getContactId());
                ZipcodeType zt = ZipcodeTypeBuilder.Builder.create()
                        .withCity(custContactDto.getCity())
                        .withState(custContactDto.getState())
                        .withZipcode(custContactDto.getZip())
                        .build();
                AddressType at = AddressTypeBuilder.Builder.create()
                        .withAddrId(custContactDto.getAddrId())
                        .withAddressLine1(custContactDto.getAddr1())
                        .withAddressLine2(custContactDto.getAddr2())
                        .withAddressLine3(custContactDto.getAddr3())
                        .withAddressLine4(custContactDto.getAddr4())
                        .withZipcode(zt)
                        .build();
                BusinessType bt = BusinessTypeBuilder.Builder.create()
                        .withBusinessId(custContactDto.getContactId())
                        .withContactFirstname(custContactDto.getContactFirstname())
                        .withContactLastname(custContactDto.getContactLastname())
                        .withContactPhone(custContactDto.getContactPhone())
                        .withLongname(custContactDto.getContactName())
                        .withTaxId(custContactDto.getTaxId())
                        .withWebsite(custContactDto.getWebsite())
                        .withContactEmail(custContactDto.getContactEmail())
                        .withAddress(at)
                        .build();
                CustomerType cust = CustomerTypeBuilder.Builder.create()
                        .withAccountNo(custDto.getAccountNo())
                        .withCustomerId(custDto.getCustomerId())
                        .withCreditLimit(custDto.getCreditLimit())
                        .withBusinessType(bt)
                        .build();

                ObjectFactory f = new ObjectFactory();
                CustomerListType clt = f.createCustomerListType();
                profile.setCustomers(clt);
                profile.getCustomers().getCustomer().add(cust);
            }
            profile.setSalesOrders(jaxbObjFactory.createSalesOrderListType());
            profile.getSalesOrders().getSalesOrder().addAll(payload);

            this.responseObj.setProfile(profile);
        }


        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}
