package org.rmt2.api.handler;

import java.util.ArrayList;
import java.util.List;

import org.dao.mapping.orm.rmt2.XactType;
import org.dto.XactTypeDto;
import org.dto.adapter.orm.transaction.Rmt2XactDtoFactory;
import org.modules.transaction.XactConst;

/**
 * @author rterrell
 *
 */
public class HandlerCacheMockData {

    public static final List<XactTypeDto> createMockXactTypes() {
        List<XactTypeDto> list = new ArrayList<>();
        XactType o = createMockOrmXactType(XactConst.XACT_TYPE_CASHSALES,
                XactConst.CATG_CREDSALES, "XACT_TYPE_CASHSALES", "CASHSALES", 1,
                -1, 400, 401, 200, 222, 1);
        XactTypeDto d = Rmt2XactDtoFactory.createXactTypeInstance(o);
        list.add(d);

        o = createMockOrmXactType(XactConst.XACT_TYPE_CASHRECEIPT,
                XactConst.CATG_CASHPAY, "XACT_TYPE_CASHRECEIPT", "CASHPAY", 1, -1,
                400, 401, 200, 222, 1);
        d = Rmt2XactDtoFactory.createXactTypeInstance(o);
        list.add(d);

        o = createMockOrmXactType(XactConst.XACT_TYPE_SALESONACCTOUNT,
                XactConst.CATG_CREDSALES, "XACT_TYPE_SALESONACCTOUNT",
                "SALESONACCTOUNT", 1, -1, 400, 401, 200, 222, 1);
        d = Rmt2XactDtoFactory.createXactTypeInstance(o);
        list.add(d);

        o = createMockOrmXactType(XactConst.XACT_TYPE_CASH_DISBURSE_ACCOUNT,
                XactConst.CATG_CASHRECT, "XACT_TYPE_CASH_DISBURSE_ACCOUNT",
                "DISBURSE_ACCOUNT", 1, -1, 400, 401, 200, 222, 1);
        d = Rmt2XactDtoFactory.createXactTypeInstance(o);
        list.add(d);

        o = createMockOrmXactType(XactConst.XACT_TYPE_CASH_DISBURSE,
                XactConst.CATG_CASHPAY, "XACT_TYPE_CASH_DISBURSE",
                "CASH_DISBURSE", 1, -1, 400, 401, 200, 222, 1);
        d = Rmt2XactDtoFactory.createXactTypeInstance(o);
        list.add(d);
        
        o = createMockOrmXactType(XactConst.XACT_TYPE_CASH_DISBURSE_ASSET,
                XactConst.CATG_CASHPAY, "XACT_TYPE_CASH_DISBURSE_ASSET",
                "CASH_DISBURSE_ASSET", 1, -1, 400, 401, 200, 222, 1);
        d = Rmt2XactDtoFactory.createXactTypeInstance(o);
        list.add(d);

        o = createMockOrmXactType(XactConst.XACT_TYPE_SALESRETURNS,
                XactConst.CATG_SALESRET, "XACT_TYPE_SALESRETURNS",
                "SALESRETURNS", 1, -1, 400, 401, 200, 222, 1);
        d = Rmt2XactDtoFactory.createXactTypeInstance(o);
        list.add(d);

        o = createMockOrmXactType(XactConst.XACT_TYPE_CREDITOR_PURCHASE,
                XactConst.CATG_PURCH, "XACT_TYPE_CREDITOR_PURCHASE",
                "CREDITOR_PURCHASE", 1, -1, 400, 401, 200, 222, 1);
        d = Rmt2XactDtoFactory.createXactTypeInstance(o);
        list.add(d);

        o = createMockOrmXactType(XactConst.XACT_SUBTYPE_NOT_ASSIGNED,
                XactConst.XACT_SUBTYPE_NOT_ASSIGNED,
                "XACT_SUBTYPE_NOT_ASSIGNED", "NOT_ASSIGNED", 1, -1, 400, 401,
                200, 222, 1);
        d = Rmt2XactDtoFactory.createXactTypeInstance(o);
        list.add(d);
        
        o = createMockOrmXactType(XactConst.XACT_SUBTYPE_REVERSE,
                XactConst.CATG_REVERSE, "XACT_SUBTYPE_REVERSE", "REVERSE", 1,
                -1, 400, 401, 200, 222, 1);
        d = Rmt2XactDtoFactory.createXactTypeInstance(o);
        list.add(d);

        o = createMockOrmXactType(XactConst.XACT_SUBTYPE_CANCEL,
                XactConst.CATG_CANCEL, "XACT_SUBTYPE_CANCEL", "CANCEL", 1, -1,
                400, 401, 200, 222, 1);
        d = Rmt2XactDtoFactory.createXactTypeInstance(o);
        list.add(d);

        o = createMockOrmXactType(XactConst.XACT_SUBTYPE_FINAL,
                XactConst.XACT_SUBTYPE_FINAL, "XACT_SUBTYPE_FINAL", "FINAL", 1,
                -1, 400, 401, 200, 222, 1);
        d = Rmt2XactDtoFactory.createXactTypeInstance(o);
        list.add(d);
        
        return list;
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
}
