package org.rmt2.api;

import java.math.BigInteger;
import java.util.Date;

import org.dao.mapping.orm.rmt2.Creditor;
import org.dao.mapping.orm.rmt2.CreditorType;
import org.dao.mapping.orm.rmt2.Customer;
import org.dao.mapping.orm.rmt2.GlAccountCategory;
import org.dao.mapping.orm.rmt2.GlAccountTypes;
import org.dao.mapping.orm.rmt2.GlAccounts;
import org.dao.mapping.orm.rmt2.ItemMaster;
import org.dao.mapping.orm.rmt2.ItemMasterStatus;
import org.dao.mapping.orm.rmt2.ItemMasterStatusHist;
import org.dao.mapping.orm.rmt2.ItemMasterType;
import org.dao.mapping.orm.rmt2.PurchaseOrder;
import org.dao.mapping.orm.rmt2.PurchaseOrderItems;
import org.dao.mapping.orm.rmt2.PurchaseOrderStatus;
import org.dao.mapping.orm.rmt2.PurchaseOrderStatusHist;
import org.dao.mapping.orm.rmt2.SalesInvoice;
import org.dao.mapping.orm.rmt2.SalesOrder;
import org.dao.mapping.orm.rmt2.SalesOrderItems;
import org.dao.mapping.orm.rmt2.SalesOrderStatus;
import org.dao.mapping.orm.rmt2.SalesOrderStatusHist;
import org.dao.mapping.orm.rmt2.VendorItems;
import org.dao.mapping.orm.rmt2.VwAccount;
import org.dao.mapping.orm.rmt2.VwBusinessAddress;
import org.dao.mapping.orm.rmt2.VwCommonContact;
import org.dao.mapping.orm.rmt2.VwCreditorXactHist;
import org.dao.mapping.orm.rmt2.VwCustomerXactHist;
import org.dao.mapping.orm.rmt2.VwGenericXactList;
import org.dao.mapping.orm.rmt2.VwItemAssociations;
import org.dao.mapping.orm.rmt2.VwSalesOrderInvoice;
import org.dao.mapping.orm.rmt2.VwSalesorderItemsBySalesorder;
import org.dao.mapping.orm.rmt2.VwVendorItemPurchaseOrderItem;
import org.dao.mapping.orm.rmt2.VwVendorItems;
import org.dao.mapping.orm.rmt2.VwXactCreditChargeList;
import org.dao.mapping.orm.rmt2.VwXactList;
import org.dao.mapping.orm.rmt2.VwXactTypeItemActivity;
import org.dao.mapping.orm.rmt2.XactCategory;
import org.dao.mapping.orm.rmt2.XactCodeGroup;
import org.dao.mapping.orm.rmt2.XactCodes;
import org.dao.mapping.orm.rmt2.XactType;
import org.dao.mapping.orm.rmt2.XactTypeItem;
import org.dao.mapping.orm.rmt2.XactTypeItemActivity;
import org.dto.AccountDto;
import org.dto.adapter.orm.account.generalledger.Rmt2AccountDtoFactory;
import org.rmt2.jaxb.BusinessType;
import org.rmt2.jaxb.CodeDetailType;
import org.rmt2.jaxb.ObjectFactory;

import com.SystemException;
import com.api.util.RMT2Date;

public class AccountingMockDataFactory {

    public static final GlAccounts createMockOrmGlAccounts(int acctId,
            int acctTypeId, int acctCatgId, int acctSeq, String acctNo,
            String acctName, String acctCode, String acctDescription,
            int acctBalTypeId) {
        GlAccounts orm = new GlAccounts();
        orm.setAcctId(acctId);
        orm.setAcctTypeId(acctTypeId);
        orm.setAcctBaltypeId(acctBalTypeId);
        orm.setAcctCatgId(acctCatgId);
        orm.setAcctNo(acctNo);
        orm.setAcctSeq(acctSeq);
        orm.setCode(acctCode);
        orm.setDescription(acctDescription);
        orm.setName(acctName);
        return orm;
    }

    public static final AccountDto createMockDtoGlAccounts(int acctId,
            int acctTypeId, int acctCatgId, int acctSeq, String acctNo,
            String acctName, String acctCode, String acctDescription,
            int acctBalTypeId) {
        GlAccounts orm = AccountingMockDataFactory.createMockOrmGlAccounts(
                acctId, acctTypeId, acctCatgId, acctSeq, acctNo, acctName,
                acctCode, acctDescription, acctBalTypeId);
        AccountDto dto = Rmt2AccountDtoFactory.createAccountInstance(orm);
        return dto;
    }

    /**
     * 
     * @param id
     * @param acctBalTypeId
     * @return
     */
    public static final GlAccountTypes createMockOrmGlAccountTypes(int id,
            int acctBalTypeId, String description) {
        GlAccountTypes orm = new GlAccountTypes();
        orm.setAcctTypeId(id);
        orm.setAcctBaltypeId(acctBalTypeId);
        orm.setDescription(description);
        return orm;
    }

    /**
     * 
     * @param id
     * @param acctTypeId
     * @param description
     * @return
     */
    public static final GlAccountCategory createMockOrmGlAccountCategory(
            int id, int acctTypeId, String description) {
        GlAccountCategory orm = new GlAccountCategory();
        orm.setAcctCatgId(id);
        orm.setAcctTypeId(acctTypeId);
        orm.setDescription(description);
        return orm;
    }

    /**
     * 
     * @param acctId
     * @param acctTypeId
     * @param acctCatgId
     * @param acctSeq
     * @param acctNo
     * @param acctName
     * @param acctCode
     * @param acctDescription
     * @param acctBalTypeId
     * @return
     */
    public static final VwAccount createMockOrmVwAccounts(int acctId,
            int acctTypeId, int acctCatgId, int acctSeq, String acctNo,
            String acctName, String acctCode, String acctDescription,
            int acctBalTypeId) {
        VwAccount orm = new VwAccount();
        orm.setId(acctId);
        orm.setAcctTypeId(acctTypeId);
        orm.setBalanceTypeId(acctBalTypeId);
        orm.setAcctCatId(acctCatgId);
        orm.setAcctNo(acctNo);
        orm.setAcctSeq(acctSeq);
        orm.setCode(acctCode);
        orm.setDescription(acctDescription);
        orm.setName(acctName);
        orm.setAccttypedescr("AccountTypeDescription" + acctId);
        orm.setAcctcatgdescr("AccountCategoryDescription" + acctCatgId);
        return orm;
    }

    /**
     * 
     * @param id
     * @param itemTypeId
     * @param serialNo
     * @param vendorItemNo
     * @param creditorId
     * @param description
     * @param qty
     * @param unitCost
     * @param reatilPrice
     * @param active
     * @return
     */
    public static final ItemMaster createMockOrmItemMaster(int id,
            int itemTypeId, String serialNo, String vendorItemNo,
            int creditorId, String description, int qty, double unitCost,
            boolean active) {
        ItemMaster i = new ItemMaster();
        i.setItemId(id);
        i.setItemTypeId(itemTypeId);
        i.setItemSerialNo(serialNo);
        i.setVendorItemNo(vendorItemNo);
        i.setCreditorId(creditorId);
        i.setDescription(description);
        i.setQtyOnHand(qty);
        i.setUnitCost(unitCost);
        i.setActive(active ? 1 : 0);
        i.setOverrideRetail(0);
        i.setMarkup(5);
        i.setRetailPrice((qty * unitCost) * i.getMarkup());
        return i;
    }

    /**
     * 
     * @param id
     * @param description
     * @return
     */
    public static final ItemMasterType createMockOrmItemMasterType(int id,
            String description) {
        ItemMasterType i = new ItemMasterType();
        i.setItemTypeId(id);
        i.setDescription(description);
        return i;
    }

    /**
     * 
     * @param id
     * @param description
     * @return
     */
    public static final ItemMasterStatus createMockOrmItemMasterStatus(int id,
            String description) {
        ItemMasterStatus i = new ItemMasterStatus();
        i.setItemStatusId(id);
        i.setDescription(description);
        return i;
    }

    /**
     * 
     * @param id
     * @param itemId
     * @param statusId
     * @param unitCost
     * @param markup
     * @param effDate
     * @param endDate
     * @param reason
     * @return
     */
    public static final ItemMasterStatusHist createMockOrmItemMasterStatusHistory(
            int id, int itemId, int statusId, double unitCost, double markup,
            String effDate, String endDate, String reason) {
        ItemMasterStatusHist i = new ItemMasterStatusHist();
        i.setItemStatusHistId(id);
        i.setItemId(itemId);
        i.setItemStatusId(statusId);
        i.setUnitCost(unitCost);
        i.setMarkup(markup);
        try {
            i.setEffectiveDate(RMT2Date.stringToDate(effDate));
        } catch (SystemException e) {
            i.setEffectiveDate(new Date());
        }
        try {
            i.setEndDate(RMT2Date.stringToDate(endDate));
        } catch (SystemException e) {
            i.setEndDate(new Date());
        }
        i.setReason(reason);
        return i;
    }

    /**
     * 
     * @param id
     * @param serialNo
     * @param vendorItemNo
     * @param creditorId
     * @param description
     * @param qty
     * @param unitCost
     * @param active
     * @return
     */
    public static final VwVendorItems createMockOrmVwVendorItems(int id,
            String serialNo, String vendorItemNo, int creditorId,
            String description, int qty, double unitCost) {
        VwVendorItems i = new VwVendorItems();
        i.setItemId(id);
        i.setItemSerialNo(serialNo);
        i.setVendorItemNo(vendorItemNo);
        i.setCreditorId(creditorId);
        i.setDescription(description);
        i.setQtyOnHand(qty);
        i.setUnitCost(unitCost);
        i.setOverrideRetail(0);
        i.setMarkup(3);
        return i;
    }

    /**
     * 
     * @param id
     * @param businessId
     * @param acctId
     * @param acctNo
     * @param extAcctNo
     * @param creditorTypeId
     * @return
     */
    public static final Creditor createMockOrmCreditor(int id, int businessId,
            int acctId, String acctNo, String extAcctNo, int creditorTypeId) {
        Creditor o = new Creditor();
        o.setCreditorId(id);
        o.setBusinessId(businessId);
        o.setAcctId(acctId);
        o.setAccountNumber(acctNo);
        o.setExtAccountNumber(extAcctNo);
        o.setCreditorTypeId(creditorTypeId);
        o.setCreditLimit(10000);
        o.setApr(12.5);
        o.setActive(1);
        o.setDateCreated(new Date());
        o.setDateUpdated(o.getDateCreated());
        o.setUserId("testuser");
        o.setIpCreated("111.222.101.100");
        o.setIpUpdated(o.getIpCreated());
        return o;
    }

    /**
     * 
     * @param businessId
     * @param longName
     * @param contactFirstName
     * @param contactLastName
     * @param contactPhone
     * @param contactEmail
     * @param taxId
     * @param website
     * @return
     */
    public static final BusinessType createMockJaxbBusiness(int businessId,
            String longName, String contactFirstName, String contactLastName,
            String contactPhone, String contactEmail, String taxId,
            String website) {
        ObjectFactory f = new ObjectFactory();
        BusinessType b = f.createBusinessType();
        b.setBusinessId(BigInteger.valueOf(businessId));
        b.setLongName(longName);
        b.setContactFirstname(contactFirstName);
        b.setContactLastname(contactLastName);
        b.setContactPhone(contactPhone);
        b.setContactEmail(contactEmail);
        CodeDetailType cdt1 = f.createCodeDetailType();
        cdt1.setCodeId(BigInteger.valueOf(130));
        CodeDetailType cdt2 = f.createCodeDetailType();
        cdt2.setCodeId(BigInteger.valueOf(100));
        b.setServiceType(cdt1);
        b.setEntityType(cdt2);
        b.setTaxId(taxId);
        b.setWebsite(website);
        return b;
    }

    /**
     * 
     * @param businessId
     * @param longName
     * @param contactFirstName
     * @param contactLastName
     * @param contactPhone
     * @param contactEmail
     * @param taxId
     * @param website
     * @return
     */
    public static final VwBusinessAddress createMockOrmBusinessAddress(
            int businessId, String longName, String contactFirstName,
            String contactLastName, String contactPhone, String contactEmail,
            String taxId, String website) {
        VwBusinessAddress b = new VwBusinessAddress();
        b.setBusinessId(businessId);
        b.setBusLongname(longName);
        b.setBusContactFirstname(contactFirstName);
        b.setBusContactLastname(contactLastName);
        b.setBusContactPhone(contactPhone);
        b.setContactEmail(contactEmail);
        b.setBusServTypeId(130);
        b.setBusEntityTypeId(100);
        b.setBusTaxId(taxId);
        b.setBusWebsite(website);

        b.setAddrId(2222);
        b.setAddrBusinessId(1351);
        b.setAddr1("94393 Hall Ave.");
        b.setAddr2("Suite 948");
        b.setAddr3("P.O. Box 84763");
        b.setZipCity("dallas");
        b.setZipState("TX");
        b.setAddrZip(75028);
        b.setAddrZipext(1234);
        return b;
    }

    /**
     * 
     * @param contactId
     * @param contactName
     * @param contactType
     * @param addressId
     * @param addr1
     * @param addr2
     * @param addr3
     * @param addr4
     * @param city
     * @param state
     * @param zip
     * @return
     */
    public static final VwCommonContact createMockOrmCommonContact(
            int contactId, String contactName, String contactType,
            int addressId, String addr1, String addr2, String addr3,
            String addr4, String city, String state, int zip) {
        VwCommonContact o = new VwCommonContact();
        o.setContactId(contactId);
        o.setContactName(contactName);
        o.setContactType(contactType);

        o.setAddrId(addressId);
        o.setContactId(contactId);
        o.setAddr1(addr1);
        o.setAddr2(addr2);
        o.setAddr3(addr3);
        o.setAddr4(addr4);
        o.setZipCity(city);
        o.setZipState(state);
        o.setAddrZip(zip);
        o.setAddrZipext(7001);
        return o;
    }

    /**
     * 
     * @param businessId
     * @param contactName
     * @param addressId
     * @param addr1
     * @param addr2
     * @param addr3
     * @param addr4
     * @param city
     * @param state
     * @param zip
     * @return
     */
    public static final VwBusinessAddress createMockOrmBusinessContact(
            int businessId, String contactName, int addressId, String addr1,
            String addr2, String addr3, String addr4, String city,
            String state, int zip) {
        VwBusinessAddress o = new VwBusinessAddress();
        o.setBusinessId(businessId);
        o.setBusLongname(contactName);
        o.setAddrPhoneMain("3188889873");
        o.setBusEntityTypeId(1);
        o.setBusServTypeId(2);
        o.setAddrId(addressId);
        o.setAddr1(addr1);
        o.setAddr2(addr2);
        o.setAddr3(addr3);
        o.setAddr4(addr4);
        o.setZipCity(city);
        o.setZipState(state);
        o.setAddrZip(zip);
        o.setAddrZipext(7001);
        return o;
    }

    /**
     * 
     * @param assocId
     * @param assocItemId
     * @param itemMasterId
     * @param assocType
     * @param qty
     * @param cost
     * @return
     */
    public static final VwItemAssociations createMockOrmVwItemAssociations(
            int assocId, int assocItemId, int itemMasterId, String assocType,
            double qty, double cost) {
        VwItemAssociations o = new VwItemAssociations();
        o.setAssocId(assocId);
        o.setAssocItemId(assocItemId);
        o.setItemId(itemMasterId);
        o.setAssocType(assocType);
        o.setOrderQty(qty);
        o.setItemCost(cost);
        return o;
    }

    /**
     * 
     * @param id
     * @param description
     * @return
     */
    public static final CreditorType createMockOrmCreditorType(int id,
            String description) {
        CreditorType o = new CreditorType();
        o.setCreditorTypeId(id);
        o.setDescription(description);
        o.setDateCreated(new Date());
        o.setDateUpdated(o.getDateCreated());
        o.setUserId("testuser");
        return o;
    }

    /**
     * 
     * @param xactId
     * @param creditorId
     * @param acctNo
     * @param xactAmt
     * @param xactDate
     * @param xactTypeId
     * @return
     */
    public static final VwCreditorXactHist createMockOrmCreditorXactHistory(
            int xactId, int creditorId, String acctNo, double xactAmt,
            Date xactDate, int xactTypeId) {
        VwCreditorXactHist o = new VwCreditorXactHist();
        o.setXactId(xactId);
        o.setCreditorId(creditorId);
        o.setCreditorTypeId(creditorId);
        o.setAccountNumber(acctNo);
        o.setXactAmount(xactAmt);
        o.setXactDate(xactDate);
        o.setXactTypeId(xactTypeId);
        o.setReason("Transaction History for creditor, " + creditorId);
        o.setDateCreated(new Date());
        o.setUserId("testuser");
        o.setActive(1);
        o.setApr(1.56);
        o.setCreditLimit(5000.00);
        o.setCreditorTypeDescription("Creditor type description for creditor, "
                + creditorId);
        o.setXactSubtypeId(1);
        o.setXactTypeName("Xact Type Name" + xactId);
        o.setConfirmNo(String.valueOf(o.getDateCreated().getTime()));
        o.setTenderId(35);
        o.setDocumentId(xactId + creditorId);
        o.setCreditorActivityId(xactId * o.getTenderId());
        o.setCreditorActivityAmount(xactAmt);
        return o;
    }

    /**
     * 
     * @param id
     * @param businessId
     * @param personId
     * @param acctId
     * @param acctNo
     * @param description
     * @return
     */
    public static final Customer createMockOrmCustomer(int id, int businessId,
            int personId, int acctId, String acctNo, String description) {
        Customer o = new Customer();
        o.setCustomerId(id);
        o.setBusinessId(businessId);
        o.setPersonId(personId);
        o.setAcctId(acctId);
        o.setAccountNo(acctNo);
        o.setDescription(description);
        o.setCreditLimit(10000);
        o.setActive(1);
        o.setDateCreated(new Date());
        o.setDateUpdated(o.getDateCreated());
        o.setUserId("testuser");
        o.setIpCreated("111.222.101.100");
        o.setIpUpdated(o.getIpCreated());
        return o;
    }

    public static final VwCustomerXactHist createMockOrmCustomerXactHistory(
            int xactId, int customerId, int businessId, int personId,
            String acctNo, double xactAmt, Date xactDate, int xactTypeId) {
        VwCustomerXactHist o = new VwCustomerXactHist();
        o.setXactId(xactId);
        o.setCustomerId(customerId);
        o.setBusinessId(businessId);
        o.setPersonId(personId);
        o.setAccountNo(acctNo);
        o.setXactAmount(xactAmt);
        o.setXactDate(xactDate);
        o.setXactTypeId(xactTypeId);
        o.setReason("Transaction History for customer, " + customerId);
        o.setActive(1);
        o.setCreditLimit(5000.00);
        o.setXactSubtypeId(1);
        o.setXactTypeName("Xact Type Name" + xactId);
        o.setConfirmNo(String.valueOf(xactDate.getTime()));
        o.setDocumentId(xactId + customerId);
        o.setCustomerActivityId(xactId * customerId);
        o.setCustomerActivityAmount(xactAmt);
        return o;
    }

    /**
     * 
     * @param xactCatgId
     * @param description
     * @param code
     * @return
     */
    public static final XactCategory createMockOrmXactCategory(int xactCatgId,
            String description, String code) {
        XactCategory o = new XactCategory();
        o.setXactCatgId(xactCatgId);
        o.setDescription(description);
        o.setCode(code);
        return o;
    }

    /**
     * 
     * @param xactCodeId
     * @param xactGroupId
     * @param description
     * @return
     */
    public static final XactCodes createMockOrmXactCode(int xactCodeId,
            int xactGroupId, String description) {
        XactCodes o = new XactCodes();
        o.setXactCodeId(xactCodeId);
        o.setDescription(description);
        o.setXactCodeGrpId(xactGroupId);
        o.setDateCreated(new Date());
        o.setDateUpdated(o.getDateCreated());
        o.setUserId("testuser");
        return o;
    }

    /**
     * 
     * @param xactGroupId
     * @param description
     * @return
     */
    public static final XactCodeGroup createMockOrmXactCodeGroup(
            int xactGroupId, String description) {
        XactCodeGroup o = new XactCodeGroup();
        o.setDescription(description);
        o.setXactCodeGrpId(xactGroupId);
        o.setDateCreated(new Date());
        o.setDateUpdated(o.getDateCreated());
        o.setUserId("testuser");
        return o;
    }

    /**
     * 
     * @param xactTypeId
     * @param xactCatgId
     * @param description
     * @param code
     * @param toMultiplier
     * @param fromMultiplier
     * @param toAcctTypeId
     * @param fromAcctTypeId
     * @param toAcctCatgId
     * @param fromAcctCatgId
     * @param hasSubsidiary
     * @return
     */
    public static final XactType createMockOrmXactType(int xactTypeId,
            int xactCatgId, String description, String code, int toMultiplier,
            int fromMultiplier, int toAcctTypeId, int fromAcctTypeId,
            int toAcctCatgId, int fromAcctCatgId, int hasSubsidiary) {
        XactType o = new XactType();
        o.setXactCatgId(xactCatgId);
        o.setDescription(description);
        o.setCode(code);
        o.setXactTypeId(xactTypeId);

        o.setToMultiplier(toMultiplier);
        o.setFromMultiplier(fromMultiplier);
        o.setToAcctTypeId(toAcctTypeId);
        o.setToAcctCatgId(toAcctCatgId);
        o.setFromAcctTypeId(fromAcctTypeId);
        o.setFromAcctCatgId(fromAcctCatgId);
        o.setHasSubsidiary(hasSubsidiary);
        return o;
    }

    /**
     * 
     * @param xactId
     * @param xactTypeId
     * @param xactSubType
     * @param xactDate
     * @param xactAmount
     * @param tenderId
     * @param negInstrNo
     * @return
     */
    public static final VwXactList createMockOrmXact(int xactId,
            int xactTypeId, int xactSubType, Date xactDate, double xactAmount,
            int tenderId, String negInstrNo) {
        VwXactList o = new VwXactList();
        o.setId(xactId);
        o.setReason("reason for transaction id " + xactId);
        o.setXactTypeId(xactTypeId);
        o.setXactSubtypeId(xactSubType);
        o.setXactDate(xactDate);
        o.setXactAmount(xactAmount);
        o.setTenderId(tenderId);
        o.setNegInstrNo(negInstrNo);
        o.setPostedDate(xactDate);
        o.setConfirmNo(String.valueOf(xactDate.getTime()));
        o.setDocumentId(xactId + tenderId);
        return o;
    }

    /**
     * 
     * @param xactItemId
     * @param xactTypeId
     * @param name
     * @return
     */
    public static final XactTypeItem createMockOrmXactTypeItem(int xactItemId,
            int xactTypeId, String name) {
        XactTypeItem o = new XactTypeItem();
        o.setXactItemId(xactItemId);
        o.setName(name);
        o.setXactTypeId(xactTypeId);
        o.setDateCreated(new Date());
        o.setDateUpdated(o.getDateCreated());
        o.setUserId("testuser");
        return o;
    }

    /**
     * 
     * @param xactTypeItemActvId
     * @param xactId
     * @param xactItemId
     * @param amount
     * @param desctiption
     * @return
     */
    public static final XactTypeItemActivity createMockOrmXactTypeItemActivity(
            int xactTypeItemActvId, int xactId, int xactItemId, double amount,
            String desctiption) {
        XactTypeItemActivity o = new XactTypeItemActivity();
        o.setXactTypeItemActvId(xactTypeItemActvId);
        o.setXactId(xactId);
        o.setXactItemId(xactItemId);
        o.setDescription(desctiption);
        o.setAmount(amount);
        o.setDateCreated(new Date());
        o.setDateUpdated(o.getDateCreated());
        o.setUserId("testuser");
        return o;
    }

    /**
     * 
     * @param id
     * @param xactId
     * @param xactAmount
     * @param xactDate
     * @param itemAmount
     * @param xactTypeId
     * @param xactTypeItemId
     * @param xactTypeXactCode
     * @param xactCatgId
     * @param xactCatgCode
     * @return
     */
    public static final VwXactTypeItemActivity createMockOrmVwXactTypeItemActivity(
            int id, int xactId, double xactAmount, String xactDate,
            double itemAmount, int xactTypeId, int xactTypeItemId,
            String xactTypeXactCode, int xactCatgId, String xactCatgCode) {
        VwXactTypeItemActivity o = new VwXactTypeItemActivity();
        o.setId(id);
        o.setXactId(xactId);
        o.setXactAmount(xactAmount);
        o.setXactDate(RMT2Date.stringToDate(xactDate));
        o.setXactTypeItemId(xactTypeItemId);
        o.setItemAmount(itemAmount);
        o.setItemName("ItemName for " + id);
        o.setReason("XactReason" + xactId);
        o.setConfirmNo("ConfirmationNo" + xactId);
        o.setDocumentId(Integer.parseInt(String.valueOf(id)
                + String.valueOf(xactCatgId)));
        o.setXactTypeId(xactTypeId);
        o.setXactTypeItemName("XactTypeItemName" + xactTypeId);
        o.setXactTypeDescription("XactTypeDescription" + xactTypeId);
        o.setXactTypeXactCode(xactTypeXactCode);
        o.setXactCategoryCode(xactCatgCode);
        o.setXactCategoryId(xactCatgId);
        o.setXactCategoryDescription("XactCategoryDescription" + xactCatgId);
        return o;
    }

    /**
     * 
     * @param xactId
     * @param creditorId
     * @param businessId
     * @param xactTypeId
     * @param acctNo
     * @param xactSubType
     * @param xactDate
     * @param xactAmount
     * @param tenderId
     * @param negInstrNo
     * @return
     */
    public static final VwXactCreditChargeList createMockOrmXVwXactCreditChargeList(
            int xactId, int creditorId, int businessId, int xactTypeId,
            String acctNo, int xactSubType, double xactAmount, String xactDate,
            int tenderId, String negInstrNo) {
        VwXactCreditChargeList o = new VwXactCreditChargeList();
        o.setCreditorId(creditorId);
        o.setBusinessId(businessId);
        o.setAccountNo(acctNo);
        o.setCreditorTypeDescription("creditor");
        o.setXactId(xactId);
        o.setReason("reason for transaction id " + creditorId);
        o.setXactTypeId(xactTypeId);
        o.setXactTypeName("XactTypeName" + xactTypeId);
        o.setXactSubtypeId(xactSubType);
        o.setXactDate(RMT2Date.stringToDate(xactDate));
        o.setXactAmount(xactAmount);
        o.setTenderId(tenderId);
        o.setTenderDescription("TenderDescription" + tenderId);
        o.setNegInstrNo(negInstrNo);
        o.setPostedDate(o.getXactDate());
        o.setConfirmNo(String.valueOf(o.getXactDate().getTime()));
        o.setDocumentId(xactId + tenderId);
        o.setXactEntryDate(o.getXactDate());
        o.setCreditorDateCreated(o.getXactDate());
        o.setCreditLimit(1500.50);
        o.setApr(1.5);
        o.setBalance(500.00);

        o.setToMultiplier(1);
        o.setFromMultiplier(-1);
        o.setToAcctTypeId(888);
        o.setFromAcctTypeId(999);
        o.setToAcctCatgId(777);

        o.setFromAcctCatgId(666);
        o.setHasSubsidiary(1);

        return o;
    }

    /**
     * 
     * @param id
     * @param customerId
     * @param invoiced
     * @param orderTotal
     * @param effectiveDate
     * @return
     */
    public static final SalesOrder createMockOrmSalesOrder(int SalesOrderId,
            int customerId, int invoiced, double orderTotal,
            String effectiveDate) {
        SalesOrder o = new SalesOrder();
        o.setSoId(SalesOrderId);
        o.setCustomerId(customerId);
        o.setInvoiced(invoiced);
        o.setEffectiveDate(RMT2Date.stringToDate(effectiveDate));
        o.setOrderTotal(orderTotal);
        o.setDateCreated(new Date());
        o.setDateUpdated(o.getDateCreated());
        o.setUserId("testuser");
        o.setIpCreated("111.222.101.100");
        o.setIpUpdated(o.getIpCreated());
        return o;
    }

    /**
     * 
     * @param id
     * @param salesOrderId
     * @param xactId
     * @param invoiceNo
     * @return
     */
    public static final SalesInvoice createMockOrmSalesInvoice(
            int salesInvoiceId, int salesOrderId, int xactId, String invoiceNo) {
        SalesInvoice o = new SalesInvoice();
        o.setInvoiceId(salesInvoiceId);
        o.setSoId(salesOrderId);
        o.setXactId(xactId);
        o.setInvoiceNo(invoiceNo);
        o.setDateCreated(new Date());
        o.setDateUpdated(o.getDateCreated());
        o.setUserId("testuser");
        o.setIpCreated("111.222.101.100");
        o.setIpUpdated(o.getIpCreated());
        return o;
    }

    /**
     * 
     * @param soItemId
     * @param itemId
     * @param soId
     * @param qty
     * @param cost
     * @return
     */
    public static final SalesOrderItems createMockOrmSalesOrderItem(
            int soItemId, int itemId, int soId, double qty, double cost) {
        SalesOrderItems i = new SalesOrderItems();
        i.setSoItemId(soItemId);
        i.setItemId(itemId);
        i.setSoId(soId);
        i.setOrderQty(qty);
        i.setInitUnitCost(cost);
        i.setInitMarkup(3.0);
        i.setItemNameOverride("ItemNameOverride" + itemId);
        i.setBackOrderQty(100);
        return i;
    }

    /**
     * 
     * @param items
     * @param so
     * @param cust
     * @param im
     * @param imt
     * @return
     */
    public static final VwSalesorderItemsBySalesorder createMockOrmVwSalesorderItemsBySalesorder(
            SalesOrderItems items, SalesOrder so, Customer cust, ItemMaster im,
            ItemMasterType imt) {
        VwSalesorderItemsBySalesorder i = new VwSalesorderItemsBySalesorder();

        // Sales order
        i.setSoId(so.getSoId());
        i.setInvoiced(so.getInvoiced());

        // Customer
        i.setCustomerId(cust.getCustomerId());
        i.setBusinessId(cust.getBusinessId());
        i.setPersonId(cust.getPersonId());

        // Item Master
        i.setCreditorId(im.getCreditorId());
        i.setItemName(im.getDescription());
        i.setVendorItemNo(im.getVendorItemNo());
        i.setItemSerialNo(im.getItemSerialNo());
        i.setQtyOnHand(im.getQtyOnHand());
        i.setUnitCost(im.getUnitCost());
        i.setMarkup(im.getMarkup());
        i.setRetailPrice(im.getRetailPrice());

        // Item Master Type
        i.setItemTypeId(imt.getItemTypeId());
        i.setItemTypeDescr(imt.getDescription());

        // Sales order item
        i.setSalesOrderItemId(items.getSoItemId());
        i.setItemId(items.getItemId());
        i.setOrderQty(items.getOrderQty());
        i.setInitUnitCost(items.getInitUnitCost());
        i.setInitMarkup(items.getInitMarkup());
        i.setItemNameOverride(items.getItemNameOverride());
        i.setBackOrderQty(items.getBackOrderQty());
        return i;
    }

    /**
     * 
     * @param invoiceId
     * @param salesOrderId
     * @param saleOrderDate
     * @param orderTotal
     * @param orderStatusId
     * @param invoiceNo
     * @param invoiced
     * @param invoiceDate
     * @param xactId
     * @param customerId
     * @param acctId
     * @param acctNo
     * @return
     */
    public static final VwSalesOrderInvoice createMockOrmVwSalesOrderInvoice(
            int invoiceId, int salesOrderId, String saleOrderDate,
            double orderTotal, int orderStatusId, String invoiceNo,
            int invoiced, String invoiceDate, int xactId, int customerId,
            int acctId, String acctNo) {
        VwSalesOrderInvoice o = new VwSalesOrderInvoice();
        o.setInvoiceId(invoiceId);
        o.setInvoiceDate(RMT2Date.stringToDate(invoiceDate));
        o.setInvoiceNo(invoiceNo);
        o.setXactId(xactId);

        o.setCustomerId(customerId);
        o.setAccountNo(acctNo);
        o.setAcctId(acctId);
        o.setDescription("Description" + acctId);
        o.setCreditLimit(1000.00);

        o.setSalesOrderId(salesOrderId);
        o.setInvoiced(invoiced);
        o.setSalesOrderDate(RMT2Date.stringToDate(saleOrderDate));
        o.setOrderTotal(orderTotal);
        o.setOrderStatusId(orderStatusId);
        o.setOrderStatusDescr("SalesOrderStatus" + orderStatusId);

        return o;
    }

    /**
     * 
     * @param salesOrderStatusId
     * @param description
     * @return
     */
    public static final SalesOrderStatus createMockOrmSalesOrderStatus(
            int salesOrderStatusId, String description) {
        SalesOrderStatus o = new SalesOrderStatus();
        o.setSoStatusId(salesOrderStatusId);
        o.setDescription(description);
        return o;
    }

    /**
     * 
     * @param soStatusHistId
     * @param soId
     * @param soStatusId
     * @param effectiveDate
     * @param endDate
     * @return
     */
    public static final SalesOrderStatusHist createMockOrmSalesOrderStatusHist(
            int soStatusHistId, int soId, int soStatusId, String effectiveDate,
            String endDate) {
        SalesOrderStatusHist o = new SalesOrderStatusHist();
        o.setSoStatusHistId(soStatusHistId);
        o.setSoId(soId);
        o.setSoStatusId(soStatusId);
        o.setReason("SalesOrderStatusHistoryReason" + soStatusId);
        o.setEffectiveDate(RMT2Date.stringToDate(effectiveDate));
        o.setEndDate(endDate == null ? null : RMT2Date.stringToDate(endDate));

        o.setDateCreated(RMT2Date.stringToDate(effectiveDate));
        o.setUserId("testuser");
        o.setIpCreated("111.222.101.100");
        o.setIpUpdated(o.getIpCreated());
        return o;
    }

    /**
     * 
     * @param poId
     * @param xactId
     * @param creditorId
     * @param amount
     * @param refNo
     * @return
     */
    public static final PurchaseOrder createPurchaseOrder(int poId, int xactId,
            int creditorId, double amount, String refNo) {
        PurchaseOrder o = new PurchaseOrder();
        o.setPoId(poId);
        o.setXactId(xactId);
        o.setCreditorId(creditorId);
        o.setTotal(amount);
        o.setRefNo(refNo);
        o.setDateCreated(RMT2Date.stringToDate("2017-01-01"));
        o.setDateUpdated(o.getDateCreated());
        o.setUserId("testuser");
        return o;
    }

    /**
     * 
     * @param poItemId
     * @param poId
     * @param itemId
     * @param unitCost
     * @param qty
     * @param qtyRcvd
     * @param qtyRtn
     * @return
     */
    public static final PurchaseOrderItems createPurchaseOrderItem(
            int poItemId, int poId, int itemId, double unitCost, int qty,
            int qtyRcvd, int qtyRtn) {
        PurchaseOrderItems o = new PurchaseOrderItems();
        o.setPoItemId(poItemId);
        o.setPoId(poId);
        o.setItemId(itemId);
        o.setUnitCost(unitCost);
        o.setQty(qty);
        o.setQtyRcvd(qtyRcvd);
        o.setQtyRtn(qtyRtn);
        return o;
    }

    /**
     * 
     * @param itemId
     * @param creditorId
     * @param vendorItemNo
     * @param itemSerialNo
     * @param unitCost
     * @return
     */
    public static final VendorItems createVendorItems(int itemId,
            int creditorId, String vendorItemNo, String itemSerialNo,
            double unitCost) {
        VendorItems o = new VendorItems();
        o.setItemId(itemId);
        o.setCreditorId(creditorId);
        o.setVendorItemNo(vendorItemNo);
        o.setItemSerialNo(itemSerialNo);
        o.setUnitCost(unitCost);
        return o;
    }

    /**
     * 
     * @param poId
     * @param itemId
     * @param vendorId
     * @param unitCost
     * @param qtyOrderd
     * @param qtyOnHand
     * @param qtyReceived
     * @param qtyReturned
     * @param overrideRetail
     * @return
     */
    public static final VwVendorItemPurchaseOrderItem createVwVendorItemPurchaseOrderItem(
            int poId, int itemId, int vendorId, double unitCost, int qtyOrderd,
            int qtyOnHand, int qtyReceived, int qtyReturned, int overrideRetail) {
        VwVendorItemPurchaseOrderItem o = new VwVendorItemPurchaseOrderItem();
        o.setPoId(poId);
        o.setItemId(itemId);
        o.setVendorId(vendorId);
        o.setUnitCost(unitCost);
        o.setQtyOrderd(qtyOrderd);
        o.setQtyOnHand(qtyOnHand);
        o.setQtyReceived(qtyReceived);
        o.setQtyReturned(qtyReturned);
        o.setOverrideRetail(overrideRetail);
        o.setMarkup(3);
        o.setVendorItemNo("VendorItemNo-" + vendorId + "-" + poId);
        o.setItemSerialNo("ItemSerialNo-" + vendorId + "-" + poId);
        o.setDescription("Description-" + vendorId + "-" + poId);
        return o;
    }

    /**
     * 
     * @param itemId
     * @param vendorId
     * @param unitCost
     * @param qtyOnHand
     * @param overrideRetail
     * @return
     */
    public static final VwVendorItems createVwVendorItems(int itemId,
            int vendorId, double unitCost, int qtyOnHand, int overrideRetail) {
        VwVendorItems o = new VwVendorItems();
        o.setItemId(itemId);
        o.setCreditorId(vendorId);
        o.setUnitCost(unitCost);
        o.setQtyOnHand(qtyOnHand);
        o.setOverrideRetail(overrideRetail);
        o.setMarkup(3);
        o.setVendorItemNo("VendorItemNo-" + vendorId + "-" + itemId);
        o.setItemSerialNo("ItemSerialNo-" + vendorId + "-" + itemId);
        o.setDescription("Description-" + vendorId + "-" + itemId);
        return o;
    }

    /**
     * 
     * @param statusId
     * @param description
     * @return
     */
    public static final PurchaseOrderStatus createPurchaseOrderStatus(
            int statusId, String description) {
        PurchaseOrderStatus o = new PurchaseOrderStatus();
        o.setPoStatusId(statusId);
        o.setDescription(description);
        return o;
    }

    /**
     * 
     * @param poStatusHistId
     * @param poStatusId
     * @param poId
     * @param effectiveDate
     * @param endDate
     * @return
     */
    public static final PurchaseOrderStatusHist createPurchaseOrderStatusHist(
            int poStatusHistId, int poStatusId, int poId, String effectiveDate,
            String endDate) {
        PurchaseOrderStatusHist o = new PurchaseOrderStatusHist();
        o.setPoStatusHistId(poStatusHistId);
        o.setPoStatusId(poStatusId);
        o.setPoId(poId);
        o.setEffectiveDate(RMT2Date.stringToDate(effectiveDate));
        o.setEndDate(endDate == null ? null : RMT2Date.stringToDate(endDate));
        o.setUserId("testuser");
        return o;
    }

    /**
     * 
     * @param xactId
     * @param xactTypeId
     * @param xactSubType
     * @param xactDate
     * @param xactAmount
     * @param xactTypeName
     * @param businessId
     * @param businessName
     * @param acctNo
     * @param invoiceNo
     * @param pareentEntityId
     * @param level1Id
     * @param level1Date
     * @param level2Id
     * @param level2Date
     * @return
     */
    public static final VwGenericXactList createMockOrmGenericXact(int xactId, int xactTypeId,
            int xactSubType, Date xactDate, double xactAmount, String xactTypeName, int businessId, String businessName,
            String acctNo, String invoiceNo, int pareentEntityId, int level1Id, Date level1Date, int level2Id, Date level2Date) {
        VwGenericXactList o = new VwGenericXactList();
        o.setXactId(xactId);
        o.setXactReason("reason for transaction id " + xactId);
        o.setXactTypeId(xactTypeId);
        o.setXactSubtypeId(xactSubType);
        o.setXactDate(xactDate);
        o.setXactAmount(xactAmount);
        o.setBusinessId(businessId);
        o.setBusinessName(businessName);
        o.setInvoiceNo(invoiceNo);
        o.setConfirmNo(String.valueOf(xactDate.getTime()));
        o.setAccountNo(acctNo);
        o.setDocumentId(xactId + xactTypeId);
        o.setXactTypeName(xactTypeName);
        o.setParentEntityId(pareentEntityId);
        o.setSpecXactLevel1Id(level1Id);
        o.setSpecXactLevel1Date(level1Date);
        o.setSpecXactLevel2Id(level2Id);
        o.setSpecXactLevel2Date(level2Date);
        return o;
    }
}
