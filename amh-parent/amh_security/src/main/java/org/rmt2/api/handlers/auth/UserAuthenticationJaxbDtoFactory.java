package org.rmt2.api.handlers.auth;

import org.dto.UserDto;
import org.dto.adapter.orm.Rmt2OrmDtoFactory;
import org.rmt2.jaxb.ApplicationAccessType;

import com.RMT2Base;

/**
 * A factory for transferring User Application Authentication data to and from DTO/JAXB instances for
 * the Authentication API.
 * 
 * @author Roy Terrell.
 * 
 */
public class UserAuthenticationJaxbDtoFactory extends RMT2Base {

    /**
     * Creates an instance of <i>UserDto</i> using a valid
     * <i>ApplicationAccessType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link ApplicationAccessType}
     * @return an instance of {@link UserDto}
     */
    public static final UserDto createDtoInstance(ApplicationAccessType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        UserDto dto = Rmt2OrmDtoFactory.getNewUserInstance();
        dto.setUsername(jaxbObj.getUserInfo().getUserName());
        dto.setPassword(jaxbObj.getUserInfo().getPassword());
        return dto;
    }

  
}
