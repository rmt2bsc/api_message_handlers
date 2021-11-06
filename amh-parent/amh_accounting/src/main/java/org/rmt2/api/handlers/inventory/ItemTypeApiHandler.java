package org.rmt2.api.handlers.inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.ItemMasterTypeDto;
import org.modules.CommonAccountingConst;
import org.modules.inventory.InventoryApi;
import org.modules.inventory.InventoryApiFactory;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.InventoryDetailGroup;
import org.rmt2.jaxb.InventoryItemtypeType;
import org.rmt2.jaxb.InventoryRequest;
import org.rmt2.jaxb.InventoryResponse;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ReplyStatusType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.AbstractJaxbMessageHandler;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes Item Master Type related messages to the Accounting
 * API.
 * 
 * @author roy.terrell
 *
 */
public class ItemTypeApiHandler extends 
                  AbstractJaxbMessageHandler<InventoryRequest, InventoryResponse, List<InventoryItemtypeType>> {
    
    private static final Logger logger = Logger.getLogger(ItemTypeApiHandler.class);
    private ObjectFactory jaxbObjFactory;
    private InventoryApi api;

    /**
     * @param payload
     */
    public ItemTypeApiHandler() {
        super();
        InventoryApiFactory f = new InventoryApiFactory();
        this.api = f.createApi(CommonAccountingConst.APP_NAME);
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createInventoryResponse();
        logger.info(ItemTypeApiHandler.class.getName() + " was instantiated successfully");
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
            // IS-70: Added logic to close API in cases of an error so to
            // prevent memory leaks.
            if (this.api != null) {
                this.api.close();
            }
            return r;
        }
        switch (command) {
            case ApiTransactionCodes.INVENTORY_ITEM_TYPE_GET:
                r = this.fetch(this.requestObj);
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
     * Item Master type ojects.
     * 
     * @param req
     *            an instance of {@link InventoryRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults fetch(InventoryRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<InventoryItemtypeType> queryDtoResults = null;

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            ItemMasterTypeDto criteriaDto = InventoryJaxbDtoFactory
                    .createItemTypeDtoCriteriaInstance(req.getCriteria().getItemTypeCriteria());
            
            List<ItemMasterTypeDto> dtoList = this.api.getItemType(criteriaDto);
            if (dtoList == null) {
                rs.setMessage("Inventory item type data not found!");
                rs.setRecordCount(0);
            }
            else {
                queryDtoResults = this.buildJaxbListData(dtoList);
                rs.setMessage("Inventory item  type record(s) found");
                rs.setRecordCount(dtoList.size());
            }
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to retrieve inventory item type");
            rs.setExtMessage(e.getMessage());
        } finally {
            this.api.close();
        }

        String xml = this.buildResponse(queryDtoResults, rs);
        results.setPayload(xml);
        return results;
    }
    
   
    private List<InventoryItemtypeType> buildJaxbListData(List<ItemMasterTypeDto> results) {
        List<InventoryItemtypeType> list = new ArrayList<>();
        for (ItemMasterTypeDto item : results) {
            InventoryItemtypeType jaxbObj = InventoryJaxbDtoFactory.createItemTypeJaxbInstance(item);
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
            throw new InvalidRequestException("Inventory item type message request element is invalid");
        }
        
        try {
            Verifier.verifyNotNull(req.getCriteria());
            Verifier.verifyNotNull(req.getCriteria().getItemTypeCriteria());
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("Inventory item type selection criteria is required for query operation");
        }
    }

    @Override
    protected String buildResponse(List<InventoryItemtypeType> payload,  MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);    
        }
        
        if (payload != null) {
            InventoryDetailGroup profile = this.jaxbObjFactory.createInventoryDetailGroup();
            this.responseObj.setProfile(profile);
            this.responseObj.getProfile().getInvItemType().addAll(payload);
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}
