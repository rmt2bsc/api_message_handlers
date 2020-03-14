
package org.rmt2.api.handler.util;

import org.rmt2.jaxb.ReplyStatusType;
import org.rmt2.util.ReplyStatusTypeBuilder;

import com.api.messaging.handler.MessageHandlerCommonReplyStatus;

/**
 * @author roy.terrell
 *
 */
public class MessageHandlerUtility {

    /**
     * 
     */
    public MessageHandlerUtility() {
        
    }

    /**
     * Create a ReplyStatusType object from a MessageHandlerCommonReplyStatus
     * object.
     * 
     * @param source
     *            instance of {@link MessageHandlerCommonReplyStatus}
     * @return instance of {@link ReplyStatusType}
     */
    public static final ReplyStatusType createReplyStatus(MessageHandlerCommonReplyStatus source) {
        if (source == null) {
            return null;
        }
        
        ReplyStatusType rs = ReplyStatusTypeBuilder.Builder.create()
                .withReturnCode(source.getReturnCode())
                .withRecordCount(source.getRecordCount())
                .withStatus(source.getReturnStatus())
                .withMessage(source.getMessage())
                .withDetailMessage(source.getExtMessage()).build();
        
        return rs;
    }
}
