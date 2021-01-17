package org.rmt2.api.handler;

import java.util.ArrayList;
import java.util.List;

import org.dao.mapping.orm.rmt2.AvArtist;
import org.dao.mapping.orm.rmt2.AvGenre;
import org.dao.mapping.orm.rmt2.AvMediaType;
import org.dao.mapping.orm.rmt2.AvProject;
import org.dao.mapping.orm.rmt2.AvProjectType;
import org.dao.mapping.orm.rmt2.AvTracks;
import org.dao.mapping.orm.rmt2.VwAudioVideoArtists;
import org.dto.ArtistDto;
import org.dto.GenreDto;
import org.dto.MediaTypeDto;
import org.dto.ProjectDto;
import org.dto.ProjectTypeDto;
import org.dto.TracksDto;
import org.dto.VwArtistDto;
import org.dto.adapter.orm.Rmt2MediaDtoFactory;

/**
 * Audio/Video Media testing facility that is mainly responsible for setting up mock data.
 * <p>
 * All derived media related Api unit tests should inherit this class
 * to prevent duplicating common functionality.
 * 
 * @author rterrell
 * 
 */
public class MediaMockDtoFactory {
    public static final int TEST_PROJECT_TYPE_ID_AUDIO = 1;
    public static final int TEST_PROJECT_TYPE_ID_VIDEO = 2;

    public static final List<GenreDto> createGenreSingleMockData() {
        List<GenreDto> list = new ArrayList<>();
        AvGenre o = MediaMockOrmFactory.createOrmAvGenre(MediaMockOrmFactory.TEST_GENRE_ID, "Genre"
                + MediaMockOrmFactory.TEST_GENRE_ID);
        GenreDto d = Rmt2MediaDtoFactory.getAvGenreInstance(o);
        list.add(d);
        return list;
    }

    public static final List<GenreDto> createGenreMockData() {
        List<GenreDto> list = new ArrayList<>();
        int ndx = MediaMockOrmFactory.TEST_GENRE_ID;
        AvGenre o = MediaMockOrmFactory.createOrmAvGenre(ndx, "Genre" + ndx);
        GenreDto d = Rmt2MediaDtoFactory.getAvGenreInstance(o);
        list.add(d);
        
        o = MediaMockOrmFactory.createOrmAvGenre(++ndx, "Genre" + ndx);
        d = Rmt2MediaDtoFactory.getAvGenreInstance(o);
        list.add(d);

        o = MediaMockOrmFactory.createOrmAvGenre(++ndx, "Genre" + ndx);
        d = Rmt2MediaDtoFactory.getAvGenreInstance(o);
        list.add(d);

        o = MediaMockOrmFactory.createOrmAvGenre(++ndx, "Genre" + ndx);
        d = Rmt2MediaDtoFactory.getAvGenreInstance(o);
        list.add(d);

        o = MediaMockOrmFactory.createOrmAvGenre(++ndx, "Genre" + ndx);
        d = Rmt2MediaDtoFactory.getAvGenreInstance(o);
        list.add(d);
        
        return list;
    }
    
    public static final List<ProjectTypeDto> createProjectTypeMockData() {
        List<ProjectTypeDto> list = new ArrayList<>();
        int ndx = MediaMockOrmFactory.TEST_PROJECTTYPE_ID;
        AvProjectType o = MediaMockOrmFactory.createOrmAvProjectType(ndx, "ProjectType" + ndx);
        ProjectTypeDto d = Rmt2MediaDtoFactory.getAvProjectTypeInstance(o);
        list.add(d);
        
        o = MediaMockOrmFactory.createOrmAvProjectType(++ndx, "ProjectType" + ndx);
        d = Rmt2MediaDtoFactory.getAvProjectTypeInstance(o);
        list.add(d);

        o = MediaMockOrmFactory.createOrmAvProjectType(++ndx, "ProjectType" + ndx);
        d = Rmt2MediaDtoFactory.getAvProjectTypeInstance(o);
        list.add(d);

        o = MediaMockOrmFactory.createOrmAvProjectType(++ndx, "ProjectType" + ndx);
        d = Rmt2MediaDtoFactory.getAvProjectTypeInstance(o);
        list.add(d);

        o = MediaMockOrmFactory.createOrmAvProjectType(++ndx, "ProjectType" + ndx);
        d = Rmt2MediaDtoFactory.getAvProjectTypeInstance(o);
        list.add(d);
        
        return list;
    }
    
    public static final List<MediaTypeDto> createMediaTypeSingleMockData() {
        List<MediaTypeDto> list = new ArrayList<>();
        int ndx = MediaMockOrmFactory.TEST_MEDIA_TYPE_ID;
        AvMediaType o = MediaMockOrmFactory.createOrmAvMediaType(ndx, "MediaType" + ndx);
        MediaTypeDto d = Rmt2MediaDtoFactory.getAvMediaTypeInstance(o);
        list.add(d);
        return list;
    }

    public static final List<MediaTypeDto> createMediaTypeMockData() {
        List<MediaTypeDto> list = new ArrayList<>();
        int ndx = MediaMockOrmFactory.TEST_MEDIA_TYPE_ID;
        AvMediaType o = MediaMockOrmFactory.createOrmAvMediaType(ndx, "MediaType" + ndx);
        MediaTypeDto d = Rmt2MediaDtoFactory.getAvMediaTypeInstance(o);
        list.add(d);
        
        o = MediaMockOrmFactory.createOrmAvMediaType(++ndx, "MediaType" + ndx);
        d = Rmt2MediaDtoFactory.getAvMediaTypeInstance(o);
        list.add(d);

        o = MediaMockOrmFactory.createOrmAvMediaType(++ndx, "MediaType" + ndx);
        d = Rmt2MediaDtoFactory.getAvMediaTypeInstance(o);
        list.add(d);

        o = MediaMockOrmFactory.createOrmAvMediaType(++ndx, "MediaType" + ndx);
        d = Rmt2MediaDtoFactory.getAvMediaTypeInstance(o);
        list.add(d);

        o = MediaMockOrmFactory.createOrmAvMediaType(++ndx, "MediaType" + ndx);
        d = Rmt2MediaDtoFactory.getAvMediaTypeInstance(o);
        list.add(d);
        
        return list;
    }
    
    public static final List<ArtistDto> createArtistMockData() {
        List<ArtistDto> list = new ArrayList<>();
        int ndx = MediaMockOrmFactory.TEST_ARTIST_ID;
        AvArtist o = MediaMockOrmFactory.createOrmAvArtist(ndx, "Artist" + ndx);
        ArtistDto d = Rmt2MediaDtoFactory.getAvArtistInstance(o);
        list.add(d);
        
        o = MediaMockOrmFactory.createOrmAvArtist(++ndx, "Artist" + ndx);
        d = Rmt2MediaDtoFactory.getAvArtistInstance(o);
        list.add(d);

        o = MediaMockOrmFactory.createOrmAvArtist(++ndx, "Artist" + ndx);
        d = Rmt2MediaDtoFactory.getAvArtistInstance(o);
        list.add(d);

        o = MediaMockOrmFactory.createOrmAvArtist(++ndx, "Artist" + ndx);
        d = Rmt2MediaDtoFactory.getAvArtistInstance(o);
        list.add(d);

        o = MediaMockOrmFactory.createOrmAvArtist(++ndx, "Artist" + ndx);
        d = Rmt2MediaDtoFactory.getAvArtistInstance(o);
        list.add(d);
        
        return list;
    }
    
    public static final List<ProjectDto> createProjectMockData() {
        List<ProjectDto> list = new ArrayList<>();
        int ndx = MediaMockOrmFactory.TEST_PROJECT_ID;
        AvProject o = MediaMockOrmFactory.createOrmAvProject(ndx,
                MediaMockOrmFactory.TEST_ARTIST_ID,
                MediaMockOrmFactory.TEST_PROJECTTYPE_ID,
                MediaMockOrmFactory.TEST_GENRE_ID,
                MediaMockOrmFactory.TEST_MEDIA_TYPE_ID, "Title" + ndx, 2018,
                "/FilePath/" + ndx, "ProjectFileName" + ndx);
        ProjectDto d = Rmt2MediaDtoFactory.getAvProjectInstance(o);
        list.add(d);
        
        o = MediaMockOrmFactory.createOrmAvProject(++ndx,
                MediaMockOrmFactory.TEST_ARTIST_ID,
                MediaMockOrmFactory.TEST_PROJECTTYPE_ID,
                MediaMockOrmFactory.TEST_GENRE_ID,
                MediaMockOrmFactory.TEST_MEDIA_TYPE_ID, "Title" + ndx, 2018,
                "/FilePath/" + ndx, "ProjectFileName" + ndx);
        d = Rmt2MediaDtoFactory.getAvProjectInstance(o);
        list.add(d);

        o = MediaMockOrmFactory.createOrmAvProject(++ndx,
                MediaMockOrmFactory.TEST_ARTIST_ID,
                MediaMockOrmFactory.TEST_PROJECTTYPE_ID,
                MediaMockOrmFactory.TEST_GENRE_ID,
                MediaMockOrmFactory.TEST_MEDIA_TYPE_ID, "Title" + ndx, 2018,
                "/FilePath/" + ndx, "ProjectFileName" + ndx);
        d = Rmt2MediaDtoFactory.getAvProjectInstance(o);
        list.add(d);

        o = MediaMockOrmFactory.createOrmAvProject(++ndx,
                MediaMockOrmFactory.TEST_ARTIST_ID,
                MediaMockOrmFactory.TEST_PROJECTTYPE_ID,
                MediaMockOrmFactory.TEST_GENRE_ID,
                MediaMockOrmFactory.TEST_MEDIA_TYPE_ID, "Title" + ndx, 2018,
                "/FilePath/" + ndx, "ProjectFileName" + ndx);
        d = Rmt2MediaDtoFactory.getAvProjectInstance(o);
        list.add(d);

        o = MediaMockOrmFactory.createOrmAvProject(++ndx,
                MediaMockOrmFactory.TEST_ARTIST_ID,
                MediaMockOrmFactory.TEST_PROJECTTYPE_ID,
                MediaMockOrmFactory.TEST_GENRE_ID,
                MediaMockOrmFactory.TEST_MEDIA_TYPE_ID, "Title" + ndx, 2018,
                "/FilePath/" + ndx, "ProjectFileName" + ndx);
        d = Rmt2MediaDtoFactory.getAvProjectInstance(o);
        list.add(d);
        
        return list;
    }
    
    public static final List<TracksDto> createTrackMockData() {
        List<TracksDto> list = new ArrayList<>();
        int ndx = MediaMockOrmFactory.TEST_TRACK_ID;
        int trackNo = 0;
        AvTracks o = MediaMockOrmFactory.createOrmAvTracks(ndx,
                MediaMockOrmFactory.TEST_PROJECT_ID, ++trackNo,
                "Track" + trackNo, 5, 30, 00, "1", "/FilePath/" + ndx,
                "ProjectFileName" + ndx);
        TracksDto d = Rmt2MediaDtoFactory.getAvTrackInstance(o);
        list.add(d);
        
        o = MediaMockOrmFactory.createOrmAvTracks(++ndx,
                MediaMockOrmFactory.TEST_PROJECT_ID, ++trackNo,
                "Track" + trackNo, 5, 30, 00, "1", "/FilePath/" + ndx,
                "ProjectFileName" + ndx);
        d = Rmt2MediaDtoFactory.getAvTrackInstance(o);
        list.add(d);
        
        o = MediaMockOrmFactory.createOrmAvTracks(++ndx,
                MediaMockOrmFactory.TEST_PROJECT_ID, ++trackNo,
                "Track" + trackNo, 5, 30, 00, "1", "/FilePath/" + ndx,
                "ProjectFileName" + ndx);
        d = Rmt2MediaDtoFactory.getAvTrackInstance(o);
        list.add(d);
        
        o = MediaMockOrmFactory.createOrmAvTracks(++ndx,
                MediaMockOrmFactory.TEST_PROJECT_ID, ++trackNo,
                "Track" + trackNo, 5, 30, 00, "1", "/FilePath/" + ndx,
                "ProjectFileName" + ndx);
        d = Rmt2MediaDtoFactory.getAvTrackInstance(o);
        list.add(d);
        
        o = MediaMockOrmFactory.createOrmAvTracks(++ndx,
                MediaMockOrmFactory.TEST_PROJECT_ID, ++trackNo,
                "Track" + trackNo, 5, 30, 00, "1", "/FilePath/" + ndx,
                "ProjectFileName" + ndx);
        d = Rmt2MediaDtoFactory.getAvTrackInstance(o);
        list.add(d);
        
        return list;
    }

    public static final List<VwArtistDto> createConsolidatedMediaDistinctMockData() {
        List<VwArtistDto> list = new ArrayList<>();
        int ndx = 0;
        VwAudioVideoArtists o = MediaMockOrmFactory.createOrmVwAudioVideoArtists(MediaMockOrmFactory.TEST_ARTIST_ID + ndx,
                "Artist" + ndx, MediaMockOrmFactory.TEST_PROJECT_ID + ndx, "Project Name" + ndx++,
                MediaMockOrmFactory.TEST_TRACK_ID, "Track Name", 1, 1, true, MediaMockDtoFactory.TEST_PROJECT_TYPE_ID_AUDIO);
        VwArtistDto d = Rmt2MediaDtoFactory.getVwAudioVideoArtistsInstance(o);
        list.add(d);

        o = MediaMockOrmFactory.createOrmVwAudioVideoArtists(MediaMockOrmFactory.TEST_ARTIST_ID + ndx,
                "Artist" + ndx, MediaMockOrmFactory.TEST_PROJECT_ID + ndx, "Project Name" + ndx++,
                MediaMockOrmFactory.TEST_TRACK_ID, "Track Name", 1, 1, true, MediaMockDtoFactory.TEST_PROJECT_TYPE_ID_AUDIO);
        d = Rmt2MediaDtoFactory.getVwAudioVideoArtistsInstance(o);
        list.add(d);

        o = MediaMockOrmFactory.createOrmVwAudioVideoArtists(MediaMockOrmFactory.TEST_ARTIST_ID + ndx,
                "Artist" + ndx, MediaMockOrmFactory.TEST_PROJECT_ID + ndx, "Project Name" + ndx++,
                MediaMockOrmFactory.TEST_TRACK_ID, "Track Name", 1, 1, true, MediaMockDtoFactory.TEST_PROJECT_TYPE_ID_AUDIO);
        d = Rmt2MediaDtoFactory.getVwAudioVideoArtistsInstance(o);
        list.add(d);

        o = MediaMockOrmFactory.createOrmVwAudioVideoArtists(MediaMockOrmFactory.TEST_ARTIST_ID + ndx,
                "Artist" + ndx, MediaMockOrmFactory.TEST_PROJECT_ID + ndx, "Project Name" + ndx++,
                MediaMockOrmFactory.TEST_TRACK_ID, "Track Name", 1, 1, true, MediaMockDtoFactory.TEST_PROJECT_TYPE_ID_AUDIO);
        d = Rmt2MediaDtoFactory.getVwAudioVideoArtistsInstance(o);
        list.add(d);

        o = MediaMockOrmFactory.createOrmVwAudioVideoArtists(MediaMockOrmFactory.TEST_ARTIST_ID + ndx,
                "Artist" + ndx, MediaMockOrmFactory.TEST_PROJECT_ID + ndx, "Project Name" + ndx++,
                MediaMockOrmFactory.TEST_TRACK_ID, "Track Name", 1, 1, true, MediaMockDtoFactory.TEST_PROJECT_TYPE_ID_AUDIO);
        d = Rmt2MediaDtoFactory.getVwAudioVideoArtistsInstance(o);
        list.add(d);

        return list;
    }

    public static final List<VwArtistDto> createConsolidatedMediaLikeMockData() {
        List<VwArtistDto> list = new ArrayList<>();
        int ndx = 0;
        VwAudioVideoArtists o = MediaMockOrmFactory.createOrmVwAudioVideoArtists(MediaMockOrmFactory.TEST_ARTIST_ID,
                "Artist", MediaMockOrmFactory.TEST_PROJECT_ID, "Project Name",
                MediaMockOrmFactory.TEST_TRACK_ID + ndx, "Track Name" + ndx++, 1, 1, true,
                MediaMockDtoFactory.TEST_PROJECT_TYPE_ID_AUDIO);
        VwArtistDto d = Rmt2MediaDtoFactory.getVwAudioVideoArtistsInstance(o);
        list.add(d);

        o = MediaMockOrmFactory.createOrmVwAudioVideoArtists(MediaMockOrmFactory.TEST_ARTIST_ID,
                "Artist", MediaMockOrmFactory.TEST_PROJECT_ID, "Project Name",
                MediaMockOrmFactory.TEST_TRACK_ID + ndx, "Track Name" + ndx++, 2, 1, true,
                MediaMockDtoFactory.TEST_PROJECT_TYPE_ID_AUDIO);
        d = Rmt2MediaDtoFactory.getVwAudioVideoArtistsInstance(o);
        list.add(d);

        o = MediaMockOrmFactory.createOrmVwAudioVideoArtists(MediaMockOrmFactory.TEST_ARTIST_ID,
                "Artist", MediaMockOrmFactory.TEST_PROJECT_ID, "Project Name",
                MediaMockOrmFactory.TEST_TRACK_ID + ndx, "Track Name" + ndx++, 3, 1, true,
                MediaMockDtoFactory.TEST_PROJECT_TYPE_ID_AUDIO);
        d = Rmt2MediaDtoFactory.getVwAudioVideoArtistsInstance(o);
        list.add(d);

        o = MediaMockOrmFactory.createOrmVwAudioVideoArtists(MediaMockOrmFactory.TEST_ARTIST_ID,
                "Artist", MediaMockOrmFactory.TEST_PROJECT_ID, "Project Name",
                MediaMockOrmFactory.TEST_TRACK_ID + ndx, "Track Name" + ndx++, 4, 1, true,
                MediaMockDtoFactory.TEST_PROJECT_TYPE_ID_AUDIO);
        d = Rmt2MediaDtoFactory.getVwAudioVideoArtistsInstance(o);
        list.add(d);

        o = MediaMockOrmFactory.createOrmVwAudioVideoArtists(MediaMockOrmFactory.TEST_ARTIST_ID,
                "Artist", MediaMockOrmFactory.TEST_PROJECT_ID, "Project Name",
                MediaMockOrmFactory.TEST_TRACK_ID + ndx, "Track Name" + ndx, 5, 1, true,
                MediaMockDtoFactory.TEST_PROJECT_TYPE_ID_AUDIO);
        d = Rmt2MediaDtoFactory.getVwAudioVideoArtistsInstance(o);
        list.add(d);

        return list;
    }

}