package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.dto.request.RequestStatus;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class RequestClientFallback implements RequestClient {
    @Override
    public Long countByStatus(Long eventId, RequestStatus status) {
        log.warn("Сервис запросов недоступен");
        return 0L;
    }

    @Override
    public boolean hasVisitedEvent(long userId, long eventId) {
        log.warn("Сервис запросов недоступен");
        return false;
    }

    @Override
    public Map<Long, Long> countConfirmedByEventIds(List<Long> eventIds) {
        log.warn("Сервис запросов недоступен");
        return null;
    }
}
