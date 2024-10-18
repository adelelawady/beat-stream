package com.konsol.beatstream.service.mapper;

import com.konsol.beatstream.domain.BeatStreamFile;
import com.konsol.beatstream.service.dto.BeatStreamFileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BeatStreamFile} and its DTO {@link BeatStreamFileDTO}.
 */
@Mapper(componentModel = "spring")
public interface BeatStreamFileMapper extends EntityMapper<BeatStreamFileDTO, BeatStreamFile> {}
