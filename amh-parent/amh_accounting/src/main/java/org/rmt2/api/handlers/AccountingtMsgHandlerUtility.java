package org.rmt2.api.handlers;

import java.util.ArrayList;
import java.util.List;

import org.dto.XactDto;
import org.dto.XactTypeItemActivityDto;
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

/**
 * Utility class for accounting API Message Handling 
 * 
 * @author roy.terrell
 *
 */
public class AccountingtMsgHandlerUtility {

    /**
     * Default constructor
     */
    public AccountingtMsgHandlerUtility() {
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
