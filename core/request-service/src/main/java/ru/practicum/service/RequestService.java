package ru.practicum.service;

import ru.practicum.dto.event.EventRequestStatusUpdateRequestDto;
import ru.practicum.dto.event.EventRequestStatusUpdateResultDto;
import ru.practicum.dto.request.ParticipationRequestDto;

import java.util.List;
import java.util.Map;

public interface RequestService {

    List<ParticipationRequestDto> getUserRequests(Long userId);

    ParticipationRequestDto addParticipationRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResultDto changeRequestStatus(Long userId, Long eventId,
                                                          EventRequestStatusUpdateRequestDto updateRequestDto);

    boolean hasVisitedEvent(Long userId, Long eventId);

    Map<Long, Long> countConfirmedByEventIds(List<Long> eventIds);
}
