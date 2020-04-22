package org.rmt2.api.handlers.transaction.receipts;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.dao.mapping.orm.rmt2.SalesOrder;
import org.dao.mapping.orm.rmt2.Xact;
import org.dao.subsidiary.CustomerDao;
import org.dao.subsidiary.SubsidiaryDaoException;
import org.dao.subsidiary.SubsidiaryDaoFactory;
import org.dao.transaction.XactDao;
import org.dao.transaction.XactDaoException;
import org.dao.transaction.XactDaoFactory;
import org.dao.transaction.sales.SalesOrderDao;
import org.dao.transaction.sales.SalesOrderDaoException;
import org.dao.transaction.sales.SalesOrderDaoFactory;
import org.dto.BusinessContactDto;
import org.dto.ContactDto;
import org.dto.CustomerDto;
import org.dto.SalesOrderDto;
import org.dto.XactCodeDto;
import org.dto.XactDto;
import org.dto.adapter.orm.Rmt2AddressBookDtoFactory;
import org.dto.adapter.orm.account.subsidiary.Rmt2SubsidiaryDtoFactory;
import org.dto.adapter.orm.transaction.Rmt2XactDtoFactory;
import org.dto.adapter.orm.transaction.sales.Rmt2SalesOrderDtoFactory;
import org.modules.CommonAccountingConst;
import org.modules.contacts.ContactsApi;
import org.modules.contacts.ContactsApiException;
import org.modules.contacts.ContactsApiFactory;
import org.rmt2.jaxb.CustomerPaymentConfirmation;
import org.rmt2.jaxb.CustomerPaymentConfirmation.CustomerData;
import org.rmt2.jaxb.CustomerPaymentConfirmation.SalesOrderData;
import org.rmt2.jaxb.CustomerPaymentConfirmation.XactData;
import org.rmt2.util.accounting.transaction.sales.PayConfirmCustomerBuilder;
import org.rmt2.util.accounting.transaction.sales.PayConfirmSalesOrderBuilder;
import org.rmt2.util.accounting.transaction.sales.PayConfirmXactBuilder;
import org.rmt2.util.accounting.transaction.sales.PayConfirmationBuilder;

import com.InvalidDataException;
import com.RMT2Base;
import com.api.config.ConfigConstants;
import com.api.config.SystemConfigurator;
import com.api.messaging.MessageException;
import com.api.messaging.email.EmailMessageBean;
import com.api.messaging.email.smtp.SmtpApi;
import com.api.messaging.email.smtp.SmtpFactory;
import com.api.util.RMT2Date;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;
import com.api.xml.RMT2XmlUtility;
import com.api.xml.jaxb.JaxbUtil;

/**
 * Utility class for managing cash receipts transactions.
 * 
 * @author roy.terrell
 *
 */
public class CashReceiptsRequestUtil extends RMT2Base {
    private static final Logger logger = Logger.getLogger(CashReceiptsRequestUtil.class);
    public static final String ERROR_MSG_XACTID_REQUIRED = "Transaction Id is required";
    public static final String ERROR_MSG_XACTID_GREATER_ZERO = "Transaction Id must be a value greater than zero";
    public static final String ERROR_MSG_CUSTOMERID_REQUIRED = "Customer Id is required";
    public static final String ERROR_MSG_CUSTOMERID_GREATER_ZERO = "Customer Id must be a value greater than zero";

    private double bal;

    /**
     * Default constructor
     */
    public CashReceiptsRequestUtil() {
        super();
    }

    private void validateEmailParms(Integer customerId, Integer xactId) {
        // Customer id cannot be null
        try {
            Verifier.verifyNotNull(customerId);
        } catch (VerifyException e) {
            this.msg = ERROR_MSG_CUSTOMERID_REQUIRED;
            throw new InvalidDataException(this.msg, e);
        }

        // Customer id must be greater than zero
        try {
            Verifier.verifyPositive(customerId);
        } catch (VerifyException e) {
            this.msg = ERROR_MSG_CUSTOMERID_GREATER_ZERO;
            throw new InvalidDataException(this.msg, e);
        }

        // Transaction id cannot be null
        try {
            Verifier.verifyNotNull(xactId);
        } catch (VerifyException e) {
            this.msg = ERROR_MSG_XACTID_REQUIRED;
            throw new InvalidDataException(this.msg, e);
        }

        // Transaction id must be greater than zero
        try {
            Verifier.verifyPositive(xactId);
        } catch (VerifyException e) {
            this.msg = ERROR_MSG_XACTID_GREATER_ZERO;
            throw new InvalidDataException(this.msg, e);
        }
    }

    /**
     * Emails a payment confirmation message to the email address on the
     * customer's profile.
     * 
     * @param customerId
     *            the id of the customer
     * @param salesOrderId
     *            the sales order id
     * @param xactId
     *            the transaction id
     * @return int the reulsts of the SMTP call for sending the email to its
     *         recipients.
     * @throws InvalidDataException
     * @throws PaymentEmailConfirmationException
     */
    public int emailPaymentConfirmation(Integer customerId, Integer salesOrderId, Integer xactId)
            throws PaymentEmailConfirmationException {

        this.validateEmailParms(customerId, xactId);
        String xmlData = this.buildPaymentConfirmation(customerId, salesOrderId, xactId);

        // Transform XML to HTML document
        String appFilePath = "reports/";
        RMT2XmlUtility xsl = RMT2XmlUtility.getInstance();
        String xslFile = appFilePath + "CustomerPaymentConfirmation.xsl";
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            xsl.transform(xslFile, xmlData, baos);
            // xsl.transformXslt(this.xslFileName, this.xmlFileName,
            // this.foFileName);
        } catch (Exception e) {
            this.msg = "XSL Customer Payment Email transformation failed for resource, " + xslFile + " due to a System error.  "
                    + e.getMessage();
            logger.error(this.msg);
            throw new PaymentEmailConfirmationException(this.msg, e);
        } finally {
            xsl = null;
        }

        // Get results of transformation
        String html = baos.toString();

        // Build email message
        String emailSubject = "RMT2 Business Systems Corp Account Payment Confirmation";
        EmailMessageBean emailBean = new EmailMessageBean();
        emailBean.setFromAddress(System.getProperty("CompContactEmail"));

        BusinessContactDto bus = this.getBusinessContact(customerId);
        emailBean.setToAddress(bus.getContactEmail());

        // For last minute quick testing
        // msg.setToAddress("rmt2bsc@gmail.com");

        emailBean.setSubject(emailSubject);
        emailBean.setBody(html, EmailMessageBean.HTML_CONTENT);

        // TODO: Remove after testing
        // emailBean.addAttachment("C:/AppServer/data/mime/acct_cd_17927.jpg");

        // Send Email message to intended recipient
        SmtpApi api = SmtpFactory.getSmtpInstance();
        if (api == null) {
            return -100;
        }
        try {
            int rc = (Integer) api.sendMessage(emailBean);
            api.close();
            this.msg = "Customer payment confirmation was sent via email successfully";
            return rc;
        } catch (MessageException e) {
            this.msg = "Customer payment confirmation error.  " + e.getMessage();
            logger.error(this.msg);
            throw new PaymentEmailConfirmationException(e);
        }
    }

    /**
     * Creates customer payment confirmation message.
     * 
     * @param customerId
     * @param salesOrderId
     * @param xactId
     * @return
     * @throws PaymentEmailConfirmationException
     */
    private String buildPaymentConfirmation(Integer customerId, Integer salesOrderId, Integer xactId)
            throws PaymentEmailConfirmationException {

        // Get Transaction data
        Xact xact = null;
        String tender = null;
        XactDto criteria = Rmt2XactDtoFactory.createXactInstance((Xact) null);
        criteria.setXactId(xactId);
        try {
            XactDaoFactory xactDaoFactory = new XactDaoFactory();
            XactDao xactDao = xactDaoFactory.createRmt2OrmXactDao(CommonAccountingConst.APP_NAME);
            List<XactDto> xactDto = xactDao.fetchXact(criteria);
            if (xactDto != null && xactDto.size() == 1) {
                xact = XactDaoFactory.createXact(xactDto.get(0));
                XactCodeDto codeCriteria = Rmt2XactDtoFactory.createXactCodeInstance(null);
                codeCriteria.setEntityId(xact.getTenderId());
                List<XactCodeDto> codes = xactDao.fetchCode(codeCriteria);
                if (codes != null && codes.size() == 1) {
                    tender = codes.get(0).getEntityName();
                }
                else {
                    tender = "Unknown";
                }
            }
            else {
                this.msg = "Transaction was not found: " + xactId;
                throw new PaymentEmailConfirmationException(this.msg);
            }
        } catch (XactDaoException e) {
            throw new PaymentEmailConfirmationException(e);
        }

        // Get Customer data
        CustomerDto cust = null;
        cust = this.getCustomer(customerId);

        // Get Sales Order data
        SalesOrder so = null;
        if (salesOrderId != null) {
            so = this.getSalesOrder(salesOrderId);
        }
        else {
            so = new SalesOrder();
            so.setCustomerId(cust.getCustomerId());
        }

        // Construct JAXB objects
        CustomerData cd = PayConfirmCustomerBuilder.Builder.create()
                .withBalance(this.bal)
                .withBusinessId(cust.getContactId())
                .withContactEmail(cust.getContactEmail())
                .withCustomerId(cust.getCustomerId())
                .withAccoutNo(cust.getAccountNo())
                .build();

        SalesOrderData sod = PayConfirmSalesOrderBuilder.Builder.create()
                .withCustomerId(so.getCustomerId())
                .withEffectiveDate(RMT2Date.formatDate(so.getEffectiveDate(), "yyyy-MM-dd"))
                .withInvoiced(so.getInvoiced())
                .withOrderTotal(so.getOrderTotal())
                .build();

        XactData xd = PayConfirmXactBuilder.Builder.create()
                .withTender(tender)
                .withOrderTotal(xact.getXactAmount())
                .withXactDate(RMT2Date.formatDate(xact.getXactDate(), "yyyy-MM-dd"))
                .withXactId(xact.getXactId())
                .withXactTypeId(xact.getXactTypeId())
                .withConfirmNo(xact.getConfirmNo())
                .withXactReason(xact.getReason())
                .build();

        CustomerPaymentConfirmation pc = PayConfirmationBuilder.Builder.create()
                .withAppRoot("c:/tmp/")
                .withPageTitle("Customer Payment Confirmation")
                .withCustomer(cd)
                .withSalesOrder(sod)
                .withXact(xd)
                .build();

        JaxbUtil jaxb = SystemConfigurator.getJaxb(ConfigConstants.JAXB_CONTEXNAME_DEFAULT);
        String xml = jaxb.marshalMessage(pc);
        return xml;
    }

    private BusinessContactDto getBusinessContact(int customerId) throws PaymentEmailConfirmationException {
        CustomerDto cust = this.getCustomer(customerId);
        ContactsApi contactsApi = ContactsApiFactory.createApi();
        BusinessContactDto criteria = Rmt2AddressBookDtoFactory.getBusinessInstance(null);
        criteria.setContactId(cust.getContactId());
        try {
            List<ContactDto> contacts = contactsApi.getContact(criteria);
            if (contacts != null && contacts.size() == 1 && contacts.get(0) instanceof BusinessContactDto) {
                return (BusinessContactDto) contacts.get(0);
            }
            else {
                this.msg = "Unable to send payment confirmation email due to customer's business contact details could not be found for ccustomer id: "
                        + customerId;
                throw new PaymentEmailConfirmationException(this.msg);
            }
        } catch (ContactsApiException e) {
            throw new PaymentEmailConfirmationException(e);
        }
    }

    private SalesOrder getSalesOrder(int salesOrderId) throws PaymentEmailConfirmationException {
        SalesOrderDaoFactory soDaoFact = new SalesOrderDaoFactory();
        SalesOrderDao soDao = soDaoFact.createRmt2OrmDao(CommonAccountingConst.APP_NAME);
        SalesOrderDto soCriteria = Rmt2SalesOrderDtoFactory.createSalesOrderInstance(null);
        soCriteria.setSalesOrderId(salesOrderId);
        try {
            List<SalesOrderDto> soDto = soDao.fetchSalesOrder(soCriteria);
            if (soDto != null && soDto.size() == 1) {
                return SalesOrderDaoFactory.createOrmSalesOrder(soDto.get(0));
            }
            else {
                this.msg = "Sales order was not found: " + salesOrderId;
                throw new PaymentEmailConfirmationException(this.msg);
            }
        } catch (SalesOrderDaoException e) {
            throw new PaymentEmailConfirmationException(e);
        }
    }

    private CustomerDto getCustomer(int customerId) throws PaymentEmailConfirmationException {
        CustomerDao custDao = SubsidiaryDaoFactory.createRmt2OrmCustomerDao(CommonAccountingConst.APP_NAME);
        CustomerDto custCriteria = Rmt2SubsidiaryDtoFactory.createCustomerInstance(null, null);
        custCriteria.setCustomerId(customerId);
        try {
            List<CustomerDto> custList = custDao.fetch(custCriteria);
            if (custList != null && custList.size() == 1) {
                this.bal = custDao.calculateBalance(customerId);
                return custList.get(0);
            }
            else {
                this.msg = "Unable to fetch customer details to perform cash receipts confirmation due to customer, "
                        + customerId + ", was not found";
                throw new PaymentEmailConfirmationException(this.msg);
            }
        } catch (SubsidiaryDaoException e) {
            throw new PaymentEmailConfirmationException(e);
        }
    }
}
