package org.rmt2.api.handlers.inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.ItemMasterDto;
import org.modules.CommonAccountingConst;
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
import org.rmt2.jaxb.SimpleItemType;

import com.InvalidDataException;
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
            case ApiTransactionCodes.INVENTORY_ITEM_MASTER_ACTIVATE:
                r = this.activate(this.requestObj);
                break;
            case ApiTransactionCodes.INVENTORY_ITEM_MASTER_DEACTIVATE:
                r = this.deactivate(this.requestObj);
                break;
            case ApiTransactionCodes.INVENTORY_ITEM_RETAIL_OVERRIDE_ADD:
                r = this.addInventoryOverride(this.requestObj);
                break;
            case ApiTransactionCodes.INVENTORY_ITEM_RETAIL_OVERRIDE_REMOVE:
                r = this.removeInventoryOverride(this.requestObj);
                break;
            case ApiTransactionCodes.INVENTORY_VENDOR_UNASSIGNED_ITEMS_GET:
                r = this.fetchVendorUnassginedItems(this.requestObj);
                break;
            default:
                r = this.createErrorReply(MessagingConstants.RETURN_CODE_FAILURE,
                        MessagingConstants.RETURN_STATUS_BAD_REQUEST,
                        ERROR_MSG_TRANS_NOT_FOUND + command);
        }
        return r;
    }

    /**
     * Handler for invoking the appropriate API in order to activate an
     * inventory item.
     * 
     * @param req
     *            an instance of {@link InventoryRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults activate(InventoryRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        ItemMasterDto criteriaDto = null;

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            criteriaDto = InventoryJaxbDtoFactory
                    .createItemMasterDtoCriteriaInstance(req.getCriteria().getItemCriteria());
            
           int rc = this.api.activateItemMaster(criteriaDto.getItemId());
           rs.setMessage("Inventory item was activated successfully");
           rs.setReturnCode(rc);
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to activate inventory item, " + criteriaDto.getItemId());
            rs.setExtMessage(e.getMessage());
        } finally {
            this.api.close();
        }

        String xml = this.buildResponse(null, rs);
        results.setPayload(xml);
        return results;
    }
    
    /**
     * Handler for invoking the appropriate API in order to deactivate an
     * inventory item.
     * 
     * @param req
     *            an instance of {@link InventoryRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults deactivate(InventoryRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        ItemMasterDto criteriaDto = null;

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            criteriaDto = InventoryJaxbDtoFactory
                    .createItemMasterDtoCriteriaInstance(req.getCriteria().getItemCriteria());
            
           int rc = this.api.deactivateItemMaster(criteriaDto.getItemId());
           rs.setMessage("Inventory item was deactivated successfully");
           rs.setReturnCode(rc);
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to deactivate inventory item, " + criteriaDto.getItemId());
            rs.setExtMessage(e.getMessage());
        } finally {
            this.api.close();
        }

        String xml = this.buildResponse(null, rs);
        results.setPayload(xml);
        return results;
    }
    
    /**
     * Handler for invoking the appropriate API in order to add inventory item
     * retail overrided.
     * 
     * @param req
     *            an instance of {@link InventoryRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults addInventoryOverride(InventoryRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        ItemMasterDto criteriaDto = null;

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            criteriaDto = InventoryJaxbDtoFactory
                    .createItemMasterDtoCriteriaInstance(req.getCriteria().getItemCriteria());
            
           Integer[] itemIdList = this.getItemIdList(req.getCriteria().getItemCriteria().getItems().getItem());
           int rc = this.api.addInventoryOverride(criteriaDto.getVendorId(), itemIdList);
           rs.setMessage("Inventory item retail override was applied");
           rs.setReturnCode(rc);
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to apply inventory item retail override, " + criteriaDto.getItemId());
            rs.setExtMessage(e.getMessage());
        } finally {
            this.api.close();
        }

        String xml = this.buildResponse(null, rs);
        results.setPayload(xml);
        return results;
    }
    
    /**
     * Handler for invoking the appropriate API in order to remove inventory item
     * retail overrided.
     * 
     * @param req
     *            an instance of {@link InventoryRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults removeInventoryOverride(InventoryRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        ItemMasterDto criteriaDto = null;

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            criteriaDto = InventoryJaxbDtoFactory
                    .createItemMasterDtoCriteriaInstance(req.getCriteria().getItemCriteria());
            
           Integer[] itemIdList = this.getItemIdList(req.getCriteria().getItemCriteria().getItems().getItem());
           int rc = this.api.removeInventoryOverride(criteriaDto.getVendorId(), itemIdList);
           rs.setMessage("Inventory item retail override was removed");
           rs.setReturnCode(rc);
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to remove inventory item retail override, " + criteriaDto.getItemId());
            rs.setExtMessage(e.getMessage());
        } finally {
            this.api.close();
        }

        String xml = this.buildResponse(null, rs);
        results.setPayload(xml);
        return results;
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
                rs.setMessage("Inventory item data not found!");
                rs.setReturnCode(0);
            }
            else {
                queryDtoResults = this.buildJaxbListData(dtoList);
                rs.setMessage("Inventory item record(s) found");
                rs.setReturnCode(dtoList.size());
            }
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to retrieve inventory item(s)");
            rs.setExtMessage(e.getMessage());
        } finally {
            this.api.close();
        }

        String xml = this.buildResponse(queryDtoResults, rs);
        results.setPayload(xml);
        return results;
    }
    
    /**
     * Handler for invoking the appropriate API in order to fetch one or more
     * Vendor Unassigned Items.
     * 
     * @param req
     *            an instance of {@link InventoryRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults fetchVendorUnassginedItems(InventoryRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<InventoryItemType> queryDtoResults = null;
        int vendorId = 0;
        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            vendorId = req.getCriteria().getVendorItemCriteria().getCreditorId().intValue();
            
            List<ItemMasterDto> dtoList = this.api.getVendorUnassignItems(vendorId);
            if (dtoList == null) {
                rs.setMessage("Vendor unassigned item data not found for vendor id, " + vendorId);
                rs.setReturnCode(0);
            }
            else {
                queryDtoResults = this.buildJaxbListData(dtoList);
                rs.setMessage("Vendor unassigned item record(s) found for vendor id, " + vendorId);
                rs.setReturnCode(dtoList.size());
            }
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to retrieve Vendor unassigned item(s) for vendor id, " + vendorId);
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
                    .createItemMasterDtoInstance(req.getProfile().getInvItem().get(0));
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
                rs.setMessage("Inventory item was created successfully");
                rs.setExtMessage("The new acct id is " + rc);
            }
            else {
                rs.setMessage("Inventory item was modified successfully");
                rs.setExtMessage("Total number of rows modified: " + rc);
            }
            this.api.commitTrans();
            
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to update " + (newRec ? "new" : "existing")  + " inventory item");
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
            
            if (rc > 0) {
                rs.setMessage("Inventory item was deleted successfully");
                rs.setExtMessage("The inventory item id deleted was " + criteriaDto.getItemId());    
            } 
            else if (rc == 0) {
                rs.setMessage("Inventory item was not deleted");
                rs.setExtMessage("The inventory item id was not found: " + criteriaDto.getItemId());    
            }
            
            this.api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to delete inventory item record");
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
            throw new InvalidRequestException("Inventory item message request element is invalid");
        }
        
        // Validate request for update/delete operation
        switch (this.command) {
            case ApiTransactionCodes.INVENTORY_ITEM_MASTER_GET:
            case ApiTransactionCodes.INVENTORY_ITEM_MASTER_DELETE:
            case ApiTransactionCodes.INVENTORY_ITEM_MASTER_ACTIVATE:
            case ApiTransactionCodes.INVENTORY_ITEM_RETAIL_OVERRIDE_ADD:
            case ApiTransactionCodes.INVENTORY_ITEM_RETAIL_OVERRIDE_REMOVE:
                try {
                    Verifier.verifyNotNull(req.getCriteria());
                    Verifier.verifyNotNull(req.getCriteria().getItemCriteria());
                }
                catch (VerifyException e) {
                    throw new InvalidRequestException("Inventory item selection criteria is required for query/delete operation");
                }
                if (this.command.equals(ApiTransactionCodes.INVENTORY_ITEM_MASTER_DELETE)) {
                    try {
                        Verifier.verifyNotNull(req.getCriteria().getItemCriteria().getItemId());
                        Verifier.verifyPositive(req.getCriteria().getItemCriteria().getItemId());
                    }
                    catch (VerifyException e) {
                        throw new InvalidRequestException("A valid item id is required when deleting an inventory item from the database");
                    }   
                }
                
                if (this.command.equals(ApiTransactionCodes.INVENTORY_ITEM_RETAIL_OVERRIDE_ADD)
                        || this.command.equals(ApiTransactionCodes.INVENTORY_ITEM_RETAIL_OVERRIDE_REMOVE)) {
                    try {
                        Verifier.verifyNotNull(req.getCriteria().getItemCriteria().getItems());
                        Verifier.verifyNotNull(req.getCriteria().getItemCriteria().getItems().getItem());
                        Verifier.verifyNotEmpty(req.getCriteria().getItemCriteria().getItems().getItem());
                    }
                    catch (VerifyException e) {
                        throw new InvalidRequestException("A valid list of item id's is required when adding or removing item retail override");
                    }   
                }
                break;
                
            case ApiTransactionCodes.INVENTORY_VENDOR_UNASSIGNED_ITEMS_GET:
                try {
                    Verifier.verifyNotNull(req.getCriteria());
                    Verifier.verifyNotNull(req.getCriteria().getVendorItemCriteria());
                }
                catch (VerifyException e) {
                    throw new InvalidRequestException("Vendor item selection criteria is required");
                }
                try {
                    Verifier.verifyNotNull(req.getCriteria().getVendorItemCriteria().getCreditorId());
                }
                catch (VerifyException e) {
                    throw new InvalidRequestException("Vendor Id is required for vendor unassigned item query operation");
                }
                break;

            case ApiTransactionCodes.INVENTORY_ITEM_MASTER_UPDATE:
            
                try {
                    Verifier.verifyNotNull(req.getProfile());
                    Verifier.verifyNotEmpty(req.getProfile().getInvItem());
                }
                catch (VerifyException e) {
                    throw new InvalidRequestException("Inventory item data is required for update operation");
                }
                try {
                    Verifier.verifyTrue(req.getProfile().getInvItem().size() == 1);
                }
                catch (VerifyException e) {
                    throw new InvalidRequestException("Only one (1) inventory item record is required for update operation");
                }
                break;
            default:
                break;
        }
    }

    private Integer[] getItemIdList(List<SimpleItemType> items) {
        if (items == null) {
            return null;
        }
        Integer[] list = new Integer[items.size()];
        for (int ndx = 0; ndx < items.size(); ndx++) {
            list[ndx] = items.get(ndx).getItemId().intValue();
        }
        return list;
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
