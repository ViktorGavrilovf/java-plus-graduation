package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.model.Request;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    @Mapping(target = "requesterId", source = "requesterId")
    @Mapping(target = "eventId", source = "request.event.id")
    ParticipationRequestDto toDto(Request request);

    @Mapping(target = "requesterId", source = "requesterId")
    @Mapping(target = "event.id", source = "eventId")
    Request toEntity(ParticipationRequestDto dto);
}