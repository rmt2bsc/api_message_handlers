package org.rmt2.api.handlers.media.mediatype;

import java.util.ArrayList;
import java.util.List;

import org.dto.MediaTypeDto;
import org.dto.adapter.orm.Rmt2MediaDtoFactory;
import org.rmt2.jaxb.AudioVisualCriteriaType;
import org.rmt2.jaxb.MediatypeType;
import org.rmt2.util.media.MediatypeTypeBuilder;

import com.RMT2Base;

/**
 * A factory for creating DTO and JAXB instances for the Media-Type Media
 * message handler project.
 * 
 * @author Roy Terrell.
 * 
 */
public class MediaTypeJaxbDtoFactory extends RMT2Base {

    /**
     * Creates an instance of <i>MediaTypeDto</i> using a valid
     * <i>AudioVisualCriteriaType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link AudioVisualCriteriaType}
     * @return an instance of {@link MediaTypeDto}
     */
    public static final MediaTypeDto createMediaTypeDtoInstance(AudioVisualCriteriaType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        MediaTypeDto dto = Rmt2MediaDtoFactory.getAvMediaTypeInstance(null);
        if (jaxbObj.getMediaTypeId() != null) {
            dto.setUid(jaxbObj.getMediaTypeId());
        }
        dto.setDescription(jaxbObj.getMediaTypeName());
        return dto;
    }

    /**
     * Creates an instance of <i>MediaTypeDto</i> using a valid
     * <i>MediatypeType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link MediatypeType}
     * @return an instance of {@link MediaTypeDto}
     */
    public static final MediaTypeDto createMediaTypeDtoInstance(MediatypeType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        MediaTypeDto dto = Rmt2MediaDtoFactory.getAvMediaTypeInstance(null);
        if (jaxbObj.getMediaTypeId() != null) {
            dto.setUid(jaxbObj.getMediaTypeId());
        }
        dto.setDescription(jaxbObj.getMediaTypeName());
        return dto;
    }

    /**
     * Creates an instance of <i>MediatypeType</i> using a valid
     * <i>MediaTypeDto</i> JAXB object.
     * 
     * @param dto
     *            an instance of {@link MediaTypeDto}
     * @return an instance of {@link MediatypeType}
     */
    public static final MediatypeType createMediaTypeJaxbInstance(MediaTypeDto dto) {
        if (dto == null) {
            return null;
        }
        MediatypeType obj = MediatypeTypeBuilder.Builder.create()
                .withName(dto.getDescritpion())
                .withUID(dto.getUid())
                .build();
        return obj;
    }

    /**
     * Creates a List instance of <i>MediatypeType</i> using a valid List of
     * <i>MediaTypeDto</i> DTO objects.
     * 
     * @param results
     *            List of {@link MediaTypeDto}
     * @return List of {@link MediatypeType} objects
     */
    public static final List<MediatypeType> createMediaTypeJaxbInstance(List<MediaTypeDto> results) {
        List<MediatypeType> list = new ArrayList<>();
        for (MediaTypeDto item : results) {
            MediatypeType jaxbObj = MediatypeTypeBuilder.Builder.create()
                    .withUID(item.getUid())
                    .withName(item.getDescritpion())
                    .build();

            list.add(jaxbObj);
        }
        return list;
    }
}
