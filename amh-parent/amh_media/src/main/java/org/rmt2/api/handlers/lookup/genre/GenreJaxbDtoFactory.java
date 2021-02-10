package org.rmt2.api.handlers.lookup.genre;

import java.util.ArrayList;
import java.util.List;

import org.dto.GenreDto;
import org.dto.adapter.orm.Rmt2MediaDtoFactory;
import org.rmt2.jaxb.AudioVideoCriteriaType;
import org.rmt2.jaxb.GenreType;
import org.rmt2.util.media.GenreTypeBuilder;

import com.RMT2Base;

/**
 * A factory for creating DTO and JAXB instances for the Genre Media message
 * handler project.
 * 
 * @author Roy Terrell.
 * 
 */
public class GenreJaxbDtoFactory extends RMT2Base {

    /**
     * Creates an instance of <i>GenreDto</i> using a valid
     * <i>AudioVisualCriteriaType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link AudioVisualCriteriaType}
     * @return an instance of {@link GenreDto}
     */
    public static final GenreDto createGenreDtoInstance(AudioVideoCriteriaType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        GenreDto dto = Rmt2MediaDtoFactory.getAvGenreInstance(null);
        if (jaxbObj.getGenreId() != null) {
            dto.setUid(jaxbObj.getGenreId());
        }
        dto.setDescription(jaxbObj.getGenreName());
        return dto;
    }

    /**
     * Creates an instance of <i>GenreDto</i> using a valid <i>GenreType</i>
     * JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link GenreType}
     * @return an instance of {@link GenreDto}
     */
    public static final GenreDto createGenreDtoInstance(GenreType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        GenreDto dto = Rmt2MediaDtoFactory.getAvGenreInstance(null);
        if (jaxbObj.getGenreId() != null) {
            dto.setUid(jaxbObj.getGenreId());
        }
        dto.setDescription(jaxbObj.getGenreName());
        return dto;
    }

    /**
     * Creates an instance of <i>GenreType</i> using a valid <i>GenreDto</i>
     * JAXB object.
     * 
     * @param dto
     *            an instance of {@link GenreDto}
     * @return an instance of {@link GenreType}
     */
    public static final GenreType createGenreJaxbInstance(GenreDto dto) {
        if (dto == null) {
            return null;
        }
        GenreType obj = GenreTypeBuilder.Builder.create()
                .withName(dto.getDescritpion())
                .withUID(dto.getUid())
                .build();
        return obj;
    }

    /**
     * Creates a List instance of <i>GenreType</i> using a valid List of
     * <i>GenreDto</i> DTO objects.
     * 
     * @param results
     *            List of {@link GenreDto}
     * @return List of {@link GenreType} objects
     */
    public static final List<GenreType> createGenreJaxbInstance(List<GenreDto> results) {
        List<GenreType> list = new ArrayList<>();
        for (GenreDto item : results) {
            GenreType jaxbObj = GenreTypeBuilder.Builder.create()
                    .withUID(item.getUid())
                    .withName(item.getDescritpion())
                    .build();

            list.add(jaxbObj);
        }
        return list;
    }
}
