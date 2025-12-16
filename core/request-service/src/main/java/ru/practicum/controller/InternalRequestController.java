package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.RequestStatus;
import ru.practicum.repository.RequestRepository;
import ru.practicum.service.RequestService;

@RestController
@RequestMapping("/internal/requests")
@RequiredArgsConstructor
public class InternalRequestController {

    private final RequestRepository requestRepository;
    private final RequestService requestService;

    @GetMapping("/event/{eventId}/count/{status}")
    public Long countByStatus(@PathVariable Long eventId, @PathVariable RequestStatus status) {
        return requestRepository.countByEventIdAndStatus(eventId, status);
    }

    @GetMapping("/internal/requests/confirmed")
    public boolean hasConfirmedRequest(@RequestParam Long userId, @RequestParam Long eventId) {
        return requestService.hasVisitedEvent(userId, eventId);
    }
}
