package ru.practicum.ewm.main.service;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.dto.ApiDtos.*;
import ru.practicum.ewm.main.exception.ApiException;
import ru.practicum.ewm.main.model.*;
import ru.practicum.ewm.main.repository.*;
import ru.practicum.ewm.stats.client.StatsClient;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class MainService {

 private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final UserRepository users;
    private final CategoryRepository categories;
    private final EventRepository events;
    private final RequestRepository requests;
    private final CompilationRepository compilations;
    private final StatsClient stats;

    public MainService(UserRepository users, CategoryRepository categories, EventRepository events, RequestRepository requests, 
                    CompilationRepository compilations, StatsClient stats) {
this.users = users;this.categories = categories;this.events = events;this.requests = requests;this.compilations = compilations;this.stats = stats;
}


    private ApiException notFound(String what) {
return new ApiException(HttpStatus.NOT_FOUND, what + " was not found");
}


    private ApiException conflict(String message) {
return new ApiException(HttpStatus.CONFLICT, message);
}


    private UserEntity user(long id) {
return users.findById(id).orElseThrow(() -> notFound("User id=" + id));
}


    private CategoryEntity category(long id) {
return categories.findById(id).orElseThrow(() -> notFound("Category id=" + id));
}


    private EventEntity event(long id) {
return events.findDetailed(id).orElseThrow(() -> notFound("Event id=" + id));
}


    private List<EventEntity>  resolveEvents(List<Long>  ids) {
if (ids == null || ids.isEmpty()) return new ArrayList<>();List<EventEntity>  found = events.findAllById(ids);if (found.size() != new HashSet<>(ids).size()) throw notFound("Event");return found;
}


    private CategoryDto cat(CategoryEntity c) {
return new CategoryDto(c.getId(), c.getName());
}


    private UserShortDto shortUser(UserEntity u) {
return new UserShortDto(u.getId(), u.getName());
}


    private long confirmed(long id) {
return requests.countByEventIdAndStatus(id, "CONFIRMED");
}


    private long views(long id) {
try {
return stats.getStats(LocalDateTime.of(2000, 1, 1, 0, 0), LocalDateTime.now().plusMinutes(1), List.of("/events/" + id), true).stream().mapToLong(ViewStats::getHits).sum();
} catch (Exception ex) {
return 0;
}
}


    private EventShortDto shortEvent(EventEntity e) {
return new EventShortDto(e.getAnnotation(), cat(e.getCategory()), confirmed(e.getId()), e.getEventDate(), e.getId(), shortUser(e.getInitiator()), e.isPaid(), e.getTitle(), views(e.getId()));
}


    private EventFullDto fullEvent(EventEntity e) {
return new EventFullDto(e.getAnnotation(), cat(e.getCategory()), confirmed(e.getId()), e.getCreatedOn(), e.getDescription(), e.getEventDate(), e.getId(), shortUser(e.getInitiator()), new Location(e.getLat(), e.getLon()), e.isPaid(), e.getParticipantLimit(), e.getPublishedOn(), e.isRequestModeration(), e.getState(), e.getTitle(), views(e.getId()));
}


    private ParticipationRequestDto requestDto(ParticipationRequestEntity r) {
return new ParticipationRequestDto(r.getCreated(), r.getEvent().getId(), r.getId(), r.getRequester().getId(), r.getStatus());
}


    private Map<Long, Long>  confirmedCounts(List<EventEntity>  list) {

  if (list.isEmpty()) return Map.of();List<Long>  ids = list.stream().map(EventEntity::getId).toList();Map<Long, Long>  result = new HashMap<>();
  requests.countConfirmedByEventIds(ids).forEach(row -> result.put((Long)row[0], (Long)row[1]));return result;

}


    private Map<Long, Long>  viewCounts(List<EventEntity>  list) {

  if (list.isEmpty()) return Map.of();List<String>  uris = list.stream().map(e -> "/events/" + e.getId()).toList();
  try {
return stats.getStats(LocalDateTime.of(2000, 1, 1, 0, 0), LocalDateTime.now().plusMinutes(1), uris, true).stream().collect(Collectors.toMap(v -> Long.parseLong(v.getUri().substring("/events/".length())), ViewStats::getHits, (a, b) -> a));
} catch (Exception ex) {
return Map.of();
}

}


    private EventShortDto shortEvent(EventEntity e, Map<Long, Long>  confirmed, Map<Long, Long>  views) {
return new EventShortDto(e.getAnnotation(), cat(e.getCategory()), confirmed.getOrDefault(e.getId(), 0L), e.getEventDate(), e.getId(), shortUser(e.getInitiator()), e.isPaid(), e.getTitle(), views.getOrDefault(e.getId(), 0L));
}


    private EventFullDto fullEvent(EventEntity e, Map<Long, Long>  confirmed, Map<Long, Long>  views) {
return new EventFullDto(e.getAnnotation(), cat(e.getCategory()), confirmed.getOrDefault(e.getId(), 0L), e.getCreatedOn(), e.getDescription(), e.getEventDate(), e.getId(), shortUser(e.getInitiator()), new Location(e.getLat(), e.getLon()), e.isPaid(), e.getParticipantLimit(), e.getPublishedOn(), e.isRequestModeration(), e.getState(), e.getTitle(), views.getOrDefault(e.getId(), 0L));
}


    private List<EventFullDto>  fullEvents(List<EventEntity>  list) {
Map<Long, Long>  c = confirmedCounts(list), v = viewCounts(list);return list.stream().map(e -> fullEvent(e, c, v)).toList();
}


    private List<EventShortDto>  shortEvents(List<EventEntity>  list) {
Map<Long, Long>  c = confirmedCounts(list), v = viewCounts(list);return list.stream().map(e -> shortEvent(e, c, v)).toList();
}


    private void hit(HttpServletRequest request, String uri) {
try {
stats.saveHit(new EndpointHit(null, "ewm-main-service", uri, request.getRemoteAddr(), LocalDateTime.now().format(FORMAT)));
} catch (Exception ignored) {
/* stats service must not break public reads */
}
}


    public CategoryDto addCategory(NewCategoryDto dto) {
return cat(categories.save(new CategoryEntity(dto.name())));
}


    public CategoryDto updateCategory(long id, CategoryDto dto) {
CategoryEntity c = category(id);c.setName(dto.name());return cat(c);
}


    public void deleteCategory(long id) {
CategoryEntity c = category(id);if (events.exists((r, q, b) -> b.equal(r.get("category").get("id"), id))) throw conflict("The category is not empty");categories.delete(c);
}

 @Transactional(readOnly = true) public List<CategoryDto>  getCategories(int from, int size) {
return categories.findAll(new OffsetPageRequest(from, size, Sort.by("id"))).map(this::cat).getContent();
}

 @Transactional(readOnly = true) public CategoryDto getCategory(long id) {
return cat(category(id));
}


    public UserDto addUser(NewUserRequest dto) {
return userDto(users.save(new UserEntity(dto.name(), dto.email())));
}


    private UserDto userDto(UserEntity u) {
return new UserDto(u.getId(), u.getName(), u.getEmail());
}

 @Transactional(readOnly = true) public List<UserDto>  getUsers(List<Long>  ids, int from, int size) {
Pageable p = new OffsetPageRequest(from, size, Sort.by("id"));return (ids == null || ids.isEmpty() ? users.findAll(p) : users.findAll((r, q, b) -> r.get("id").in(ids), p)).map(this::userDto).getContent();
}


    public void deleteUser(long id) {
users.delete(user(id));
}


    public EventFullDto addEvent(long userId, NewEventDto d) {
UserEntity u = user(userId);CategoryEntity c = category(d.category());if (d.eventDate().isBefore(LocalDateTime.now().plusHours(2))) throw new ApiException(HttpStatus.BAD_REQUEST, "Event date must be at least two hours in the future");if (d.participantLimit() != null && d.participantLimit() < 0) throw new ApiException(HttpStatus.BAD_REQUEST, "Participant limit must not be negative");EventEntity e = new EventEntity(d.annotation(), c, d.description(), d.eventDate(), d.location().lat(), d.location().lon(), u, Boolean.TRUE.equals(d.paid()), d.participantLimit() == null ? 0 : d.participantLimit(), d.requestModeration() == null || d.requestModeration(), d.title());return fullEvent(events.save(e), Map.of(e.getId(), 0L), Map.of());
}

 @Transactional(readOnly = true) public List<EventFullDto>  userEvents(long userId, int from, int size) {
user(userId);return fullEvents(events.findByInitiatorId(userId, new OffsetPageRequest(from, size, Sort.by("id"))).getContent());
}

 @Transactional(readOnly = true) public EventFullDto userEvent(long userId, long eventId) {
EventEntity e = event(eventId);if (!e.getInitiator().getId().equals(userId)) throw notFound("Event");return fullEvent(e, Map.of(e.getId(), confirmed(e.getId())), Map.of(e.getId(), views(e.getId())));
}


    public EventFullDto updateUserEvent(long userId, long eventId, UpdateEventUserRequest d) {
EventEntity e = event(eventId);if (!e.getInitiator().getId().equals(userId)) throw notFound("Event");if ("PUBLISHED".equals(e.getState())) throw conflict("Only pending or canceled events can be changed");CategoryEntity c = d.category() == null ? null : category(d.category());if (d.eventDate() != null && d.eventDate().isBefore(LocalDateTime.now().plusHours(2))) throw new ApiException(HttpStatus.BAD_REQUEST, "Event date must be at least two hours in the future");e.update(d.annotation(), c, d.description(), d.eventDate(), d.location() == null ? null : d.location().lat(), d.location() == null ? null : d.location().lon(), d.paid(), d.participantLimit(), d.requestModeration(), d.title());if ("SEND_TO_REVIEW".equals(d.stateAction()))e.sendToReview();if ("CANCEL_REVIEW".equals(d.stateAction()))e.cancel();return fullEvent(e, Map.of(e.getId(), confirmed(e.getId())), Map.of(e.getId(), views(e.getId())));
}


    public EventFullDto updateAdminEvent(long id, UpdateEventAdminRequest d) {
EventEntity e = event(id);CategoryEntity c = d.category() == null ? null : category(d.category());if (d.eventDate() != null && e.getPublishedOn() != null && d.eventDate().isBefore(e.getPublishedOn().plusHours(1))) throw conflict("Event date must be at least one hour after publication");e.update(d.annotation(), c, d.description(), d.eventDate(), d.location() == null ? null : d.location().lat(), d.location() == null ? null : d.location().lon(), d.paid(), d.participantLimit(), d.requestModeration(), d.title());if ("PUBLISH_EVENT".equals(d.stateAction())) {
if (!"PENDING".equals(e.getState())) throw conflict("Only pending events can be published");if (e.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) throw conflict("Event date must be at least one hour in future");e.publish();
} else if ("REJECT_EVENT".equals(d.stateAction())) {
if ("PUBLISHED".equals(e.getState())) throw conflict("Published event cannot be rejected");e.reject();
}return fullEvent(e, Map.of(e.getId(), confirmed(e.getId())), Map.of(e.getId(), views(e.getId())));
}


    private Specification<EventEntity>  eventFilters(List<Long>  userIds, List<String>  states, List<Long>  categoryIds, LocalDateTime start, LocalDateTime end, String text, Boolean paid, Boolean onlyAvailable) {
return (r, q, b) -> {
List<jakarta.persistence.criteria.Predicate>  p = new ArrayList<>();if (userIds != null && !userIds.isEmpty())p.add(r.get("initiator").get("id").in(userIds));if (states != null && !states.isEmpty())p.add(r.get("state").in(states));if (categoryIds != null && !categoryIds.isEmpty())p.add(r.get("category").get("id").in(categoryIds));if (start != null)p.add(b.greaterThanOrEqualTo(r.get("eventDate"), start));if (end != null)p.add(b.lessThanOrEqualTo(r.get("eventDate"), end));if (text != null && !text.isBlank()) {
String t = "%" + text.toLowerCase() + "%";p.add(b.or(b.like(b.lower(r.get("annotation")), t), b.like(b.lower(r.get("description")), t)));
}if (paid != null)p.add(b.equal(r.get("paid"), paid));if (Boolean.TRUE.equals(onlyAvailable)) {
jakarta.persistence.criteria.Subquery<Long>  sq = q.subquery(Long.class);jakarta.persistence.criteria.Root<ParticipationRequestEntity>  req = sq.from(ParticipationRequestEntity.class);sq.select(b.count(req)).where(b.equal(req.get("event").get("id"), r.get("id")), b.equal(req.get("status"), "CONFIRMED"));p.add(b.or(b.equal(r.get("participantLimit"), 0), b.gt(r.<Integer>get("participantLimit"), sq)));
}return b.and(p.toArray(new jakarta.persistence.criteria.Predicate[0]));
};
}

 @Transactional(readOnly = true) public List<EventFullDto>  adminEvents(List<Long>  us, List<String>  states, List<Long>  cats, LocalDateTime start, LocalDateTime end, int from, int size) {
if (start != null && end != null && start.isAfter(end)) throw new ApiException(HttpStatus.BAD_REQUEST, "rangeStart must not be after rangeEnd");return fullEvents(events.findAll(eventFilters(us, states, cats, start, end, null, null, null), new OffsetPageRequest(from, size, Sort.by("eventDate"))).getContent());
}

 @Transactional(readOnly = true) public List<EventShortDto>  publicEvents(String text, List<Long>  cats, Boolean paid, LocalDateTime start, LocalDateTime end, Boolean onlyAvailable, String sort, int from, int size, HttpServletRequest request) {
hit(request, "/events");
  if (from < 0 || size < 1) throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid pagination parameters");
  if (start == null && end == null)start = LocalDateTime.now();
  if (start != null && end != null && start.isAfter(end)) throw new ApiException(HttpStatus.BAD_REQUEST, "rangeStart must not be after rangeEnd");
  if (sort != null && !sort.isBlank() && !"EVENT_DATE".equals(sort) && !"VIEWS".equals(sort)) throw new ApiException(HttpStatus.BAD_REQUEST, "Unknown sort type");
  Specification<EventEntity>  f = eventFilters(null, List.of("PUBLISHED"), cats, start, end, text, paid, onlyAvailable);List<EventEntity>  list;
  if ("VIEWS".equals(sort)) {
list = events.findAll(f, Sort.by("eventDate"));List<EventShortDto>  all = shortEvents(list);return all.stream().sorted(Comparator.comparing(EventShortDto::views).reversed()).skip(from).limit(size).toList();
}
  list = events.findAll(f, new OffsetPageRequest(from, size, Sort.by("eventDate"))).getContent();return shortEvents(list);
}

 @Transactional(readOnly = true) public EventFullDto publicEvent(long id, HttpServletRequest request) {
hit(request, "/events/" + id);EventEntity e = event(id);if (!"PUBLISHED".equals(e.getState())) throw notFound("Event");return fullEvent(e, Map.of(e.getId(), confirmed(e.getId())), Map.of(e.getId(), views(e.getId())));
}


    public CompilationDto addCompilation(NewCompilationDto d) {
CompilationEntity c = compilations.save(new CompilationEntity(d.title(), Boolean.TRUE.equals(d.pinned()), resolveEvents(d.events())));return compilationDto(c);
}


    public CompilationDto updateCompilation(long id, UpdateCompilationRequest d) {
CompilationEntity c = compilations.findById(id).orElseThrow(() -> notFound("Compilation"));c.update(d.title(), d.pinned(), d.events() == null ? null : resolveEvents(d.events()));return compilationDto(c);
}


    public void deleteCompilation(long id) {
compilations.delete(compilations.findById(id).orElseThrow(() -> notFound("Compilation")));
}

 @Transactional(readOnly = true) public List<CompilationDto>  getCompilations(Boolean pinned, int from, int size) {
Page<CompilationEntity>  page = pinned == null ? compilations.findAll(new OffsetPageRequest(from, size, Sort.by("id"))) : compilations.findAll((r, q, b) -> b.equal(r.get("pinned"), pinned), new OffsetPageRequest(from, size, Sort.by("id")));return page.map(this::compilationDto).getContent();
}

 @Transactional(readOnly = true) public CompilationDto getCompilation(long id) {
return compilationDto(compilations.findById(id).orElseThrow(() -> notFound("Compilation")));
}


    private CompilationDto compilationDto(CompilationEntity c) {
return new CompilationDto(shortEvents(c.getEvents()), c.getId(), c.isPinned(), c.getTitle());
}


    public ParticipationRequestDto addRequest(long userId, long eventId) {
UserEntity u = user(userId);EventEntity e = event(eventId);if (e.getInitiator().getId().equals(userId)) throw conflict("Initiator cannot request own event");if (!"PUBLISHED".equals(e.getState())) throw conflict("Event is not published");if (requests.existsByRequesterIdAndEventId(userId, eventId)) throw conflict("Request already exists");if (e.getParticipantLimit() > 0 && confirmed(eventId) >= e.getParticipantLimit()) throw conflict("Participant limit reached");ParticipationRequestEntity r = new ParticipationRequestEntity(e, u);if (!e.isRequestModeration() || e.getParticipantLimit() == 0)r.setStatus("CONFIRMED");return requestDto(requests.save(r));
}

 @Transactional(readOnly = true) public List<ParticipationRequestDto>  userRequests(long userId) {
user(userId);return requests.findByRequesterIdOrderByIdAsc(userId).stream().map(this::requestDto).toList();
}


    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
user(userId);ParticipationRequestEntity r = requests.findById(requestId).orElseThrow(() -> notFound("Request"));if (!r.getRequester().getId().equals(userId)) throw notFound("Request");r.setStatus("CANCELED");return requestDto(r);
}

 @Transactional(readOnly = true) public List<ParticipationRequestDto>  eventRequests(long userId, long eventId) {
EventEntity e = event(eventId);if (!e.getInitiator().getId().equals(userId)) throw notFound("Event");return requests.findByEventIdOrderByIdAsc(eventId).stream().map(this::requestDto).toList();
}


    public EventRequestStatusUpdateResult changeRequestStatus(long userId, long eventId, EventRequestStatusUpdateRequest d) {

  EventEntity e = event(eventId);
  if (!e.getInitiator().getId().equals(userId)) throw notFound("Event");
  if (e.getParticipantLimit() == 0 || !e.isRequestModeration()) throw conflict("Requests are not moderated");
  if (!"CONFIRMED".equals(d.status()) && !"REJECTED".equals(d.status())) throw new ApiException(HttpStatus.BAD_REQUEST, "Unknown request status");
  List<ParticipationRequestDto>  confirmedList = new ArrayList<>(), rejectedList = new ArrayList<>();
  for (Long id : d.requestIds()) {

   ParticipationRequestEntity r = requests.findById(id).orElseThrow(() -> notFound("Request"));
   if (!r.getEvent().getId().equals(eventId) || !"PENDING".equals(r.getStatus())) throw conflict("Request cannot be updated");
   if ("REJECTED".equals(d.status())) {
r.setStatus("REJECTED");rejectedList.add(requestDto(r));
} else if (confirmed(eventId) < e.getParticipantLimit()) {

    r.setStatus("CONFIRMED");confirmedList.add(requestDto(r));

} else {

    r.setStatus("REJECTED");rejectedList.add(requestDto(r));

}

}
  if (confirmed(eventId) >= e.getParticipantLimit()) {

   requests.findByEventIdOrderByIdAsc(eventId).stream().filter(r -> "PENDING".equals(r.getStatus())).forEach(r -> r.setStatus("REJECTED"));

}
  return new EventRequestStatusUpdateResult(confirmedList, rejectedList);

}


}
