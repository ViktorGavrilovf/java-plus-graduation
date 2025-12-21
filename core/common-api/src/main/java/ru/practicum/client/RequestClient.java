package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.config.FeignRetryConfig;
import ru.practicum.dto.request.RequestStatus;

import java.util.List;
import java.util.Map;

@FeignClient(
        name = "request-service",
        path = "/internal/requests",
        configuration = FeignRetryConfig.class,
        fallback = RequestClientFallback.class)

public interface RequestClient {

    @GetMapping("/event/{eventId}/count/{status}")
    Long countByStatus(@PathVariable Long eventId, @PathVariable RequestStatus status);

    @GetMapping("/confirmed")
    boolean hasVisitedEvent(@RequestParam long userId, @RequestParam long eventId);

    @PostMapping("/confirmed/batch")
    Map<Long, Long> countConfirmedByEventIds(@RequestBody List<Long> eventIds);
}
