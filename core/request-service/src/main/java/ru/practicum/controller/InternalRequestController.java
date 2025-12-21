package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.RequestStatus;
import ru.practicum.repository.RequestRepository;
import ru.practicum.service.RequestService;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/confirmed")
    public boolean hasConfirmedRequest(@RequestParam Long userId, @RequestParam Long eventId) {
        return requestService.hasVisitedEvent(userId, eventId);
    }

    @PostMapping("/confirmed/batch")
    public Map<Long, Long> countConfirmedByEventIds(@RequestBody List<Long> eventIds) {
        return requestService.countConfirmedByEventIds(eventIds);
    }
}
