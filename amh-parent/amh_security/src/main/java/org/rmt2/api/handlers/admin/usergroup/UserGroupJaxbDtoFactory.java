package org.rmt2.api.handlers.admin.usergroup;

import java.util.ArrayList;
import java.util.List;

import org.dto.UserDto;
import org.dto.adapter.orm.Rmt2OrmDtoFactory;
import org.rmt2.jaxb.UserCriteriaType;
import org.rmt2.jaxb.UserGroupType;
import org.rmt2.util.authentication.UserGroupTypeBuilder;

import com.RMT2Base;

/**
 * A factory for transferring User Group Type data to and from DTO/JAXB
 * instances for the Authentication API.
 * 
 * @author Roy Terrell.
 * 
 */
public class UserGroupJaxbDtoFactory extends RMT2Base {

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
        UserDto dto = Rmt2OrmDtoFactory.getNewUserGroupInstance();
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
     *            an instance of {@link UserGroupType}
     * @return an instance of {@link UserDto}
     */
    public static final UserDto createDtoInstance(UserGroupType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        UserDto dto = Rmt2OrmDtoFactory.getNewGroupInstance();
        if (jaxbObj.getGrpId() != null) {
            dto.setGroupId(jaxbObj.getGrpId());
        }
        dto.setGrpDescription(jaxbObj.getDescription());
        return dto;
    }

    /**
     * Creates a List of <i>UserDto</i> using a valid List of
     * <i>UserGroupType</i> JAXB objects.
     * 
     * @param jaxbObj
     *            a List of {@link UserGroupType}
     * @return an List of {@link UserDto}
     */
    public static final List<UserDto> createDtoInstance(List<UserGroupType> jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        List<UserDto> list = new ArrayList<>();
        for (UserGroupType item : jaxbObj) {
            list.add(UserGroupJaxbDtoFactory.createDtoInstance(item));
        }
        return list;
    }

    /**
     * Creates an instance of <i>UserGroupType</i> using a valid <i>UserDto</i>
     * JAXB object.
     * 
     * @param dto
     *            an instance of {@link UserDto}
     * @return an instance of {@link UserGroupType}
     */
    public static final UserGroupType createJaxbInstance(UserDto dto) {
        if (dto == null) {
            return null;
        }
        UserGroupType obj = UserGroupTypeBuilder.Builder.create()
                .withGroupId(dto.getGroupId())
                .withDescription(dto.getGrpDescription())
                .build();
        return obj;
    }

    /**
     * Creates a List of UserGroupType using a valid List of UserDto DTO objects
     * containing the user group data.
     * 
     * @param results
     *            List of {@link UserDto}
     * @return a List of {@link ResourcesInfoType}
     */
    public static final List<UserGroupType> createJaxbResourcesInfoInstance(List<UserDto> results) {
        List<UserGroupType> list = new ArrayList<>();
        for (UserDto item : results) {
            list.add(UserGroupJaxbDtoFactory.createJaxbInstance(item));
        }
        return list;
    }
}
