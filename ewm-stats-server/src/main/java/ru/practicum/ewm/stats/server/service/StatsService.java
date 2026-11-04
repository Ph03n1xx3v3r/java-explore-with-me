package ru.practicum.ewm.stats.server.service;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;
import ru.practicum.ewm.stats.server.model.Hit;
import ru.practicum.ewm.stats.server.repository.HitRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    public List<ViewStats> getStats(String startValue, String endValue, List<String> uris, boolean unique) {
        LocalDateTime start = LocalDateTime.parse(startValue, FORMATTER);
        LocalDateTime end = LocalDateTime.parse(endValue, FORMATTER);

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date must not be after end date");
        }

        List<Hit> hits = (uris == null || uris.isEmpty())
                ? repository.findByTimestampBetween(start, end)
                : repository.findByTimestampBetweenAndUriIn(start, end, uris);

        Map<StatsKey, StatsCounter> counters = new HashMap<>();
        for (Hit hit : hits) {
            StatsKey key = new StatsKey(hit.getApp(), hit.getUri());
            StatsCounter counter = counters.computeIfAbsent(key, ignored -> new StatsCounter());
            counter.add(hit.getIp());
        }

        List<ViewStats> result = new ArrayList<>();
        for (Map.Entry<StatsKey, StatsCounter> entry : counters.entrySet()) {
            long count = unique ? entry.getValue().uniqueCount() : entry.getValue().totalCount();
            result.add(new ViewStats(entry.getKey().app(), entry.getKey().uri(), count));
        }

        result.sort(Comparator.comparing(ViewStats::getHits).reversed()
                .thenComparing(ViewStats::getApp)
                .thenComparing(ViewStats::getUri));
        return result;
    }

    private record StatsKey(String app, String uri) {
    }

    private static class StatsCounter {
        private long total;
        private final Set<String> ips = new HashSet<>();

        void add(String ip) {
            total++;
            ips.add(ip);
        }

        long totalCount() {
            return total;
        }

        long uniqueCount() {
            return ips.size();
        }
    }
}
