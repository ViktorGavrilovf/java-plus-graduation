package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.config.FeignRetryConfig;
import ru.practicum.dto.event.EventFullDto;

@FeignClient(name = "main-service", path = "/internal/events", configuration = FeignRetryConfig.class)
public interface EventClient {

    @GetMapping("/{eventId}")
    EventFullDto getEvent(@PathVariable Long eventId);
}
