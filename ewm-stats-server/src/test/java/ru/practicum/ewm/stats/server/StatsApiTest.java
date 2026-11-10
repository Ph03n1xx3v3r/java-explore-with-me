package ru.practicum.ewm.stats.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.stats.server.repository.HitRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StatsApiTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private HitRepository repository;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void shouldSaveHitAndReturnCreated() throws Exception {
        mvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"app\":\"ewm-main-service\",\"uri\":\"/events\","
                                + "\"ip\":\"192.168.0.1\",\"timestamp\":\"2025-01-01 10:00:00\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));
        org.junit.jupiter.api.Assertions.assertEquals(1, repository.count());
    }

    @Test
    void shouldReturnTotalAndUniqueStatistics() throws Exception {
        save("/events", "192.168.0.1", "2025-01-01 10:00:00");
        save("/events", "192.168.0.1", "2025-01-01 10:01:00");
        save("/events", "192.168.0.2", "2025-01-01 10:02:00");
        save("/events/1", "192.168.0.1", "2025-01-01 10:03:00");

        mvc.perform(get("/stats")
                        .param("start", "2025-01-01 00:00:00")
                        .param("end", "2025-01-02 00:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uri").value("/events"))
                .andExpect(jsonPath("$[0].hits").value(3));

        mvc.perform(get("/stats")
                        .param("start", "2025-01-01 00:00:00")
                        .param("end", "2025-01-02 00:00:00")
                        .param("uris", "/events")
                        .param("unique", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uri").value("/events"))
                .andExpect(jsonPath("$[0].hits").value(2));
    }


    @Test
    void shouldRejectInvalidDateFormat() throws Exception {
        mvc.perform(get("/stats")
                        .param("start", "not-a-date")
                        .param("end", "2025-01-02 00:00:00"))
                .andExpect(status().isBadRequest());
    }

    private void save(String uri, String ip, String timestamp) throws Exception {
        mvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"app\":\"ewm-main-service\",\"uri\":\"" + uri + "\","
                                + "\"ip\":\"" + ip + "\",\"timestamp\":\"" + timestamp + "\"}"))
                .andExpect(status().isCreated());
    }
}
