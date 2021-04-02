package org.rmt2.api.handlers.admin.approle;

import java.util.ArrayList;
import java.util.List;

import org.dto.CategoryDto;
import org.dto.adapter.orm.Rmt2OrmDtoFactory;
import org.rmt2.jaxb.AppRoleType;
import org.rmt2.jaxb.ApplicationType;
import org.rmt2.jaxb.RoleType;
import org.rmt2.util.authentication.AppRoleTypeBuilder;
import org.rmt2.util.authentication.ApplicationTypeBuilder;
import org.rmt2.util.authentication.RoleTypeBuilder;

import com.RMT2Base;

/**
 * A factory for transferring AppRole Type data to and from DTO/JAXB instances
 * for the Authentication API.
 * 
 * @author Roy Terrell.
 * 
 */
public class AppRoleJaxbDtoFactory extends RMT2Base {

    /**
     * Creates an instance of <i>CategoryDto</i> using a valid
     * <i>AppRoleType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link AppRoleType}
     * @return an instance of {@link CategoryDto}
     */
    public static final CategoryDto createDtoInstance(AppRoleType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        CategoryDto dto = Rmt2OrmDtoFactory.getAppRoleDtoInstance(null);
        // Get app-role info
        dto.setAppRoleId(jaxbObj.getAppRoleId());
        dto.setAppRoleCode(jaxbObj.getAppRoleCode());
        dto.setAppRoleDescription(jaxbObj.getAppRoleDesc());

        // Get application info
        if (jaxbObj.getAppInfo() != null) {
            dto.setApplicationId(jaxbObj.getAppInfo().getAppId());
            dto.setAppDescription(jaxbObj.getAppInfo().getDescription());
            dto.setAppName(jaxbObj.getAppInfo().getAppCode());
        }

        // Get role info
        if (jaxbObj.getRoleInfo() != null) {
            dto.setRoleId(jaxbObj.getRoleInfo().getRoleId());
            dto.setRoleDescription(jaxbObj.getRoleInfo().getRoleDescription());
            dto.setRoleName(jaxbObj.getRoleInfo().getRoleName());
        }

        return dto;
    }

    /**
     * Creates a List of <i>CategoryDto</i> using a valid List of
     * <i>AppRoleType</i> JAXB objects.
     * 
     * @param jaxbObj
     *            a List of {@link AppRoleType}
     * @return an List of {@link CategoryDto}
     */
    public static final List<CategoryDto> createDtoInstance(List<AppRoleType> jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        List<CategoryDto> list = new ArrayList<>();
        for (AppRoleType item : jaxbObj) {
            list.add(AppRoleJaxbDtoFactory.createDtoInstance(item));
        }
        return list;
    }

    /**
     * Creates an instance of <i>AppRoleType</i> using a valid
     * <i>CategoryDto</i> JAXB object.
     * 
     * @param dto
     *            an instance of {@link CategoryDto}
     * @return an instance of {@link AppRoleType}
     */
    public static final AppRoleType createJaxbInstance(CategoryDto dto) {
        if (dto == null) {
            return null;
        }

        ApplicationType at = ApplicationTypeBuilder.Builder
                .create()
                .withAppId(dto.getApplicationId())
                .withName(dto.getAppName())
                .withDescription(dto.getAppDescription())
                .build();

        RoleType rt = RoleTypeBuilder.Builder
                .create()
                .withRoleId(dto.getRoleId())
                .withName(dto.getRoleName())
                .withDescription(dto.getRoleDescription())
                .build();

        AppRoleType obj = AppRoleTypeBuilder.Builder.create()
                .withAppRoleId(dto.getAppRoleId())
                .withCode(dto.getAppRoleCode())
                .withName(dto.getAppRoleName())
                .withDescription(dto.getAppRoleDescription())
                .withApplication(at)
                .withRole(rt)
                .build();
        return obj;
    }

    /**
     * Creates a List of AppRoleType using a valid List of CategoryDto DTO
     * objects containing the user group data.
     * 
     * @param results
     *            List of {@link CategoryDto}
     * @return a List of {@link AppRoleType}
     */
    public static final List<AppRoleType> createJaxbInstance(List<CategoryDto> results) {
        List<AppRoleType> list = new ArrayList<>();
        for (CategoryDto item : results) {
            list.add(AppRoleJaxbDtoFactory.createJaxbInstance(item));
        }
        return list;
    }
}
