package ru.practicum.controller.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.client.UserClient;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Event;
import ru.practicum.repository.EventRepository;

@RestController
@RequestMapping("/internal/events")
@RequiredArgsConstructor
public class InternalEventController {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserClient userClient;

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PathVariable Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event", "id", eventId));

        EventFullDto dto = eventMapper.toFullDto(event);

        dto.setInitiator(userClient.getUser(event.getInitiatorId()));

        return dto;
    }
}
