package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.dto.request.RequestStatus;

@Component
@Slf4j
public class RequestClientFallback implements RequestClient {
    @Override
    public Long countByStatus(Long eventId, RequestStatus status) {
        log.warn("Сервис запросов недоступен");
        return 0L;
    }
}
