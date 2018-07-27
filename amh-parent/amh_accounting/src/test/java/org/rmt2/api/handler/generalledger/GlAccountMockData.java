package org.rmt2.api.handler.generalledger;

import java.util.ArrayList;
import java.util.List;

import org.dao.mapping.orm.rmt2.GlAccountCategory;
import org.dao.mapping.orm.rmt2.GlAccountTypes;
import org.dao.mapping.orm.rmt2.GlAccounts;
import org.dto.AccountCategoryDto;
import org.dto.AccountDto;
import org.dto.AccountTypeDto;
import org.dto.adapter.orm.account.generalledger.Rmt2AccountDtoFactory;
import org.rmt2.api.AccountingMockDataFactory;

public class GlAccountMockData {

    public GlAccountMockData() {
    }
    
    /**
     * 
     * @return
     */
    public static final List<AccountDto> createMockGlAccounts() {
        List<AccountDto> list = new ArrayList<>();
        GlAccounts p = AccountingMockDataFactory.createMockOrmGlAccounts(100, 1, 120, 1, "AccountNo1", "AccountName1",
                "AccountCode1", "AccountDescription1", 1);
        AccountDto dto = Rmt2AccountDtoFactory.createAccountInstance(p);
        list.add(dto);

        p = AccountingMockDataFactory.createMockOrmGlAccounts(101, 2, 121, 1, "AccountNo2", "AccountName2", "AccountCode2",
                "AccountDescription2", 1);
        dto = Rmt2AccountDtoFactory.createAccountInstance(p);
        list.add(dto);

        p = AccountingMockDataFactory.createMockOrmGlAccounts(102, 3, 122, 1, "AccountNo3", "AccountName3", "AccountCode3",
                "AccountDescription3", 1);
        dto = Rmt2AccountDtoFactory.createAccountInstance(p);
        list.add(dto);

        p = AccountingMockDataFactory.createMockOrmGlAccounts(103, 4, 123, 1, "AccountNo4", "AccountName4", "AccountCode4",
                "AccountDescription4", 1);
        dto = Rmt2AccountDtoFactory.createAccountInstance(p);
        list.add(dto);
        return list;
    }
    
    
    /**
     * 
     * @return
     */
    public static final List<AccountTypeDto> createMockGlAccountTypes() {
        List<AccountTypeDto> list = new ArrayList<>();
        GlAccountTypes p = AccountingMockDataFactory.createMockOrmGlAccountTypes(100, 1, "AccountType1");
        AccountTypeDto dto = Rmt2AccountDtoFactory.createAccountTypeInstance(p);
        list.add(dto);

        p = AccountingMockDataFactory.createMockOrmGlAccountTypes(101, 1, "AccountType2");
        dto = Rmt2AccountDtoFactory.createAccountTypeInstance(p);
        list.add(dto);;
        
        p = AccountingMockDataFactory.createMockOrmGlAccountTypes(102, 2, "AccountType3");
        dto = Rmt2AccountDtoFactory.createAccountTypeInstance(p);
        list.add(dto);
        
        p = AccountingMockDataFactory.createMockOrmGlAccountTypes(103, 2, "AccountType4");
        dto = Rmt2AccountDtoFactory.createAccountTypeInstance(p);
        list.add(dto);
        
        p = AccountingMockDataFactory.createMockOrmGlAccountTypes(104, 2, "AccountType5");
        dto = Rmt2AccountDtoFactory.createAccountTypeInstance(p);
        list.add(dto);
        return list;
    }
    
    /**
     * 
     * @return
     */
    public static final List<AccountCategoryDto> createMockGlAccountCategories() {
        List<AccountCategoryDto> list = new ArrayList<>();
        GlAccountCategory p = AccountingMockDataFactory.createMockOrmGlAccountCategory(100, 1, "Category1");
        AccountCategoryDto dto = Rmt2AccountDtoFactory.createAccountCategoryInstance(p);
        list.add(dto);

        p = AccountingMockDataFactory.createMockOrmGlAccountCategory(101, 2, "Category2");
        dto = Rmt2AccountDtoFactory.createAccountCategoryInstance(p);
        list.add(dto);
        
        p = AccountingMockDataFactory.createMockOrmGlAccountCategory(102, 3, "Category3");
        dto = Rmt2AccountDtoFactory.createAccountCategoryInstance(p);
        list.add(dto);
        
        p = AccountingMockDataFactory.createMockOrmGlAccountCategory(103, 4, "Category4");
        dto = Rmt2AccountDtoFactory.createAccountCategoryInstance(p);
        list.add(dto);
        
        p = AccountingMockDataFactory.createMockOrmGlAccountCategory(104, 5, "Category5");
        dto = Rmt2AccountDtoFactory.createAccountCategoryInstance(p);
        list.add(dto);
        return list;
    }
}
