package org.rmt2.api.handlers.admin;

import org.dto.ApplicationDto;
import org.dto.adapter.orm.Rmt2OrmDtoFactory;
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
}
