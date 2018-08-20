package org.rmt2.api.handler.subsidiary;

import java.util.ArrayList;
import java.util.List;

import org.dao.mapping.orm.rmt2.Customer;
import org.dto.CustomerDto;
import org.dto.adapter.orm.account.subsidiary.Rmt2SubsidiaryDtoFactory;
import org.rmt2.api.AccountingMockDataFactory;

public class SubsidiaryMockData {

    public SubsidiaryMockData() {
    }
    
    /**
     * 
     * @return
     */
    public static final List<CustomerDto> createMockCustomers() {
        List<CustomerDto> list = new ArrayList<>();
        Customer o = AccountingMockDataFactory.createMockOrmCustomer(200, 1351, 0,
                333, "C1234580", "Customer 1");
        CustomerDto d = Rmt2SubsidiaryDtoFactory.createCustomerInstance(o, null);
        list.add(d);
        
        o = AccountingMockDataFactory.createMockOrmCustomer(201, 1352, 0,
                333, "C1234581", "Customer 2");
        d = Rmt2SubsidiaryDtoFactory.createCustomerInstance(o, null);
        list.add(d);
        
        o = AccountingMockDataFactory.createMockOrmCustomer(202, 1353, 0,
                333, "C1234582", "Customer 3");
        d = Rmt2SubsidiaryDtoFactory.createCustomerInstance(o, null);
        list.add(d);
        
        o = AccountingMockDataFactory.createMockOrmCustomer(203, 1354, 0,
                333, "C1234583", "Customer 4");
        d = Rmt2SubsidiaryDtoFactory.createCustomerInstance(o, null);
        list.add(d);
        
        o = AccountingMockDataFactory.createMockOrmCustomer(204, 1355, 0,
                333, "C1234584", "Customer 5");
        d = Rmt2SubsidiaryDtoFactory.createCustomerInstance(o, null);
        list.add(d);
        return list;
    }
}
