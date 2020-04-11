package org.rmt2.api.handlers.transaction.purchases;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.XactCreditChargeDto;
import org.dto.XactTypeItemActivityDto;
import org.modules.transaction.purchases.creditor.CreditorPurchasesApi;
import org.modules.transaction.purchases.creditor.CreditorPurchasesApiFactory;
import org.rmt2.api.handlers.transaction.TransactionJaxbDtoFactory;
import org.rmt2.api.handlers.transaction.XactApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionRequest;
import org.rmt2.jaxb.XactType;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.RMT2String;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes Accounting API messages pertaining the the creation of
 * Creditor Purchases transactions.
 * 
 * @author rterrell
 *
 */
public class CreateCreditorPurchasesApiHandler extends XactApiHandler {
    private static final Logger logger = Logger.getLogger(CreateCreditorPurchasesApiHandler.class);
    public static final String MSG_CREATE_FAILURE = "Failure to create Creditor purchases transaction(s)";
    public static final String MSG_CREATE_SUCCESS = "New creditor purchases transaction was created: %s";
    public static final String MSG_MISSING_CREDITOR_PROFILE_DATA = "Creditor profile is required when creating a Creditor purchases for a creditor";
    
    private CreditorPurchasesApi api;
    
    /**
     * 
     */
    public CreateCreditorPurchasesApiHandler() {
        super();
        this.api = CreditorPurchasesApiFactory.createApi();
        logger.info(CreateCreditorPurchasesApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Processes requests pertaining to the creation of creditor purchase
     * transactions.
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
                // Ancestor was not able to find command.  Continue processing.
            }
            else {
             // This means an error occurred.
                return r;    
            }
        }
        switch (command) {
            case ApiTransactionCodes.ACCOUNTING_CREDITPURCHASE_CREATE:
                r = this.create(this.requestObj);
                break;
                
            default:
                r = this.createErrorReply(MessagingConstants.RETURN_CODE_FAILURE,
                        MessagingConstants.RETURN_STATUS_BAD_REQUEST,
                        ERROR_MSG_TRANS_NOT_FOUND + command);
        }
        return r;
    }

    

    /**
     * Handler for invoking the appropriate API in order to create a creditor
     * purchases accounting transaction object.
     * 
     * @param req
     *            an instance of {@link AccountingTransactionRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    @Override
    protected MessageHandlerResults create(AccountingTransactionRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        XactType reqXact = req.getProfile().getTransactions().getTransaction().get(0);
        List<XactType> tranRresults = new ArrayList<>();
        
        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            XactCreditChargeDto xactDto = CreditorPurchasesJaxbDtoFactory.createCreditorPurchasesDtoInstance(reqXact);
            List<XactTypeItemActivityDto> itemsDtoList = TransactionJaxbDtoFactory
                    .createXactItemDtoInstance(reqXact.getLineitems().getLineitem());
            
            int newXactId = this.api.update(xactDto, itemsDtoList);
            reqXact.setXactId(BigInteger.valueOf(newXactId));

            // Get transaction confirmation message.
            XactCreditChargeDto dto = this.api.get(newXactId);
            if (dto == null) {
                rs.setExtMessage("Unable to obtain confirmation message for transaction");
            }
            else {
                rs.setExtMessage(dto.getXactReason());
                tranRresults = TransactionJaxbDtoFactory.buildJaxbCreditPurchasesTransaction(dto);
            }

            String msg = RMT2String.replace(MSG_CREATE_SUCCESS, String.valueOf(reqXact.getXactId()), "%s");
            rs.setMessage(msg);

            rs.setRecordCount(1);
            
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            this.responseObj.setHeader(req.getHeader());
            this.api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage(CreateCreditorPurchasesApiHandler.MSG_CREATE_FAILURE);
            rs.setExtMessage(e.getMessage());
            this.api.rollbackTrans();
        } finally {
            this.api.close();
        }
        
        String xml = this.buildResponse(tranRresults, rs);
        results.setPayload(xml);
        return results;
    }
    
    /* (non-Javadoc)
     * @see org.rmt2.api.handlers.transaction.XactApiHandler#validateRequest(org.rmt2.jaxb.AccountingTransactionRequest)
     */
    @Override
    protected void validateRequest(AccountingTransactionRequest req) throws InvalidDataException {
        super.validateRequest(req);
        super.validateUpdateRequest(req);

        // Verify that creditor profile exist
        if (ApiTransactionCodes.ACCOUNTING_CREDITPURCHASE_CREATE.equalsIgnoreCase(this.command)) {
            try {
                Verifier.verifyNotNull(req.getProfile().getTransactions().getTransaction().get(0).getCreditor());
            } catch (VerifyException e) {
                throw new InvalidRequestException(CreateCreditorPurchasesApiHandler.MSG_MISSING_CREDITOR_PROFILE_DATA, e);
            }
        }
    }

    
    /* (non-Javadoc)
     * @see org.rmt2.api.handlers.transaction.XactApiHandler#buildResponse(java.util.List, com.api.messaging.handler.MessageHandlerCommonReplyStatus)
     */
    @Override
    protected String buildResponse(List<XactType> payload, MessageHandlerCommonReplyStatus replyStatus) {
        return super.buildResponse(payload, replyStatus);
    }

}
