package org.rmt2.api.handlers.subsidiary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dto.CustomerDto;
import org.dto.CustomerXactHistoryDto;
import org.dto.XactDto;
import org.modules.CommonAccountingConst;
import org.modules.subsidiary.CustomerApi;
import org.modules.subsidiary.SubsidiaryApiFactory;
import org.modules.transaction.XactApi;
import org.modules.transaction.XactApiException;
import org.modules.transaction.XactApiFactory;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionRequest;
import org.rmt2.jaxb.AccountingTransactionResponse;
import org.rmt2.jaxb.CustomerType;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ReplyStatusType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.rmt2.jaxb.TransactionDetailGroup;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.AbstractJaxbMessageHandler;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes Customer related messages to the Accounting API.
 * 
 * @author roy.terrell
 *
 */
public class CustomerApiHandler extends 
                  AbstractJaxbMessageHandler<AccountingTransactionRequest, AccountingTransactionResponse, List<CustomerType>> {
    
    private static final Logger logger = Logger.getLogger(CustomerApiHandler.class);
    public static final String MSG_UPDATE_MISSING_CRITERIA = "Customer transaction selection criteria is required for query/delete operation";
    public static final String MSG_UPDATE_MISSING_PROFILE = "Customer transaction profile data is required for update operation";
    private ObjectFactory jaxbObjFactory;
    private CustomerApi api;

    /**
     * @param payload
     */
    public CustomerApiHandler() {
        super();
        SubsidiaryApiFactory f = new SubsidiaryApiFactory();
        this.api = f.createCustomerApi(CommonAccountingConst.APP_NAME);
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createAccountingTransactionResponse();
        logger.info(CustomerApiHandler.class.getName() + " was instantiated successfully");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.api.messaging.jms.handler.AbstractMessageHandler#processRequest(java
     * .lang.String, java.io.Serializable)
     */
    @Override
    public MessageHandlerResults processMessage(String command, Serializable payload) throws MessageHandlerCommandException {
        MessageHandlerResults r = super.processMessage(command, payload);

        if (r != null) {
            // This means an error occurred.
            return r;
        }
        switch (command) {
            case ApiTransactionCodes.SUBSIDIARY_CUSTOMER_GET:
                r = this.fetchCustomer(this.requestObj);
                break;
            case ApiTransactionCodes.SUBSIDIARY_CUSTOMER_TRAN_HIST_GET:
                r = this.fetchTransHistory(this.requestObj);
                break;
            case ApiTransactionCodes.SUBSIDIARY_CUSTOMER_UPDATE:
                r = this.updateCustomer(this.requestObj);
                break;
            case ApiTransactionCodes.SUBSIDIARY_CUSTOMER_DELETE:
                r = this.deleteCustomer(this.requestObj);
                break;
            default:
                r = this.createErrorReply(MessagingConstants.RETURN_CODE_FAILURE,
                        MessagingConstants.RETURN_STATUS_BAD_REQUEST,
                        ERROR_MSG_TRANS_NOT_FOUND + command);
        }
        return r;
    }

    /**
     * Handler for invoking the appropriate API in order to fetch one or more
     * Customer ojects.
     * 
     * @param req
     *            an instance of {@link AccountingTransactionRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults fetchCustomer(AccountingTransactionRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<CustomerType> queryDtoResults = null;

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            CustomerDto criteriaDto = SubsidiaryJaxbDtoFactory
                    .createCustomerDtoCriteriaInstance(req.getCriteria().getCustomerCriteria());
            
            List<CustomerDto> dtoList = this.api.getExt(criteriaDto);
            if (dtoList == null) {
                rs.setMessage("Customer data not found!");
            }
            else {
                queryDtoResults = this.buildJaxbListData(dtoList);
                rs.setMessage("Customer record(s) found");
                rs.setRecordCount(dtoList.size());
            }
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to retrieve customer(s)");
            rs.setExtMessage(e.getMessage());
        } finally {
            this.api.close();
        }

        String xml = this.buildResponse(queryDtoResults, rs);
        results.setPayload(xml);
        return results;
    }
    
    /**
     * Handler for invoking the appropriate API in order to update a customer's
     * profile.
     * 
     * @param req
     *            an instance of {@link AccountingTransactionRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults updateCustomer(AccountingTransactionRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        int rc = 0;

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            CustomerDto criteriaDto = SubsidiaryJaxbDtoFactory
                    .createCustomerDtoInstance(req.getProfile().getCustomers().getCustomer().get(0));
            
            rc = this.api.update(criteriaDto);
            if (rc > 0) {
                rs.setMessage("Customer profile was updated successfully");    
            } else {
                rs.setMessage("Customer profile was not found - No updates performed");
            }
            rs.setReturnCode(rc);
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to update customer");
            rs.setExtMessage(e.getMessage());
        } finally {
            this.api.close();
        }

        List<CustomerType> queryDtoResults = new ArrayList<>();
        queryDtoResults.add(req.getProfile().getCustomers().getCustomer().get(0));
        String xml = this.buildResponse(queryDtoResults, rs);
        results.setPayload(xml);
        return results;
    }
    
    /**
     * Handler for invoking the appropriate API in order to delete a
     * Customer ojects.
     * 
     * @param req
     *            an instance of {@link AccountingTransactionRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults deleteCustomer(AccountingTransactionRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        int rc = 0;
        CustomerDto criteriaDto = null;

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            criteriaDto = SubsidiaryJaxbDtoFactory
                    .createCustomerDtoCriteriaInstance(req.getCriteria().getCustomerCriteria());
            
            rc = this.api.delete(criteriaDto);
            rs.setMessage("Customer delete operation completed!");
            rs.setExtMessage("Total records deleted: " + rc);
            rs.setReturnCode(rc);
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to delete customer: " + criteriaDto.getCustomerId());
            rs.setExtMessage(e.getMessage());
        } finally {
            this.api.close();
        }

        String xml = this.buildResponse(null, rs);
        results.setPayload(xml);
        return results;
    }
    
    /**
     * Handler for invoking the appropriate API in order to fetch the Customer's
     * transaction history ojects.
     * <p>
     * This method includes the customer base and all transaction history data
     * which serves as a FULL query.
     * 
     * @param req
     *            an instance of {@link AccountingTransactionRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults fetchTransHistory(AccountingTransactionRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<CustomerType> queryDtoResults = null;

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            CustomerDto criteriaDto = SubsidiaryJaxbDtoFactory
                    .createCustomerDtoCriteriaInstance(req.getCriteria().getCustomerCriteria());
            
            // Expecting only the creditor id to be included in the criteria
            // object to bring back only on instance
            List<CustomerDto> dtoCustList = this.api.getExt(criteriaDto);
            if (dtoCustList != null && dtoCustList.size() == 1) {
                List<CustomerXactHistoryDto> dtoXactHistList = this.api.getTransactionHistory(criteriaDto.getCustomerId());
                if (dtoXactHistList == null) {
                    rs.setMessage("Customer transaction history data not found!");
                    rs.setRecordCount(0);
                }
                else {
                    queryDtoResults = this.buildJaxbListData(dtoCustList.get(0), dtoXactHistList);
                    rs.setMessage("Customer transaction history record(s) found");
                    rs.setRecordCount(dtoCustList.size());
                }
            }
            else {
                rs.setMessage("Customer data not found or too many customers were fetched");
                rs.setRecordCount(0);
            }
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to retrieve customer transaction history");
            rs.setExtMessage(e.getMessage());
        } finally {
            this.api.close();
        }

        String xml = this.buildResponse(queryDtoResults, rs);
        results.setPayload(xml);
        return results;
    }
   
    private List<CustomerType> buildJaxbListData(List<CustomerDto> results) {
        List<CustomerType> list = new ArrayList<>();
        for (CustomerDto item : results) {
            CustomerType jaxbObj = SubsidiaryJaxbDtoFactory.createCustomerJaxbInstance(item, null, null);
            list.add(jaxbObj);
        }
        return list;
    }
    
    private List<CustomerType> buildJaxbListData(CustomerDto customer, List<CustomerXactHistoryDto> transHistory) {
        List<CustomerType> list = new ArrayList<>();
        Map<Integer, XactDto> xactDetails = this.buildActivityToXactMap(transHistory);
        CustomerType cust = SubsidiaryJaxbDtoFactory.createCustomerJaxbInstance(customer, xactDetails, transHistory);
        list.add(cust);
        return list;
    }
    
    private Map<Integer, XactDto> buildActivityToXactMap(List<CustomerXactHistoryDto> transHistory) {
        Map<Integer, XactDto> map = new HashMap<>();
        
        XactApi xactApi = XactApiFactory.createDefaultXactApi();
        for (CustomerXactHistoryDto item : transHistory) {
            try {
                XactDto dto = xactApi.getXactById(item.getXactId());
                if (dto != null) {
                    map.put(item.getActivityId(), dto);
                }
            } catch (XactApiException e) {
                logger.error("Unable to fetch transaction details for customer transaction history item, " + item.getActivityId(), e);
            }
        }
        return map;
    }
    
    @Override
    protected void validateRequest(AccountingTransactionRequest req) throws InvalidDataException {
        try {
            Verifier.verifyNotNull(req);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("Customer transaction request element is invalid");
        }
        
        switch (this.command) {
            case ApiTransactionCodes.SUBSIDIARY_CUSTOMER_GET:
            case ApiTransactionCodes.SUBSIDIARY_CUSTOMER_DELETE:
            case ApiTransactionCodes.SUBSIDIARY_CUSTOMER_TRAN_HIST_GET:
                try {
                    Verifier.verifyNotNull(req.getCriteria());
                    Verifier.verifyNotNull(req.getCriteria().getCustomerCriteria());
                }
                catch (VerifyException e) {
                    throw new InvalidRequestException(CustomerApiHandler.MSG_UPDATE_MISSING_CRITERIA);
                }    
                break;
                
            case ApiTransactionCodes.SUBSIDIARY_CUSTOMER_UPDATE:
                try {
                    Verifier.verifyNotNull(req.getProfile());
                    Verifier.verifyNotNull(req.getProfile().getCustomers());
                    Verifier.verifyNotEmpty(req.getProfile().getCustomers().getCustomer());
                }
                catch (VerifyException e) {
                    throw new InvalidRequestException(CustomerApiHandler.MSG_UPDATE_MISSING_PROFILE);
                }    
                break;
                
             default:
                 logger.warn("Customer API message handler command key, " + this.command + ", could not be validated");
                 break;
        }
    }

    @Override
    protected String buildResponse(List<CustomerType> payload,  MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);    
        }
        
        if (payload != null) {
            TransactionDetailGroup profile = this.jaxbObjFactory.createTransactionDetailGroup();
            profile.setCustomers(this.jaxbObjFactory.createCustomerListType());
            this.responseObj.setProfile(profile);
            this.responseObj.getProfile().getCustomers().getCustomer().addAll(payload);
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}
