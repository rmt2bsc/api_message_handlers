package org.rmt2.api.handlers.transaction.cashdisbursement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.CreditorDto;
import org.dto.XactDto;
import org.dto.XactTypeItemActivityDto;
import org.dto.adapter.orm.transaction.Rmt2XactDtoFactory;
import org.modules.transaction.XactConst;
import org.modules.transaction.disbursements.DisbursementsApi;
import org.modules.transaction.disbursements.DisbursementsApiFactory;
import org.rmt2.api.handlers.transaction.TransactionJaxbDtoFactory;
import org.rmt2.api.handlers.transaction.XactApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionRequest;
import org.rmt2.jaxb.CreditorType;
import org.rmt2.jaxb.XactType;
import org.rmt2.jaxb.XacttypeType;
import org.rmt2.util.accounting.subsidiary.CreditorTypeBuilder;
import org.rmt2.util.accounting.transaction.XactTypeBuilder;
import org.rmt2.util.accounting.transaction.XacttypeTypeBuilder;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.RMT2String;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes Cash Disbursement Transaction messages to the Accounting API.
 * 
 * @author rterrell
 *
 */
public class CreateCashDisbursementApiHandler extends XactApiHandler {
    private static final Logger logger = Logger.getLogger(CreateCashDisbursementApiHandler.class);
    public static final String MSG_FAILURE = "Failure to create Cash disbursement transaction(s)";
    public static final String MSG_CREATE_SUCCESS = "New cash disbursement transaction was created: %s";
    public static final String MSG_MISSING_CREDITOR_PROFILE_DATA = "Creditor profile is required when creating a cash disbursement for a creditor";
    
    private DisbursementsApi api;
    
    /**
     * 
     */
    public CreateCashDisbursementApiHandler() {
        super();
        this.api = DisbursementsApiFactory.createApi();
        logger.info(CreateCashDisbursementApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Processes requests pertaining to fetching and creating of cash
     * disbursement transactions.
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
            case ApiTransactionCodes.ACCOUNTING_CASHDISBURSE_CREATE:
                r = this.create(this.requestObj);
                break;
                
            case ApiTransactionCodes.ACCOUNTING_CASHDISBURSE_CREDITOR_CREATE:
                r = this.createForCreditor(this.requestObj);
                break;
                
            default:
                r = this.createErrorReply(MessagingConstants.RETURN_CODE_FAILURE,
                        MessagingConstants.RETURN_STATUS_BAD_REQUEST,
                        ERROR_MSG_TRANS_NOT_FOUND + command);
        }
        return r;
    }

    /**
     * Handler for invoking the appropriate API in order to create a cash
     * disbursement accounting transaction object.
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
            XactDto xactDto = TransactionJaxbDtoFactory.createXactDtoInstance(reqXact);
            List<XactTypeItemActivityDto> itemsDtoList = TransactionJaxbDtoFactory
                    .createXactItemDtoInstance(reqXact.getLineitems().getLineitem());
            
            // Force transaction type to plain cash disbursement
            xactDto.setXactTypeId(XactConst.XACT_TYPE_CASH_DISBURSE);

            int newXactId = this.api.updateTrans(xactDto, itemsDtoList);
            xactDto.setXactId(newXactId);

            // Verify new transaction.
            XactDto newXactCriteriaDto = Rmt2XactDtoFactory.createXactBaseInstance(null);
            newXactCriteriaDto.setXactId(newXactId);
            List<XactDto> newDto = this.api.get(newXactCriteriaDto, null);
            if (newDto == null) {
                rs.setExtMessage("Unable to obtain confirmation message for new cash disbursment transaction");
            }
            else {
                rs.setExtMessage(newDto.get(0).getXactReason());
                tranRresults = this.buildJaxbTransaction(newDto.get(0), null);
            }

            String msg = RMT2String.replace(MSG_CREATE_SUCCESS, String.valueOf(newXactId), "%s");
            rs.setMessage(msg);
            rs.setRecordCount(1);
            
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            this.responseObj.setHeader(req.getHeader());
            this.api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage(CreateCashDisbursementApiHandler.MSG_FAILURE);
            rs.setExtMessage(e.getMessage());
            this.api.rollbackTrans();
        } finally {
            this.api.close();
        }
        
        String xml = this.buildResponse(tranRresults, rs);
        results.setPayload(xml);
        return results;
    }
    
    /**
     * Handler for invoking the appropriate API in order to create a cash
     * disbursement accounting transaction object for a given creditor.
     * 
     * @param req
     *            an instance of {@link AccountingTransactionRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults createForCreditor(AccountingTransactionRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        XactType reqXact = req.getProfile().getTransactions().getTransaction().get(0);
        List<XactType> tranRresults = new ArrayList<>();
        
        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            XactDto xactDto = TransactionJaxbDtoFactory.createXactDtoInstance(reqXact);
            List<XactTypeItemActivityDto> itemsDtoList =
                    TransactionJaxbDtoFactory.createXactItemDtoInstance(reqXact.getLineitems().getLineitem());
            CreditorDto credDto = TransactionJaxbDtoFactory.createCreditorDtoInstance(reqXact); 
            
            // Force transaction type to creditor cash disbursement
            xactDto.setXactTypeId(XactConst.XACT_TYPE_CREDITOR_PURCHASE);

            int newXactId = this.api.updateTrans(xactDto, itemsDtoList, credDto.getCreditorId());
            xactDto.setXactId(newXactId);

            // Verify new transaction
            XactDto newXactCriteriaDto = Rmt2XactDtoFactory.createXactBaseInstance(null);
            newXactCriteriaDto.setXactId(newXactId);
            List<XactDto> newDto = this.api.get(newXactCriteriaDto, null);
            if (newDto == null) {
                rs.setExtMessage("Unable to obtain confirmation message for new creditor cash disbursment transaction");
            }
            else {
                rs.setExtMessage(newDto.get(0).getXactReason());
                tranRresults = this.buildJaxbTransaction(newDto.get(0), credDto.getCreditorId());
            }

            String msg = RMT2String.replace(MSG_CREATE_SUCCESS, String.valueOf(newXactId), "%s");
            rs.setMessage(msg);
            rs.setRecordCount(1);
            
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage(CreateCashDisbursementApiHandler.MSG_FAILURE);
            rs.setExtMessage(e.getMessage());
        } finally {
            this.api.close();
        }
        
        String xml = this.buildResponse(tranRresults, rs);
        results.setPayload(xml);
        return results;
    }

    /**
     * Builds a List of XactType objects from a List of XactDto objects.
     * 
     * @param results
     *            List<{@link XactDto}>
     * @return List<{@link XactType}>
     */
    private List<XactType> buildJaxbTransaction(XactDto item, Integer creditorId) {
        List<XactType> list = new ArrayList<>();

        XacttypeType xt = XacttypeTypeBuilder.Builder.create()
                .withXactTypeId(item.getXactTypeId())
                .withDescription(item.getXactTypeDescription())
                .withCode(item.getXactTypeCode())
                .build();

        XactType x = null;
        if (creditorId != null) {
            CreditorType c = CreditorTypeBuilder.Builder.create()
                    .withCreditorId(creditorId)
                    .build();

            x = XactTypeBuilder.Builder.create()
                    .withXactId(item.getXactId())
                    .withXactType(xt)
                    .withCreditor(c)
                    .build();
        }
        else {
            x = XactTypeBuilder.Builder.create()
                    .withXactId(item.getXactId())
                    .withXactType(xt)
                    .build();
        }

        list.add(x);
        return list;
    }

    /* (non-Javadoc)
     * @see org.rmt2.api.handlers.transaction.XactApiHandler#validateRequest(org.rmt2.jaxb.AccountingTransactionRequest)
     */
    @Override
    protected void validateRequest(AccountingTransactionRequest req) throws InvalidDataException {
        super.validateRequest(req);
        
        // Validate request for fetch operations
        switch (this.command) {
            case ApiTransactionCodes.ACCOUNTING_CASHDISBURSE_GET:
                // Must contain flag that indicates what level of the transaction object to populate with data
                this.validateSearchRequest(req);
                break;
                
            case ApiTransactionCodes.ACCOUNTING_CASHDISBURSE_CREATE:
            case ApiTransactionCodes.ACCOUNTING_CASHDISBURSE_CREDITOR_CREATE:
                // Transaction profile must exist
                this.validateUpdateRequest(req);
                break;
                
            default:
                break;
        }
    }

    /**
     * Verifies that the creditor profile exists for the create creditor cash
     * disbursment transaction.
     * 
     * @param req
     * @throws InvalidDataException
     */
    @Override
    protected void validateUpdateRequest(AccountingTransactionRequest req) throws InvalidDataException {
        super.validateUpdateRequest(req);
        
        // Verify that creditor profile exist
        if (ApiTransactionCodes.ACCOUNTING_CASHDISBURSE_CREDITOR_CREATE.equalsIgnoreCase(this.command)) {
            try {
                Verifier.verifyNotNull(req.getProfile().getTransactions().getTransaction().get(0).getCreditor());
            }
            catch (VerifyException e) {
                throw new InvalidRequestException(CreateCashDisbursementApiHandler.MSG_MISSING_CREDITOR_PROFILE_DATA, e);    
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
