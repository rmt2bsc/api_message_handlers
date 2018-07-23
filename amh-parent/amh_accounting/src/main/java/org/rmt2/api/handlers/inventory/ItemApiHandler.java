package org.rmt2.api.handlers.inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.ItemMasterDto;
import org.modules.CommonAccountingConst;
import org.modules.generalledger.GeneralLedgerApiException;
import org.modules.inventory.InventoryApi;
import org.modules.inventory.InventoryApiFactory;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.InventoryDetailGroup;
import org.rmt2.jaxb.InventoryItemType;
import org.rmt2.jaxb.InventoryRequest;
import org.rmt2.jaxb.InventoryResponse;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ReplyStatusType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.InvalidDataException;
import com.NotFoundException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.AbstractJaxbMessageHandler;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes Item Master related messages to the Accounting
 * API.
 * 
 * @author roy.terrell
 *
 */
public class ItemApiHandler extends 
                  AbstractJaxbMessageHandler<InventoryRequest, InventoryResponse, List<InventoryItemType>> {
    
    private static final Logger logger = Logger.getLogger(ItemApiHandler.class);
    private ObjectFactory jaxbObjFactory;
    private InventoryApi api;

    /**
     * @param payload
     */
    public ItemApiHandler() {
        super();
        InventoryApiFactory f = new InventoryApiFactory();
        this.api = f.createApi(CommonAccountingConst.APP_NAME);
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createInventoryResponse();
        logger.info(ItemApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.INVENTORY_ITEM_MASTER_GET:
                r = this.fetch(this.requestObj);
                break;
            case ApiTransactionCodes.INVENTORY_ITEM_MASTER_UPDATE:
                r = this.update(this.requestObj);
                break;
            case ApiTransactionCodes.INVENTORY_ITEM_MASTER_DELETE:
                r = this.delete(this.requestObj);
                
                break;
            default:
                r = this.createErrorReply(MessagingConstants.RETURN_CODE_FAILURE,
                        MessagingConstants.RETURN_STATUS_BAD_REQUEST,
                        ERROR_MSG_TRANS_NOT_FOUND + command);
        }
        return r;
    }

    /**
     * Handler for invoking the appropriate API in order to fetch one or more Item Master objects.
     * 
     * @param req
     *            an instance of {@link InventoryRequest}
     * @return an instance of {@link MessageHandlerResults}           
     */
    protected MessageHandlerResults fetch(InventoryRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<InventoryItemType> queryDtoResults = null;

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            ItemMasterDto criteriaDto = InventoryJaxbDtoFactory
                    .createItemMasterDtoCriteriaInstance(req.getCriteria().getItemCriteria());
            
            List<ItemMasterDto> dtoList = this.api.getItem(criteriaDto);
            if (dtoList == null) {
                rs.setMessage("Item master data not found!");
                rs.setReturnCode(0);
            }
            else {
                queryDtoResults = this.buildJaxbListData(dtoList);
                rs.setMessage("Item master record(s) found");
                rs.setReturnCode(dtoList.size());
            }
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to retrieve GL Account(s)");
            rs.setExtMessage(e.getMessage());
        } finally {
            this.api.close();
        }

        String xml = this.buildResponse(queryDtoResults, rs);
        results.setPayload(xml);
        return results;
    }
    
    /**
     * Handler for invoking the appropriate API in order to update the specified
     * GL Account.
     * 
     * @param req
     *            an instance of {@link InventoryRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults update(InventoryRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<InventoryItemType> updateData = null;
        
        boolean newRec = false;
        int rc = 0;
        try {
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            ItemMasterDto dataObjDto = InventoryJaxbDtoFactory
                    .createItemMasterDtoCriteriaInstance(req.getProfile().getInvItem().get(0));
            newRec = (dataObjDto.getItemId() == 0);
            
            // call api
            this.api.beginTrans();
            rc = this.api.updateItemMaster(dataObjDto);
            
            // prepare response with updated contact data
            List<ItemMasterDto> updateList = new ArrayList<>();
            updateList.add(dataObjDto);
            updateData = this.buildJaxbListData(updateList);
            
            // Return code is either the total number of rows updated or the new group id
            rs.setReturnCode(rc);
            if (newRec) {
                rs.setMessage("Item master was created successfully");
                rs.setExtMessage("The new acct id is " + rc);
            }
            else {
                rs.setMessage("Item master was modified successfully");
                rs.setExtMessage("Total number of rows modified: " + rc);
            }
            this.api.commitTrans();
            
        } catch (GeneralLedgerApiException | NotFoundException | InvalidDataException e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to update " + (newRec ? "new" : "existing")  + " Item master");
            rs.setExtMessage(e.getMessage());
            updateData = req.getProfile().getInvItem();
            this.api.rollbackTrans();
        } finally {
            this.api.close();
        }
        String xml = this.buildResponse(updateData, rs);
        results.setPayload(xml);
        return results;
    }
    
    /**
     * Handler for invoking the appropriate API in order to delete the specified
     * GL Account.
     * 
     * @param req
     *            an instance of {@link InventoryRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults delete(InventoryRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        
        int rc = 0;
        try {
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            ItemMasterDto criteriaDto = InventoryJaxbDtoFactory
                    .createItemMasterDtoCriteriaInstance(req.getCriteria().getItemCriteria());
            
            // call api
            this.api.beginTrans();
            rc = this.api.deleteItemMaster(criteriaDto.getItemId());
            
            // Return code is either the total number of rows deleted
            rs.setReturnCode(rc);
            rs.setMessage("Item master was deleted successfully");
            rs.setExtMessage("The item master id deleted was " + criteriaDto.getItemId());
            this.api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to delelte item master");
            rs.setExtMessage(e.getMessage());
            this.api.rollbackTrans();
        } finally {
            this.api.close();
        }
        
        String xml = this.buildResponse(null, rs);
        results.setPayload(xml);
        return results;
    }
    
    private List<InventoryItemType> buildJaxbListData(List<ItemMasterDto> results) {
        List<InventoryItemType> list = new ArrayList<>();
        for (ItemMasterDto item : results) {
            InventoryItemType jaxbObj = InventoryJaxbDtoFactory.createItemMasterJaxbInstance(item);
            list.add(jaxbObj);
        }
        return list;
    }
    
    
    @Override
    protected void validateRequest(InventoryRequest req) throws InvalidDataException {
        try {
            Verifier.verifyNotNull(req);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("Item master message request element is invalid");
        }
        
        // Validate request for update/delete operation
        switch (this.command) {
            case ApiTransactionCodes.INVENTORY_ITEM_MASTER_UPDATE:
            case ApiTransactionCodes.INVENTORY_ITEM_MASTER_DELETE:
                try {
                    Verifier.verifyNotNull(req.getProfile());
                    Verifier.verifyNotEmpty(req.getProfile().getInvItem());
                }
                catch (VerifyException e) {
                    throw new InvalidRequestException("Item master data is required for update/delete operation");
                }
                try {
                    Verifier.verifyTrue(req.getProfile().getInvItem().size() == 1);
                }
                catch (VerifyException e) {
                    throw new InvalidRequestException("Only one (1) item master record is required for update/delete operation");
                }
                
                if (this.command.equals(ApiTransactionCodes.INVENTORY_ITEM_MASTER_DELETE)) {
                    try {
                        Verifier.verifyNotNull(req.getProfile().getInvItem().get(0).getItemId());
                        Verifier.verifyPositive(req.getProfile().getInvItem().get(0).getItemId());
                    }
                    catch (VerifyException e) {
                        throw new InvalidRequestException("A valid item id is required when deleting an item master from the database");
                    }   
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected String buildResponse(List<InventoryItemType> payload,  MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);    
        }
        
        if (payload != null) {
            InventoryDetailGroup profile = this.jaxbObjFactory.createInventoryDetailGroup();
            this.responseObj.setProfile(profile);
            this.responseObj.getProfile().getInvItem().addAll(payload);
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}
