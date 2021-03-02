package org.rmt2.api.handlers.admin.resource.type;

import java.util.ArrayList;
import java.util.List;

import org.dto.ResourceDto;
import org.dto.adapter.orm.Rmt2OrmDtoFactory;
import org.rmt2.jaxb.ResourcesInfoType;
import org.rmt2.jaxb.ResourcesubtypeType;
import org.rmt2.jaxb.ResourcetypeType;
import org.rmt2.util.authentication.ResourceSubtypeTypeBuilder;
import org.rmt2.util.authentication.ResourcesInfoTypeBuilder;
import org.rmt2.util.authentication.ResourcetypeTypeBuilder;

import com.RMT2Base;

/**
 * A factory for transferring Resource Type data to and from DTO/JAXB instances
 * for the Authentication API.
 * 
 * @author Roy Terrell.
 * 
 */
public class ResourceTypeJaxbDtoFactory extends RMT2Base {

    /**
     * Creates an instance of <i>ResourceDto</i> using a valid
     * <i>ResourcetypeType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link ResourcetypeType}
     * @return an instance of {@link ResourceDto}
     */
    public static final ResourceDto createDtoInstance(ResourcetypeType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        ResourceDto dto = Rmt2OrmDtoFactory.getNewResourceTypeInstance();
        dto.setTypeId(jaxbObj.getUid() == null ? 0 : jaxbObj.getUid());
        dto.setTypeDescription(jaxbObj.getDescription());
        return dto;
    }

    /**
     * Creates an instance of <i>ResourceDto</i> using a valid
     * <i>ResourcesubtypeType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link ResourcesubtypeType}
     * @return an instance of {@link ResourceDto}
     */
    public static final ResourceDto createDtoInstance(ResourcesubtypeType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        ResourceDto dto = Rmt2OrmDtoFactory.getNewResourceSubTypeInstance();
        if (jaxbObj.getUid() != null) {
            dto.setSubTypeId(jaxbObj.getUid());
        }
        if (jaxbObj.getResourceTypeId() != null) {
            dto.setTypeId(jaxbObj.getResourceTypeId());
        }
        dto.setSubTypeName(jaxbObj.getCode());
        dto.setSubTypeDescription(jaxbObj.getDescription());
        return dto;
    }

    /**
     * Creates an instance of <i>ResourcetypeType</i> using a valid
     * <i>ResourceDto</i> JAXB object.
     * 
     * @param dto
     *            an instance of {@link ResourceDto}
     * @return an instance of {@link ResourcetypeType}
     */
    public static final ResourcetypeType createJaxbResourceTypeInstance(ResourceDto dto) {
        if (dto == null) {
            return null;
        }
        ResourcetypeType obj = ResourcetypeTypeBuilder.Builder.create()
                .withTypeId(dto.getTypeId())
                .withDescription(dto.getTypeDescription())
                .build();
        return obj;
    }

    /**
     * Creates an instance of <i>ResourcesubtypeType</i> using a valid
     * <i>ResourceDto</i> JAXB object.
     * 
     * @param dto
     *            an instance of {@link ResourceDto}
     * @return an instance of {@link ResourcesubtypeType}
     */
    public static final ResourcesubtypeType createJaxbResourceSubTypeInstance(ResourceDto dto) {
        if (dto == null) {
            return null;
        }
        ResourcesubtypeType obj = ResourceSubtypeTypeBuilder.Builder.create()
                .withSubTypeId(dto.getSubTypeId())
                .withTypeId(dto.getTypeId())
                .withName(dto.getSubTypeName())
                .withDescription(dto.getSubTypeDescription())
                .build();
        return obj;
    }

    /**
     * Creates an instance of ResourcesInfoType using a valid ResourceDto JAXB
     * object.
     * 
     * @param dto
     *            an instance of {@link ResourceDto}
     * @return an instance of {@link ResourcesInfoType}
     */
    public static final ResourcesInfoType createJaxbResourcesInfoInstance(ResourceDto dto) {
        if (dto == null) {
            return null;
        }

        ResourcetypeType rt = ResourceTypeJaxbDtoFactory.createJaxbResourceTypeInstance(dto);
        ResourcesubtypeType rst = ResourceTypeJaxbDtoFactory.createJaxbResourceSubTypeInstance(dto);
        ResourcesInfoType obj = ResourcesInfoTypeBuilder.Builder.create()
                .addResourceType(rt)
                .addResourceSubType(rst)
                .build();
        return obj;
    }

    /**
     * Creates an instance of ResourcesInfoType using a valid List of
     * ResourceDto DTO objects.
     * 
     * @param results
     *            List of {@link ResourceDto}
     * @return an instance of {@link ResourcesInfoType}
     */
    public static final ResourcesInfoType createJaxbResourcesInfoInstance(List<ResourceDto> results) {
        List<ResourcetypeType> rtList = new ArrayList<>();
        List<ResourcesubtypeType> rstList = new ArrayList<>();
        for (ResourceDto item : results) {
            rtList.add(ResourceTypeJaxbDtoFactory.createJaxbResourceTypeInstance(item));
            rstList.add(ResourceTypeJaxbDtoFactory.createJaxbResourceSubTypeInstance(item));
        }
        ResourcesInfoType obj = ResourcesInfoTypeBuilder.Builder.create()
                .withResourceTypes(rtList)
                .withResourceSubTypes(rstList)
                .build();
        return obj;
    }
}
