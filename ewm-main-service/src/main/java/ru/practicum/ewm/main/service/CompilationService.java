package ru.practicum.ewm.main.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.dto.CategoryDto;
import ru.practicum.ewm.main.dto.CompilationDto;
import ru.practicum.ewm.main.dto.EventShortDto;
import ru.practicum.ewm.main.exception.ApiException;
import ru.practicum.ewm.main.model.CompilationEntity;
import ru.practicum.ewm.main.model.EventEntity;
import ru.practicum.ewm.main.model.UserEntity;
import ru.practicum.ewm.main.repository.CompilationRepository;
import ru.practicum.ewm.main.repository.EventRepository;
import ru.practicum.ewm.main.repository.RequestRepository;
import ru.practicum.ewm.stats.client.StatsClient;
import ru.practicum.ewm.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class CompilationService {
    private final CompilationRepository compilations;
    private final EventRepository events;
    private final RequestRepository requests;
    private final StatsClient stats;

    public CompilationService(CompilationRepository compilations, EventRepository events,
                              RequestRepository requests, StatsClient stats) {
        this.compilations = compilations;
        this.events = events;
        this.requests = requests;
        this.stats = stats;
    }

    public CompilationDto add(ru.practicum.ewm.main.dto.NewCompilationDto dto) {
        CompilationEntity compilation = compilations.save(new CompilationEntity(dto.title(),
                Boolean.TRUE.equals(dto.pinned()), resolveEvents(dto.events())));
        return toDto(compilation);
    }

    public CompilationDto update(long id, ru.practicum.ewm.main.dto.UpdateCompilationRequest dto) {
        CompilationEntity compilation = getEntity(id);
        compilation.update(dto.title(), dto.pinned(), dto.events() == null ? null : resolveEvents(dto.events()));
        return toDto(compilation);
    }

    public void delete(long id) {
        compilations.delete(getEntity(id));
    }

    @Transactional(readOnly = true)
    public List<CompilationDto> getAll(Boolean pinned, int from, int size) {
        Page<CompilationEntity> page = pinned == null
                ? compilations.findAll(new OffsetPageRequest(from, size, Sort.by("id")))
                : compilations.findAll((root, query, builder) -> builder.equal(root.get("pinned"), pinned),
                new OffsetPageRequest(from, size, Sort.by("id")));
        return page.map(this::toDto).getContent();
    }

    @Transactional(readOnly = true)
    public CompilationDto get(long id) {
        return toDto(getEntity(id));
    }

    private CompilationEntity getEntity(long id) {
        return compilations.findById(id).orElseThrow(() ->
                new ApiException(HttpStatus.NOT_FOUND, "Compilation was not found"));
    }

    private List<EventEntity> resolveEvents(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        List<EventEntity> found = events.findAllById(ids);
        if (found.size() != ids.stream().distinct().count()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Event was not found");
        }
        return found;
    }

    private CompilationDto toDto(CompilationEntity compilation) {
        List<EventEntity> eventList = compilation.getEvents();
        Map<Long, Long> confirmed = confirmedCounts(eventList);
        Map<Long, Long> views = viewCounts(eventList);
        List<EventShortDto> eventDtos = eventList.stream().map(event -> new EventShortDto(
                event.getAnnotation(), new CategoryDto(event.getCategory().getId(), event.getCategory().getName()),
                confirmed.getOrDefault(event.getId(), 0L), event.getEventDate(), event.getId(),
                shortUser(event.getInitiator()), event.isPaid(), event.getTitle(), views.getOrDefault(event.getId(), 0L)))
                .toList();
        return new CompilationDto(eventDtos, compilation.getId(), compilation.isPinned(), compilation.getTitle());
    }

    private Map<Long, Long> confirmedCounts(List<EventEntity> eventList) {
        if (eventList.isEmpty()) {
            return Map.of();
        }
        List<Long> ids = eventList.stream().map(EventEntity::getId).toList();
        Map<Long, Long> counts = new HashMap<>();
        requests.countConfirmedByEventIds(ids).forEach(row -> counts.put((Long) row[0], (Long) row[1]));
        return counts;
    }

    private Map<Long, Long> viewCounts(List<EventEntity> eventList) {
        if (eventList.isEmpty()) {
            return Map.of();
        }
        List<String> uris = eventList.stream().map(event -> "/events/" + event.getId()).toList();
        try {
            return stats.getStats(LocalDateTime.of(2000, 1, 1, 0, 0), LocalDateTime.now().plusMinutes(1),
                    uris, true).stream().collect(Collectors.toMap(
                    view -> Long.parseLong(view.getUri().substring("/events/".length())),
                    ViewStats::getHits, (first, second) -> first));
        } catch (Exception exception) {
            return Map.of();
        }
    }

    private ru.practicum.ewm.main.dto.UserShortDto shortUser(UserEntity user) {
        return new ru.practicum.ewm.main.dto.UserShortDto(user.getId(), user.getName());
    }
}
