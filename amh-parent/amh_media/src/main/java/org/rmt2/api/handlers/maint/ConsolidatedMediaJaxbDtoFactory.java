package org.rmt2.api.handlers.maint;

import java.util.ArrayList;
import java.util.List;

import org.dto.VwArtistDto;
import org.dto.adapter.orm.Rmt2MediaDtoFactory;
import org.rmt2.jaxb.ArtistType;
import org.rmt2.jaxb.AudioVideoCriteriaType;
import org.rmt2.jaxb.AvProjectType;
import org.rmt2.jaxb.TrackType;
import org.rmt2.util.media.AVProjectTypeBuilder;
import org.rmt2.util.media.ArtistTypeBuilder;
import org.rmt2.util.media.TrackTypeBuilder;

import com.RMT2Base;

/**
 * A factory for creating DTO and JAXB instances for the audio/video artist
 * related message handlers.
 * 
 * @author Roy Terrell.
 * 
 */
public class ConsolidatedMediaJaxbDtoFactory extends RMT2Base {
    /**
     * Creates an instance of <i>VwArtistDto</i> using a valid
     * <i>AudioVideoCriteriaType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link AudioVideoCriteriaType}
     * @return an instance of {@link VwArtistDto}
     */
    public static final VwArtistDto createVwArtistProjectDtoInstance(AudioVideoCriteriaType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        VwArtistDto dto = Rmt2MediaDtoFactory.getVwAudioVideoArtistsInstance(null);
        if (jaxbObj.getArtistId() != null) {
            // Get specific artist by id
            dto.setArtistId(jaxbObj.getArtistId());
        }
        if (jaxbObj.getArtistName() != null && !jaxbObj.getArtistName().isEmpty()) {
            dto.setArtistName(jaxbObj.getArtistName());
        }

        if (jaxbObj.getProjectId() != null) {
            dto.setProjectId(jaxbObj.getProjectId());
        }

        dto.setProjectName(jaxbObj.getProjectTitle());

        if (jaxbObj.getTrackId() != null) {
            dto.setTrackId(jaxbObj.getTrackId());
        }

        dto.setTrackName(jaxbObj.getTrackTitle());

        if (jaxbObj.getProjectTypeId() != null) {
            dto.setProjectTypeId(jaxbObj.getProjectTypeId());
        }

        dto.setProjectTypeName(jaxbObj.getProjectTypeName());

        if (jaxbObj.getYear() != null) {
            dto.setYear(jaxbObj.getYear());
        }

        if (jaxbObj.getRipped() != null) {
            dto.setRippedInd(jaxbObj.getRipped());
        }

        if (jaxbObj.getGenreId() != null) {
            dto.setGenreId(jaxbObj.getGenreId());
        }

        if (jaxbObj.getMediaTypeId() != null) {
            dto.setMediaTypeId(jaxbObj.getMediaTypeId());
        }
        dto.setSearchTerm(jaxbObj.getSearchTerm());
        return dto;
    }

    /**
     * Creates a List instance of <i>ArtistType</i> using a valid List of
     * <i>VwArtistDto</i> DTO objects by filtering out duplicate records.
     * 
     * @param dto
     *            List of {@link VwArtistDto} objects
     * @param searchTerm
     *            the search term value that was used to fetch the data which is
     *            also used to filter the results.
     * @return List of {@link ArtistType} objects
     */
    public static final List<ArtistType> createMediaJaxbInstance(List<VwArtistDto> dto, String searchTerm) {
        if (dto == null) {
            return null;
        }
        if (searchTerm == null || searchTerm.isEmpty()) {
            return null;
        }

        List<ArtistType> artists = new ArrayList<>();
        List<AvProjectType> projs = new ArrayList<>();
        List<TrackType> tracks = new ArrayList<>();
        int prevArtistId = 0;
        int prevProjId = 0;

        // Evaluate data from the lowest entity to highest i.e. track to project
        // to artist
        for (VwArtistDto item : dto) {
            // Begin evauluating the track
            if (item.getTrackName() != null && item.getTrackName().toLowerCase().contains(searchTerm.toLowerCase())) {
                // Build and add artist/project/track nodes
                tracks.add(ConsolidatedMediaJaxbDtoFactory.createTrackTypeJaxbInstance(item));
                AvProjectType proj = ConsolidatedMediaJaxbDtoFactory.createAvProjectTypeJaxbInstance(item, tracks);
                projs.add(proj);
                ArtistType artist = ConsolidatedMediaJaxbDtoFactory.createArtistTypeJaxbInstance(item, projs);
                artists.add(artist);
                projs.clear();
                tracks.clear();
            }
            else {
                // Next, evaluate project
                if (item.getProjectName() != null && item.getProjectName().toLowerCase().contains(searchTerm.toLowerCase())
                        && item.getProjectId() != prevProjId) {
                    // Build and add artist/project nodes
                    AvProjectType proj = ConsolidatedMediaJaxbDtoFactory.createAvProjectTypeJaxbInstance(item, null);
                    projs.add(proj);
                    ArtistType artist = ConsolidatedMediaJaxbDtoFactory.createArtistTypeJaxbInstance(item, projs);
                    artists.add(artist);
                    projs.clear();

                    // Update previous project id variable
                    prevProjId = item.getProjectId();
                }
                else {
                    // Next, evaluate artist
                    if (item.getArtistName() != null && item.getArtistName().toLowerCase().contains(searchTerm.toLowerCase())
                            && item.getArtistId() != prevArtistId) {
                        // Build and add artist node
                        ArtistType artist = ConsolidatedMediaJaxbDtoFactory.createArtistTypeJaxbInstance(item, null);
                        artists.add(artist);

                        // Update previous artist id variable
                        prevArtistId = item.getArtistId();
                    }
                }
            }
        }
        return artists;
    }

    /**
     * Creates a List instance of <i>ArtistType</i> using a valid List of
     * <i>VwArtistDto</i> DTO objects.
     * 
     * @param dto
     *            List of {@link VwArtistDto}
     * @return List of {@link ArtistType} objects
     */
    public static final List<ArtistType> createMediaJaxbInstance(List<VwArtistDto> dto) {
        if (dto == null) {
            return null;
        }

        String prevArtist = null;
        int prevProjId = 0;
        int ndx = 0;
        List<ArtistType> artists = new ArrayList<>();
        List<AvProjectType> projs = new ArrayList<>();
        List<TrackType> tracks = new ArrayList<>();

        for (VwArtistDto item : dto) {
            // Handle first time iteration
            if (prevArtist == null && prevProjId == 0) {
                prevArtist = item.getArtistName();
                prevProjId = item.getProjectId();
            }

            // Start comparing rows
            if (prevArtist.equalsIgnoreCase(item.getArtistName())) {
                // Do not attempt to add track data for movies.
                if (prevProjId == item.getProjectId() && item.getProjectTypeId() != 2) {
                    tracks.add(ConsolidatedMediaJaxbDtoFactory.createTrackTypeJaxbInstance(item));
                }
                else {
                    AvProjectType proj = ConsolidatedMediaJaxbDtoFactory
                            .createAvProjectTypeJaxbInstance(dto.get(ndx - 1), tracks);
                    projs.add(proj);
                    tracks.clear();
                    prevProjId = item.getProjectId();

                    // // Do not attempt to add track data for movies.
                    // if (item.getProjectTypeId() != 2) {
                    // tracks.add(ConsolidatedMediaJaxbDtoFactory.createTrackTypeJaxbInstance(item));
                    // }
                }
            }
            else {
                AvProjectType proj = ConsolidatedMediaJaxbDtoFactory
                        .createAvProjectTypeJaxbInstance(dto.get(ndx - 1), tracks);
                projs.add(proj);
                ArtistType artist = ConsolidatedMediaJaxbDtoFactory.createArtistTypeJaxbInstance(dto.get(ndx - 1), projs);
                artists.add(artist);
                projs.clear();
                tracks.clear();
                prevArtist = item.getArtistName();
                prevProjId = item.getProjectId();
                // Do not attempt to add track data for movies.
                if (item.getProjectTypeId() != 2) {
                    tracks.add(ConsolidatedMediaJaxbDtoFactory.createTrackTypeJaxbInstance(item));
                }
            }
            ndx++;
        }
        // Add the last entry
        AvProjectType proj = ConsolidatedMediaJaxbDtoFactory
                .createAvProjectTypeJaxbInstance(dto.get(ndx - 1), tracks);
        projs.add(proj);
        ArtistType artist = ConsolidatedMediaJaxbDtoFactory.createArtistTypeJaxbInstance(dto.get(ndx - 1), projs);
        artists.add(artist);
        return artists;
    }

    private static final ArtistType createArtistTypeJaxbInstance(VwArtistDto dto, List<AvProjectType> projects) {
        if (dto == null) {
            return null;
        }
        ArtistType obj = ArtistTypeBuilder.Builder.create()
                .withArtistName(dto.getArtistName())
                .withArtistId(dto.getArtistId())
                .withProjects(projects)
                .build();
        return obj;
    }

    private static final AvProjectType createAvProjectTypeJaxbInstance(VwArtistDto dto, List<TrackType> tracks) {
        if (dto == null) {
            return null;
        }
        AvProjectType pt = AVProjectTypeBuilder.Builder.create()
                .withProjectId(dto.getProjectId())
                .withArtistId(dto.getArtistId())
                .withComments(dto.getProjectComments())
                .withTitle(dto.getProjectName())
                .withTotalTime(dto.getTotalTime())
                .withProducer(dto.getProducer())
                .withYear(dto.getYear())
                .withCost(dto.getCost())
                .withRipped(dto.getRippedInd())
                .withMediaTypeId(dto.getMediaTypeId())
                .withGenreTypeId(dto.getGenreId())
                .withProjectTypeId(dto.getProjectTypeId())
                .withTracks(tracks)
                .build();
        return pt;
    }

    private static final TrackType createTrackTypeJaxbInstance(VwArtistDto dto) {
        if (dto == null) {
            return null;
        }
        TrackType tt = TrackTypeBuilder.Builder.create()
                .withTrackId(dto.getTrackId())
                .withProjectId(dto.getProjectId())
                .withGenreId(dto.getGenreId())
                .withTrackName(dto.getTrackName())
                .withArtist(dto.getArtistName())
                .withComments(dto.getTrackComments())
                .withLocationFilename(dto.getContentFilename())
                .withLocationPath(dto.getContentPath())
                .withArtist(dto.getArtistName())
                .withDiscNumber(dto.getTrackDiscNumber() != null ? Integer.valueOf(dto.getTrackDiscNumber()) : 1)
                .withTrackNumber(dto.getTrackNumber())
                .withProducer(dto.getProducer())
                .withHours(dto.getTrackHours())
                .withMinutes(dto.getTrackMinutes())
                .withSeconds(dto.getTrackSeconds())
                .build();
        return tt;
    }
}
