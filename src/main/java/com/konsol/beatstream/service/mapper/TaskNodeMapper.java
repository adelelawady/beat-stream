package com.konsol.beatstream.service.mapper;

import com.konsol.beatstream.domain.TaskNode;
import com.konsol.beatstream.service.dto.TaskNodeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TaskNode} and its DTO {@link TaskNodeDTO}.
 */
@Mapper(componentModel = "spring")
public interface TaskNodeMapper extends EntityMapper<TaskNodeDTO, TaskNode> {
    @Mapping(target = "parentTask", source = "parentTask", qualifiedByName = "taskNodeId")
    TaskNodeDTO toDto(TaskNode s);

    @Named("taskNodeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TaskNodeDTO toDtoTaskNodeId(TaskNode taskNode);
}
