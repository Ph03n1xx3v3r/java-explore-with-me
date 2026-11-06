package ru.practicum.ewm.stats.client;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class StatsClient {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RestTemplate restTemplate;
    private final String serverUrl;

    public StatsClient(String serverUrl) {
        this(serverUrl, new RestTemplate());
    }

    public StatsClient(String serverUrl, RestTemplate restTemplate) {
        this.serverUrl = serverUrl.endsWith("/")
                ? serverUrl.substring(0, serverUrl.length() - 1) : serverUrl;
        this.restTemplate = restTemplate;
    }

    public void saveHit(EndpointHit hit) {
        restTemplate.postForLocation(serverUrl + "/hit", hit);
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end,
                                    List<String> uris, boolean unique) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(serverUrl + "/stats")
                .queryParam("start", start.format(FORMATTER))
                .queryParam("end", end.format(FORMATTER))
                .queryParam("unique", unique);

        if (uris != null) {
            for (String uri : uris) {
                builder.queryParam("uris", uri);
            }
        }

        URI requestUri = builder.encode().build().toUri();
        ViewStats[] response = restTemplate.getForObject(requestUri, ViewStats[].class);
        return response == null ? List.of() : Arrays.asList(response);
    }
}
