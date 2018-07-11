package org.rmt2.api.handlers.generalledger;

import org.apache.log4j.Logger;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.jaxb.AccountingGeneralLedgerRequest;
import org.rmt2.jaxb.AccountingGeneralLedgerResponse;
import org.rmt2.jaxb.GlDetailGroup;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ReplyStatusType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.AbstractJaxbMessageHandler;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;

/**
 * 
 * @author roy.terrell
 *
 */
public class GeneralLedgerPayloadHandler extends
        AbstractJaxbMessageHandler<AccountingGeneralLedgerRequest, AccountingGeneralLedgerResponse, GlDetailGroup> {
    private static final Logger logger = Logger.getLogger(GeneralLedgerPayloadHandler.class);
    private ObjectFactory jaxbObjFactory;

    /**
     * @param payload
     */
    public GeneralLedgerPayloadHandler() {
        super();
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createAccountingGeneralLedgerResponse();
        logger.info(GeneralLedgerPayloadHandler.class.getName() + " was instantiated successfully");
    }

    @Override
    protected void validateRequest(AccountingGeneralLedgerRequest req) throws InvalidRequestException {
        if (req == null) {
            throw new InvalidRequestException("Accounting General Ledger message request element is invalid");
        }
    }

    @Override
    protected String buildResponse(GlDetailGroup payload, MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);    
        }
        
        if (payload != null) {
            this.responseObj.getProfile().add(payload);
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }

    // /*
    // * (non-Javadoc)
    // *
    // * @see
    // *
    // com.api.messaging.jms.handler.AbstractMessageHandler#processRequest(java
    // * .lang.String, java.io.Serializable)
    // */
    // @Override
    // public MessageHandlerResults processMessage(String command, Serializable
    // payload)
    // throws MessageHandlerCommandException {
    // super.processMessage(command, payload);
    // String data = this.getPayloadAsString();
    //
    // List<AccountDto> list = this.getAccount("Accounts Payable");
    // MessageHandlerResults r = new MessageHandlerResults();
    // r.setPayload("GL Account description ==================> " +
    // list.get(0).getAcctDescription());
    // return r;
    // }
    //
    // private List<AccountDto> getAccount(String acctName) {
    // // Test config access
    // AppServerConfig config = SystemConfigurator.getConfig();
    // GeneralLedgerApiFactory f = new GeneralLedgerApiFactory();
    // GlAccountApi api = f.createApi();
    // AccountDto criteria = Rmt2AccountDtoFactory.createAccountInstance(null);
    // criteria.setAcctName(acctName);
    // try {
    // return api.getAccount(criteria);
    // } catch (Exception e) {
    // throw new RMT2RuntimeException(e);
    // }
    // }

}
