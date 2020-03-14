package org.rmt2.api.handler.transaction.purchases;

import java.util.ArrayList;
import java.util.List;

import org.dao.mapping.orm.rmt2.VwXactCreditChargeList;
import org.dao.mapping.orm.rmt2.XactTypeItemActivity;
import org.dto.XactCreditChargeDto;
import org.dto.XactTypeItemActivityDto;
import org.dto.adapter.orm.transaction.Rmt2XactDtoFactory;
import org.dto.adapter.orm.transaction.purchases.creditor.Rmt2CreditChargeDtoFactory;
import org.modules.transaction.XactConst;
import org.rmt2.api.AccountingMockDataFactory;
import org.rmt2.api.handler.subsidiary.SubsidiaryMockData;

/**
 * @author rterrell
 *
 */
public class CreditorPurchasesMockData extends SubsidiaryMockData {
    
    public static final int NEW_XACT_ID = 1234567;
    public static final int EXISTING_XACT_ID = 7777;

    public static final List<XactCreditChargeDto> createMockCreditPurchaseHeader() {
        List<XactCreditChargeDto> list = new ArrayList<>();
        VwXactCreditChargeList o = AccountingMockDataFactory.createMockOrmXVwXactCreditChargeList(
                NEW_XACT_ID, 
                111111, 
                1351,
                XactConst.XACT_TYPE_CREDITOR_PURCHASE, 
                "1111",
                XactConst.XACT_SUBTYPE_NOT_ASSIGNED, 
                100.00,
                "2017-01-01", 
                XactConst.TENDER_CREDITCARD,
                "1111-0000-0000-0000");
        XactCreditChargeDto dto = Rmt2CreditChargeDtoFactory.createCreditChargeInstance(o, null);
        list.add(dto);
        return list;
    }
    
    /**
     * 
     * @return
     */
    public static final List<XactTypeItemActivityDto> createMockCreditPurchaseDetails() {
        List<XactTypeItemActivityDto> list = new ArrayList<XactTypeItemActivityDto>();
        XactTypeItemActivity o = AccountingMockDataFactory
                .createMockOrmXactTypeItemActivity(NEW_XACT_ID, 111111, 601, 20.00,
                        "Item1");
        XactTypeItemActivityDto d = Rmt2XactDtoFactory.createXactTypeItemActivityInstance(o);
        list.add(d);

        o = AccountingMockDataFactory.createMockOrmXactTypeItemActivity(NEW_XACT_ID,
                111111, 602, 20.00, "Item2");
        d = Rmt2XactDtoFactory.createXactTypeItemActivityInstance(o);
        list.add(d);

        o = AccountingMockDataFactory.createMockOrmXactTypeItemActivity(NEW_XACT_ID,
                111111, 603, 20.00, "Item3");
        d = Rmt2XactDtoFactory.createXactTypeItemActivityInstance(o);
        list.add(d);

        o = AccountingMockDataFactory.createMockOrmXactTypeItemActivity(NEW_XACT_ID,
                111111, 604, 20.00, "Item4");
        d = Rmt2XactDtoFactory.createXactTypeItemActivityInstance(o);
        list.add(d);

        o = AccountingMockDataFactory.createMockOrmXactTypeItemActivity(NEW_XACT_ID,
                111111, 605, 20.00, "Item5");
        d = Rmt2XactDtoFactory.createXactTypeItemActivityInstance(o);
        list.add(d);
        return list;
    }
}
