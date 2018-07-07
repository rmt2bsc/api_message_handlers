package org.rmt2.api.handler;

import java.util.ArrayList;
import java.util.List;

import org.dao.mapping.orm.rmt2.GeneralCodes;
import org.dao.mapping.orm.rmt2.GeneralCodesGroup;
import org.dao.mapping.orm.rmt2.VwBusinessAddress;
import org.dao.mapping.orm.rmt2.Zipcode;
import org.dto.BusinessContactDto;
import org.dto.ContactDto;
import org.dto.CountryDto;
import org.dto.LookupCodeDto;
import org.dto.LookupGroupDto;
import org.dto.RegionDto;
import org.dto.ZipcodeDto;
import org.dto.adapter.orm.Rmt2AddressBookDtoFactory;

public class AddressBookMockData {

    public AddressBookMockData() {
    }
    
    /**
     * 
     * @return
     */
    public static final List<ContactDto> createMockSingleContactDtoResponseData() {
        List<ContactDto> list = new ArrayList<ContactDto>();
        VwBusinessAddress bus = new VwBusinessAddress();
        bus.setBusinessId(1351);
        bus.setBusLongname("BusinessName_1");
        bus.setBusContactFirstname("firstname_1");
        bus.setBusContactLastname("lastname_1");
        bus.setContactEmail(bus.getBusContactFirstname() + "." + bus.getBusContactLastname() + "@gte.net");
        bus.setBusContactPhone("9999999991");
        bus.setAddrId(2001);
        bus.setBusinessId(1351);
        bus.setAddr1("address_line1_1");
        bus.setAddr2("address_line2_1");
        bus.setAddr3("address_line3_1");
        bus.setAddr4("address_line4_1");
        bus.setZipCity("Dallas");
        bus.setZipState("Tx");
        bus.setAddrZip(75232);
        bus.setAddrPhoneMain("2143738001");
        bus.setBusTaxId("750000001");
        bus.setBusWebsite("www.BusinessName_1.com");
        bus.setBusShortname("shortname");
        BusinessContactDto busDto = Rmt2AddressBookDtoFactory.getBusinessInstance(bus);
        list.add(busDto);
        return list;
    }

    public static final List<ContactDto> createMockContactDtoResponseListData() {
        List<ContactDto> list = new ArrayList<ContactDto>();
        
        VwBusinessAddress bus = new VwBusinessAddress();
        bus.setBusinessId(1351);
        bus.setBusLongname("BusinessName_1");
        bus.setBusContactFirstname("firstname_1");
        bus.setBusContactLastname("lastname_1");
        bus.setContactEmail(bus.getBusContactFirstname() + "." + bus.getBusContactLastname() + "@gte.net");
        bus.setBusContactPhone("9999999991");
        bus.setAddrId(2001);
        bus.setAddr1("address_line1_1");
        bus.setAddr2("address_line2_1");
        bus.setAddr3("address_line3_1");
        bus.setAddr4("address_line4_1");
        bus.setZipCity("Dallas");
        bus.setZipState("Tx");
        bus.setAddrZip(75232);
        bus.setAddrPhoneMain("2143738001");
        bus.setBusTaxId("750000001");
        bus.setBusWebsite("www.BusinessName_1.com");
        bus.setBusShortname("shortname");
        BusinessContactDto busDto = Rmt2AddressBookDtoFactory.getBusinessInstance(bus);
        list.add(busDto);
        
        bus = new VwBusinessAddress();
        bus.setBusinessId(1352);
        bus.setBusLongname("BusinessName_2");
        bus.setBusContactFirstname("firstname_2");
        bus.setBusContactLastname("lastname_2");
        bus.setContactEmail(bus.getBusContactFirstname() + "." + bus.getBusContactLastname() + "@gte.net");
        bus.setBusContactPhone("9999999992");
        bus.setAddrId(2002);
        bus.setBusinessId(1352);
        bus.setAddr1("address_line1_2");
        bus.setAddr2("address_line2_2");
        bus.setAddr3("address_line3_2");
        bus.setAddr4("address_line4_2");
        bus.setZipCity("Dallas");
        bus.setZipState("Tx");
        bus.setAddrZip(75232);
        bus.setAddrPhoneMain("2143738002");
        bus.setBusTaxId("750000002");
        bus.setBusWebsite("www.BusinessName_2.com");
        bus.setBusShortname("shortname");
         busDto = Rmt2AddressBookDtoFactory.getBusinessInstance(bus);
        list.add(busDto);
        
        bus = new VwBusinessAddress();
        bus.setBusinessId(1353);
        bus.setBusLongname("BusinessName_3");
        bus.setBusContactFirstname("firstname_3");
        bus.setBusContactLastname("lastname_3");
        bus.setContactEmail(bus.getBusContactFirstname() + "." + bus.getBusContactLastname() + "@gte.net");
        bus.setBusContactPhone("9999999993");
        bus.setAddrId(2003);
        bus.setBusinessId(1353);
        bus.setAddr1("address_line1_3");
        bus.setAddr2("address_line2_3");
        bus.setAddr3("address_line3_3");
        bus.setAddr4("address_line4_3");
        bus.setZipCity("Dallas");
        bus.setZipState("Tx");
        bus.setAddrZip(75232);
        bus.setAddrPhoneMain("2143738003");
        bus.setBusTaxId("750000003");
        bus.setBusWebsite("www.BusinessName_3.com");
        bus.setBusShortname("shortname");
         busDto = Rmt2AddressBookDtoFactory.getBusinessInstance(bus);
        list.add(busDto);
        
        return list;
    }
    
    /**
     * 
     * @param zipId
     * @param zipCode
     * @param state
     * @param city
     * @param areaCode
     * @param countyName
     * @param timeZoneId
     * @return
     */
    public static final ZipcodeDto createZipcodeOrm(int zipId, int zipCode,
            String state, String city, String areaCode, String countyName,
            int timeZoneId) {
        Zipcode obj = new Zipcode();
        obj.setZipId(zipId);
        obj.setZip(zipCode);
        obj.setState(state);
        obj.setCity(city);
        obj.setAreaCode(areaCode);
        obj.setCountyName(countyName);
        obj.setTimeZoneId(timeZoneId);
        obj.setLatitude(382372382323.3883828);
        obj.setLongitude(48484848.4843949);
        obj.setBlackPopulation(239000);
        obj.setWhitePopulation(10000000);
        obj.setHispanicPopulation(30000);
        obj.setCityAliasName(city + "_Alias");
        obj.setAverageHouseValue(87674.84);
        obj.setCbsa(123.88);
        obj.setCbsaDiv(23.88);
        obj.setCityAliasAbbr("AAB");
        obj.setCityTypeId("City_type");
        obj.setCountiesArea(333);
        obj.setCountyFips("Counties FIPS");
        obj.setDayLightSaving("TRUE");
        obj.setElevation(2345.89);
        obj.setHouseholdsPerZipcode(600);
        obj.setIncomePerHousehold(569.76);
        obj.setMsa(757575);
        obj.setPersonsPerHousehold(4);
        obj.setPmsa(123);
        obj.setZipcodePopulation(25000);
        
        ZipcodeDto dto = Rmt2AddressBookDtoFactory.getZipCodeInstance(obj);
        return dto;
    }
 
    
   /**
    * 
    * @return
    */
    public static final List<LookupGroupDto> createMockLookupGroupDtoListResponse() {
        List<LookupGroupDto> dtoList = new ArrayList<>();
        
        List<GeneralCodesGroup> list = new ArrayList<GeneralCodesGroup>();
        GeneralCodesGroup p = new GeneralCodesGroup();
        p.setCodeGrpId(100);
        p.setDescription("Group1");
        list.add(p);

        p = new GeneralCodesGroup();
        p.setCodeGrpId(101);
        p.setDescription("Group2");
        list.add(p);

        p = new GeneralCodesGroup();
        p.setCodeGrpId(102);
        p.setDescription("Group3");
        list.add(p);

        p = new GeneralCodesGroup();
        p.setCodeGrpId(103);
        p.setDescription("Group4");
        list.add(p);
        
        for (GeneralCodesGroup grp : list) {
            LookupGroupDto dto = Rmt2AddressBookDtoFactory.getCodeInstance(grp);
            dtoList.add(dto);
        }
        
        return dtoList;
    }
    
    /**
     * 
     * @return
     */
    public static final List<LookupCodeDto> createMockLookupCodeDtoListResponse() {
        List<LookupCodeDto> dtoList = new ArrayList<>();
    
        List<GeneralCodes> list = new ArrayList<GeneralCodes>();
        GeneralCodes p = new GeneralCodes();
        p.setCodeGrpId(100);
        p.setCodeId(500);
        p.setLongdesc("Code1");
        p.setShortdesc("CD1");
        list.add(p);

        p = new GeneralCodes();
        p.setCodeGrpId(100);
        p.setCodeId(501);
        p.setLongdesc("Code2");
        p.setShortdesc("CD2");
        list.add(p);

        p = new GeneralCodes();
        p.setCodeGrpId(100);
        p.setCodeId(502);
        p.setLongdesc("Code3");
        p.setShortdesc("CD3");
        list.add(p);

        p = new GeneralCodes();
        p.setCodeGrpId(100);
        p.setCodeId(503);
        p.setLongdesc("Code4");
        p.setShortdesc("CD4");
        list.add(p);
        
        for (GeneralCodes grp : list) {
            LookupCodeDto dto = Rmt2AddressBookDtoFactory.getCodeInstance(grp);
            dtoList.add(dto);
        }
        
        return dtoList;
    }
    
    public static final List<Zipcode> createMockFetchAllOrmResults() {
        List<Zipcode> list = new ArrayList<Zipcode>();
        Zipcode p = createMockZipcodeOrm(75231,75231, "State1", "City1", "AreaCode1", "County1", 6);
        list.add(p);

        p = createMockZipcodeOrm(75232,75232, "State2", "City2", "AreaCode2", "County2", 6);
        list.add(p);

        p = createMockZipcodeOrm(75233,75233, "State3", "City3", "AreaCode3", "County3", 6);
        list.add(p);

        p = createMockZipcodeOrm(75234,75234, "State4", "City4", "AreaCode4", "County4", 6);
        list.add(p);
        
        p = createMockZipcodeOrm(75235,75235, "State5", "City5", "AreaCode5", "County5", 6);
        list.add(p);
        
        return list;
    }
    
    public static final List<ZipcodeDto> createMockFetchZipDtoResults() {
        List<ZipcodeDto> list = new ArrayList<>();
        ZipcodeDto p = createMockZipcodeDto(75231,75231, "State1", "City1", "AreaCode1", "County1", 6);
        list.add(p);

        p = createMockZipcodeDto(75232,75232, "State2", "City2", "AreaCode2", "County2", 6);
        list.add(p);

        p = createMockZipcodeDto(75233,75233, "State3", "City3", "AreaCode3", "County3", 6);
        list.add(p);

        p = createMockZipcodeDto(75234,75234, "State4", "City4", "AreaCode4", "County4", 6);
        list.add(p);
        
        p = createMockZipcodeDto(75235,75235, "State5", "City5", "AreaCode5", "County5", 6);
        list.add(p);
        
        return list;
    }

    public static final ZipcodeDto createMockZipcodeDto(int zipId, int zipCode, String state,
            String city, String areaCode, String countyName, int timeZoneId) {
        ZipcodeDto dto = Rmt2AddressBookDtoFactory.getNewZipCodeInstance();
        dto.setId(zipId);
        dto.setZip(zipCode);
        dto.setStateCode(state);
        dto.setCity(city);
        dto.setAreaCode(areaCode);
        dto.setCountyName(countyName);
        dto.setTimeZoneId(timeZoneId);
        dto.setLatitude(382372382323.3883828);
        dto.setLongitude(48484848.4843949);
        dto.setBlackPopulation(239000);
        dto.setWhitePopulation(10000000);
        dto.setHispanicPopulation(30000);
        dto.setCityAliasName(city + "_Alias");
        dto.setAverageHouseValue(87674.84);
        dto.setCbsa(123.88);
        dto.setCbsaDiv(23.88);
        dto.setCityAliasAbbr("AAB");
        dto.setCityTypeId("City_type");
        dto.setCountiesArea(333);
        dto.setCountyFips("Counties FIPS");
        dto.setDayLightSaving("TRUE");
        dto.setElevation(2345.89);
        dto.setHouseholdsPerZipcode(600);
        dto.setIncomePerHousehold(569.76);
        dto.setMsa(757575);
        dto.setPersonsPerHousehold(4);
        dto.setPmsa(123);
        dto.setZipPopulation(25000);
        return dto;
    }

    public static final Zipcode createMockZipcodeOrm(int zipId, int zipCode, String state,
            String city, String areaCode, String countyName, int timeZoneId) {
        Zipcode dto = new Zipcode();
        dto.setZipId(zipId);
        dto.setZip(zipCode);
        dto.setState(state);
        dto.setCity(city);
        dto.setAreaCode(areaCode);
        dto.setCountyName(countyName);
        dto.setTimeZoneId(timeZoneId);
        dto.setLatitude(382372382323.3883828);
        dto.setLongitude(48484848.4843949);
        dto.setBlackPopulation(239000);
        dto.setWhitePopulation(10000000);
        dto.setHispanicPopulation(30000);
        dto.setCityAliasName(city + "_Alias");
        dto.setAverageHouseValue(87674.84);
        dto.setCbsa(123.88);
        dto.setCbsaDiv(23.88);
        dto.setCityAliasAbbr("AAB");
        dto.setCityTypeId("City_type");
        dto.setCountiesArea(333);
        dto.setCountyFips("Counties FIPS");
        dto.setDayLightSaving("TRUE");
        dto.setElevation(2345.89);
        dto.setHouseholdsPerZipcode(600);
        dto.setIncomePerHousehold(569.76);
        dto.setMsa(757575);
        dto.setPersonsPerHousehold(4);
        dto.setPmsa(123);
        dto.setZipcodePopulation(25000);
        return dto;
    }
    
    /**
     * 
     * @return
     */
    public static final List<CountryDto> createMockCountryDto() {
        List<CountryDto> list = new ArrayList<>();
        CountryDto o = Rmt2AddressBookDtoFactory.getNewCountryInstance();
        o.setCountryId(100);
        o.setCountryName("CountryName1");
        o.setCountryCode("CountryCode1");
        list.add(o);

        o = Rmt2AddressBookDtoFactory.getNewCountryInstance();
        o.setCountryId(101);
        o.setCountryName("CountryName2");
        o.setCountryCode("CountryCode2");
        list.add(o);

        o = Rmt2AddressBookDtoFactory.getNewCountryInstance();
        o.setCountryId(102);
        o.setCountryName("CountryName3");
        o.setCountryCode("CountryCode3");
        list.add(o);

        o = Rmt2AddressBookDtoFactory.getNewCountryInstance();
        o.setCountryId(103);
        o.setCountryName("CountryName4");
        o.setCountryCode("CountryCode4");
        list.add(o);
        return list;
    }
    
    /**
     * 
     * @return
     */
    public static final List<RegionDto> createMockRegionDto() {
        List<RegionDto> list = new ArrayList<RegionDto>();
        RegionDto o = Rmt2AddressBookDtoFactory.getNewRegionInstance();
        o.setStateId(10);
        o.setStateCode("AbbrCode1");
        o.setStateName("StateName1");
        o.setCountryId(100);
        list.add(o);

        o = Rmt2AddressBookDtoFactory.getNewRegionInstance();
        o.setStateId(11);
        o.setStateCode("AbbrCode2");
        o.setStateName("StateName2");
        o.setCountryId(100);
        list.add(o);
        
        o = Rmt2AddressBookDtoFactory.getNewRegionInstance();
        o.setStateId(12);
        o.setStateCode("AbbrCode3");
        o.setStateName("StateName3");
        o.setCountryId(100);
        list.add(o);
        
        o = Rmt2AddressBookDtoFactory.getNewRegionInstance();
        o.setStateId(13);
        o.setStateCode("AbbrCode4");
        o.setStateName("StateName4");
        o.setCountryId(100);
        list.add(o);
        
        o = Rmt2AddressBookDtoFactory.getNewRegionInstance();
        o.setStateId(14);
        o.setStateCode("AbbrCode5");
        o.setStateName("StateName5");
        o.setCountryId(100);
        list.add(o);
        return list;
    }
}
