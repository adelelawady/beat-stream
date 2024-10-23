package com.konsol.beatstream.service.mapper;

import com.konsol.beatstream.domain.Album;
import com.konsol.beatstream.domain.Artist;
import com.konsol.beatstream.domain.Playlist;
import com.konsol.beatstream.domain.Track;
import com.konsol.beatstream.service.dto.AlbumDTO;
import com.konsol.beatstream.service.dto.ArtistDTO;
import com.konsol.beatstream.service.dto.TrackDTO;
import java.math.BigDecimal;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Track} and its DTO {@link TrackDTO}.
 */
@Mapper(componentModel = "spring")
public interface TrackMapper extends EntityMapper<TrackDTO, Track> {
    @Mapping(target = "artist", source = "artist", qualifiedByName = "artistId")
    @Mapping(target = "album", source = "album", qualifiedByName = "albumId")
    TrackDTO toDto(Track s);

    @Named("artistId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ArtistDTO toDtoArtistId(Artist artist);

    @Named("albumId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AlbumDTO toDtoAlbumId(Album album);

    public default com.konsol.beatstream.service.api.dto.Track TrackIntoTrackDTO(com.konsol.beatstream.domain.Track track) {
        com.konsol.beatstream.service.api.dto.Track track1 = new com.konsol.beatstream.service.api.dto.Track();
        track1.setId(track.getId());
        track1.title(track.getTitle());
        track1.playCount(BigDecimal.valueOf(track.getPlayCount()));
        try {
            track1.duration(new BigDecimal(track.getDuration()));
        } catch (Exception e) {
            track1.duration(new BigDecimal(0));
        }

        track1.setPlaylists(track.getPlaylists().stream().map(Playlist::getId).toList());

        return track1;
    }
}
