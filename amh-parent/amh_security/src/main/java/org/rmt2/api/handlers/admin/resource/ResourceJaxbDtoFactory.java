package org.rmt2.api.handlers.admin.resource;

import java.util.ArrayList;
import java.util.List;

import org.dto.ResourceDto;
import org.dto.WebServiceDto;
import org.dto.adapter.orm.Rmt2OrmDtoFactory;
import org.rmt2.jaxb.ResourceType;
import org.rmt2.jaxb.ResourcesInfoType;
import org.rmt2.jaxb.ResourcesubtypeType;
import org.rmt2.jaxb.ResourcetypeType;
import org.rmt2.util.authentication.ResourceSubtypeTypeBuilder;
import org.rmt2.util.authentication.ResourceTypeBuilder;
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
public class ResourceJaxbDtoFactory extends RMT2Base {

    /**
     * Creates an instance of <i>ResourceDto</i> using a valid
     * <i>ResourceType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link ResourceType}
     * @return an instance of {@link ResourceDto}
     */
    public static final WebServiceDto createDtoInstance(ResourceType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        WebServiceDto dto = Rmt2OrmDtoFactory.getNewResourceInstance();
        if (jaxbObj.getUid() != null) {
            dto.setUid(jaxbObj.getUid());
        }
        if (jaxbObj.getTypeId() != null) {
            dto.setTypeId(jaxbObj.getTypeId());
        }
        if (jaxbObj.getSubtypeId() != null) {
            dto.setSubTypeId(jaxbObj.getSubtypeId());
        }
        dto.setName(jaxbObj.getCode());
        dto.setDescription(jaxbObj.getDescription());
        dto.setSecured(jaxbObj.getSecured() == null ? -1 : jaxbObj.getSecured());
        dto.setRequestUrl(jaxbObj.getUrl());
        dto.setHost(jaxbObj.getHost());

        return dto;
    }

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
     * Creates an instance of <i>ResourceType</i> using a valid
     * <i>WebServiceDto</i> JAXB object.
     * 
     * @param dto
     *            an instance of {@link WebServiceDto}
     * @return an instance of {@link ResourceType}
     */
    public static final ResourceType createJaxbResourceInstance(WebServiceDto dto) {
        if (dto == null) {
            return null;
        }
        ResourceType obj = ResourceTypeBuilder.Builder.create()
                .withResourceId(dto.getUid())
                .withName(dto.getName())
                .withDescription(dto.getDescription())
                .withTypeId(dto.getTypeId())
                .withSubTypeId(dto.getSubTypeId())
                .withSecuredFlag(dto.getSecured())
                .withUrl(dto.getRequestUrl())
                .withHost(dto.getHost())
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

        ResourceType r = null;
        if (dto instanceof WebServiceDto) {
            r = ResourceJaxbDtoFactory.createJaxbResourceInstance((WebServiceDto) dto);
        }

        ResourcetypeType rt = ResourceJaxbDtoFactory.createJaxbResourceTypeInstance(dto);
        ResourcesubtypeType rst = ResourceJaxbDtoFactory.createJaxbResourceSubTypeInstance(dto);
        ResourcesInfoType obj = ResourcesInfoTypeBuilder.Builder.create()
                .addResource(r)
                .addResourceType(rt)
                .addResourceSubType(rst)
                .build();
        return obj;
    }

    /**
     * Creates an instance of ResourcesInfoType using a valid List of
     * ResourceDto DTO objects which could be in the form of resources, resource
     * types, or resource sub types.
     * 
     * @param results
     *            List of {@link ResourceDto}
     * @return an instance of {@link ResourcesInfoType}
     */
    public static final ResourcesInfoType createJaxbResourcesInfoInstance(List<ResourceDto> results) {
        List<ResourceType> rList = new ArrayList<>();
        List<ResourcetypeType> rtList = new ArrayList<>();
        List<ResourcesubtypeType> rstList = new ArrayList<>();
        for (ResourceDto item : results) {
            if (item instanceof WebServiceDto) {
                rList.add(ResourceJaxbDtoFactory.createJaxbResourceInstance((WebServiceDto) item));
            }
            rtList.add(ResourceJaxbDtoFactory.createJaxbResourceTypeInstance(item));
            rstList.add(ResourceJaxbDtoFactory.createJaxbResourceSubTypeInstance(item));
        }
        ResourcesInfoType obj = ResourcesInfoTypeBuilder.Builder.create()
                .withResources(rList)
                .withResourceTypes(rtList)
                .withResourceSubTypes(rstList)
                .build();
        return obj;
    }
}
