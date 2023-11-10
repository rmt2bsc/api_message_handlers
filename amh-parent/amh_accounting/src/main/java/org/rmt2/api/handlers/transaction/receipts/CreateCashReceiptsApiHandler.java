package org.rmt2.api.handlers.transaction.receipts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.CustomerDto;
import org.dto.XactDto;
import org.modules.transaction.XactConst;
import org.modules.transaction.receipts.CashReceiptApi;
import org.modules.transaction.receipts.CashReceiptApiFactory;
import org.rmt2.api.handlers.subsidiary.SubsidiaryJaxbDtoFactory;
import org.rmt2.api.handlers.transaction.TransactionJaxbDtoFactory;
import org.rmt2.api.handlers.transaction.XactApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionRequest;
import org.rmt2.jaxb.XactType;

import com.InvalidDataException;
import com.api.config.AppPropertyPool;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.RMT2String;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes Cash Receipts Tupdate tansaction messages to the
 * Accounting API.
 * 
 * @author rterrell
 *
 */
public class CreateCashReceiptsApiHandler extends XactApiHandler {
    private static final Logger logger = Logger.getLogger(CreateCashReceiptsApiHandler.class);
    public static final String MSG_CREATE_SUCCESS = "New cash receipt transaction was created: %s";
    public static final String MSG_MISSING_CUSTOMER_PROFILE_DATA = "Customer profile is required when creating a cash receipt for a Customer";
    public static final String MSG_FAILURE = "Failure to create Cash receipt transaction";
    public static final String MSG_FAILURE_XACT_REQUIRED = "Transaction amount is required and must be numeric";
    public static final String MSG_REVERSE_SUCCESS = "Cash receipt transaction reversal was created: %s";
    public static final String MSG_REVERSE_FAILURE = "Failure to reverse cash receipt transaction";
    
    /**
     * Default constructor
     */
    public CreateCashReceiptsApiHandler() {
        super();
        logger.info(CreateCashReceiptsApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Processes requests pertaining to creating of cash receipt transactions.
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
            case ApiTransactionCodes.ACCOUNTING_CASHRECEIPT_CREATE:
                // Handles cash receipt creation and reversal
                r = this.update(this.requestObj);
                break;

            case ApiTransactionCodes.ACCOUNTING_CASHRECEIPT_REVERSE:
                // Handles cash receipt reversal
                r = this.reverse(this.requestObj);
                break;

            default:
                r = this.createErrorReply(MessagingConstants.RETURN_CODE_FAILURE, MessagingConstants.RETURN_STATUS_BAD_REQUEST,
                        ERROR_MSG_TRANS_NOT_FOUND + command);
        }
        return r;
    }


    /**
     * Handler for invoking the appropriate API in order to create a
     * cash receipt accounting transaction object.
     * 
     * @param req
     *            an instance of {@link AccountingTransactionRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    @Override
    protected MessageHandlerResults update(AccountingTransactionRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        XactType reqXact = req.getProfile().getTransactions().getTransaction().get(0);
        List<XactType> tranRresults = new ArrayList<>();
        int newXactId = 0;
        int customerId = 0;

        // IS-71:  Changed the scope to local to prevent conflicts class scoped api variable in XactApiHandler
        CashReceiptApi api = null;
        try {
        	api = CashReceiptApiFactory.createApi();

            // UI-37: Added for capturing the update user id
            api.setApiUser(this.userId);
        	
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            rs.setRecordCount(0);
            
            // Get transaction data
            XactDto xactDto = TransactionJaxbDtoFactory.createXactDtoInstance(reqXact);
            // Force transaction type to be cash receipts in the event user did
            // not supply value
            xactDto.setXactTypeId(XactConst.XACT_TYPE_CASHRECEIPT);
            // Get customer data
            CustomerDto criteriaDto = SubsidiaryJaxbDtoFactory.createCustomerDtoInstance(reqXact.getCustomer());

            api.beginTrans();
            newXactId = api.receivePayment(xactDto, criteriaDto.getCustomerId());
            xactDto.setXactId(newXactId);
            customerId = criteriaDto.getCustomerId();

            // Verify new transaction
            XactDto newXactDto = api.getXactById(newXactId);
            if (newXactDto == null) {
                rs.setExtMessage("Unable to obtain confirmation message for new cash receipt transaction");
            }
            else {
                rs.setExtMessage(newXactDto.getXactReason());
                tranRresults = TransactionJaxbDtoFactory.buildJaxbCustomerTransaction(newXactDto, criteriaDto.getCustomerId());
            }

            String msg = RMT2String.replace(MSG_CREATE_SUCCESS, String.valueOf(newXactId), "%s");
            rs.setMessage(msg);
            rs.setRecordCount(1);
            this.responseObj.setHeader(req.getHeader());
            api.commitTrans();

            // Send Email Confirmation
			// TODO: Email confirmation logic needs attention due to SMTP "Connection refused: connect"
			// errors which causes the entire process to malfunction.  The problem stems from the fact 
            // that the SMTP server will need to be setup to allow outside connections to send messages.
            boolean generateEmailConfirmation = Boolean.valueOf(AppPropertyPool.getProperty("GenerateEmailConfirmation"));
            if (generateEmailConfirmation) {
                try {
                    CashReceiptsRequestUtil util = new CashReceiptsRequestUtil();
                    util.emailPaymentConfirmation(customerId, null, newXactId);
                } catch (PaymentEmailConfirmationException e) {
                    logger.error(e);
                } catch (Exception e) {
                    logger.error("A general API error occurred attempting to send email confirmation", e);
                }
            }
            
        } catch (Exception e) {
            logger.error("Error occurred during Cash Receipts API Message Handler operation, " + this.command, e);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage(CreateCashReceiptsApiHandler.MSG_FAILURE);
            rs.setExtMessage(e.getMessage());
            api.rollbackTrans();
        } finally {
            // tranRresults.add(reqXact);
            api.close();
        }

        String xml = this.buildResponse(tranRresults, rs);
        results.setPayload(xml);
        return results;
    }

    /**
     * Handler for invoking the appropriate API in order to reverse a
     * cash receipt accounting transaction object.
     * 
     * @param req
     *            an instance of {@link AccountingTransactionRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    @Override
    protected MessageHandlerResults reverse(AccountingTransactionRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        XactType reqXact = req.getProfile().getTransactions().getTransaction().get(0);
        List<XactType> tranRresults = new ArrayList<>();
        int newXactId = 0;
        int customerId = 0;

        // IS-71:  Changed the scope to local to prevent conflicts class scoped api variable in XactApiHandler
        CashReceiptApi api = null;
        try {
        	api = CashReceiptApiFactory.createApi();

            // UI-37: Added for capturing the update user id
            this.transApi = api;
        	
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            rs.setRecordCount(0);
            
            // Get transaction data
            XactDto xactDto = TransactionJaxbDtoFactory.createXactDtoInstance(reqXact);
            // Force transaction type to be cash receipts in the event user did
            // not supply value
            xactDto.setXactTypeId(XactConst.XACT_TYPE_CASHRECEIPT);
            // Get customer data
            CustomerDto criteriaDto = SubsidiaryJaxbDtoFactory.createCustomerDtoInstance(reqXact.getCustomer());

            api.beginTrans();
            newXactId = api.receivePayment(xactDto, criteriaDto.getCustomerId());
            xactDto.setXactId(newXactId);
            customerId = criteriaDto.getCustomerId();

            // Verify new transaction
            XactDto newXactDto = api.getXactById(newXactId);
            if (newXactDto == null) {
                rs.setExtMessage("Unable to obtain confirmation message for new cash receipt transaction");
            }
            else {
                rs.setExtMessage(newXactDto.getXactReason());
                tranRresults = TransactionJaxbDtoFactory.buildJaxbCustomerTransaction(newXactDto, criteriaDto.getCustomerId());
            }

            String msg = RMT2String.replace(MSG_REVERSE_SUCCESS, String.valueOf(newXactId), "%s");
            rs.setMessage(msg);
            rs.setRecordCount(1);
            this.responseObj.setHeader(req.getHeader());
            api.commitTrans();

            // Send Email Confirmation
			// TODO: This may not be needed for this handler...will research more. Email
			// confirmation logic needs attention due to SMTP "Connection refused: connect"
			// errors which causes the entire process to malfunction. The problem stems from
			// the fact that the SMTP server will need to be setup to allow outside connections to
			// send messages.
//            try {
//                CashReceiptsRequestUtil util = new CashReceiptsRequestUtil();
//                util.emailPaymentConfirmation(customerId, null, newXactId);
//            } catch (PaymentEmailConfirmationException e) {
//                logger.error(e);
//            } catch (Exception e) {
//            	logger.error("A general API error occurred attempting to send email confirmation", e);
//            }
            
        } catch (Exception e) {
            logger.error("Error occurred during Cash Receipts API Message Handler reversal operation, " + this.command, e);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage(CreateCashReceiptsApiHandler.MSG_REVERSE_FAILURE);
            rs.setExtMessage(e.getMessage());
            api.rollbackTrans();
        } finally {
            // tranRresults.add(reqXact);
            api.close();
        }

        String xml = this.buildResponse(tranRresults, rs);
        results.setPayload(xml);
        return results;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.rmt2.api.handlers.transaction.XactApiHandler#validateRequest(org.
     * rmt2.jaxb.AccountingTransactionRequest)
     */
    @Override
    protected void validateRequest(AccountingTransactionRequest req) throws InvalidDataException {
        super.validateRequest(req);

        // Transaction profile must exist
        this.validateUpdateRequest(req);
    }

    /**
     * Verifies that the Customer profile exists for the create Customer cash
     * receipt transaction.
     * 
     * @param req
     * @throws InvalidDataException
     */
    @Override
    protected void validateUpdateRequest(AccountingTransactionRequest req) throws InvalidDataException {
        super.validateUpdateRequest(req);

        // Verify that Customer profile exist
        try {
            Verifier.verifyNotNull(req.getProfile().getTransactions().getTransaction().get(0).getCustomer());
        } catch (VerifyException e) {
            throw new InvalidRequestException(CreateCashReceiptsApiHandler.MSG_MISSING_CUSTOMER_PROFILE_DATA, e);
        }
        
        try {
            Verifier.verifyNotNull(req.getProfile().getTransactions().getTransaction().get(0).getXactAmount());
        } catch (VerifyException e) {
            throw new InvalidRequestException(CreateCashReceiptsApiHandler.MSG_FAILURE_XACT_REQUIRED, e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.rmt2.api.handlers.transaction.XactApiHandler#buildResponse(java.util
     * .List, com.api.messaging.handler.MessageHandlerCommonReplyStatus)
     */
    @Override
    protected String buildResponse(List<XactType> payload, MessageHandlerCommonReplyStatus replyStatus) {
        return super.buildResponse(payload, replyStatus);
    }

}
