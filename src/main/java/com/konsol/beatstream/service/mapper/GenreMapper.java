package com.konsol.beatstream.service.mapper;

import com.konsol.beatstream.domain.Artist;
import com.konsol.beatstream.domain.Genre;
import com.konsol.beatstream.service.dto.ArtistDTO;
import com.konsol.beatstream.service.dto.GenreDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Genre} and its DTO {@link GenreDTO}.
 */
@Mapper(componentModel = "spring")
public interface GenreMapper extends EntityMapper<GenreDTO, Genre> {
    @Mapping(target = "artists", source = "artists", qualifiedByName = "artistIdSet")
    GenreDTO toDto(Genre s);

    @Mapping(target = "removeArtist", ignore = true)
    Genre toEntity(GenreDTO genreDTO);

    @Named("artistId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ArtistDTO toDtoArtistId(Artist artist);

    @Named("artistIdSet")
    default Set<ArtistDTO> toDtoArtistIdSet(Set<Artist> artist) {
        return artist.stream().map(this::toDtoArtistId).collect(Collectors.toSet());
    }
}
