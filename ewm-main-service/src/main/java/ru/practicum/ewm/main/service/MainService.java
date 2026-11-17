package ru.practicum.ewm.main.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.dto.*;
import ru.practicum.ewm.main.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Application facade. Domain rules live in the dedicated category, user, event,
 * request and compilation services.
 */
@Service
public class MainService {
    private final CategoryService categories;

    private final UserService users;

    private final EventService events;

    private final RequestService requests;

    private final CompilationService compilations;

    public MainService(CategoryService categories, UserService users, EventService events,
                       RequestService requests, CompilationService compilations) {
        this.categories = categories;
        this.users = users;
        this.events = events;
        this.requests = requests;
        this.compilations = compilations;
    }

    public CategoryDto addCategory(NewCategoryDto dto) {
        return categories.add(dto);
    }

    public CategoryDto updateCategory(long id, CategoryDto dto) {
        return categories.update(id, dto);
    }

    public void deleteCategory(long id) {
        categories.delete(id);
    }

    public List<CategoryDto> getCategories(int from, int size) {
        return categories.getAll(from, size);
    }

    public CategoryDto getCategory(long id) {
        return categories.get(id);
    }

    public UserDto addUser(NewUserRequest dto) {
        return users.add(dto);
    }

    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        return users.getAll(ids, from, size);
    }

    public void deleteUser(long id) {
        users.delete(id);
    }

    public EventFullDto addEvent(long userId, NewEventDto dto) {
        return events.addEvent(userId, dto);
    }

    public List<EventFullDto> userEvents(long userId, int from, int size) {
        return events.userEvents(userId, from, size);
    }

    public EventFullDto userEvent(long userId, long eventId) {
        return events.userEvent(userId, eventId);
    }

    public EventFullDto updateUserEvent(long userId, long eventId, UpdateEventUserRequest dto) {
        return events.updateUserEvent(userId, eventId, dto);
    }

    public EventFullDto updateAdminEvent(long id, UpdateEventAdminRequest dto) {
        return events.updateAdminEvent(id, dto);
    }

    public List<EventFullDto> adminEvents(List<Long> users, List<EventState> states, List<Long> categories,
                                          LocalDateTime start, LocalDateTime end, int from, int size) {
        return events.adminEvents(users, states, categories, start, end, from, size);
    }

    public List<EventShortDto> publicEvents(String text, List<Long> categories, Boolean paid, LocalDateTime start,
                                            LocalDateTime end, Boolean onlyAvailable, String sort, int from,
                                            int size, HttpServletRequest request) {
        return events.publicEvents(text, categories, paid, start, end, onlyAvailable, sort, from, size, request);
    }

    public EventFullDto publicEvent(long id, HttpServletRequest request) {
        return events.publicEvent(id, request);
    }

    public ParticipationRequestDto addRequest(long userId, long eventId) {
        return requests.add(userId, eventId);
    }

    public List<ParticipationRequestDto> userRequests(long userId) {
        return requests.getUserRequests(userId);
    }

    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        return requests.cancel(userId, requestId);
    }

    public List<ParticipationRequestDto> eventRequests(long userId, long eventId) {
        return requests.getEventRequests(userId, eventId);
    }

    public EventRequestStatusUpdateResult changeRequestStatus(long userId, long eventId,
                                                               EventRequestStatusUpdateRequest dto) {
        return requests.updateStatuses(userId, eventId, dto);
    }

    public CompilationDto addCompilation(NewCompilationDto dto) {
        return compilations.add(dto);
    }

    public CompilationDto updateCompilation(long id, UpdateCompilationRequest dto) {
        return compilations.update(id, dto);
    }

    public void deleteCompilation(long id) {
        compilations.delete(id);
    }

    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        return compilations.getAll(pinned, from, size);
    }

    public CompilationDto getCompilation(long id) {
        return compilations.get(id);
    }
}
