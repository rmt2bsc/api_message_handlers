package org.rmt2.api.handlers.admin.role;

import java.util.ArrayList;
import java.util.List;

import org.dto.CategoryDto;
import org.dto.adapter.orm.Rmt2OrmDtoFactory;
import org.rmt2.jaxb.RoleType;
import org.rmt2.util.authentication.RoleTypeBuilder;

import com.RMT2Base;

/**
 * A factory for transferring Role data to and from DTO/JAXB instances for the
 * Authentication API.
 * 
 * @author Roy Terrell.
 * 
 */
public class RoleJaxbDtoFactory extends RMT2Base {

    /**
     * Creates an instance of <i>CategoryDto</i> using a valid <i>RoleType</i>
     * JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link RoleType}
     * @return an instance of {@link CategoryDto}
     */
    public static final CategoryDto createDtoInstance(RoleType jaxbObj) {
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
     * Creates an instance of <i>RoleType</i> using a valid <i>CategoryDto</i>
     * JAXB object.
     * 
     * @param dto
     *            an instance of {@link CategoryDto}
     * @return an instance of {@link RoleType}
     */
    public static final RoleType createJaxbInstance(CategoryDto dto) {
        if (dto == null) {
            return null;
        }
        RoleType obj = RoleTypeBuilder.Builder
                .create()
                .withRoleId(dto.getRoleId())
                .withName(dto.getRoleName())
                .withDescription(dto.getRoleDescription())
                .build();
        return obj;
    }

    /**
     * Creates a List instance of <i>RoleType</i> using a valid List of
     * <i>CategoryDto</i> DTO objects.
     * 
     * @param results
     *            List of {@link CategoryDto}
     * @return List of {@link ApplicationType} objects
     */
    public static final List<RoleType> createJaxbInstance(List<CategoryDto> results) {
        List<RoleType> list = new ArrayList<>();
        for (CategoryDto item : results) {
            list.add(RoleJaxbDtoFactory.createJaxbInstance(item));
        }
        return list;
    }
}
