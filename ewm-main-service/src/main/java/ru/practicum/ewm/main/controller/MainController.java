package ru.practicum.ewm.main.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.ApiDtos.*;
import ru.practicum.ewm.main.service.MainService;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class MainController {

 private final MainService service;

    public MainController(MainService service) {
this.service = service;
}

 @PostMapping("/admin/categories") @ResponseStatus(HttpStatus.CREATED) public CategoryDto addCategory(@Valid @RequestBody NewCategoryDto d) {
return service.addCategory(d);
}

 @PatchMapping("/admin/categories/{catId}") public CategoryDto updateCategory(@PathVariable long catId, @Valid @RequestBody CategoryDto d) {
return service.updateCategory(catId, d);
}

 @DeleteMapping("/admin/categories/{catId}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteCategory(@PathVariable long catId) {
service.deleteCategory(catId);
}

 @GetMapping("/categories") public List<CategoryDto>  categories(@RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "10") int size) {
return service.getCategories(from, size);
}

 @GetMapping("/categories/{catId}") public CategoryDto category(@PathVariable long catId) {
return service.getCategory(catId);
}

 @PostMapping("/admin/users") @ResponseStatus(HttpStatus.CREATED) public UserDto addUser(@Valid @RequestBody NewUserRequest d) {
return service.addUser(d);
}

 @GetMapping("/admin/users") public List<UserDto>  users(@RequestParam(required = false) List<Long>  ids, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "10") int size) {
return service.getUsers(ids, from, size);
}

 @DeleteMapping("/admin/users/{userId}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteUser(@PathVariable long userId) {
service.deleteUser(userId);
}

 @PostMapping("/users/{userId}/events") @ResponseStatus(HttpStatus.CREATED) public EventFullDto addEvent(@PathVariable long userId, @Valid @RequestBody NewEventDto d) {
return service.addEvent(userId, d);
}

 @GetMapping("/users/{userId}/events") public List<EventFullDto>  userEvents(@PathVariable long userId, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "10") int size) {
return service.userEvents(userId, from, size);
}

 @GetMapping("/users/{userId}/events/{eventId}") public EventFullDto userEvent(@PathVariable long userId, @PathVariable long eventId) {
return service.userEvent(userId, eventId);
}

 @PatchMapping("/users/{userId}/events/{eventId}") public EventFullDto updateUserEvent(@PathVariable long userId, @PathVariable long eventId, @RequestBody UpdateEventUserRequest d) {
return service.updateUserEvent(userId, eventId, d);
}

 @GetMapping("/admin/events") public List<EventFullDto>  adminEvents(@RequestParam(required = false) List<Long>  users, @RequestParam(required = false) List<String>  states, @RequestParam(required = false) List<Long>  categories, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "10") int size) {
return service.adminEvents(users, states, categories, rangeStart, rangeEnd, from, size);
}

 @PatchMapping("/admin/events/{eventId}") public EventFullDto updateAdminEvent(@PathVariable long eventId, @RequestBody UpdateEventAdminRequest d) {
return service.updateAdminEvent(eventId, d);
}

 @GetMapping("/events") public List<EventShortDto>  publicEvents(@RequestParam(required = false) String text, @RequestParam(required = false) List<Long>  categories, @RequestParam(required = false) Boolean paid, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd, @RequestParam(defaultValue = "false") Boolean onlyAvailable, @RequestParam(required = false) String sort, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "10") int size, HttpServletRequest request) {
return service.publicEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
}

 @GetMapping("/events/{id}") public EventFullDto publicEvent(@PathVariable long id, HttpServletRequest request) {
return service.publicEvent(id, request);
}

 @PostMapping("/admin/compilations") @ResponseStatus(HttpStatus.CREATED) public CompilationDto addCompilation(@Valid @RequestBody NewCompilationDto d) {
return service.addCompilation(d);
}

 @PatchMapping("/admin/compilations/{compId}") public CompilationDto updateCompilation(@PathVariable long compId, @RequestBody UpdateCompilationRequest d) {
return service.updateCompilation(compId, d);
}

 @DeleteMapping("/admin/compilations/{compId}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteCompilation(@PathVariable long compId) {
service.deleteCompilation(compId);
}

 @GetMapping("/compilations") public List<CompilationDto>  compilations(@RequestParam(required = false) Boolean pinned, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "10") int size) {
return service.getCompilations(pinned, from, size);
}

 @GetMapping("/compilations/{compId}") public CompilationDto compilation(@PathVariable long compId) {
return service.getCompilation(compId);
}

 @PostMapping("/users/{userId}/requests") @ResponseStatus(HttpStatus.CREATED) public ParticipationRequestDto addRequest(@PathVariable long userId, @RequestParam long eventId) {
return service.addRequest(userId, eventId);
}

 @GetMapping("/users/{userId}/requests") public List<ParticipationRequestDto>  userRequests(@PathVariable long userId) {
return service.userRequests(userId);
}

 @PatchMapping("/users/{userId}/requests/{requestId}/cancel") public ParticipationRequestDto cancelRequest(@PathVariable long userId, @PathVariable long requestId) {
return service.cancelRequest(userId, requestId);
}

 @GetMapping("/users/{userId}/events/{eventId}/requests") public List<ParticipationRequestDto>  eventRequests(@PathVariable long userId, @PathVariable long eventId) {
return service.eventRequests(userId, eventId);
}

 @PatchMapping("/users/{userId}/events/{eventId}/requests") public EventRequestStatusUpdateResult changeRequestStatus(@PathVariable long userId, @PathVariable long eventId, @Valid @RequestBody EventRequestStatusUpdateRequest d) {
return service.changeRequestStatus(userId, eventId, d);
}

}
