package org.rmt2.api.handlers.admin.client;

import java.util.ArrayList;
import java.util.List;

import org.dto.ClientDto;
import org.dto.adapter.orm.ProjectObjectFactory;
import org.rmt2.jaxb.BusinessType;
import org.rmt2.jaxb.ClientCriteriaType;
import org.rmt2.jaxb.ClientType;
import org.rmt2.jaxb.CustomerType;
import org.rmt2.jaxb.ProjectCriteriaGroup;
import org.rmt2.jaxb.ProjectProfileRequest;
import org.rmt2.jaxb.RecordTrackingType;
import org.rmt2.util.RecordTrackingTypeBuilder;
import org.rmt2.util.accounting.subsidiary.CustomerTypeBuilder;
import org.rmt2.util.addressbook.BusinessTypeBuilder;
import org.rmt2.util.projecttracker.admin.ClientTypeBuilder;

import com.RMT2Base;

/**
 * A factory for converting Client project tracker administration related JAXB
 * objects to DTO and vice versa.
 * 
 * @author Roy Terrell.
 * 
 */
public class ClientJaxbDtoFactory extends RMT2Base {

    /**
     * Creates an instance of <i>ClientDto</i> using a valid
     * <i>ClientCriteriaType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link ClientCriteriaType}
     * @return an instance of {@link ClientDto}
     */
    public static final ClientDto createClientDtoCriteriaInstance(ProjectCriteriaGroup criteria) {

        if (criteria == null || criteria.getClientCriteria() == null) {
            return null;
        }
        ClientCriteriaType jaxbObj = criteria.getClientCriteria();
        ClientDto dto = ProjectObjectFactory.createClientDtoInstance(null);
        if (jaxbObj.getClientId() != null) {
            dto.setClientId(jaxbObj.getClientId().intValue());
        }
        if (jaxbObj.getBusinessId() != null) {
            dto.setBusinessId(jaxbObj.getBusinessId().intValue());
        }
        if (jaxbObj.getClientName() != null) {
            dto.setClientName(jaxbObj.getClientName());
        }
        if (jaxbObj.getAccountNo() != null) {
            dto.setAccountNo(jaxbObj.getAccountNo());
        }
        return dto;
    }
    
    /**
     * Obtains a list of customer identifiers from the Project Profile Request object.
     * 
     * @param jaxbObj
     *            an instance of {@link ProjectProfileRequest}
     * @return List of integers representing the customer identifiers
     */
    public static final List<Integer> getCustomerImportIdentifiers(ProjectProfileRequest jaxbObj) {
        if (jaxbObj == null || jaxbObj.getProfile() == null || jaxbObj.getProfile().getClient() == null) {
            return null;
        }
        List<Integer> custIdList = new ArrayList<>();
        List<ClientType> clients = jaxbObj.getProfile().getClient();
        for (ClientType clientObj : clients) {
            if (clientObj.getClientId() != null) {
                custIdList.add(clientObj.getClientId().intValue());
            }
        }
        return custIdList;
    }
        
    /**
     * Created an instance of ClientDto from an ClientType object
     * 
     * @param jaxbObj
     *            an instance of {@link ClientType}
     * @return an instance of {@link ClientDto}
     */
    public static final ClientDto createClientDtoInstance(ClientType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        ClientDto dto = ProjectObjectFactory.createClientDtoInstance(null);
        if (jaxbObj.getClientId() != null) {
            dto.setClientId(jaxbObj.getClientId().intValue());
        }
        if (jaxbObj.getName() != null) {
            dto.setClientName(jaxbObj.getName());
        }
        if (jaxbObj.getBillRate() != null) {
            dto.setClientBillRate(jaxbObj.getBillRate().doubleValue());
        }
        if (jaxbObj.getOtBillRate() != null) {
            dto.setClientOtBillRate(jaxbObj.getOtBillRate().doubleValue());
        }
        if (jaxbObj.getCustomer() != null) {
            dto.setAccountNo(jaxbObj.getCustomer().getAccountNo());
            if (jaxbObj.getCustomer().getBusinessContactDetails() != null) {
                if (jaxbObj.getCustomer().getBusinessContactDetails().getBusinessId() != null) {
                    dto.setBusinessId(jaxbObj.getCustomer().getBusinessContactDetails().getBusinessId().intValue());
                }
                dto.setClientContactFirstname(jaxbObj.getCustomer().getBusinessContactDetails().getContactFirstname());
                dto.setClientContactLastname(jaxbObj.getCustomer().getBusinessContactDetails().getContactLastname());
                dto.setClientContactPhone(jaxbObj.getCustomer().getBusinessContactDetails().getContactPhone());
                dto.setClientContactExt(jaxbObj.getCustomer().getBusinessContactDetails().getContactExt());
                dto.setClientContactEmail(jaxbObj.getCustomer().getBusinessContactDetails().getContactEmail());
            }
        }

        return dto;
    }
    
    /**
     * Created an instance of ClientType from an ClientDto object
     * 
     * @param dto
     *            an instance of {@link ClientDto}
     * @return an instance of {@link ClientType}
     */
    public static final ClientType createClientJaxbInstance(ClientDto dto) {
        if (dto == null) {
            return null;
        }
        BusinessType bt = BusinessTypeBuilder.Builder.create()
                .withBusinessId(dto.getBusinessId())
                .withContactFirstname(dto.getClientContactFirstname())
                .withContactLastname(dto.getClientContactLastname())
                .withContactEmail(dto.getClientContactEmail())
                .build();

        CustomerType customer = CustomerTypeBuilder.Builder.create()
                .withAccountNo(dto.getAccountNo())
                .withBusinessType(bt)
                .build();

        RecordTrackingType tracking = RecordTrackingTypeBuilder.Builder.create()
                .withDateCreated(dto.getDateCreated())
                .withDateUpdate(dto.getDateUpdated())
                .withUserId(dto.getUpdateUserId())
                .build();

        ClientType client = ClientTypeBuilder.Builder.create()
                .withClientId(dto.getClientId())
                .withClientName(dto.getClientName())
                .withCustomerData(customer)
                .withBillRate(dto.getClientBillRate())
                .withOvertimeBillRate(dto.getClientOtBillRate())
                .withRecordTracking(tracking)
                .build();

        return client;
    }

}
