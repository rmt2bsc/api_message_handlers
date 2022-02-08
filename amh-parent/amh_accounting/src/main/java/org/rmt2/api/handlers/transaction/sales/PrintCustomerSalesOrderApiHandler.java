package org.rmt2.api.handlers.transaction.sales;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
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
import org.rmt2.api.handler.util.PdfReportUtility;
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
import org.rmt2.jaxb.ReportAttachmentType;
import org.rmt2.jaxb.SalesInvoiceType;
import org.rmt2.jaxb.SalesOrderCriteria;
import org.rmt2.jaxb.SalesOrderItemType;
import org.rmt2.jaxb.SalesOrderType;
import org.rmt2.jaxb.TransactionDetailGroup;
import org.rmt2.jaxb.XactCriteriaType;
import org.rmt2.jaxb.XactType;
import org.rmt2.jaxb.ZipcodeType;
import org.rmt2.util.ReportAttachmentTypeBuilder;
import org.rmt2.util.accounting.subsidiary.CustomerTypeBuilder;
import org.rmt2.util.addressbook.AddressTypeBuilder;
import org.rmt2.util.addressbook.BusinessTypeBuilder;
import org.rmt2.util.addressbook.ZipcodeTypeBuilder;

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
 * Handles and routes messages pertaining to the printing of a customer's aales
 * order in the Accounting API.
 * 
 * @author rterrell
 *
 */
public class PrintCustomerSalesOrderApiHandler extends SalesOrderApiHandler {
    private static final Logger logger = Logger.getLogger(PrintCustomerSalesOrderApiHandler.class);
    private static final String REPORT_NAME = "SalesOrderInvoiceReport.xsl";
    private PdfReportUtility xform;

    /**
     * 
     */
    public PrintCustomerSalesOrderApiHandler() {
        super();
        logger.info(PrintCustomerSalesOrderApiHandler.class.getName() + " was instantiated successfully");
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
        Map<Integer, ContactDto> contactMap = new HashMap<>();
        int recCount = 0;
        boolean error = false;

        // IS-71: Changed the scope to local to prevent memory leaks as a result
        // of sharing the API instance that was once contained in ancestor
        // class, SalesORderApiHandler.
        SalesApi salesApi = SalesApiFactory.createApi();
        // IS-70: Change the scope of the remaining API declarations so that
        // they can be made available in the top most finally clause to be
        // closed.
        CustomerApi custApi = SubsidiaryApiFactory.createCustomerApi();
        ContactsApi contactApi = ContactsApiFactory.createApi();
        XactApi xactApi = XactApiFactory.createDefaultXactApi();
        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            rs.setRecordCount(0);

            // Use SalesInvoiceDto instance instead of SalesOrderDto for the
            // purpose of obtaining extra sales order data
            SalesInvoiceDto criteriaDto = SalesOrderJaxbDtoFactory.createSalesInvoiceCriteriaDtoInstance(jaxbSalesOrderCriteria);
            salesOrders = salesApi.getInvoice(criteriaDto);
            
            // Get customer info
            Customer cust = new Customer();
            cust.setCustomerId(jaxbCustomerCriteria.getCustomer().getCustomerId().intValue());
            CustomerDto custDto = Rmt2SubsidiaryDtoFactory.createCustomerInstance(cust, null);
            List<CustomerDto> customer = custApi.getExt(custDto);

            // Get general contact info
            if (customer != null && customer.size() == 1) {
                // Get contact info and assign to customer object
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
                    List<SalesOrderItemDto> items = salesApi.getLineItemsExt(header.getSalesOrderId());
                    itemsMap.put(header.getSalesOrderId(), items);
                    
                    // Include transaction details only if available in the request
                    if (jaxbXactCriteria != null && jaxbXactCriteria.getBasicCriteria() != null 
                            && jaxbXactCriteria.getBasicCriteria().getXactId() != null) {
                        Xact orm = new Xact();
                        orm.setXactId(jaxbXactCriteria.getBasicCriteria().getXactId().intValue());
                        XactDto xactDto = Rmt2XactDtoFactory.createXactBaseInstance(orm);
                        List<XactDto> xact = xactApi.getXact(xactDto);
                        xactMap.put(header.getSalesOrderId(), xact);
                    }
                }
            }

            // Convert query results to JAXB objects
            jaxbResults = this.setupJaxbSalesOrderReportXml(salesOrders, itemsMap, xactMap);

            // Assign messages to the reply status that apply to the outcome of
            // this operation
            rs.setMessage(SalesOrderHandlerConst.MSG_PRINT_SUCCESS);
            rs.setRecordCount(recCount);
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage(SalesOrderHandlerConst.MSG_PRINT_FAILURE);
            rs.setExtMessage(e.getMessage());
            error = true;
        } finally {
            // IS-70: Close API instances to prevent memory leaks
            salesApi.close();
            custApi.close();
            contactApi.close();
            xactApi.close();
            
            // Build message handler response
            String xml = this.buildResponse(jaxbResults, custMap, contactMap, null, rs);

            // Create PDF file from JAXB XML.
            if (!error) {
                ByteArrayOutputStream pdf = null;
                try {
                    Object obj = this.generatePdf(xml);
                    if (obj instanceof ByteArrayOutputStream) {
                        pdf = (ByteArrayOutputStream) obj;
                    }
                } catch (Exception e) {
                    rs.setExtMessage(rs.getExtMessage() + "; " + e.getMessage());
                } finally {
                    jaxbResults = this.createJaxbReportResponse(salesOrders, custMap);
                    xml = this.buildResponse(jaxbResults, custMap, contactMap, pdf, rs);
                }
            }
            results.setPayload(xml);
        }
        return results;
    }

    private List<SalesOrderType> createJaxbReportResponse(List<SalesInvoiceDto> salesOrders,
            Map<Integer, CustomerDto> custMap) {

        List<SalesOrderType> jaxbResults = new ArrayList<>();
        for (SalesInvoiceDto header : salesOrders) {
            SalesOrderType sot = SalesOrderJaxbDtoFactory.createSalesOrderHeaderJaxbInstance(header);

            // Add customer data
            CustomerDto custDto = custMap.get(header.getSalesOrderId());
            sot.setCustomerId(BigInteger.valueOf(custDto.getCustomerId()));
            sot.setCustomerName(custDto.getContactName());
            jaxbResults.add(sot);
        }
        return jaxbResults;
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
            if (xactMap.get(header.getSalesOrderId()) != null) {
                XactDto xactDto = xactMap.get(header.getSalesOrderId()).get(0);
                XactType xact = TransactionJaxbDtoFactory.createXactJaxbInstance(xactDto, 0, null);
                sot.getInvoiceDetails().setTransaction(xact);
            } else {
                XactType xact = TransactionJaxbDtoFactory.createXactJaxbInstance(null, 0, null);
                sot.getInvoiceDetails().setTransaction(xact);
            }
            jaxbResults.add(sot);
        }
        return jaxbResults;
    }

    private OutputStream generatePdf(String jaxbXml) {
        this.xform = new PdfReportUtility(REPORT_NAME, jaxbXml, true, this.sessionId);
        try {
            return this.xform.buildReport();
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
        } catch (VerifyException e) {
            throw new InvalidRequestException(SalesOrderHandlerConst.MSG_MISSING_GENERAL_CRITERIA);
        }

        try {
            Verifier.verifyNotNull(req.getCriteria().getSalesCriteria());
        } catch (VerifyException e) {
            throw new InvalidRequestException(SalesOrderHandlerConst.MSG_MISSING_SALESORDER_CRITERIA_STRUCTURE);
        }

        try {
            Verifier.verifyNotNull(req.getCriteria().getCustomerCriteria());
            Verifier.verifyNotNull(req.getCriteria().getCustomerCriteria().getCustomer());
        } catch (VerifyException e) {
            throw new InvalidRequestException(SalesOrderHandlerConst.MSG_MISSING_CUSTOMER_STRUCTURE);
        }
        try {
            Verifier.verifyNotNull(req.getCriteria().getCustomerCriteria().getCustomer().getCustomerId());
            Verifier.verifyPositive(req.getCriteria().getSalesCriteria().getSalesOrderId());
        } catch (VerifyException e) {
            throw new InvalidRequestException(SalesOrderHandlerConst.MSG_MISSING_PRINT_PARAMETERS);
        }
    }


    protected String buildResponse(List<SalesOrderType> payload, Map<Integer, CustomerDto> custMap,
            Map<Integer, ContactDto> contactMap, ByteArrayOutputStream pdf,
            MessageHandlerCommonReplyStatus replyStatus) {

        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);
        }

        if (payload != null) {
            TransactionDetailGroup profile = this.jaxbObjFactory.createTransactionDetailGroup();

            // We don't want to included detail contact info for customer and
            // main company when building the final reply which includes the PDF
            // file.
            if (pdf == null) {
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
            }
            profile.setSalesOrders(jaxbObjFactory.createSalesOrderListType());
            profile.getSalesOrders().getSalesOrder().addAll(payload);

            if (pdf != null) {
                ReportAttachmentType rat = ReportAttachmentTypeBuilder.Builder.create()
                        .withFilePath(this.xform.getPdfFileName())
                        .withFileSize(pdf.toByteArray().length)
                        .withMimeType("pdf")
                        .withReportId(this.xform.getReportId())
                        .withReportContent(pdf)
                        .build();
                profile.setAttachment(rat);
            }

            this.responseObj.setProfile(profile);
        }


        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}
