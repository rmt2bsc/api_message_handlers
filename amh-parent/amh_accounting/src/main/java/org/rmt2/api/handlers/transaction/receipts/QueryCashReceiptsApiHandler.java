package org.rmt2.api.handlers.transaction.receipts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.XactDto;
import org.dto.XactTypeItemActivityDto;
import org.modules.transaction.XactApiException;
import org.modules.transaction.receipts.CashReceiptApi;
import org.modules.transaction.receipts.CashReceiptApiFactory;
import org.rmt2.api.handlers.transaction.TransactionJaxbDtoFactory;
import org.rmt2.api.handlers.transaction.XactApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionRequest;
import org.rmt2.jaxb.XactType;

import com.InvalidDataException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;

/**
 * Handles and routes the querying of Cash Receipts Transaction messages to the
 * Accounting API.
 * 
 * @author rterrell
 *
 */
public class QueryCashReceiptsApiHandler extends XactApiHandler {
    private static final Logger logger = Logger.getLogger(CreateCashReceiptsApiHandler.class);
    public static final String MSG_DATA_FOUND = "Cash receipt record(s) found";
    public static final String MSG_DATA_NOT_FOUND = "Cash receipt data not found!";
    public static final String MSG_FAILURE = "Failure to retrieve Cash receipt transaction(s)";

    /**
     * Default constructor
     */
    public QueryCashReceiptsApiHandler() {
        super();
        logger.info(CreateCashReceiptsApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Processes requests pertaining to fetching and creating of cash receipt
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
                // Ancestor was not able to find command. Continue processing.
            }
            else {
                // This means an error occurred.
                return r;
            }
        }
        switch (command) {
            case ApiTransactionCodes.ACCOUNTING_CASHRECEIPT_GET:
                r = this.fetch(this.requestObj);
                break;

            default:
                r = this.createErrorReply(MessagingConstants.RETURN_CODE_FAILURE, MessagingConstants.RETURN_STATUS_BAD_REQUEST,
                        ERROR_MSG_TRANS_NOT_FOUND + command);
        }
        return r;
    }

    /**
     * Handler for invoking the appropriate API in order to fetch one or more
     * cash receipt transaction objects.
     * <p>
     * Currently, the target level, <i>DETAILS</i>, is not supported.
     * 
     * @param req
     *            an instance of {@link AccountingTransactionRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    @Override
    protected MessageHandlerResults fetch(AccountingTransactionRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<XactType> queryDtoResults = null;
        
        // IS-71:  Changed the scope to local to prevent conflicts class scoped api variable in XactApiHandler
        CashReceiptApi api = null;
        try {
        	api = CashReceiptApiFactory.createApi();
        	
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            rs.setRecordCount(0);
            
            XactDto criteriaDto = TransactionJaxbDtoFactory.createBaseXactDtoCriteriaInstance(req.getCriteria().getXactCriteria().getBasicCriteria());
            List<XactDto> dtoList = api.getXact(criteriaDto);
            if (dtoList == null) {
                rs.setMessage(QueryCashReceiptsApiHandler.MSG_DATA_NOT_FOUND);
            }
            else {
                queryDtoResults = this.buildJaxbTransaction(dtoList);
                rs.setMessage(QueryCashReceiptsApiHandler.MSG_DATA_FOUND);
                rs.setRecordCount(dtoList.size());
            }
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage(QueryCashReceiptsApiHandler.MSG_FAILURE);
            rs.setExtMessage(e.getMessage());
        } finally {
            api.close();
        }

        String xml = this.buildResponse(queryDtoResults, rs);
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

        // Must contain flag that indicates what level of the
        // transaction object to populate with data
        this.validateSearchRequest(req);
    }


    /**
     * Builds a List of XactType objects from a List of XactDto objects.
     * 
     * @param results
     *            List<{@link XactDto}>
     * @param customCriteriaDto
     *            custom relational criteria (optional)
     * @return List<{@link XactType}>
     */
    private List<XactType> buildJaxbTransaction(List<XactDto> results) {
        List<XactType> list = new ArrayList<>();

		// IS-71: Changed the scope to local to prevent conflicts class scoped api
		// variable in XactApiHandler
		CashReceiptApi api = null;
		try {
			api = CashReceiptApiFactory.createApi();

			for (XactDto item : results) {
				List<XactTypeItemActivityDto> xactItems = null;

				// retrieve line items
				try {
					xactItems = api.getXactTypeItemActivity(item.getXactId());
				} catch (XactApiException e) {
					logger.error("Unable to fetch cash receipt line items for transaction id, " + item.getXactId());
				}
				XactType jaxbObj = TransactionJaxbDtoFactory.createXactJaxbInstance(item, 0, xactItems);
				list.add(jaxbObj);
			}
		} finally {
			api.close();
			api = null;
		}
		return list;
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
