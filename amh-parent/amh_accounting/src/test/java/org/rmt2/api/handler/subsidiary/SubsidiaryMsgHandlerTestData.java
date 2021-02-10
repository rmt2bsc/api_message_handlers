package org.rmt2.api.handler.subsidiary;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dao.mapping.orm.rmt2.Creditor;
import org.dao.mapping.orm.rmt2.CreditorType;
import org.dao.mapping.orm.rmt2.Customer;
import org.dao.mapping.orm.rmt2.GlAccounts;
import org.dao.mapping.orm.rmt2.VwBusinessAddress;
import org.dao.mapping.orm.rmt2.VwCreditorXactHist;
import org.dao.mapping.orm.rmt2.VwCustomerXactHist;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.rmt2.api.AccountingMockDataFactory;
import org.rmt2.api.handler.BaseAccountingMessageHandlerTest;

import com.api.persistence.DatabaseException;

/**
 * Common subsidiary testing facility that is mainly responsible for setting up mock data.
 * 
 * @author rterrell
 * 
 */
public class SubsidiaryMsgHandlerTestData extends BaseAccountingMessageHandlerTest {
    protected List<Customer> mockCustomerNotFoundFetchResponse;
    protected List<Customer> mockCustomerFetchAllResponse;
    protected List<Customer> mockCustomerFetchSingleResponse;
    protected List<Creditor> mockCreditorNotFoundFetchResponse;
    protected List<Creditor> mockCreditorFetchAllResponse;
    protected List<Creditor> mockCreditorFetchSingleResponse;
    protected List<VwBusinessAddress> mockBusinessContactFetchSingleResponse;
    protected List<VwBusinessAddress> mockBusinessContactFetchAllResponse;
    protected List<VwBusinessAddress> mockBusinessContactNotFoundResponse;
    protected List<CreditorType> mockCreditorTypeFetchAllResponse;
    protected List<CreditorType> mockCreditorTypeFetchSingleResponse;
    protected List<CreditorType> mockCreditorTypeNotFoundResponse;
    protected List<VwCreditorXactHist> mockCreditorXactHistoryResponse;
    protected List<VwCustomerXactHist> mockCustomerXactHistoryResponse;
    protected List<GlAccounts> mockSingleCreditorGLAccountFetchResponse;
    protected List<GlAccounts> mockSingleCustomerGLAccountFetchResponse;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.mockCustomerFetchAllResponse = this.createMockFetchAllCustomerResponse();
        this.mockCustomerFetchSingleResponse = this.createMockSingleCustomerFetchResponse();
        this.mockCustomerNotFoundFetchResponse = this.createMockCustomerNotFoundResponse();
        this.mockCreditorFetchAllResponse = this.createMockFetchAllCreditorResponse();
        this.mockCreditorNotFoundFetchResponse = this.createMockCreditorNotFoundResponse();
        this.mockCreditorFetchSingleResponse = this.createMockSingleCreditorFetchResponse();
        this.mockCreditorTypeFetchAllResponse = this.createMockFetchAllCreditorTypeResponse();
        this.mockCreditorTypeFetchSingleResponse = this.createMockFetchSingleCreditorTypeResponse();
        this.mockCreditorTypeNotFoundResponse = this.createMockFetchNotFoundCreditorTypeResponse();
        this.mockBusinessContactFetchSingleResponse = this.createMockSingleContactFetchResponse();
        this.mockBusinessContactFetchAllResponse = this.createMockFetchAllContactResponse();
        this.mockBusinessContactNotFoundResponse = this.createMockNotFoundContactFetchResponse();
        this.mockCreditorXactHistoryResponse = this.createMockFetchCreditorXactHistoryResponse();
        this.mockCustomerXactHistoryResponse = this.createMockFetchCustomerXactHistoryResponse();
        this.mockSingleCreditorGLAccountFetchResponse = this.createMockSingleCreditorGLAccountFetchResponse();
        this.mockSingleCustomerGLAccountFetchResponse = this.createMockSingleCustomerGLAccountFetchResponse();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        return;
    }

    private List<Customer> createMockCustomerNotFoundResponse() {
        List<Customer> list = null;
        return list;
    }
    
    private List<Customer> createMockFetchAllCustomerResponse() {
        List<Customer> list = new ArrayList<Customer>();
        Customer o = AccountingMockDataFactory.createMockOrmCustomer(200, 1351, 0,
                333, "C1234580", "Customer 1");
        list.add(o);
        
        o = AccountingMockDataFactory.createMockOrmCustomer(201, 1352, 0,
                333, "C1234581", "Customer 2");
        list.add(o);
        
        o = AccountingMockDataFactory.createMockOrmCustomer(202, 1353, 0,
                333, "C1234582", "Customer 3");
        list.add(o);
        
        o = AccountingMockDataFactory.createMockOrmCustomer(203, 1354, 0,
                333, "C1234583", "Customer 4");
        list.add(o);
        
        o = AccountingMockDataFactory.createMockOrmCustomer(204, 1355, 0,
                333, "C1234584", "Customer 5");
        list.add(o);
        return list;
    }
    
    private List<Customer> createMockSingleCustomerFetchResponse() {
        List<Customer> list = new ArrayList<Customer>();
        Customer o = AccountingMockDataFactory.createMockOrmCustomer(200, 1351, 0,
                333, "C1234589", "Customer 1");
        list.add(o);
        return list;
    }
    
    private List<Creditor> createMockCreditorNotFoundResponse() {
        List<Creditor> list = null;
        return list;
    }

    private List<Creditor> createMockFetchAllCreditorResponse() {
        List<Creditor> list = new ArrayList<Creditor>();
        Creditor o = AccountingMockDataFactory.createMockOrmCreditor(200, 1351,
                333, "C1234580", "7437437JDJD8480", 22);
        list.add(o);
        
        o = AccountingMockDataFactory.createMockOrmCreditor(201, 1352,
                444, "C1234581", "7437437JDJD8481", 22);
        list.add(o);
        
        o = AccountingMockDataFactory.createMockOrmCreditor(202, 1353,
                555, "C1234582", "7437437JDJD8482", 22);
        list.add(o);
        
        o = AccountingMockDataFactory.createMockOrmCreditor(203, 1354,
                666, "C1234583", "7437437JDJD8483", 22);
        list.add(o);
        
        o = AccountingMockDataFactory.createMockOrmCreditor(204, 1355,
                777, "C1234584", "7437437JDJD8484", 22);
        list.add(o);
        return list;
    }

    
    private List<Creditor> createMockSingleCreditorFetchResponse() {
        List<Creditor> list = new ArrayList<Creditor>();
        Creditor o = AccountingMockDataFactory.createMockOrmCreditor(200, 1351,
                333, "C1234589", "123-456-789", 22);
        list.add(o);
        return list;
    }

    private List<CreditorType> createMockFetchAllCreditorTypeResponse() {
        List<CreditorType> list = new ArrayList<CreditorType>();
        CreditorType o = AccountingMockDataFactory.createMockOrmCreditorType(100, "Creditor Type 1");
        list.add(o);
        
        o = AccountingMockDataFactory.createMockOrmCreditorType(200, "Creditor Type 2");
        list.add(o);
        
        o = AccountingMockDataFactory.createMockOrmCreditorType(300, "Creditor Type 3");
        list.add(o);
        
        o = AccountingMockDataFactory.createMockOrmCreditorType(400, "Creditor Type 4");
        list.add(o);
        
        o = AccountingMockDataFactory.createMockOrmCreditorType(500, "Creditor Type 5");
        list.add(o);
        return list;
    }
    
    private List<CreditorType> createMockFetchSingleCreditorTypeResponse() {
        List<CreditorType> list = new ArrayList<CreditorType>();
        CreditorType o = AccountingMockDataFactory.createMockOrmCreditorType(100, "Creditor Type 1");
        list.add(o);
        return list;
    }

    private List<CreditorType> createMockFetchNotFoundCreditorTypeResponse() {
        List<CreditorType> list = null;
        return list;
    }
    
    private List<VwBusinessAddress> createMockFetchAllContactResponse() {
        List<VwBusinessAddress> list = new ArrayList<VwBusinessAddress>();
        VwBusinessAddress p = AccountingMockDataFactory
                .createMockOrmBusinessContact(1351, "Company1", 2222,
                        "94393 Hall Ave.", "Building 123", "Suite 300",
                        "Room 45", "Dallas", "TX", 75232);
        list.add(p);

        p = AccountingMockDataFactory.createMockOrmBusinessContact(1352,
                "Company2", 4444, "9382 Frank St.", null, null, null, "Irving",
                "TX", 75240);
        list.add(p);

        p = AccountingMockDataFactory.createMockOrmBusinessContact(1353,
                "Company3", 5555, "6718 Bernard Dr", "Building 4353",
                "Suite 982", null, "Shreveport", "LA", 71118);
        list.add(p);

        p = AccountingMockDataFactory.createMockOrmBusinessContact(1354,
                "Company4", 6666, "9328 Forest Ave", "Building 854",
                "Suite 9212", "Room 555", "FLower Mound", "TX", 75028);
        list.add(p);

        p = AccountingMockDataFactory.createMockOrmBusinessContact(1355,
                "Company5", 7777, "8327 Spring Ave", null, null, null, "Dallas",
                "TX", 75232);
        list.add(p);
        return list;
    }

    private List<VwBusinessAddress> createMockSingleContactFetchResponse() {
        List<VwBusinessAddress> list = new ArrayList<VwBusinessAddress>();
        VwBusinessAddress p = AccountingMockDataFactory
                .createMockOrmBusinessContact(1351, "ABC Company", 2222,
                        "94393 Hall Ave.", "Building 123", "Suite 300",
                        "Room 45", "Dallas", "TX", 75232);
        list.add(p);
        return list;
    }
    
    private List<VwBusinessAddress> createMockNotFoundContactFetchResponse() {
        List<VwBusinessAddress> list = null;
        return list;
    }

    /**
     * Setup mocks to retrieve single matching creditor and common business
     * contact data.
     * 
     * @param busContactCriteria
     * @param creditorCriteria
     */
    protected void setupSingleSubsidiaryContactInfoFetch(VwBusinessAddress busContactCriteria, Creditor creditorCriteria) {
        try {
            when(this.mockPersistenceClient.retrieveList(eq(busContactCriteria))).thenReturn(
                            this.mockBusinessContactFetchSingleResponse);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Single Business Contact for creditor fetch test case setup failed");
        }

        try {
            when(this.mockPersistenceClient.retrieveList(eq(creditorCriteria)))
                    .thenReturn(this.mockCreditorFetchSingleResponse);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Single Creditor fetch test case setup failed");
        }
    }

    /**
     * 
     * @param busContactCriteria
     * @param creditorCriteria
     */
    protected void setupMultipleSubsidiaryContactInfoFetchDbException(VwBusinessAddress busContactCriteria,
            Creditor creditorCriteria) {
        try {
            when(this.mockPersistenceClient
                    .retrieveList(eq(busContactCriteria))).thenThrow(DatabaseException.class);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(
                    "Multiple Business Contact for creditor fetch database exception test case setup failed");
        }

        try {
            when(this.mockPersistenceClient.retrieveList(eq(creditorCriteria)))
                    .thenReturn(this.mockCreditorFetchAllResponse);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Multiple Creditor fetch test case setup failed");
        }
    }
    
    /**
     * Setup mocks to retrieve multiple matching creditor and common business
     * contact data.
     * 
     * @param busContactCriteria
     * @param creditorCriteria
     */
    protected void setupMultipleSubsidiaryContactInfoFetch(
            VwBusinessAddress busContactCriteria, Creditor creditorCriteria) {
        try {
            when(this.mockPersistenceClient
                    .retrieveList(eq(busContactCriteria))).thenReturn(
                            this.mockBusinessContactFetchAllResponse);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(
                    "Multiple Business Contact for creditor fetch test case setup failed");
        }

        try {
            when(this.mockPersistenceClient.retrieveList(eq(creditorCriteria)))
                    .thenReturn(this.mockCreditorFetchAllResponse);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Multiple Creditor fetch test case setup failed");
        }
    }
    
    protected void setupNotFoundSubsidiaryContactInfoFetch(
            VwBusinessAddress busContactCriteria, Creditor creditorCriteria) {
        try {
            when(this.mockPersistenceClient
                    .retrieveList(eq(busContactCriteria))).thenReturn(
                            this.mockBusinessContactNotFoundResponse);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(
                    "Single Business Contact for creditor fetch test case setup failed");
        }

        try {
            when(this.mockPersistenceClient.retrieveList(eq(creditorCriteria)))
                    .thenReturn(this.mockCreditorNotFoundFetchResponse);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Single Creditor fetch test case setup failed");
        }
    }

    /**
     * Setup mocks to retrieve single matching customer and common business
     * contact data.
     * 
     * @param busContactCriteria
     * @param customerCriteria
     */
    protected void setupSingleSubsidiaryContactInfoFetch(VwBusinessAddress busContactCriteria, Customer customerCriteria) {
        // For some reason, the "any" matcher must be delcared before "eq"
        // matcher in order for this to work.
        try {
            when(this.mockPersistenceClient
                    .retrieveList(any(VwBusinessAddress.class))).thenReturn(
                    this.mockBusinessContactFetchSingleResponse);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(
                    "Single Business Contact for customer fetch test case setup failed");
        }

        try {
            when(this.mockPersistenceClient.retrieveList(eq(customerCriteria)))
                    .thenReturn(this.mockCustomerFetchSingleResponse);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Single Customer fetch test case setup failed");
        }
    }

    /**
     * 
     * @param busContactCriteria
     * @param customerCriteria
     */
    protected void setupSingleSubsidiaryContactInfoExactFetch(VwBusinessAddress busContactCriteria, Customer customerCriteria) {
        // For some reason, the "any" matcher must be delcared before "eq"
        // matcher in order for this to work.
        try {
            // Could not use "eq" matcher for this mock due to the API is using
            // the setContactIdList(List) method to identify the customer's
            // contact id as criteria. The setContactIdList is directly
            // associated with the adapter and not the Customer ORM class. So
            // the eq matcher does not work here.
            when(this.mockPersistenceClient.retrieveList(any(Customer.class)))
                    .thenReturn(this.mockCustomerFetchSingleResponse);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Single Customer fetch test case setup failed");
        }
        try {
            when(this.mockPersistenceClient.retrieveList(eq(busContactCriteria)))
                    .thenReturn(this.mockBusinessContactFetchSingleResponse);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(
                    "Single Business Contact for customer fetch test case setup failed");
        }
    }

    /**
     * Setup mocks to retrieve multiple matching customer and common business
     * contact data.
     * 
     * @param busContactCriteria
     * @param customerCriteria
     */
    protected void setupMultipleSubsidiaryContactInfoFetch(
            VwBusinessAddress busContactCriteria, Customer customerCriteria) {
        try {
            when(this.mockPersistenceClient
                    .retrieveList(any(VwBusinessAddress.class))).thenReturn(
                            this.mockBusinessContactFetchAllResponse);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(
                    "Single Business Contact for customer fetch test case setup failed");
        }

        try {
            when(this.mockPersistenceClient.retrieveList(eq(customerCriteria)))
                    .thenReturn(this.mockCustomerFetchAllResponse);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Single Customer fetch test case setup failed");
        }
    }

    
    protected void setupNotFoundSubsidiaryContactInfoFetch(
            VwBusinessAddress busContactCriteria, Customer customerCriteria) {
        try {
            when(this.mockPersistenceClient
                    .retrieveList(eq(busContactCriteria))).thenReturn(
                            this.mockBusinessContactNotFoundResponse);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(
                    "Single Business Contact for customer fetch test case setup failed");
        }

        try {
            when(this.mockPersistenceClient.retrieveList(eq(customerCriteria)))
                    .thenReturn(this.mockCustomerNotFoundFetchResponse);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Single Customer fetch test case setup failed");
        }
    }

    private List<GlAccounts> createMockSingleCreditorGLAccountFetchResponse() {
        List<GlAccounts> list = new ArrayList<GlAccounts>();
        GlAccounts p = AccountingMockDataFactory.createMockOrmGlAccounts(1234, 2, 300, 1, "GL_200", "ACCT_PAY", "234",
                "Accounts Payable", 1);
        list.add(p);
        return list;
    }
    
    private List<VwCreditorXactHist> createMockFetchCreditorXactHistoryResponse() {
        List<VwCreditorXactHist> list = new ArrayList<VwCreditorXactHist>();
        VwCreditorXactHist o = AccountingMockDataFactory
                .createMockOrmCreditorXactHistory(1200, 100, "C8434", 1000.00,
                        new Date(), 1);
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmCreditorXactHistory(1201,
                100, "C8434", 32.00, new Date(), 1);
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmCreditorXactHistory(1202,
                100, "C8434", 1223.00, new Date(), 2);
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmCreditorXactHistory(1203,
                100, "C8434", 25.67, new Date(), 1);
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmCreditorXactHistory(1204,
                100, "C8434", 745.59, new Date(), 3);
        list.add(o);
        return list;
    }
    
    
    private List<GlAccounts> createMockSingleCustomerGLAccountFetchResponse() {
        List<GlAccounts> list = new ArrayList<GlAccounts>();
        GlAccounts p = AccountingMockDataFactory.createMockOrmGlAccounts(1234, 1, 100, 1, "GL_100", "ACCT_RCV", "1234",
                "Accounts Receivable", 2);
        list.add(p);
        return list;
    }
    
    
    private List<VwCustomerXactHist> createMockFetchCustomerXactHistoryResponse() {
        List<VwCustomerXactHist> list = new ArrayList<VwCustomerXactHist>();
        VwCustomerXactHist o = AccountingMockDataFactory
                .createMockOrmCustomerXactHistory(1200, 100, 1351, 0, "C8434", 1000.00,
                        new Date(), 1);
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmCustomerXactHistory(2345, 100, 1351, 0, "C8434", 1000.00,
                new Date(), 1);
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmCustomerXactHistory(3333, 100, 1351, 0, "C8434", 2000.00,
                new Date(), 2);
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmCustomerXactHistory(6543, 100, 1351, 0, "C8434", 3000.00,
                new Date(), 3);
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmCustomerXactHistory(8765, 100, 1351, 0, "C8434", 4000.00,
                new Date(), 4);
        list.add(o);
        return list;
    }
}