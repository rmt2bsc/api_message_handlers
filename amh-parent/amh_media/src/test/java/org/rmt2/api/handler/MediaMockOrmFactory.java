package org.rmt2.api.handler;

import org.dao.mapping.orm.rmt2.AvArtist;
import org.dao.mapping.orm.rmt2.AvGenre;
import org.dao.mapping.orm.rmt2.AvMediaType;
import org.dao.mapping.orm.rmt2.AvProject;
import org.dao.mapping.orm.rmt2.AvProjectType;
import org.dao.mapping.orm.rmt2.AvTracks;
import org.dao.mapping.orm.rmt2.VwAudioVideoArtists;

public class MediaMockOrmFactory {
    public static final int TEST_ARTIST_ID = 123450;
    public static final int TEST_PROJECT_ID = 1000;
    public static final int TEST_TRACK_ID = 1;
    public static final int TEST_NEW_ARTIST_ID = 55555;
    public static final int TEST_NEW_PROJECT_ID = 66666;
    public static final int TEST_NEW_TRACK_ID = 77777;
    public static final int TEST_GENRE_ID = 200;
    public static final int TEST_PROJECTTYPE_ID = 300;
    public static final int TEST_MEDIA_TYPE_ID = 400;
    public static final int TEST_UPDATE_RC = 1;
    public static final String TEST_AUDIO_DIR = "media/audio";
    
    /**
     * 
     * @param id
     * @param description
     * @return
     */
    public static final AvGenre createOrmAvGenre(int id, String description) {
        AvGenre o = new AvGenre();
        o.setGenreId(id);
        o.setDescription(description);
        return o;
    }
    
    /**
     * 
     * @param id
     * @param description
     * @return
     */
    public static final AvMediaType createOrmAvMediaType(int id, String description) {
        AvMediaType o = new AvMediaType();
        o.setMediaTypeId(id);
        o.setDescription(description);
        return o;
    }
    
    /**
     * 
     * @param id
     * @param description
     * @return
     */
    public static final AvProjectType createOrmAvProjectType(int id, String description) {
        AvProjectType o = new AvProjectType();
        o.setProjectTypeId(id);
        o.setDescription(description);
        return o;
    }
    
    /**
     * 
     * @param id
     * @param name
     * @return
     */
    public static final AvArtist createOrmAvArtist(int id, String name) {
        AvArtist o = new AvArtist();
        o.setArtistId(id);
        o.setName(name);
        return o;
    }
    
    /**
     * 
     * @param projectId
     * @param artistId
     * @param projectTypeId
     * @param genreId
     * @param mediaTypeId
     * @param title
     * @param year
     * @param artWorkPath
     * @param artWorkFilename
     * @return
     */
    public static final AvProject createOrmAvProject(int projectId,
            int artistId, int projectTypeId, int genreId, int mediaTypeId,
            String title, int year, String artWorkPath,
            String artWorkFilename) {
        AvProject o = new AvProject();
        o.setProjectId(projectId);
        o.setArtistId(artistId);
        o.setProjectTypeId(projectTypeId);
        o.setGenreId(genreId);
        o.setMediaTypeId(mediaTypeId);
        o.setTitle(title);
        o.setYear(year);
        o.setArtWorkPath(artWorkPath);
        o.setArtWorkFilename(artWorkFilename);
        o.setCost(12.99);
        o.setProjectComments("ProjectCommentsFor" + projectId);
        o.setRipped(1);
        o.setMasterDupId(projectId + 1000);
        
        return o;
    }
 
    /**
     * 
     * @param trackId
     * @param projectId
     * @param trackNumber
     * @param title
     * @param hh
     * @param mm
     * @param ss
     * @param disc
     * @param filePath
     * @param fileName
     * @return
     */
    public static final AvTracks createOrmAvTracks(int trackId, int projectId,
            int trackNumber, String title, int hh, int mm, int ss, String disc,
            String filePath, String fileName) {

        AvTracks o = new AvTracks();
        o.setTrackId(trackId);
        o.setProjectId(projectId);
        o.setTrackNumber(trackNumber);
        o.setTrackTitle(title);
        o.setTrackHours(hh);
        o.setTrackMinutes(mm);
        o.setTrackSeconds(ss);
        o.setTrackDisc(disc);
        o.setLocPath(filePath);
        o.setLocFilename(fileName);
        o.setLocServername("ServerName");
        o.setLocRootPath("www.rmt2.net");
        o.setLocSharename("multimedia");
        o.setComments("CommentsFor" + trackId);
        o.setTrackComposer("john smith");
        o.setTrackLyricist("john smith");
        o.setTrackProducer("john smith");
        return o;
    }
    
    /**
     * 
     * @param artistId
     * @param artistName
     * @param projId
     * @param projName
     * @param trackId
     * @param trackName
     * @param primary
     * @param projTypeId
     * @return
     */
    public static final VwAudioVideoArtists createOrmVwAudioVideoArtists(int artistId, String artistName, int projId,
            String projName, int trackId, String trackName, boolean primary, int projTypeId) {
        VwAudioVideoArtists o = new VwAudioVideoArtists();

        o.setPrimaryArtist(primary ? 1 : 0);
        o.setProjectTypeId(projTypeId);
        o.setProjectTypeName(projTypeId == 1 ? "Audio" : "Video");
        o.setArtistId(artistId);
        o.setArtist(artistName);
        o.setProjectId(projId);
        o.setProjectTitle(projName);
        o.setTrackId(trackId);
        o.setTrackTitle(trackName);
        o.setProjectComments("Project Comments");
        o.setTrackComments("Track Comments");
        o.setGenreId(100);
        o.setContentId(200);
        o.setMediaTypeId(3);
        o.setYear(1999);
        o.setMasterDupId(1);
        o.setRipped(1);
        o.setCost(9.99);
        o.setContentPath("//servername/directory_path/");
        o.setContentFilename("mediafile.mp3");
        o.setArtWorkPath("//servername/directory_path/");
        o.setArtWorkFilename("artworkfile.jpg");
        o.setTotalTime(40);
        o.setProducer("Producer Name");

        return o;
    }
}
