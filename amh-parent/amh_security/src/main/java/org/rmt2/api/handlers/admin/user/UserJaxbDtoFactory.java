package org.rmt2.api.handlers.admin.user;

import java.util.ArrayList;
import java.util.List;

import org.dto.CategoryDto;
import org.dto.UserDto;
import org.dto.adapter.orm.Rmt2OrmDtoFactory;
import org.rmt2.jaxb.RecordTrackingType;
import org.rmt2.jaxb.UserAppRoleType;
import org.rmt2.jaxb.UserAppRolesType;
import org.rmt2.jaxb.UserCriteriaType;
import org.rmt2.jaxb.UserGroupType;
import org.rmt2.jaxb.UserType;
import org.rmt2.util.RecordTrackingTypeBuilder;
import org.rmt2.util.authentication.UserGroupTypeBuilder;
import org.rmt2.util.authentication.UserTypeBuilder;

import com.RMT2Base;

/**
 * A factory for transferring User Type data to and from DTO/JAXB instances for
 * the Authentication API.
 * 
 * @author Roy Terrell.
 * 
 */
public class UserJaxbDtoFactory extends RMT2Base {

    /**
     * Creates an instance of <i>UserDto</i> using a valid
     * <i>UserCriteriaType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link UserCriteriaType}
     * @return an instance of {@link UserDto}
     */
    public static final UserDto createDtoInstance(UserCriteriaType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        UserDto dto = Rmt2OrmDtoFactory.getNewUserInstance();
        if (jaxbObj.getLoginId() != null) {
            dto.setLoginUid(jaxbObj.getLoginId());
        }
        dto.setUsername(jaxbObj.getUserName());
        dto.setFirstname(jaxbObj.getFirstName());
        dto.setLastname(jaxbObj.getLastName());
        dto.setSsn(jaxbObj.getSsn());
        dto.setEmail(jaxbObj.getEmail());
        if (jaxbObj.getDob() != null) {
            dto.setBirthDate(jaxbObj.getDob().toGregorianCalendar().getTime());
        }
        if (jaxbObj.getStartDate() != null) {
            dto.setStartDate(jaxbObj.getStartDate().toGregorianCalendar().getTime());
        }
        if (jaxbObj.getTermDate() != null) {
            dto.setTerminationDate(jaxbObj.getTermDate().toGregorianCalendar().getTime());
        }

        if (jaxbObj.getGroupId() != null) {
            dto.setGroupId(jaxbObj.getGroupId());
        }
        dto.setGrpDescription(jaxbObj.getGroupName());
        return dto;
    }

    /**
     * Creates an instance of <i>UserDto</i> using a valid <i>UserGroupType</i>
     * JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link UserType}
     * @return an instance of {@link UserDto}
     */
    public static final UserDto createDtoInstance(UserType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        UserDto dto = Rmt2OrmDtoFactory.getNewUserInstance();
        dto.setLoginUid(jaxbObj.getLoginId());

        if (jaxbObj.getGroupInfo() != null && jaxbObj.getGroupInfo().getGrpId() != null) {
            dto.setGroupId(jaxbObj.getGroupInfo().getGrpId());
        }
        dto.setUsername(jaxbObj.getUserName());
        dto.setFirstname(jaxbObj.getFirstName());
        dto.setLastname(jaxbObj.getLastName());
        dto.setSsn(jaxbObj.getSsn());
        dto.setEmail(jaxbObj.getEmail());
        if (jaxbObj.getDob() != null) {
            dto.setBirthDate(jaxbObj.getDob().toGregorianCalendar().getTime());
        }
        if (jaxbObj.getStartDate() != null) {
            dto.setStartDate(jaxbObj.getStartDate().toGregorianCalendar().getTime());
        }
        if (jaxbObj.getTermDate() != null) {
            dto.setTerminationDate(jaxbObj.getTermDate().toGregorianCalendar().getTime());
        }
        dto.setUserDescription(jaxbObj.getDescription());
        dto.setPassword(jaxbObj.getPassword());

        if (jaxbObj.getTotalLogons() != null) {
            dto.setTotalLogons(jaxbObj.getTotalLogons());
        }
        if (jaxbObj.getActive() != null) {
            dto.setActive(jaxbObj.getActive());
        }
        if (jaxbObj.getLoggedIn() != null) {
            dto.setLoggedIn(jaxbObj.getLoggedIn());
        }

        return dto;
    }

    /**
     * Creates a List of <i>UserDto</i> using a valid List of <i>UserType</i>
     * JAXB objects.
     * 
     * @param jaxbObj
     *            a List of {@link UserType}
     * @return an List of {@link UserDto}
     */
    public static final List<UserDto> createDtoInstance(List<UserType> jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        List<UserDto> list = new ArrayList<>();
        for (UserType item : jaxbObj) {
            list.add(UserJaxbDtoFactory.createDtoInstance(item));
        }
        return list;
    }

    /**
     * Creates an instance of <i>UserType</i> using a valid <i>UserDto</i> JAXB
     * object.
     * 
     * @param dto
     *            an instance of {@link UserDto}
     * @return an instance of {@link UserType}
     */
    public static final UserType createJaxbInstance(UserDto dto) {
        if (dto == null) {
            return null;
        }

        RecordTrackingType rtt = RecordTrackingTypeBuilder.Builder.create()
                .withDateCreated(dto.getDateCreated())
                .withDateUpdate(dto.getDateUpdated())
                .withUserId(dto.getUpdateUserId())
                .withIpCreated(dto.getIpCreated())
                .withIpUpdate(dto.getIpUpdated())
                .build();

        UserGroupType ugt = UserGroupTypeBuilder.Builder.create()
                .withGroupId(dto.getGroupId())
                .withDescription(dto.getGrpDescription())
                .build();

        UserType obj = UserTypeBuilder.Builder.create()
                .withLoginId(dto.getLoginUid())
                .withGroupInfo(ugt)
                .withUsername(dto.getUsername())
                .withFirstname(dto.getFirstname())
                .withLastname(dto.getLastname())
                .withBirthDate(dto.getBirthDate())
                .withSsn(dto.getSsn())
                .withStartDate(dto.getStartDate())
                .withTermDate(dto.getTerminationDate())
                .withDescription(dto.getUserDescription())
                .withPassword(dto.getPassword())
                .withTotalLogins(dto.getTotalLogons())
                .withEmail(dto.getEmail())
                .withActiveFlag(dto.getActive() == 1 ? true : false)
                .withLoggedInFlag(dto.getLoggedIn() == 1 ? true : false)
                .withRecordTrackingType(rtt)
                .build();
        return obj;
    }

    /**
     * Creates a List of UserType using a valid List of UserDto DTO objects
     * containing the user group data.
     * 
     * @param results
     *            List of {@link UserDto}
     * @return a List of {@link UserType}
     */
    public static final List<UserType> createJaxbInstance(List<UserDto> results) {
        List<UserType> list = new ArrayList<>();
        for (UserDto item : results) {
            list.add(UserJaxbDtoFactory.createJaxbInstance(item));
        }
        return list;
    }

    /**
     * 
     * @param jaxbObj
     * @return
     */
    public static final List<String> createAppRoleCodeList(UserAppRolesType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        List<CategoryDto> list = UserJaxbDtoFactory.createDtoInstance(jaxbObj);
        List<String> codeList = new ArrayList<>();
        for (CategoryDto item : list) {
            codeList.add(item.getAppRoleCode());
        }
        return codeList;
    }

    /**
     * 
     * @param jaxbObj
     * @return
     */
    public static final List<CategoryDto> createDtoInstance(UserAppRolesType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        List<CategoryDto> list = new ArrayList<>();
        for (UserAppRoleType item : jaxbObj.getUserAppRole()) {
            CategoryDto dto = Rmt2OrmDtoFactory.getAppRoleDtoInstance(null);
            // Get app-role info
            dto.setUserAppRoleId(item.getUserAppRoleId());
            dto.setAppRoleId(item.getAppRoleInfo().getAppRoleId());
            list.add(dto);
        }
        return list;
    }

}
