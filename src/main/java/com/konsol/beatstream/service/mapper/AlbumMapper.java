package com.konsol.beatstream.service.mapper;

import com.konsol.beatstream.domain.Album;
import com.konsol.beatstream.domain.Artist;
import com.konsol.beatstream.domain.Genre;
import com.konsol.beatstream.service.dto.AlbumDTO;
import com.konsol.beatstream.service.dto.ArtistDTO;
import com.konsol.beatstream.service.dto.GenreDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Album} and its DTO {@link AlbumDTO}.
 */
@Mapper(componentModel = "spring")
public interface AlbumMapper extends EntityMapper<AlbumDTO, Album> {
    @Mapping(target = "artist", source = "artist", qualifiedByName = "artistId")
    @Mapping(target = "genres", source = "genres", qualifiedByName = "genreIdSet")
    AlbumDTO toDto(Album s);

    @Mapping(target = "removeGenre", ignore = true)
    Album toEntity(AlbumDTO albumDTO);

    @Named("artistId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ArtistDTO toDtoArtistId(Artist artist);

    @Named("genreId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    GenreDTO toDtoGenreId(Genre genre);

    @Named("genreIdSet")
    default Set<GenreDTO> toDtoGenreIdSet(Set<Genre> genre) {
        return genre.stream().map(this::toDtoGenreId).collect(Collectors.toSet());
    }
}
