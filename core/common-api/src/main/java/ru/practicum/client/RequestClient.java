package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.config.FeignRetryConfig;
import ru.practicum.dto.request.RequestStatus;

@FeignClient(name = "request-service", path = "/internal/requests", configuration = FeignRetryConfig.class)
public interface RequestClient {

    @GetMapping("/event/{eventId}/count/{status}")
    Long countByStatus(@PathVariable Long eventId, @PathVariable RequestStatus status);
}
