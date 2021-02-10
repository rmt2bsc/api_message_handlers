package org.rmt2.api.handlers.maint;

import org.dao.mapping.orm.rmt2.Content;
import org.dto.ContentDto;
import org.dto.adapter.orm.Rmt2MediaDtoFactory;
import org.rmt2.jaxb.ContentCriteriaType;
import org.rmt2.jaxb.MimeContentType;
import org.rmt2.jaxb.MimetypeType;
import org.rmt2.util.media.MimeContentTypeBuilder;
import org.rmt2.util.media.MimetypeTypeBuilder;

import com.RMT2Base;

/**
 * A factory for creating DTO and JAXB instances for media content related
 * message handlers.
 * 
 * @author Roy Terrell.
 * 
 */
public class MediaContentJaxbDtoFactory extends RMT2Base {

    /**
     * Creates an instance of <i>ContentDto</i> using a valid
     * <i>ContentCriteriaType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link ContentCriteriaType}
     * @return an instance of {@link ContentDto}
     */
    public static final ContentDto createMediaContentDtoInstance(ContentCriteriaType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        ContentDto dto = Rmt2MediaDtoFactory.getContentInstance((Content) null);
        dto.setAppCode(jaxbObj.getAppCode());
        dto.setModuleCode(jaxbObj.getModuleCode());
        if (jaxbObj.getContentId() != null) {
            dto.setContentId(jaxbObj.getContentId());
        }
        return dto;
    }


    /**
     * Creates an instance of <i>ContentDto</i> using a valid
     * <i>MimeContentType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link MimeContentType}
     * @return an instance of {@link ContentDto}
     */
    public static final ContentDto createMediaContentDtoInstance(MimeContentType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        ContentDto dto = Rmt2MediaDtoFactory.getContentInstance((Content) null);
        dto.setAppCode(jaxbObj.getAppCode());
        dto.setModuleCode(jaxbObj.getModuleCode());
        if (jaxbObj.getContentId() != null) {
            dto.setContentId(jaxbObj.getContentId());
        }
        dto.setFilename(jaxbObj.getFilename());
        dto.setFilepath(jaxbObj.getFilepath());
        dto.setImageData(jaxbObj.getBinaryData());
        dto.setTextData(jaxbObj.getTextData());
        return dto;
    }

    /**
     * Creates an instance of <i>MimeContentType</i> using a valid
     * <i>ContentDto</i> JAXB object.
     * 
     * @param dto
     *            an instance of {@link ContentDto}
     * @return an instance of {@link MimeContentType}
     */
    public static final MimeContentType createContentJaxbInstance(ContentDto dto) {
        if (dto == null) {
            return null;
        }
        
        MimetypeType mt = MimetypeTypeBuilder.Builder.create()
                .withUID(dto.getMimeTypeId())
                .build();
        
        MimeContentType obj = MimeContentTypeBuilder.Builder.create()
               .withApplicationCode(dto.getAppCode())
               .withModuleCode(dto.getModuleCode())
               .withContentId(dto.getContentId())
               .withMimeType(mt)
                .withFileName(dto.getFilename())
                .withFilePath(dto.getFilepath())
                .withFileSize(dto.getSize())
                .withBinaryData(dto.getImageData())
                .withTextData(dto.getTextData())
                .build();

        return obj;
    }

}
