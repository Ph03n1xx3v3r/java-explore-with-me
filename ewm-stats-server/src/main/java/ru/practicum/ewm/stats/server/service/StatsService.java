package ru.practicum.ewm.stats.server.service;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;
import ru.practicum.ewm.stats.server.model.Hit;
import ru.practicum.ewm.stats.server.repository.HitRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StatsService {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final HitRepository repository;

    public StatsService(HitRepository repository) {
        this.repository = repository;
    }

    public void saveHit(EndpointHit endpointHit) {
        LocalDateTime timestamp = LocalDateTime.parse(endpointHit.getTimestamp(), FORMATTER);
        Hit hit = new Hit(endpointHit.getApp(), endpointHit.getUri(), endpointHit.getIp(), timestamp);
        repository.save(hit);
    }

    public List<ViewStats> getStats(String startValue, String endValue, List<String> uris,
                                    boolean unique) {
        LocalDateTime start = LocalDateTime.parse(startValue, FORMATTER);
        LocalDateTime end = LocalDateTime.parse(endValue, FORMATTER);

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date must not be after end date");
        }

        List<HitRepository.StatsViewProjection> rows = uris == null || uris.isEmpty()
                ? repository.findStats(start, end, unique)
                : repository.findStatsByUris(start, end, uris, unique);

        return rows.stream()
                .map(row -> new ViewStats(row.getApp(), row.getUri(), row.getHits()))
                .toList();
    }
}
