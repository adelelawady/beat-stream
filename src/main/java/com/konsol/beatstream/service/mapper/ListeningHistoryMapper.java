package com.konsol.beatstream.service.mapper;

import com.konsol.beatstream.domain.ListeningHistory;
import com.konsol.beatstream.domain.Track;
import com.konsol.beatstream.service.dto.ListeningHistoryDTO;
import com.konsol.beatstream.service.dto.TrackDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ListeningHistory} and its DTO {@link ListeningHistoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface ListeningHistoryMapper extends EntityMapper<ListeningHistoryDTO, ListeningHistory> {
    @Mapping(target = "track", source = "track", qualifiedByName = "trackId")
    ListeningHistoryDTO toDto(ListeningHistory s);

    @Named("trackId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TrackDTO toDtoTrackId(Track track);
}
