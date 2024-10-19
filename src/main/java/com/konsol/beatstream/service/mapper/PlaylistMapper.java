package com.konsol.beatstream.service.mapper;

import com.konsol.beatstream.domain.Playlist;
import com.konsol.beatstream.domain.Track;
import com.konsol.beatstream.service.dto.PlaylistDTO;
import com.konsol.beatstream.service.dto.TrackDTO;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Playlist} and its DTO {@link PlaylistDTO}.
 */
@Mapper(componentModel = "spring")
public interface PlaylistMapper extends EntityMapper<PlaylistDTO, Playlist> {
    @Mapping(target = "tracks", source = "tracks", qualifiedByName = "trackIdSet")
    PlaylistDTO toDto(Playlist s);

    @Mapping(target = "removeTrack", ignore = true)
    Playlist toEntity(PlaylistDTO playlistDTO);

    @Named("trackId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TrackDTO toDtoTrackId(Track track);

    @Named("trackIdSet")
    default Set<TrackDTO> toDtoTrackIdSet(Set<Track> track) {
        return track.stream().map(this::toDtoTrackId).collect(Collectors.toSet());
    }

    default List<String> map(Set<Playlist> value) {
        return value.stream().map(Playlist::getId).collect(Collectors.toList());
    }

    com.konsol.beatstream.service.api.dto.Playlist toPlayListDto(Playlist playlist);
}
