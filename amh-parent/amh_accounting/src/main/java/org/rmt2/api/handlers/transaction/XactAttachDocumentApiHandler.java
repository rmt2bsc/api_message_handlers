package org.rmt2.api.handlers.transaction;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.dto.XactDto;
import org.modules.CommonAccountingConst;
import org.modules.transaction.XactApi;
import org.modules.transaction.XactApiFactory;
import org.rmt2.api.ApiMessageHandlerConst;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.api.handlers.AccountingtMsgHandlerUtility;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.MediaApplicationLinkRequest;
import org.rmt2.jaxb.MediaApplicationLinkResponse;
import org.rmt2.jaxb.MediaAttachmentDetailsType;
import org.rmt2.jaxb.MediaAttachmentType;
import org.rmt2.jaxb.MediaLinkGroup;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ReplyStatusType;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.AbstractJaxbMessageHandler;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.RMT2String;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes messages pertaining to attaching media content to
 * accounting transactions.
 * 
 * @author roy.terrell
 *
 */
public class XactAttachDocumentApiHandler extends 
        AbstractJaxbMessageHandler<MediaApplicationLinkRequest, MediaApplicationLinkResponse, MediaAttachmentDetailsType> {
    
    private static final Logger logger = Logger.getLogger(XactAttachDocumentApiHandler.class);
    public static final String MSG_UPDATE_SUCCESS = "Document, %1, was attached to accounting transaction, %2, successfully";
    public static final String MSG_UPDATE_NOTFOUND = "Unable to attach documnet due to transaction does not exits";
    public static final String MSG_UPDATE_NO_ROWS_EFFECTED = "The document attachment process did not effect any transactions";
    public static final String MSG_API_ERROR = "Media application link failed due to a system error.  Consult system administrator";
    public static final String MSG_DATA_NOT_FOUND = "Accounting transaction was not found using %1";
    public static final String MSG_MISSING_PROFILE_DATA = "Media application link request profile is required";
    public static final String MSG_MISSING_ATTACHMENT_SECTION = "Media attachment section is missing from the profile";
    
    
    private XactApi api;
    protected ObjectFactory jaxbObjFactory;
    protected String targetLevel;

    /**
     * Create XactAttachDocumentApiHandler object
     * 
     * @param connection
     *            an instance of {@link DaoClient}
     */
    public XactAttachDocumentApiHandler() {
        super();
        this.api = XactApiFactory.createDefaultXactApi();

        // UI-37: Added for capturing the update user id
        this.transApi = this.api;
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createMediaApplicationLinkResponse();
        
        // Load cache data
        AccountingtMsgHandlerUtility.loadXactTypeCache(false);
        logger.info(XactAttachDocumentApiHandler.class.getName() + " was instantiated successfully");
    }

    
    /**
     * Processes requests pertaining to updating an accounting transaction with
     * a media attachment.
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
            // This means an error occurred.
            return r;
        }
        switch (command) {
            case ApiTransactionCodes.MEDIA_CONTENT_APP_LINK:
                r = this.update(this.requestObj);
                break;
            default:
                r = this.createErrorReply(MessagingConstants.RETURN_CODE_FAILURE,
                        MessagingConstants.RETURN_STATUS_BAD_REQUEST,
                        ERROR_MSG_TRANS_NOT_FOUND + command);
        }
        return r;
    }

    /**
     * Handler for invoking the appropriate API method in order to attach a
     * media document to an accounting transaction.
     * 
     * @param req
     *            an instance of {@link MediaApplicationLinkRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults update(MediaApplicationLinkRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        MediaAttachmentDetailsType reqData = req.getProfile().getMediaLinkData().getAttachment();
        String msg = null;
        
        // Set reply status
        rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
        rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
        
        // Ensure that this handler is the intended audience for this attachment
        if (reqData.getProjectName().equalsIgnoreCase(ApiMessageHandlerConst.MEDIA_LINK_VALID_APPNAME_ACCOUNTING) &&
                reqData.getModuleName().equalsIgnoreCase(ApiMessageHandlerConst.MEDIA_LINK_VALID_MODULENAME_ACCOUNTING)) {
            try {
                XactDto xactDto = this.api.getXactById(reqData.getPropertyId());
                if (xactDto == null) {
                    msg = XactAttachDocumentApiHandler.MSG_UPDATE_NOTFOUND;
                    rs.setRecordCount(0);
                }
                else {
                    xactDto.setDocumentId(reqData.getContentId());
                    int rc = this.api.update(xactDto);
                    rs.setRecordCount(rc);

                    // Setup response message
                    if (rc == 1) {
                        msg = RMT2String.replace(XactAttachDocumentApiHandler.MSG_UPDATE_SUCCESS,
                                String.valueOf(reqData.getContentId()), ApiMessageHandlerConst.MSG_PLACEHOLDER1);
                        msg = RMT2String.replace(msg, String.valueOf(reqData.getPropertyId()),
                                ApiMessageHandlerConst.MSG_PLACEHOLDER2);
                    }
                    else {
                        msg = XactAttachDocumentApiHandler.MSG_UPDATE_NO_ROWS_EFFECTED;
                    }
                }

                rs.setMessage(msg);
                this.responseObj.setHeader(req.getHeader());
                this.api.commitTrans();
            } catch (Exception e) {
                logger.error("Error occurred during API Message Handler operation, " + this.command, e);
                rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
                rs.setMessage(XactAttachDocumentApiHandler.MSG_API_ERROR);
                rs.setExtMessage(e.getMessage());
                this.api.rollbackTrans();
            } finally {
                this.api.close();
            }
        }
        else {
            msg = RMT2String.replace(ApiMessageHandlerConst.MEDIA_LINK_PROCESSING_SKIPPED,
                    CommonAccountingConst.APP_NAME, ApiMessageHandlerConst.MSG_PLACEHOLDER);
            rs.setMessage(msg);
            rs.setRecordCount(0);
            this.responseObj.setHeader(req.getHeader());
        }
        
        String xml = this.buildResponse(reqData, rs);
        results.setPayload(xml);
        return results;
    }
    

    /**
     * Validates the MediaApplicationLinkRequest request.
     * 
     * @param req
     *            instance of {@link MediaApplicationLinkRequest}
     * @throws InvalidDataException
     *             when either <i>req</i> is null, the profile section absent,
     *             or the media attachment section is absent.
     */
    @Override
    protected void validateRequest(MediaApplicationLinkRequest req) throws InvalidDataException {
        try {
            Verifier.verifyNotNull(req);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("Transaction request element is invalid");
        }

        try {
            Verifier.verifyNotNull(req.getProfile());
            Verifier.verifyNotNull(req.getProfile().getMediaLinkData());
        } catch (VerifyException e) {
            throw new InvalidRequestException(XactAttachDocumentApiHandler.MSG_MISSING_PROFILE_DATA);
        }

        try {
            Verifier.verifyNotNull(req.getProfile().getMediaLinkData().getAttachment());
        } catch (VerifyException e) {
            throw new InvalidRequestException(XactAttachDocumentApiHandler.MSG_MISSING_ATTACHMENT_SECTION);
        }
    }
    
    @Override
    protected String buildResponse(MediaAttachmentDetailsType payload, MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);
        }

        if (payload != null) {
            MediaLinkGroup profile = this.jaxbObjFactory.createMediaLinkGroup();
            MediaAttachmentType mat = this.jaxbObjFactory.createMediaAttachmentType();
            mat.setAttachment(payload);
            profile.setMediaLinkData(mat);
            this.responseObj.setProfile(profile);
        }

        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}
