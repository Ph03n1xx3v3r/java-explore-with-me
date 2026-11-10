package ru.practicum.ewm.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.stats.server.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Long> {

    @Query(value = """
            SELECT app AS app,
                   uri AS uri,
                   CASE WHEN :unique THEN COUNT(DISTINCT ip) ELSE COUNT(*) END AS hits
            FROM endpoint_hits
            WHERE hit_timestamp BETWEEN :start AND :end
            GROUP BY app, uri
            ORDER BY hits DESC, app, uri
            """, nativeQuery = true)
    List<StatsViewProjection> findStats(@Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end,
                                        @Param("unique") boolean unique);

    @Query(value = """
            SELECT app AS app,
                   uri AS uri,
                   CASE WHEN :unique THEN COUNT(DISTINCT ip) ELSE COUNT(*) END AS hits
            FROM endpoint_hits
            WHERE hit_timestamp BETWEEN :start AND :end
              AND uri IN (:uris)
            GROUP BY app, uri
            ORDER BY hits DESC, app, uri
            """, nativeQuery = true)
    List<StatsViewProjection> findStatsByUris(@Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end,
                                              @Param("uris") List<String> uris,
                                              @Param("unique") boolean unique);

    interface StatsViewProjection {
        String getApp();

        String getUri();

        Long getHits();
    }
}
