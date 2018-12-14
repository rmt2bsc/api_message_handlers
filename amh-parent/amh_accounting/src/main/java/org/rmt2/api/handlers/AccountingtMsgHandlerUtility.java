package org.rmt2.api.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dto.XactDto;
import org.dto.XactTypeDto;
import org.dto.XactTypeItemActivityDto;
import org.modules.transaction.XactApi;
import org.modules.transaction.XactApiException;
import org.modules.transaction.XactApiFactory;
import org.rmt2.api.handlers.transaction.TransactionJaxbDtoFactory;
import org.rmt2.jaxb.RecordTrackingType;
import org.rmt2.jaxb.SimpleItemType;
import org.rmt2.jaxb.XactCodeGroupType;
import org.rmt2.jaxb.XactCodeType;
import org.rmt2.jaxb.XactLineitemType;
import org.rmt2.jaxb.XactType;
import org.rmt2.jaxb.XacttypeType;
import org.rmt2.util.RecordTrackingTypeBuilder;
import org.rmt2.util.accounting.transaction.XactCodeGroupTypeBuilder;
import org.rmt2.util.accounting.transaction.XactCodeTypeBuilder;
import org.rmt2.util.accounting.transaction.XactTypeBuilder;
import org.rmt2.util.accounting.transaction.XacttypeTypeBuilder;

import com.api.util.RMT2String;

/**
 * Utility class for accounting API Message Handling 
 * 
 * @author roy.terrell
 *
 */
public class AccountingtMsgHandlerUtility {
    private static final Logger LOGGER = Logger.getLogger(AccountingtMsgHandlerUtility.class);
    private static final String CACHE_MSG_ALREADY_LOADED = "No need to load Tranaction type cache due to it is already initialized";
    private static final String CACHE_MSG_INITIAL_LOAD = "Transaction Type Cache was loaded with %s records";
    private static final String CACHE_MSG_LOAD_ERROR = "Unable to load Transaction Type Cache";
    private static Map<Integer, XactTypeDto> XACTTYPE_CACHE;
    

    /**
     * Default constructor
     */
    public AccountingtMsgHandlerUtility() {
    }
    
    /**
     * Retrieves transaction type code from cache
     * 
     * @param xactTypeId
     * @return String
     */
    public static final String getXactTypeCodeFromCache(int xactTypeId) {
        if (XACTTYPE_CACHE != null) {
            XactTypeDto dto = XACTTYPE_CACHE.get(xactTypeId);
            if (dto != null) {
                return dto.getXactTypeCode();
            }
        }
        return null;
    }
    
    /**
     * Retrieves transaction type description from cache
     * 
     * @param xactTypeId
     * @return String
     */
    public static final String getXactTypeDescriptionFromCache(int xactTypeId) {
        if (XACTTYPE_CACHE != null) {
            XactTypeDto dto = XACTTYPE_CACHE.get(xactTypeId);
            if (dto != null) {
                return dto.getXactTypeDescription();
            }
        }
        return null;
    }
    
    /**
     * Creates the transaction type cache by loading XactTypeDto objects from
     * the Accounting API.
     * 
     * @param forceLoad
     *            set to <i>true</i> when the desire is to load unconditionally.
     *            Set to false to load only once.
     * @return int > 0 representing the total number of records loaded; 0 when
     *         cache is discovered to already be loaded or there are no records
     *         available from the datasource; -1 when error loading data
     *         occurred.
     */
    public static final int loadXactTypeCache(boolean forceLoad) {
        if (!forceLoad && XACTTYPE_CACHE != null) {
            LOGGER.info(AccountingtMsgHandlerUtility.CACHE_MSG_ALREADY_LOADED);
            return 0;
        }
        XactApi api = XactApiFactory.createDefaultXactApi();
        List<XactTypeDto> list = null;
        try {
             list = api.getXactTypes(null);
        } catch (XactApiException e) {
            LOGGER.error(AccountingtMsgHandlerUtility.CACHE_MSG_LOAD_ERROR, e);
            return -1;
        }
        
        XACTTYPE_CACHE = new HashMap<>();
        for (XactTypeDto item : list) {
            XACTTYPE_CACHE.put(item.getXactTypeId(), item);
        }
        String formattedMsg = RMT2String.replace(
                AccountingtMsgHandlerUtility.CACHE_MSG_ALREADY_LOADED,
                String.valueOf(XACTTYPE_CACHE.size()), "%s");
        LOGGER.info(formattedMsg);
        return XACTTYPE_CACHE.size();
    }
    
    
    
    /**
     * Translates all inventory item id's contained in a list of SimpleItemType
     * JAXB objects to an Integer array
     * 
     * @param items
     *            an List of {@link SimpleItemType} instances
     * @return Integer[]
     */
    public static final Integer[] getInventoryItemIdArray(List<SimpleItemType> items) {
        if (items == null) {
            return null;
        }
        Integer[] list = new Integer[items.size()];
        for (int ndx = 0; ndx < items.size(); ndx++) {
            list[ndx] = items.get(ndx).getItemId().intValue();
        }
        return list;
    }

    /**
     * Construct a XactType JAXB object
     * 
     * @param dto
     *            an instance of {@link XactDto}
     * @return {@link XactType}
     */
    public static final XactType buildTransactionDetails(XactDto dto) {
        XactType xactType = null;
        if (dto != null) {
            RecordTrackingType rtt = RecordTrackingTypeBuilder.Builder.create()
                    .withDateCreated(dto.getDateCreated())
                    .withDateUpdate(dto.getDateUpdated())
                    .withUserId(dto.getUpdateUserId())
                    .withIpCreated(dto.getIpCreated())
                    .withIpUpdate(dto.getIpUpdated()).build();
            
            xactType = XactTypeBuilder.Builder.create()
                    .withBankTransInd(dto.getXactBankTransInd())
                    .withConfirmNo(dto.getXactConfirmNo())
                    .withDocumentId(dto.getDocumentId())
                    .withEntityRefNo(dto.getXactEntityRefNo())
                    .withPostedDate(dto.getXactPostedDate())
                    .withReason(dto.getXactReason())
                    .withTenderId(dto.getXactTenderId())
                    .withXactAmount(dto.getXactAmount())
                    .withXactCodeId(dto.getXactCodeId())
                    .withXactCodeDescription(dto.getXactCodeDescription())
                    .withXactCodeGroupId(dto.getXactCodeGrpId())
                    .withXactCodeGroupDescription(dto.getXactCodeGrpDescription())
                    .withXactDate(dto.getXactDate())
                    .withXactSubtypeId(dto.getXactSubtypeId())
                    .withXactTypeId(dto.getXactTypeId())
                    .withRecordTracking(rtt).build();
        }
        return xactType;
    }

    /**
     * Construct a XactType JAXB object
     * 
     * @param xact
     *            an instance of {@link XactDto}
     * @param transactions
     *            a List of {@link XactTypeItemActivityDto} objects which represent the
     *            line items.
     * @return {@link XactType}
     */
    public static final XactType buildTransactionDetails(XactDto xact, List<XactTypeItemActivityDto> transactions) {
        XactType xactType = null;
        if (xact != null) {
            RecordTrackingType rtt = RecordTrackingTypeBuilder.Builder.create()
                    .withDateCreated(xact.getDateCreated())
                    .withDateUpdate(xact.getDateUpdated())
                    .withUserId(xact.getUpdateUserId())
                    .withIpCreated(xact.getIpCreated())
                    .withIpUpdate(xact.getIpUpdated()).build();
            
            XacttypeType xt = XacttypeTypeBuilder.Builder.create()
                    .withXactTypeId(xact.getXactTypeId())
                    .withDescription(xact.getXactTypeDescription()).build();

            XacttypeType xst = XacttypeTypeBuilder.Builder.create()
                    .withXactTypeId(xact.getXactSubtypeId()).build();

            XactCodeGroupType xcgt = XactCodeGroupTypeBuilder.Builder.create()
                    .withGroupId(xact.getXactCodeGrpId())
                    .withDescription(xact.getXactCodeGrpDescription()).build();

            XactCodeType xct = XactCodeTypeBuilder.Builder.create()
                    .withXactCodeId(xact.getXactCodeId())
                    .withDescription(xact.getXactCodeDescription()).build();
            
            List<XactLineitemType> itemList = null;
            double lineItemTotal = 0;
            if (transactions != null) {
                itemList = new ArrayList<>();
                for (XactTypeItemActivityDto trans : transactions) {
                    XactLineitemType item = TransactionJaxbDtoFactory.createXactItemJaxbInstance(trans);
                    itemList.add(item);
                    lineItemTotal += trans.getActivityAmount();
                }
            }
            
            xactType = XactTypeBuilder.Builder.create()
                    .withXactId(xact.getXactId())
                    .withXactAmount(xact.getXactAmount())
                    .withXactDate(xact.getXactDate())
                    .withPostedDate(xact.getXactPostedDate())
                    .withReason(xact.getXactReason())
                    .withConfirmNo(xact.getXactConfirmNo())
                    .withEntityRefNo(xact.getXactEntityRefNo())
                    .withNegInstrNo(xact.getXactNegInstrNo())
                    .withTenderId(xact.getXactTenderId())
                    .withBankTransInd(xact.getXactBankTransInd())
                    .withDocumentId(xact.getDocumentId())
                    .withInvoiceNo(null)
                    .withItemTotal(lineItemTotal)
                    .withXactType(xt)
                    .withXactSubtype(xst)
                    .withXactCodeGroup(xcgt)
                    .withXactCode(xct)
                    .withXactItems(itemList)
                    .withRecordTracking(rtt).build();
        }
        return xactType;
    }
    
}
