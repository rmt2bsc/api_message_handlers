package org.rmt2.api.handlers.admin;

import org.dto.ApplicationDto;
import org.dto.CategoryDto;
import org.dto.ResourceDto;
import org.dto.adapter.orm.Rmt2OrmDtoFactory;
import org.rmt2.jaxb.ResourceCriteriaType;
import org.rmt2.jaxb.UserAppRolesCriteriaType;

import com.RMT2Base;

/**
 * A factory for transferring admin criteria data to and from DTO/JAXB instances
 * as it pertains to the Authentication API.
 * 
 * @author Roy Terrell.
 * 
 */
public class AuthenticationAdminJaxbDtoFactory extends RMT2Base {

    /**
     * Creates an instance of <i>ApplicationDto</i> using a valid
     * <i>UserAppRolesCriteriaType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link UserAppRolesCriteriaType}
     * @return an instance of {@link ApplicationDto}
     */
    public static final ApplicationDto createAppCriteriaDtoInstance(UserAppRolesCriteriaType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        ApplicationDto dto = Rmt2OrmDtoFactory.getAppDtoInstance(null);
        if (jaxbObj.getAppId() != null) {
            dto.setApplicationId(jaxbObj.getAppId());
        }
        dto.setAppDescription(jaxbObj.getAppDescription());
        dto.setAppCode(jaxbObj.getAppCode());
        dto.setAppName(jaxbObj.getAppCode());
        if (jaxbObj.getAppActive() != null) {
            dto.setActive(jaxbObj.getAppActive().toString());
        }

        return dto;
    }

    /**
     * Creates an instance of <i>CategoryDto</i> using a valid
     * <i>UserAppRolesCriteriaType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link UserAppRolesCriteriaType}
     * @return an instance of {@link CategoryDto}
     */
    public static final CategoryDto createRoleCriteriaDtoInstance(UserAppRolesCriteriaType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        CategoryDto dto = Rmt2OrmDtoFactory.getRoleDtoInstance(null);
        dto.setRoleId(jaxbObj.getRoleId());
        dto.setRoleDescription(jaxbObj.getRoleDescription());
        dto.setRoleName(jaxbObj.getRoleName());
        return dto;
    }

    /**
     * Creates an instance of <i>CategoryDto</i> using a valid
     * <i>UserAppRolesCriteriaType</i> JAXB object to query App Roles .
     * 
     * @param jaxbObj
     *            an instance of {@link UserAppRolesCriteriaType}
     * @return an instance of {@link CategoryDto}
     */
    public static final CategoryDto createAppRoleCriteriaDtoInstance(UserAppRolesCriteriaType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        CategoryDto dto = Rmt2OrmDtoFactory.getAppRoleDtoInstance(null);
        // Get app-role info
        dto.setAppRoleId(jaxbObj.getAppRoleId());
        dto.setAppRoleCode(jaxbObj.getAppRoleCode());
        dto.setAppRoleName(jaxbObj.getAppRoleName());
        dto.setAppRoleDescription(jaxbObj.getAppRoleDescription());

        // Get application info
        if (jaxbObj.getAppId() != null) {
            dto.setApplicationId(jaxbObj.getAppId());
        }
        dto.setAppDescription(jaxbObj.getAppDescription());
        dto.setAppName(jaxbObj.getAppCode());
        dto.setAppName(jaxbObj.getAppCode());

        // Get role info
        dto.setRoleId(jaxbObj.getRoleId());
        dto.setRoleDescription(jaxbObj.getRoleDescription());
        dto.setRoleName(jaxbObj.getRoleName());
        return dto;
    }

    /**
     * Creates an instance of <i>ResourceDto</i> using a valid
     * <i>ResourceCriteriaType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link ResourceCriteriaType}
     * @return an instance of {@link ResourceDto}
     */
    public static final ResourceDto createResourceCriteriaDtoInstance(ResourceCriteriaType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        ResourceDto dto = Rmt2OrmDtoFactory.getNewResourceInstance();

        // Resource related criteria
        if (jaxbObj.getRsrcId() != null) {
            dto.setUid(jaxbObj.getRsrcId());
        }
        dto.setName(jaxbObj.getRsrcName());
        dto.setDescription(jaxbObj.getRsrcDescription());

        if (jaxbObj.getSecured() == null) {
            // Default to true
            dto.setSecured(null);
        }
        else {
            dto.setSecured(jaxbObj.getSecured() == null ? -1 : jaxbObj.getSecured());
        }

        // Resource Type criteria
        if (jaxbObj.getRsrcTypeId() != null) {
            dto.setTypeId(jaxbObj.getRsrcTypeId());
        }
        dto.setTypeDescription(jaxbObj.getRsrcTypeName());

        // Resource sub type criteria
        if (jaxbObj.getRsrcSubtypeId() != null) {
            dto.setSubTypeId(jaxbObj.getRsrcSubtypeId());
        }
        dto.setSubTypeName(jaxbObj.getRsrcSubtypeName());
        dto.setSubTypeDescription(jaxbObj.getRsrcSubtypeDescription());
        return dto;
    }

    /**
     * Creates an instance of <i>ResourceDto</i> using a valid
     * <i>ResourceCriteriaType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link ResourceCriteriaType}
     * @return an instance of {@link ResourceDto}
     */
    public static final ResourceDto createResourceTypeCriteriaDtoInstance(ResourceCriteriaType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        ResourceDto dto = Rmt2OrmDtoFactory.getNewResourceTypeInstance();

        // Resource type related criteria
        if (jaxbObj.getRsrcTypeId() != null) {
            dto.setTypeId(jaxbObj.getRsrcTypeId());
        }
        dto.setTypeDescription(jaxbObj.getRsrcTypeName());
        return dto;
    }

    /**
     * Creates an instance of <i>ResourceDto</i> using a valid
     * <i>ResourceCriteriaType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link ResourceCriteriaType}
     * @return an instance of {@link ResourceDto}
     */
    public static final ResourceDto createResourceSubTypeCriteriaDtoInstance(ResourceCriteriaType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        ResourceDto dto = Rmt2OrmDtoFactory.getNewResourceSubTypeInstance();
        // Resource sub type related criteria
        if (jaxbObj.getRsrcSubtypeId() != null) {
            dto.setSubTypeId(jaxbObj.getRsrcSubtypeId());
        }
        if (jaxbObj.getRsrcTypeId() != null) {
            dto.setTypeId(jaxbObj.getRsrcTypeId());
        }
        dto.setSubTypeName(jaxbObj.getRsrcSubtypeName());
        dto.setSubTypeDescription(jaxbObj.getRsrcSubtypeDescription());
        return dto;
    }
}
