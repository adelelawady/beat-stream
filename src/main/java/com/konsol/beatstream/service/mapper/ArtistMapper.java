package com.konsol.beatstream.service.mapper;

import com.konsol.beatstream.domain.Artist;
import com.konsol.beatstream.service.dto.ArtistDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Artist} and its DTO {@link ArtistDTO}.
 */
@Mapper(componentModel = "spring")
public interface ArtistMapper extends EntityMapper<ArtistDTO, Artist> {}
