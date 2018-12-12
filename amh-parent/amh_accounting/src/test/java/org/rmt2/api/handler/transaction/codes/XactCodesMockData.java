package org.rmt2.api.handler.transaction.codes;

import java.util.ArrayList;
import java.util.List;

import org.dao.mapping.orm.rmt2.XactCodeGroup;
import org.dto.XactCodeGroupDto;
import org.dto.adapter.orm.transaction.Rmt2XactDtoFactory;
import org.rmt2.api.AccountingMockDataFactory;
import org.rmt2.api.handler.subsidiary.SubsidiaryMockData;

/**
 * @author rterrell
 *
 */
public class XactCodesMockData extends SubsidiaryMockData {

    /**
     * 
     * @return
     */
    public static final List<XactCodeGroupDto> createMockXactGroup() {
        List<XactCodeGroupDto> list = new ArrayList<>();
        XactCodeGroup o = AccountingMockDataFactory.createMockOrmXactCodeGroup(101, "Group 1");
        XactCodeGroupDto d = Rmt2XactDtoFactory.createXactCodeGroupInstance(o);
        list.add(d);
        
        o = AccountingMockDataFactory.createMockOrmXactCodeGroup(102, "Group 2");
        d = Rmt2XactDtoFactory.createXactCodeGroupInstance(o);
        list.add(d);
        
        o = AccountingMockDataFactory.createMockOrmXactCodeGroup(103, "Group 3");
        d = Rmt2XactDtoFactory.createXactCodeGroupInstance(o);
        list.add(d);
        
        o = AccountingMockDataFactory.createMockOrmXactCodeGroup(104, "Group 4");
        d = Rmt2XactDtoFactory.createXactCodeGroupInstance(o);
        list.add(d);
        
        o = AccountingMockDataFactory.createMockOrmXactCodeGroup(105, "Group 5");
        d = Rmt2XactDtoFactory.createXactCodeGroupInstance(o);
        list.add(d);
        return list;
    }
    
}
