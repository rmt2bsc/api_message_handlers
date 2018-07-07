package org.rmt2.api.handlers;

import org.apache.log4j.Logger;
import org.rmt2.api.handlers.util.MessageHandlerUtility;
import org.rmt2.jaxb.MimeContentType;
import org.rmt2.jaxb.MultimediaRequest;
import org.rmt2.jaxb.MultimediaResponse;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ReplyStatusType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.AbstractJaxbMessageHandler;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;

/**
 * @author appdev
 *
 */
public class MediaPayloadHandler extends AbstractJaxbMessageHandler<MultimediaRequest, MultimediaResponse, MimeContentType> {
    private static final Logger logger = Logger.getLogger(MediaPayloadHandler.class);
    private ObjectFactory jaxbObjFactory;

    /**
     * @param payload
     */
    public MediaPayloadHandler() {
        super();
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = this.jaxbObjFactory.createMultimediaResponse();
        logger.info(MediaPayloadHandler.class.getName() + " was instantiated successfully");
    }

    @Override
    protected void validateRequest(MultimediaRequest req) throws InvalidRequestException {
        if (req == null) {
            throw new InvalidRequestException("Multimedia message request element is invalid");
        }
    }

    @Override
    protected String buildResponse(MimeContentType payload, MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);      
        }
        
        if (payload != null) {
            this.responseObj.setContent(payload);
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}
