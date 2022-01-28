package org.rmt2.api.handlers.transaction;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dto.CommonXactDto;
import org.dto.CreditorDto;
import org.dto.XactCodeDto;
import org.dto.XactCodeGroupDto;
import org.dto.XactCreditChargeDto;
import org.dto.XactCustomCriteriaDto;
import org.dto.XactDto;
import org.dto.XactTypeItemActivityDto;
import org.dto.adapter.orm.account.subsidiary.Rmt2SubsidiaryDtoFactory;
import org.dto.adapter.orm.transaction.Rmt2XactDtoFactory;
import org.modules.transaction.XactApiFactory;
import org.rmt2.api.handlers.AccountingtMsgHandlerUtility;
import org.rmt2.jaxb.CreditorType;
import org.rmt2.jaxb.CustomerType;
import org.rmt2.jaxb.RecordTrackingType;
import org.rmt2.jaxb.RelationalOperatorType;
import org.rmt2.jaxb.XactBasicCriteriaType;
import org.rmt2.jaxb.XactCodeCriteriaType;
import org.rmt2.jaxb.XactCodeGroupCriteriaType;
import org.rmt2.jaxb.XactCodeGroupType;
import org.rmt2.jaxb.XactCodeType;
import org.rmt2.jaxb.XactCustomRelationalCriteriaType;
import org.rmt2.jaxb.XactLineitemType;
import org.rmt2.jaxb.XactType;
import org.rmt2.jaxb.XacttypeType;
import org.rmt2.util.RecordTrackingTypeBuilder;
import org.rmt2.util.accounting.subsidiary.CreditorTypeBuilder;
import org.rmt2.util.accounting.subsidiary.CustomerTypeBuilder;
import org.rmt2.util.accounting.transaction.XactCodeGroupTypeBuilder;
import org.rmt2.util.accounting.transaction.XactCodeTypeBuilder;
import org.rmt2.util.accounting.transaction.XactItemTypeBuilder;
import org.rmt2.util.accounting.transaction.XactTypeBuilder;
import org.rmt2.util.accounting.transaction.XacttypeTypeBuilder;

import com.InvalidDataException;
import com.RMT2Base;
import com.api.util.RMT2Date;
import com.api.util.RMT2String2;

/**
 * A factory for converting general transaction related JAXB objects to DTO and
 * vice versa.
 * 
 * @author Roy Terrell.
 * 
 */
public class TransactionJaxbDtoFactory extends RMT2Base {
    public static final RelationalOperatorType REL_OP_EQ = RelationalOperatorType.EQUALS;
    public static final RelationalOperatorType REL_OP_NEQ = RelationalOperatorType.NOT_EQUAL;
    public static final RelationalOperatorType REL_OP_GT = RelationalOperatorType.GREATER_THAN;
    public static final RelationalOperatorType REL_OP_LT = RelationalOperatorType.LESS_THAN;
    public static final RelationalOperatorType REL_OP_LTEQ = RelationalOperatorType.LESS_THAN_OR_EQUAL;
    public static final RelationalOperatorType REL_OP_GTEQ = RelationalOperatorType.GREATER_THAN_OR_EQUAL;
    public static Map<RelationalOperatorType, String> REL_OPS;
    
    static {
        REL_OPS = new HashMap<>();
        REL_OPS.put(REL_OP_GT, ">");
        REL_OPS.put(REL_OP_EQ, "=");
        REL_OPS.put(REL_OP_GTEQ, ">=");
        REL_OPS.put(REL_OP_LT, "<");
        REL_OPS.put(REL_OP_LTEQ, "<=");
        REL_OPS.put(REL_OP_NEQ, "<>");
        
    }

    /**
     * Creates an instance of <i>XactCodeGroupDto</i> using a valid
     * <i>XactCodeGroupCriteriaType</i> JAXB object.
     * 
     * @param criteria
     *            an instance of {@link XactCodeGroupCriteriaType}
     * @return an instance of {@link XactCodeGroupDto}
     * @throws {@link
     *             com.SystemException} Transaction date could not converted
     *             from a String.
     */
    public static final XactCodeGroupDto createCodeGroupDtoCriteriaInstance(XactCodeGroupCriteriaType jaxbCriteria) {
        if (jaxbCriteria == null) {
            return null;
        }

        XactCodeGroupDto dto = Rmt2XactDtoFactory.createXactCodeGroupInstance(null);
        if (!RMT2String2.isEmpty(jaxbCriteria.getDescription())) {
            dto.setEntityName(jaxbCriteria.getDescription());
        }
        if (jaxbCriteria.getXactCodeGrpId() != null) {
            dto.setEntityId(jaxbCriteria.getXactCodeGrpId().intValue());
        }

        return dto;
    }

    /**
     * Creates an instance of <i>XactCodeGroupDto</i> using a valid
     * <i>XactCodeGroupType</i> JAXB object.
     * 
     * @param jaxbObj
     * @return
     */
    public static final XactCodeGroupDto createCodeGroupDtoInstance(XactCodeGroupType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        XactCodeGroupDto dto = Rmt2XactDtoFactory.createXactCodeGroupInstance(null);
        if (jaxbObj.getXactCodeGrpId() != null) {
            dto.setEntityId(jaxbObj.getXactCodeGrpId().intValue());
        }
        dto.setEntityName(jaxbObj.getDescription());
        return dto;
    }
    
    
    /**
     * 
     * @param dto
     * @param balance
     * @param transactions
     * @return
     */
    public static final XactCodeGroupType createCodeGroupJaxbInstance(XactCodeGroupDto dto) {
        
        if (dto == null) {
            return null;
        }

        RecordTrackingType rtt = RecordTrackingTypeBuilder.Builder.create()
                .withDateCreated(dto.getDateCreated())
                .withDateUpdate(dto.getDateUpdated())
                .withUserId(dto.getUpdateUserId())
                .withIpCreated(dto.getIpCreated())
                .withIpUpdate(dto.getIpUpdated()).build();
        
        XactCodeGroupType jaxbObj = XactCodeGroupTypeBuilder.Builder.create().withGroupId(dto.getEntityId())
                .withDescription(dto.getEntityName()).withRecordTracking(rtt).build();
        return jaxbObj;
    }
    
    /**
     * Creates an instance of <i>XactCodeDto</i> using a valid
     * <i>XactCodeCriteriaType</i> JAXB object.
     * 
     * @param criteria
     *            an instance of {@link XactCodeCriteriaType}
     * @return an instance of {@link XactCodeDto}
     * @throws {@link
     *             com.SystemException} Transaction date could not converted
     *             from a String.
     */
    public static final XactCodeDto createCodeDtoCriteriaInstance(XactCodeCriteriaType jaxbCriteria) {
        if (jaxbCriteria == null) {
            return null;
        }

        XactCodeDto dto = Rmt2XactDtoFactory.createXactCodeInstance(null);
        if (jaxbCriteria.getXactCodeId() != null) {
            dto.setEntityId(jaxbCriteria.getXactCodeId().intValue());
        }
        if (!RMT2String2.isEmpty(jaxbCriteria.getDescription())) {
            dto.setEntityName(jaxbCriteria.getDescription());
        }
        if (jaxbCriteria.getXactCodeGrp() != null && jaxbCriteria.getXactCodeGrp().getXactCodeGrpId() != null) {
            dto.setGrpId(jaxbCriteria.getXactCodeGrp().getXactCodeGrpId().intValue());
        }

        return dto;
    }
    
    /**
     * 
     * @param jaxbObj
     * @return
     */
    public static final XactCodeDto createCodeDtoInstance(XactCodeType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }

        XactCodeDto dto = Rmt2XactDtoFactory.createXactCodeInstance(null);
        if (jaxbObj.getXactCodeId() != null) {
            dto.setEntityId(jaxbObj.getXactCodeId().intValue());
        }
        dto.setEntityName(jaxbObj.getDescription());
        if (jaxbObj.getXactCodeGrp() != null && jaxbObj.getXactCodeGrp().getXactCodeGrpId() != null) {
            dto.setGrpId(jaxbObj.getXactCodeGrp().getXactCodeGrpId().intValue());
        }

        return dto;
    }
    
    /**
     * 
     * @param dto
     * @return
     */
    public static final XactCodeType createCodeJaxbInstance(XactCodeDto dto) {
        
        if (dto == null) {
            return null;
        }

        RecordTrackingType rtt = RecordTrackingTypeBuilder.Builder.create()
                .withDateCreated(dto.getDateCreated())
                .withDateUpdate(dto.getDateUpdated())
                .withUserId(dto.getUpdateUserId())
                .withIpCreated(dto.getIpCreated())
                .withIpUpdate(dto.getIpUpdated()).build();
        
        XactCodeGroupType jaxbCodeGrpObj = XactCodeGroupTypeBuilder.Builder.create().withGroupId(dto.getGrpId()).build();
        
        XactCodeType jaxbObj = XactCodeTypeBuilder.Builder.create().withXactCodeId(dto.getEntityId())
                .withDescription(dto.getEntityName()).withGroup(jaxbCodeGrpObj).withRecordTracking(rtt).build();
        return jaxbObj;
    }
 
    
    /**
     * Creates an instance of <i>XactDto</i> using a valid
     * <i>XactBasicCriteriaType</i> JAXB object.
     * 
     * @param criteria
     *            an instance of {@link XactBasicCriteriaType}
     * @return an instance of {@link XactDto}
     * @throws {@link com.SystemException} Transaction date could not converted from a String.
     */
    public static final XactDto createBaseXactDtoCriteriaInstance(XactBasicCriteriaType jaxbCriteria) {
        if (jaxbCriteria == null) {
            return null;
        }
        
        XactDto dto = Rmt2XactDtoFactory.createXactBaseInstance(null);
        if (!RMT2String2.isEmpty(jaxbCriteria.getConfirmNo())) {
            dto.setXactConfirmNo(jaxbCriteria.getConfirmNo());    
        }
        if (jaxbCriteria.getXactId() != null) {
            dto.setXactId(jaxbCriteria.getXactId().intValue());    
        }
        else {
            dto.setXactId(0);
        }
        if (jaxbCriteria.getXactDate() != null) {
            dto.setXactDate(RMT2Date.stringToDate(jaxbCriteria.getXactDate()));    
        }  
        if (jaxbCriteria.getXactTypeId() != null) {
            dto.setXactTypeId(jaxbCriteria.getXactTypeId().intValue());    
        }
        if (jaxbCriteria.getXactCatgId() != null) {
            dto.setXactCatgId(jaxbCriteria.getXactCatgId().intValue());    
        }
        if (jaxbCriteria.getTenderId() != null) {
            dto.setXactTenderId(jaxbCriteria.getTenderId().intValue());    
        }
        if (jaxbCriteria.getXactReason() != null) {
            dto.setXactReason(jaxbCriteria.getXactReason());
        }
        
        return dto;
    }
    
    /**
     * Creates an instance of <i>CommonXactDto</i> using a valid
     * <i>XactBasicCriteriaType</i> JAXB object.
     * 
     * @param criteria
     *            an instance of {@link XactBasicCriteriaType}
     * @return an instance of {@link CommonXactDto}
     * @throws {@link com.SystemException} Transaction date could not converted
     *         from a String.
     */
    public static final CommonXactDto createGerericXactDtoCriteriaInstance(XactBasicCriteriaType jaxbCriteria) {
        if (jaxbCriteria == null) {
            return null;
        }

        CommonXactDto dto = Rmt2XactDtoFactory.createGenericXactInstance(null);
        if (jaxbCriteria.getXactReason() != null) {
            dto.setReason(jaxbCriteria.getXactReason());
        }
        if (!RMT2String2.isEmpty(jaxbCriteria.getConfirmNo())) {
            dto.setConfirmNo(jaxbCriteria.getConfirmNo());
        }
        if (jaxbCriteria.getXactId() != null) {
            dto.setXactId(jaxbCriteria.getXactId().intValue());
        }
        if (jaxbCriteria.getXactDate() != null) {
            dto.setXactDate(RMT2Date.stringToDate(jaxbCriteria.getXactDate()));
        }
        if (jaxbCriteria.getXactTypeId() != null) {
            dto.setXactTypeId(jaxbCriteria.getXactTypeId().intValue());
        }
        if (jaxbCriteria.getContactId() != null) {
            dto.setBusinessId(jaxbCriteria.getContactId().intValue());
        }
        if (jaxbCriteria.getBusinessName() != null) {
            dto.setBusinessName(jaxbCriteria.getBusinessName());
        }
        if (jaxbCriteria.getInvoiceNo() != null) {
            dto.setInvoiceNo(jaxbCriteria.getInvoiceNo());
        }
        return dto;
    }

    /**
     * 
     * @param jaxbCriteria
     * @return
     */
    public static final XactCustomCriteriaDto createCustomXactDtoCriteriaInstance(XactCustomRelationalCriteriaType jaxbCriteria) {
        if (jaxbCriteria == null) {
            return null;
        }
        boolean valuesSet = false;
        XactCustomCriteriaDto dto = XactApiFactory.createCustomCriteriaInstance();
        if (jaxbCriteria.getXactReasonOptions() != null && !RMT2String2.isEmpty(jaxbCriteria.getXactReasonOptions().name())) {
            dto.setXactReasonFilterOption(jaxbCriteria.getXactReasonOptions().name());
            valuesSet = true;
        }
        
        if (jaxbCriteria.getFromRelOpXactAmount() != null && jaxbCriteria.getFromXactAmount() != null) {
            dto.setFromXactAmount(jaxbCriteria.getFromXactAmount().doubleValue());    
            dto.setFromXactAmountRelOp(REL_OPS.get(jaxbCriteria.getFromRelOpXactAmount()));
            valuesSet = true;
        }
        if (jaxbCriteria.getToRelOpXactAmount() != null && jaxbCriteria.getToXactAmount() != null) {
            dto.setToXactAmountRelOp(REL_OPS.get(jaxbCriteria.getToRelOpXactAmount()));   
            dto.setToXactAmount(jaxbCriteria.getToXactAmount().doubleValue());
            valuesSet = true;
        }
        if (jaxbCriteria.getFromRelOpItemAmount() != null && jaxbCriteria.getFromItemAmount() != null) {
            dto.setFromItemAmountRelOp(REL_OPS.get(jaxbCriteria.getFromRelOpItemAmount()));    
            dto.setFromItemAmount(jaxbCriteria.getFromItemAmount().doubleValue());
            valuesSet = true;
        }
        if (jaxbCriteria.getToRelOpItemAmount() != null && jaxbCriteria.getToItemAmount() != null) {
            dto.setToItemAmountRelOp(REL_OPS.get(jaxbCriteria.getToRelOpItemAmount()));   
            dto.setToItemAmount(jaxbCriteria.getToItemAmount().doubleValue());
            valuesSet = true;
        }
        if (jaxbCriteria.getFromRelOpXactDate() != null && jaxbCriteria.getFromXactDate() != null) {
            dto.setFromXactDateRelOp(REL_OPS.get(jaxbCriteria.getFromRelOpXactDate()));    
            dto.setFromXactDate(RMT2Date.stringToDate(jaxbCriteria.getFromXactDate()));
            valuesSet = true;
        }
        if (jaxbCriteria.getToRelOpXactDate() != null && jaxbCriteria.getToXactDate() != null) {
            dto.setToXactDateRelOp(REL_OPS.get(jaxbCriteria.getToRelOpXactDate()));  
            dto.setToXactDate(RMT2Date.stringToDate(jaxbCriteria.getToXactDate()));
            valuesSet = true;
        }
        return (valuesSet ? dto : null);
    }
    
    
    /**
     * 
     * @param jaxbObj
     * @return
     */
    public static XactDto createXactDtoInstance(XactType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        XactDto dto = Rmt2XactDtoFactory.createXactBaseInstance(null);
        if (jaxbObj.getXactId() != null) {
            dto.setXactId(jaxbObj.getXactId().intValue());    
        }
        else {
            // When creating a new transaction, use may not include the xact id
            // element in request.
            jaxbObj.setXactId(BigInteger.ZERO);
        }
        if (jaxbObj.getXactType() != null && jaxbObj.getXactType().getXactTypeId() != null) {
            dto.setXactTypeId(jaxbObj.getXactType().getXactTypeId().intValue());
        }
        if (jaxbObj.getXactCodeGroup() != null) {
            if (jaxbObj.getXactCodeGroup().getXactCodeGrpId() != null) {
                dto.setXactCodeGrpId(jaxbObj.getXactCodeGroup().getXactCodeGrpId().intValue());
            }
            if (!RMT2String2.isEmpty(jaxbObj.getXactCodeGroup().getDescription())) {
                dto.setXactCodeGrpDescription(jaxbObj.getXactCodeGroup().getDescription());
            }
        }
        if (jaxbObj.getXactCode() != null) {
            if (jaxbObj.getXactCode().getXactCodeId() != null) {
                dto.setXactCodeId(jaxbObj.getXactCode().getXactCodeId().intValue());
            }
            if (!RMT2String2.isEmpty(jaxbObj.getXactCode().getDescription())) {
                dto.setXactCodeDescription(jaxbObj.getXactCode().getDescription());
            }
        }
        
        dto.setXactAmount(jaxbObj.getXactAmount().doubleValue());    
        
        if (jaxbObj.getXactDate() != null) {
            dto.setXactDate(jaxbObj.getXactDate().toGregorianCalendar().getTime());    
        } 
        if (jaxbObj.getPostedDate() != null) {
            dto.setXactPostedDate(jaxbObj.getPostedDate().toGregorianCalendar().getTime());    
        }
        if (jaxbObj.getTenderId() != null) {
            dto.setXactTenderId(jaxbObj.getTenderId().intValue());    
        }   
        if (jaxbObj.getDocumentId() != null) {
            dto.setDocumentId(jaxbObj.getDocumentId().intValue());    
        }   
        dto.setXactReason(jaxbObj.getXactReason());
        dto.setXactEntityRefNo(jaxbObj.getEntityRefNo());
        dto.setXactNegInstrNo(jaxbObj.getNegInstrNo());
        dto.setXactConfirmNo(jaxbObj.getConfirmNo());
        dto.setXactBankTransInd(jaxbObj.getBankTransInd());
        return dto;
    }
    
    /**
     * Create Creditor DTO object.
     * <p>
     * Extracts data from the creditor profile section of the XactType instance
     * and builds the Creditor DTO object.
     * 
     * @param jaxbObj
     *            {@link XactType}
     * @return {@link CreditorDto}
     */
    public static CreditorDto createCreditorDtoInstance(XactType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        if (jaxbObj.getCreditor() == null) {
            return null;
        }
        
        CreditorDto dto = Rmt2SubsidiaryDtoFactory.createCreditorInstance(null, null);
        if (jaxbObj.getCreditor().getAcctId() != null) {
            dto.setAcctId(jaxbObj.getCreditor().getAcctId().intValue());    
        }
        if (jaxbObj.getCreditor().getCreditorId() != null) {
            dto.setCreditorId(jaxbObj.getCreditor().getCreditorId().intValue());    
        }
        
        dto.setAccountNo(jaxbObj.getCreditor().getAccountNo());
        if (jaxbObj.getCreditor().getContactDetails() != null) {
            if (jaxbObj.getCreditor().getContactDetails().getBusinessId() != null) {
                dto.setContactId(jaxbObj.getCreditor().getContactDetails().getBusinessId().intValue());    
            }
            dto.setContactEmail(jaxbObj.getCreditor().getContactDetails().getContactEmail());
            dto.setContactExt(jaxbObj.getCreditor().getContactDetails().getContactExt());
            dto.setContactFirstname(jaxbObj.getCreditor().getContactDetails().getContactFirstname());
            dto.setContactLastname(jaxbObj.getCreditor().getContactDetails().getContactLastname());
            dto.setContactName(jaxbObj.getCreditor().getContactDetails().getLongName());
            dto.setContactPhone(jaxbObj.getCreditor().getContactDetails().getContactPhone());
        }
        return dto;
    }
    
    /**
     * 
     * @param dto
     * @param balance
     * @param transactions
     * @return
     */
    public static final XactType createXactJaxbInstance(XactDto dto, double balance, 
            List<XactTypeItemActivityDto> transactions) {
        return AccountingtMsgHandlerUtility.buildTransactionDetails(dto, transactions);
    }

    /**
     * 
     * @param dto
     * @return
     */
    public static final XactLineitemType createXactItemJaxbInstance(XactTypeItemActivityDto dto) {
        if (dto == null) {
            return null;
        }

        XactLineitemType item = XactItemTypeBuilder.Builder.create()
                .withAmount(dto.getActivityAmount())
                .withDescription(dto.getXactTypeItemActvName())
                .withItemId(dto.getXactItemId())
                .withXactTypeItemActvId(dto.getXactTypeItemActvId())
                .withXactId(dto.getXactId()).build();
        return item;
    }

    /**
     * 
     * @param jaxbObjs
     * @return
     */
    public static final List<XactTypeItemActivityDto> createXactItemDtoInstance(List<XactLineitemType> jaxbObjs) {
        if (jaxbObjs == null) {
            return null;
        }
        
        List<XactTypeItemActivityDto> list = new ArrayList<>();
        for (XactLineitemType item : jaxbObjs) {
            XactTypeItemActivityDto dto = createXactItemDtoInstance(item);
            list.add(dto);
        }
        return list;
    }
    
    /**
     * 
     * @param jaxbObj
     * @return
     */
    public static final XactTypeItemActivityDto createXactItemDtoInstance(XactLineitemType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        XactTypeItemActivityDto dto = Rmt2XactDtoFactory.createXactTypeItemActivityExtInstance(null);
        if (jaxbObj.getXactTypeItemActvId() != null) {
            dto.setXactTypeItemActvId(jaxbObj.getXactTypeItemActvId().intValue());
        }
        else {
            // When creating a new disbursement, use may not include the xact
            // type item activity id element in request.
            dto.setXactTypeItemActvId(0);
        }
        if (jaxbObj.getXactId() != null) {
            dto.setXactId(jaxbObj.getXactId().intValue());
        }
        else {
            // When creating a new disbursement, use may not include the xact id
            // element in request.
            dto.setXactId(0);
        }
        if (jaxbObj.getItemId() != null) {
            dto.setXactItemId(jaxbObj.getItemId().intValue());
        }
        if (jaxbObj.getName() != null) {
            dto.setXactTypeItemActvName(jaxbObj.getName());
        }
        if (jaxbObj.getAmount() != null) {
            dto.setActivityAmount(jaxbObj.getAmount().doubleValue());
        }
        return dto;
    }

    /**
     * Builds a List of XactType objects from a List of XactDto objects that are
     * associated with a creditor.
     * 
     * @param results
     *            List<{@link XactDto}>
     * @param creditorId
     *            the id of the creditor. If null, the creditor element is not
     *            included in the results.
     * @return List<{@link XactType}>
     */
    public static final List<XactType> buildJaxbCreditorTransaction(XactDto item, Integer creditorId) {
        List<XactType> list = new ArrayList<>();

        XacttypeType xt = XacttypeTypeBuilder.Builder.create()
                .withXactTypeId(item.getXactTypeId())
                .withDescription(item.getXactTypeDescription())
                .withCode(item.getXactTypeCode())
                .build();

        XactType x = null;
        if (creditorId != null) {
            CreditorType c = CreditorTypeBuilder.Builder.create()
                    .withCreditorId(creditorId)
                    .build();

            x = XactTypeBuilder.Builder.create()
                    .withXactId(item.getXactId())
                    .withXactAmount(item.getXactAmount())
                    .withXactType(xt)
                    .withCreditor(c)
                    .build();
        }
        else {
            x = XactTypeBuilder.Builder.create()
                    .withXactId(item.getXactId())
                    .withXactAmount(item.getXactAmount())
                    .withXactType(xt)
                    .build();
        }

        list.add(x);
        return list;
    }

    /**
     * Builds a List of XactType objects from a List of XactDto objects that are
     * associated with a customer.
     * 
     * @param results
     *            List<{@link XactDto}>
     * @param customerId
     *            the id of the customer. If null, the customer element is not
     *            included in the results.
     * @return List<{@link XactType}>
     */
    public static final List<XactType> buildJaxbCustomerTransaction(XactDto item, Integer customerId) {
        List<XactType> list = new ArrayList<>();

        XacttypeType xt = XacttypeTypeBuilder.Builder.create()
                .withXactTypeId(item.getXactTypeId())
                .withDescription(item.getXactTypeDescription())
                .withCode(item.getXactTypeCode())
                .build();

        XactType x = null;
        if (customerId != null) {
            CustomerType c = CustomerTypeBuilder.Builder.create()
                    .withCustomerId(customerId)
                    .build();

            x = XactTypeBuilder.Builder.create()
                    .withXactId(item.getXactId())
                    .withXactAmount(item.getXactAmount())
                    .withXactType(xt)
                    .withCustomer(c)
                    .build();
        }
        else {
            x = XactTypeBuilder.Builder.create()
                    .withXactId(item.getXactId())
                    .withXactAmount(item.getXactAmount())
                    .withXactType(xt)
                    .build();
        }

        list.add(x);
        return list;
    }

    /**
     * Builds a List of XactType objects from a List of XactCreditChargeDto
     * objects.
     * 
     * @param results
     *            List<{@link XactCreditChargeDto}>
     * @param customCriteriaDto
     *            custom relational criteria (optional)
     * @return List<{@link XactType}>
     */
    public static final List<XactType> buildJaxbCreditPurchasesTransaction(XactCreditChargeDto xact) {
    
    	 CreditorType c = CreditorTypeBuilder.Builder.create()
                 .withCreditorId(xact.getCreditorId())
                 .build();

        XacttypeType xt = XacttypeTypeBuilder.Builder.create()
                .withXactTypeId(xact.getXactTypeId())
                .withDescription(xact.getXactTypeDescription())
                .withCode(xact.getXactTypeCode())
                .build();
    
        XactType x = XactTypeBuilder.Builder.create()
                .withXactId(xact.getXactId())
                .withXactAmount(xact.getXactAmount())
                .withXactType(xt)
                .withCreditor(c)
                .build();
    
        List<XactType> list = new ArrayList<>();
        list.add(x);
        return list;
    }
    
	/**
	 * Retrieves the list of transaction id's from the XactBasicCriteriaType element
	 * of the transaction criteria request object.
	 * 
	 * @param jaxbObj an instance of {@link XactBasicCriteriaType}
	 * @return List<Integer>
	 */
    public static List<Integer> createDeleteXactIdList(XactBasicCriteriaType jaxbObj) {
        if (jaxbObj == null || jaxbObj.getXactIdList() == null) {
            return null;
        }
        return jaxbObj.getXactIdList().getXactId();
    }
    
}

