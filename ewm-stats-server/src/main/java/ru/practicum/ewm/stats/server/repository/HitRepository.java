package ru.practicum.ewm.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.stats.server.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Long> {
    List<Hit> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<Hit> findByTimestampBetweenAndUriIn(LocalDateTime start, LocalDateTime end, List<String> uris);
}
