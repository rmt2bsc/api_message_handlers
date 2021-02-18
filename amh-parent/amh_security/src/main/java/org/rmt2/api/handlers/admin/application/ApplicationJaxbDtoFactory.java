package org.rmt2.api.handlers.admin.application;

import java.util.ArrayList;
import java.util.List;

import org.dto.ApplicationDto;
import org.dto.adapter.orm.Rmt2OrmDtoFactory;
import org.rmt2.jaxb.ApplicationType;
import org.rmt2.util.authentication.ApplicationTypeBuilder;

import com.RMT2Base;
import com.api.util.RMT2Money;

/**
 * A factory for transferring Application data to and from DTO/JAXB instances
 * for the Authentication API.
 * 
 * @author Roy Terrell.
 * 
 */
public class ApplicationJaxbDtoFactory extends RMT2Base {

    /**
     * Creates an instance of <i>ApplicationDto</i> using a valid
     * <i>ApplicationType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link ApplicationType}
     * @return an instance of {@link ApplicationDto}
     */
    public static final ApplicationDto createDtoInstance(ApplicationType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        ApplicationDto dto = Rmt2OrmDtoFactory.getAppDtoInstance(null);
        dto.setApplicationId(jaxbObj.getAppId());
        dto.setAppDescription(jaxbObj.getDescription());
        dto.setAppCode(jaxbObj.getAppCode());
        dto.setAppName(jaxbObj.getAppCode());
        if (jaxbObj.getActive() != null) {
            dto.setActive(jaxbObj.getActive().toString());
        }

        return dto;
    }

    /**
     * Creates an instance of <i>ApplicationType</i> using a valid
     * <i>ApplicationDto</i> JAXB object.
     * 
     * @param dto
     *            an instance of {@link ApplicationDto}
     * @return an instance of {@link ApplicationType}
     */
    public static final ApplicationType createJaxbInstance(ApplicationDto dto) {
        if (dto == null) {
            return null;
        }
        ApplicationType obj = ApplicationTypeBuilder.Builder
                .create()
                .withAppId(dto.getApplicationId())
                .withName(dto.getAppName())
                .withDescription(dto.getAppDescription())
                .withActive(
                        dto.getActive() != null && RMT2Money.isNumeric(dto.getActive()) ? Integer.valueOf(dto.getActive()) : 1)
                .build();
        return obj;
    }

    /**
     * Creates a List instance of <i>ApplicationType</i> using a valid List of
     * <i>ApplicationDto</i> DTO objects.
     * 
     * @param results
     *            List of {@link ApplicationDto}
     * @return List of {@link ApplicationType} objects
     */
    public static final List<ApplicationType> createJaxbInstance(List<ApplicationDto> results) {
        List<ApplicationType> list = new ArrayList<>();
        for (ApplicationDto item : results) {
            list.add(ApplicationJaxbDtoFactory.createJaxbInstance(item));
        }
        return list;
    }
}
