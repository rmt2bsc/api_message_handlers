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
        if (jaxbObj.getTypeInfo() != null) {
            dto.setTypeId(jaxbObj.getTypeInfo().getUid());
            dto.setTypeDescription(jaxbObj.getTypeInfo().getDescription());
        }
        if (jaxbObj.getSubtypeInfo() != null) {
            dto.setSubTypeId(jaxbObj.getSubtypeInfo().getUid());
            dto.setSubTypeName(jaxbObj.getSubtypeInfo().getCode());
            dto.setSubTypeDescription(jaxbObj.getSubtypeInfo().getDescription());
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
        if (jaxbObj.getTypeInfo() != null) {
            dto.setTypeId(jaxbObj.getTypeInfo().getUid());
            dto.setTypeDescription(jaxbObj.getTypeInfo().getDescription());
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

        ResourcetypeType rtt = ResourcetypeTypeBuilder.Builder.create()
                .withTypeId(dto.getTypeId())
                .withDescription(dto.getTypeDescription())
                .build();

        ResourcesubtypeType rst = ResourceSubtypeTypeBuilder.Builder.create()
                .withSubTypeId(dto.getSubTypeId())
                .withName(dto.getSubTypeName())
                .withDescription(dto.getSubTypeDescription())
                .build();

        ResourceType obj = ResourceTypeBuilder.Builder.create()
                .withResourceId(dto.getUid())
                .withName(dto.getName())
                .withDescription(dto.getDescription())
                .withType(rtt)
                .withSubType(rst)
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

        ResourcetypeType rtt = ResourcetypeTypeBuilder.Builder.create()
                .withTypeId(dto.getTypeId())
                .withDescription(dto.getTypeDescription())
                .build();

        ResourcesubtypeType obj = ResourceSubtypeTypeBuilder.Builder.create()
                .withSubTypeId(dto.getSubTypeId())
                .withType(rtt)
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
        ResourcetypeType rt = null;
        ResourcesubtypeType rst = null;
        if (dto instanceof WebServiceDto) {
            r = ResourceJaxbDtoFactory.createJaxbResourceInstance((WebServiceDto) dto);
        }
        if (dto.getTypeId() > 0 && dto.getUid() == 0 && dto.getSubTypeId() == 0) {
            rt = ResourceJaxbDtoFactory.createJaxbResourceTypeInstance(dto);
        }
        if (dto.getTypeId() > 0 && dto.getSubTypeId() > 0 && dto.getUid() == 0) {
            rst = ResourceJaxbDtoFactory.createJaxbResourceSubTypeInstance(dto);
        }
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
        List<ResourceType> rList = null;
        List<ResourcetypeType> rtList = null;
        List<ResourcesubtypeType> rstList = null;
        for (ResourceDto item : results) {
            if (item instanceof WebServiceDto) {
                if (rList == null) {
                    rList = new ArrayList<>();
                }
                rList.add(ResourceJaxbDtoFactory.createJaxbResourceInstance((WebServiceDto) item));
            }
            if (item.getTypeId() > 0 && item.getUid() == 0 && item.getSubTypeId() == 0) {
                if (rtList == null) {
                    rtList = new ArrayList<>();
                }
                rtList.add(ResourceJaxbDtoFactory.createJaxbResourceTypeInstance(item));
            }
            if (item.getTypeId() > 0 && item.getSubTypeId() > 0 && item.getUid() == 0) {
                if (rstList == null) {
                    rstList = new ArrayList<>();
                }
                rstList.add(ResourceJaxbDtoFactory.createJaxbResourceSubTypeInstance(item));
            }
        }
        ResourcesInfoType obj = ResourcesInfoTypeBuilder.Builder.create()
                .withResources(rList)
                .withResourceTypes(rtList)
                .withResourceSubTypes(rstList)
                .build();
        return obj;
    }
}
