package org.rmt2.api.handler.subsidiary;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dao.mapping.orm.rmt2.Creditor;
import org.dao.mapping.orm.rmt2.CreditorType;
import org.dao.mapping.orm.rmt2.Customer;
import org.dao.mapping.orm.rmt2.VwCreditorXactHist;
import org.dao.mapping.orm.rmt2.VwCustomerXactHist;
import org.dto.CreditorDto;
import org.dto.CreditorTypeDto;
import org.dto.CreditorXactHistoryDto;
import org.dto.CustomerDto;
import org.dto.CustomerXactHistoryDto;
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
    
    /**
     * 
     * @return
     */
    public static final List<CustomerXactHistoryDto> createMockCustomerXactHistory() {
        List<CustomerXactHistoryDto> list = new ArrayList<>();
        VwCustomerXactHist o = AccountingMockDataFactory
                .createMockOrmCustomerXactHistory(1200, 100, 1351, 0, "C8434", 1000.00,
                        new Date(), 1);
        CustomerXactHistoryDto d = Rmt2SubsidiaryDtoFactory.createCustomerTransactionInstance(o);
        list.add(d);

        o = AccountingMockDataFactory.createMockOrmCustomerXactHistory(1201, 100, 1351, 0, "C8434", 1000.00,
                new Date(), 1);
        d = Rmt2SubsidiaryDtoFactory.createCustomerTransactionInstance(o);
        list.add(d);

        o = AccountingMockDataFactory.createMockOrmCustomerXactHistory(1202, 100, 1351, 0, "C8434", 2000.00,
                new Date(), 2);
        d = Rmt2SubsidiaryDtoFactory.createCustomerTransactionInstance(o);
        list.add(d);

        o = AccountingMockDataFactory.createMockOrmCustomerXactHistory(1203, 100, 1351, 0, "C8434", 3000.00,
                new Date(), 3);
        d = Rmt2SubsidiaryDtoFactory.createCustomerTransactionInstance(o);
        list.add(d);

        o = AccountingMockDataFactory.createMockOrmCustomerXactHistory(1204, 100, 1351, 0, "C8434", 4000.00,
                new Date(), 4);
        d = Rmt2SubsidiaryDtoFactory.createCustomerTransactionInstance(o);
        list.add(d);
        return list;
    }
    
    /**
     * 
     * @return
     */
    public static final List<CreditorDto> createMockCreditors() {
        List<CreditorDto> list = new ArrayList<>();
        Creditor o = AccountingMockDataFactory.createMockOrmCreditor(200, 1351,
                330, "C1234580", "7437437JDJD8480", 22);
        CreditorDto d = Rmt2SubsidiaryDtoFactory.createCreditorInstance(o, null);
        list.add(d);
        
        o = AccountingMockDataFactory.createMockOrmCreditor(201, 1352,
                331, "C1234581", "7437437JDJD8481", 22);
        d = Rmt2SubsidiaryDtoFactory.createCreditorInstance(o, null);
        list.add(d);
        
        o = AccountingMockDataFactory.createMockOrmCreditor(202, 1353,
                332, "C1234582", "7437437JDJD8482", 22);
        d = Rmt2SubsidiaryDtoFactory.createCreditorInstance(o, null);
        list.add(d);
        
        o = AccountingMockDataFactory.createMockOrmCreditor(203, 1354,
                333, "C1234583", "7437437JDJD8483", 22);
        d = Rmt2SubsidiaryDtoFactory.createCreditorInstance(o, null);
        list.add(d);
        
        o = AccountingMockDataFactory.createMockOrmCreditor(204, 1355,
                334, "C1234584", "7437437JDJD8484", 22);
        d = Rmt2SubsidiaryDtoFactory.createCreditorInstance(o, null);
        list.add(d);
        return list;
    }
    
    /**
     * 
     * @return
     */
    public static final List<CreditorXactHistoryDto> createMockCreditorXactHistory() {
        List<CreditorXactHistoryDto> list = new ArrayList<>();
        VwCreditorXactHist o = AccountingMockDataFactory
                .createMockOrmCreditorXactHistory(1200, 100, "C8434", 1000.00,
                        new Date(), 1);
        CreditorXactHistoryDto d = Rmt2SubsidiaryDtoFactory.createCreditorTransactionInstance(o);
        list.add(d);

        o = AccountingMockDataFactory.createMockOrmCreditorXactHistory(1201,
                100, "C8434", 32.00, new Date(), 1);
        d = Rmt2SubsidiaryDtoFactory.createCreditorTransactionInstance(o);
        list.add(d);

        o = AccountingMockDataFactory.createMockOrmCreditorXactHistory(1202,
                100, "C8434", 1223.00, new Date(), 2);
        d = Rmt2SubsidiaryDtoFactory.createCreditorTransactionInstance(o);
        list.add(d);

        o = AccountingMockDataFactory.createMockOrmCreditorXactHistory(1203,
                100, "C8434", 25.67, new Date(), 1);
        d = Rmt2SubsidiaryDtoFactory.createCreditorTransactionInstance(o);
        list.add(d);

        o = AccountingMockDataFactory.createMockOrmCreditorXactHistory(1204,
                100, "C8434", 745.59, new Date(), 3);
        d = Rmt2SubsidiaryDtoFactory.createCreditorTransactionInstance(o);
        list.add(d);
        return list;
    }
    
    /**
     * 
     * @return
     */
    public static final List<CreditorTypeDto> createMockCreditorTypes() {
        List<CreditorTypeDto> list = new ArrayList<>();
        CreditorType o = AccountingMockDataFactory.createMockOrmCreditorType(100, "Creditor Type 1");
        CreditorTypeDto d = Rmt2SubsidiaryDtoFactory.createCreditorTypeInstance(o);
        list.add(d);
        
        o = AccountingMockDataFactory.createMockOrmCreditorType(200, "Creditor Type 2");
        d = Rmt2SubsidiaryDtoFactory.createCreditorTypeInstance(o);
        list.add(d);
        
        o = AccountingMockDataFactory.createMockOrmCreditorType(300, "Creditor Type 3");
        d = Rmt2SubsidiaryDtoFactory.createCreditorTypeInstance(o);
        list.add(d);
        
        o = AccountingMockDataFactory.createMockOrmCreditorType(400, "Creditor Type 4");
        d = Rmt2SubsidiaryDtoFactory.createCreditorTypeInstance(o);
        list.add(d);
        
        o = AccountingMockDataFactory.createMockOrmCreditorType(500, "Creditor Type 5");
        d = Rmt2SubsidiaryDtoFactory.createCreditorTypeInstance(o);
        list.add(d);
        return list;
    }
}
