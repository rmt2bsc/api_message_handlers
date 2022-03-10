package org.rmt2.api.handlers.employee;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.EmployeeDto;
import org.dto.PersonalContactDto;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.jaxb.AddressType;
import org.rmt2.jaxb.EmployeeTitleType;
import org.rmt2.jaxb.EmployeeType;
import org.rmt2.jaxb.EmployeetypeType;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.PersonType;
import org.rmt2.jaxb.ProjectDetailGroup;
import org.rmt2.jaxb.ProjectProfileRequest;
import org.rmt2.jaxb.ProjectProfileResponse;
import org.rmt2.jaxb.ReplyStatusType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.rmt2.jaxb.ZipcodeType;
import org.rmt2.util.addressbook.AddressTypeBuilder;
import org.rmt2.util.addressbook.PersonTypeBuilder;
import org.rmt2.util.addressbook.ZipcodeTypeBuilder;
import org.rmt2.util.projecttracker.employee.EmployeeTitleTypeBuilder;
import org.rmt2.util.projecttracker.employee.EmployeeTypeBuilder;
import org.rmt2.util.projecttracker.employee.EmployeetypeTypeBuilder;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.AbstractJaxbMessageHandler;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes Employee related messages to the Project Tracker
 * Administration API.
 * 
 * @author roy.terrell
 *
 */
public class EmployeeApiHandler extends
        AbstractJaxbMessageHandler<ProjectProfileRequest, ProjectProfileResponse, List<EmployeeType>> {
    
    // IS-71: Removed class member variable, api, which used to be shared with
    // descendant classes. This will eliminate the possibility of memory leaks
    // caused by dangling API instances. 
    private static final Logger logger = Logger.getLogger(EmployeeApiHandler.class);
    protected ObjectFactory jaxbObjFactory;

    /**
     * @param payload
     */
    public EmployeeApiHandler() {
        super();
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createProjectProfileResponse();
        logger.info(EmployeeApiHandler.class.getName() + " was instantiated successfully");
    }

    
    
    @Override
    protected void validateRequest(ProjectProfileRequest req) throws InvalidDataException {
        try {
            Verifier.verifyNotNull(req);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("Employee message request element is invalid");
        }
    }

    // IS-70: Made common to descendant classes.
    /**
     * Uses employee and personal contact data transfer objects to build a JAXB
     * employee type object which will contain employee and personal contact
     * information (including contact address data).
     * 
     * @param emp
     *            instance of {@link EmployeeDto}
     * @param person
     *            instance of {@link PersonalContactDto}
     * @return {@link EmployeeType}
     */
    protected List<EmployeeType> buildJaxbResults(EmployeeDto emp, PersonalContactDto person) {
        if (emp == null && person == null) {
            return null;
        }
        List<EmployeeType> list = new ArrayList<>();

        ZipcodeType zip = null;
        AddressType addr = null;
        if (person != null) {
            zip = ZipcodeTypeBuilder.Builder.create()
                    .withCity(person.getCity())
                    .withState(person.getState())
                    .withZipcode(person.getZip())
                    .build();

            addr = AddressTypeBuilder.Builder.create()
                    .withAddrId(person.getAddrId())
                    .withAddressLine1(person.getAddr1())
                    .withAddressLine2(person.getAddr2())
                    .withAddressLine3(person.getAddr3())
                    .withAddressLine4(person.getAddr4())
                    .withPhoneHome(person.getPhoneHome())
                    .withPhoneMobile(person.getPhoneCell())
                    .withZipcode(zip)
                    .build();
        }

        PersonType pt = PersonTypeBuilder.Builder.create()
                .withPersonId(emp.getPersonId())
                .withFirstName(emp.getEmployeeFirstname())
                .withLastName(emp.getEmployeeLastname())
                .withEmail(emp.getEmployeeEmail())
                .withSocialSecurityNumber(emp.getSsn())
                .withAddress(addr)
                .build();

        EmployeetypeType empType = EmployeetypeTypeBuilder.Builder.create()
                .withEmployeeTypeId(emp.getEmployeeTypeId())
                .withDescription(emp.getEmployeeType())
                .build();
        
        EmployeeTitleType empTitle = EmployeeTitleTypeBuilder.Builder.create()
                .withEmployeeTitleId(emp.getEmployeeTitleId())
                .withDescription(emp.getEmployeeTitle())
                .build();
        
        EmployeeType jaxbObj = EmployeeTypeBuilder.Builder.create()
                .withEmployeeId(emp.getEmployeeId())
                .withManagerId(emp.getManagerId())
                .withManagerFlag(emp.getIsManager() == 1 ? true : false)
                .withEmployeeTitleType(empTitle)
                .withEmployeeType(empType)
                .withLoginName(emp.getLoginName())
                .withLoginId(emp.getLoginId())
                .withStartDate(emp.getStartDate())
                .withTermDate(emp.getTerminationDate())
                .withProjectCount(emp.getProjectCount())
                .withContactDetails(pt)
                .build();

        list.add(jaxbObj);
        return list;
    }
    
    @Override
    protected String buildResponse(List<EmployeeType> payload,  MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);    
        }
        
        if (payload != null) {
            ProjectDetailGroup profile = this.jaxbObjFactory.createProjectDetailGroup();
            this.responseObj.setProfile(profile);
            this.responseObj.getProfile().getEmployee().addAll(payload);
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}
