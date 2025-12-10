package ru.practicum.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.EventClient;
import ru.practicum.client.UserClient;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventRequestStatusUpdateRequestDto;
import ru.practicum.dto.event.EventRequestStatusUpdateResultDto;
import ru.practicum.dto.event.EventState;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.dto.request.RequestStatus;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Request;
import ru.practicum.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserClient userClient;
    private final EventClient eventClient;
    private final RequestMapper requestMapper;

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        checkUserExists(userId);
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto addParticipationRequest(Long userId, Long eventId) {
        checkUserExists(userId);
        EventFullDto event = eventClient.getEvent(eventId);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Нельзя запросить участие в неопубликованном событии");
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Создатель не может запросить участие в своём событии.");
        }

        boolean alreadyExists = requestRepository.existsByRequesterIdAndEventId(userId, eventId);
        if (alreadyExists) {
            throw new ConflictException("Запрос уже существует");
        }

        if (event.getParticipantLimit() != 0 &&
                requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED)
                        >= event.getParticipantLimit()) {
            throw new ConflictException("Достигнут лимит участников");
        }

        RequestStatus status = RequestStatus.PENDING;
        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            status = RequestStatus.CONFIRMED;
        }

        Request request = new Request();
        request.setEventId(eventId);
        request.setRequesterId(userId);
        request.setCreated(LocalDateTime.now());
        request.setStatus(status);

        Request saved = requestRepository.save(request);
        return requestMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        checkUserExists(userId);
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request", "RequestId", requestId));

        if (!request.getRequesterId().equals(userId)) {
            throw new ConflictException("Пользователь может отменять только свои запросы");
        }

        request.setStatus(RequestStatus.CANCELED);
        return requestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        checkUserExists(userId);
        EventFullDto event = eventClient.getEvent(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Только создатель может смотреть запросы к событию");
        }

        return requestRepository.findAllByEventId(eventId).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResultDto changeRequestStatus(Long userId, Long eventId,
                                                                 EventRequestStatusUpdateRequestDto updateRequestDto) {
        checkUserExists(userId);
        EventFullDto event = eventClient.getEvent(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Только создатель может менять статус запроса");
        }

        if (event.getParticipantLimit() != 0 &&
                requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED)
                        >= event.getParticipantLimit()) {
            throw new ConflictException("Достигнут лимит участников");
        }

        List<Request> requests = requestRepository.findAllById(updateRequestDto.getRequestIds());
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        for (Request req : requests) {
            if (!req.getEventId().equals(eventId)) {
                throw new ConflictException("Запрос не относится к этому событию");
            }

            if (req.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Можно менять только статус запросов, находящихся в ожидании");
            }

            if (updateRequestDto.getStatus() == RequestStatus.CONFIRMED) {
                if (event.getParticipantLimit() != 0 &&
                        requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED)
                                >= event.getParticipantLimit()) {
                    req.setStatus(RequestStatus.REJECTED);
                    rejectedRequests.add(requestMapper.toDto(req));
                } else {
                    req.setStatus(RequestStatus.CONFIRMED);
                    confirmedRequests.add(requestMapper.toDto(req));
                }
            } else if (updateRequestDto.getStatus() == RequestStatus.REJECTED) {
                req.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(requestMapper.toDto(req));
            }
        }

        requestRepository.saveAll(requests);

        return new EventRequestStatusUpdateResultDto(confirmedRequests, rejectedRequests);
    }

    private void checkUserExists(Long userId) {
        try {
            userClient.getUser(userId);
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("User", "id", userId);
        }
    }
}