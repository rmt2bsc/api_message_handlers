package org.rmt2.api.handlers.maint;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.modules.document.DocumentContentApi;
import org.modules.document.DocumentContentApiFactory;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.MimeContentType;
import org.rmt2.jaxb.MultimediaRequest;
import org.rmt2.jaxb.MultimediaResponse;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ReplyStatusType;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.AbstractJaxbMessageHandler;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.messaging.webservice.WebServiceConstants;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * An abstract message handler for processing messages that manipulate media
 * content and metadata via the Media API.
 * 
 * @author roy.terrell
 *
 */
public abstract class MediaContentApiHandler extends
        AbstractJaxbMessageHandler<MultimediaRequest, MultimediaResponse, MimeContentType> {
    
    private static final Logger logger = Logger.getLogger(MediaContentApiHandler.class);

    protected ObjectFactory jaxbObjFactory;
    protected DocumentContentApi api;
    protected MessageHandlerCommonReplyStatus rs;
    MimeContentType jaxbResults;

    /**
     * @param payload
     */
    public MediaContentApiHandler() {
        super();
        this.api = DocumentContentApiFactory.createMediaContentApi();
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createMultimediaResponse();
        this.rs = new MessageHandlerCommonReplyStatus();
        this.jaxbResults = null;
        logger.info(MediaContentApiHandler.class.getName() + " was instantiated successfully");
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
        return r;
    }

    /**
     * Handler for invoking the appropriate API in order to fetch, add, or
     * delete media content and its meta data.
     * 
     * @param req
     *            an instance of {@link MultimediaRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults doOperation(MultimediaRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();

        // Set reply status
        this.rs.setReturnStatus(WebServiceConstants.RETURN_STATUS_SUCCESS);
        this.rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);

        // Process transaction specifically
        this.processTransactionCode(req);

        String xml = this.buildResponse(this.jaxbResults, this.rs);
        results.setPayload(xml);
        return results;
    }
    
    /**
     * Implement this method to perform specific operations based on the
     * transaction code contained in the request
     * 
     * @param req
     *            instance of {@link MultimediaRequest}
     */
    protected abstract void processTransactionCode(MultimediaRequest req);
    
    /**
     * Verifies the multimedia request payload exists.
     * 
     * @param req
     *            instance of {@link MultimediaRequest}
     */
    @Override
    protected void validateRequest(MultimediaRequest req) throws InvalidDataException {
        try {
            Verifier.verifyNotNull(req);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("Genre message request element is invalid");
        }
    }


    @Override
    protected String buildResponse(MimeContentType payload, MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);
        }

        if (payload != null) {
            this.responseObj.setProfile(this.jaxbObjFactory.createMimeDetailGroup());
            this.responseObj.getProfile().setAudioVideoContent(payload);
        }

        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}
