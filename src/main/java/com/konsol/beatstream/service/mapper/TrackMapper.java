package com.konsol.beatstream.service.mapper;

import com.konsol.beatstream.domain.Album;
import com.konsol.beatstream.domain.Artist;
import com.konsol.beatstream.domain.Track;
import com.konsol.beatstream.service.dto.AlbumDTO;
import com.konsol.beatstream.service.dto.ArtistDTO;
import com.konsol.beatstream.service.dto.TrackDTO;
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
}
