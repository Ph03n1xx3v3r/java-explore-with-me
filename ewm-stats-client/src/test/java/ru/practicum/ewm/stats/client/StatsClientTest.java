package ru.practicum.ewm.stats.client;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;\nimport static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;\nimport static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;\nimport static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.http.HttpStatus.CREATED;

class StatsClientTest {
    @Test
    void shouldSendHitToStatsServer() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        StatsClient client = new StatsClient("http://localhost:9090", restTemplate);

        server.expect(requestTo("http://localhost:9090/hit"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(CREATED));

        EndpointHit hit = new EndpointHit(null, "ewm-main-service", "/events/1",
                "127.0.0.1", "2025-01-01 10:00:00");
        client.saveHit(hit);

        server.verify();
    }

    @Test
    void shouldRequestAndReadStats() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        StatsClient client = new StatsClient("http://localhost:9090/", restTemplate);

        server.expect(request -> {
                    String query = request.getURI().getQuery();
                    assertEquals("/stats", request.getURI().getPath());
                    assertTrue(query.contains("start=2025-01-01 00:00:00"));
                    assertTrue(query.contains("end=2025-01-02 00:00:00"));
                    assertTrue(query.contains("uris=/events"));
                    assertTrue(query.contains("unique=true"));
                })
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[{\"app\":\"ewm-main-service\","
                        + "\"uri\":\"/events\",\"hits\":2}]", MediaType.APPLICATION_JSON));

        List<ViewStats> stats = client.getStats(
                LocalDateTime.of(2025, 1, 1, 0, 0),
                LocalDateTime.of(2025, 1, 2, 0, 0),
                List.of("/events"), true);

        assertEquals(1, stats.size());
        assertEquals("/events", stats.get(0).getUri());
        assertEquals(2L, stats.get(0).getHits());
        server.verify();
    }
}
