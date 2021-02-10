package org.rmt2.api.handler.transaction.common;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.dao.transaction.XactDao;
import org.dao.transaction.XactDaoFactory;
import org.dto.XactDto;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.transaction.XactApi;
import org.modules.transaction.XactApiException;
import org.modules.transaction.XactApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.ApiMessageHandlerConst;
import org.rmt2.api.handler.BaseAccountingMessageHandlerTest;
import org.rmt2.api.handlers.transaction.XactAttachDocumentApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.MediaApplicationLinkResponse;

import com.api.config.SystemConfigurator;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.persistence.AbstractDaoClientImpl;
import com.api.persistence.DatabaseException;
import com.api.persistence.db.orm.Rmt2OrmClientFactory;
import com.api.util.RMT2File;
import com.api.util.RMT2String;

/**
 * 
 * @author roy.terrell
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class, XactAttachDocumentApiHandler.class,
        XactApiFactory.class, SystemConfigurator.class })
public class XactLinkMediaContentMessageHandlerTest extends BaseAccountingMessageHandlerTest {

    private XactApi mockApi;

    /**
     * 
     */
    public XactLinkMediaContentMessageHandlerTest() {
        return;
    }

    /*
     * (non-Javadoc)
     * 
     * @see testcases.messaging.MessageToListenerToHandlerTest#setUp()
     */
    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        XactDaoFactory mockXactDaoFactory = Mockito.mock(XactDaoFactory.class);
        XactDao mockDao = Mockito.mock(XactDao.class);
        mockApi = Mockito.mock(XactApi.class);
        PowerMockito.mockStatic(XactApiFactory.class);
        when(mockXactDaoFactory.createRmt2OrmXactDao(isA(String.class))).thenReturn(mockDao);
        PowerMockito.when(XactApiFactory.createDefaultXactApi()).thenReturn(this.mockApi);
        doNothing().when(this.mockApi).close();
        return;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see testcases.messaging.MessageToListenerToHandlerTest#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        return;
    }

    @Test
    public void test_Success() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/common/MediaApplicationLinkRequest.xml");

        try {
            when(this.mockApi.getXactById(isA(Integer.class))).thenReturn(CommonXactMockData.createMockSingleXact().get(0));
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for reversing a transaction");
        }
        try {
            when(this.mockApi.update(isA(XactDto.class))).thenReturn(1);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for reversing a transaction");
        }
        
        MessageHandlerResults results = null;
        XactAttachDocumentApiHandler handler = new XactAttachDocumentApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_CONTENT_APP_LINK, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        MediaApplicationLinkResponse actualRepsonse =
                (MediaApplicationLinkResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getMediaLinkData());
        Assert.assertNotNull(actualRepsonse.getProfile().getMediaLinkData().getAttachment());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        String msg = RMT2String.replace(XactAttachDocumentApiHandler.MSG_UPDATE_SUCCESS,
                String.valueOf(actualRepsonse.getProfile().getMediaLinkData().getAttachment().getContentId()),
                ApiMessageHandlerConst.MSG_PLACEHOLDER1);
        msg = RMT2String.replace(msg,
                String.valueOf(actualRepsonse.getProfile().getMediaLinkData().getAttachment().getPropertyId()),
                ApiMessageHandlerConst.MSG_PLACEHOLDER2);
        Assert.assertEquals(msg, actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testSuccess_NotFound() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/common/MediaApplicationLinkRequest.xml");

        try {
            when(this.mockApi.getXactById(isA(Integer.class))).thenReturn(null);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetcing a transaction");
        }
        try {
            when(this.mockApi.update(isA(XactDto.class))).thenReturn(1);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for reversing a transaction");
        }

        MessageHandlerResults results = null;
        XactAttachDocumentApiHandler handler = new XactAttachDocumentApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_CONTENT_APP_LINK, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        MediaApplicationLinkResponse actualRepsonse =
                (MediaApplicationLinkResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getMediaLinkData());
        Assert.assertNotNull(actualRepsonse.getProfile().getMediaLinkData().getAttachment());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(XactAttachDocumentApiHandler.MSG_UPDATE_NOTFOUND, actualRepsonse.getReplyStatus().getMessage());
    }
  
    @Test
    public void testSuccess_No_Rows_Effected() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/common/MediaApplicationLinkRequest.xml");

        try {
            when(this.mockApi.getXactById(isA(Integer.class))).thenReturn(CommonXactMockData.createMockSingleXact().get(0));
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetcing a transaction");
        }
        try {
            when(this.mockApi.update(isA(XactDto.class))).thenReturn(0);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for reversing a transaction");
        }

        MessageHandlerResults results = null;
        XactAttachDocumentApiHandler handler = new XactAttachDocumentApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_CONTENT_APP_LINK, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        MediaApplicationLinkResponse actualRepsonse =
                (MediaApplicationLinkResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getMediaLinkData());
        Assert.assertNotNull(actualRepsonse.getProfile().getMediaLinkData().getAttachment());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(XactAttachDocumentApiHandler.MSG_UPDATE_NO_ROWS_EFFECTED, actualRepsonse.getReplyStatus()
                .getMessage());
    }
    
    @Test
    public void testError_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/common/MediaApplicationLinkRequest.xml");

        try {
            when(this.mockApi.getXactById(isA(Integer.class))).thenThrow(new DatabaseException("Test API Error occurred"));
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetcing a transaction");
        }

        MessageHandlerResults results = null;
        XactAttachDocumentApiHandler handler = new XactAttachDocumentApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_CONTENT_APP_LINK, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        MediaApplicationLinkResponse actualRepsonse =
                (MediaApplicationLinkResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getMediaLinkData());
        Assert.assertNotNull(actualRepsonse.getProfile().getMediaLinkData().getAttachment());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_FAILURE, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(XactAttachDocumentApiHandler.MSG_API_ERROR, actualRepsonse.getReplyStatus()
                .getMessage());
    }


    
    @Test
    public void testValidation_Missing_Profile() {
        String request = RMT2File
                .getFileContentsAsString("xml/transaction/common/MediaApplicationLinkRequest_MissingProfile.xml");
        
        MessageHandlerResults results = null;
        XactAttachDocumentApiHandler handler = new XactAttachDocumentApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_CONTENT_APP_LINK, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        MediaApplicationLinkResponse actualRepsonse =
                (MediaApplicationLinkResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus()
                .getReturnStatus());
        Assert.assertEquals(XactAttachDocumentApiHandler.MSG_MISSING_PROFILE_DATA, actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Missing_MediaLinkData_Section() {
        String request = RMT2File
                .getFileContentsAsString("xml/transaction/common/MediaApplicationLinkRequest_MissingProfileMediaLinkData.xml");

        MessageHandlerResults results = null;
        XactAttachDocumentApiHandler handler = new XactAttachDocumentApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_CONTENT_APP_LINK, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        MediaApplicationLinkResponse actualRepsonse =
                (MediaApplicationLinkResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus()
                .getReturnStatus());
        Assert.assertEquals(XactAttachDocumentApiHandler.MSG_MISSING_PROFILE_DATA, actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Missing_Attachment_Section() {
        String request = RMT2File
                .getFileContentsAsString("xml/transaction/common/MediaApplicationLinkRequest_MissingAttachment.xml");

        MessageHandlerResults results = null;
        XactAttachDocumentApiHandler handler = new XactAttachDocumentApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_CONTENT_APP_LINK, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        MediaApplicationLinkResponse actualRepsonse =
                (MediaApplicationLinkResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus()
                .getReturnStatus());
        Assert.assertEquals(XactAttachDocumentApiHandler.MSG_MISSING_ATTACHMENT_SECTION, actualRepsonse.getReplyStatus()
                .getMessage());
    }
}
