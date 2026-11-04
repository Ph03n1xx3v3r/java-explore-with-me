package ru.practicum.ewm.stats.server.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;
import ru.practicum.ewm.stats.server.service.StatsService;

import java.util.List;

@RestController
public class StatsController {
    private final StatsService service;

    public StatsController(StatsService service) {
        this.service = service;
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHit(@Valid @RequestBody HitRequest request) {
        EndpointHit hit = new EndpointHit();
        hit.setApp(request.getApp());
        hit.setUri(request.getUri());
        hit.setIp(request.getIp());
        hit.setTimestamp(request.getTimestamp());
        service.saveHit(hit);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") boolean unique) {
        return service.getStats(start, end, uris, unique);
    }

    public static class HitRequest {
        @NotBlank
        private String app;
        @NotBlank
        private String uri;
        @NotBlank
        private String ip;
        @NotBlank
        private String timestamp;

        public String getApp() {
            return app;
        }

        public void setApp(String app) {
            this.app = app;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }
}
