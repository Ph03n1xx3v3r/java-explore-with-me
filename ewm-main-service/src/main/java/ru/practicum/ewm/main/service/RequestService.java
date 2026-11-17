package ru.practicum.ewm.main.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.main.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.main.dto.ParticipationRequestDto;
import ru.practicum.ewm.main.exception.ApiException;
import ru.practicum.ewm.main.model.EventEntity;
import ru.practicum.ewm.main.model.EventState;
import ru.practicum.ewm.main.model.ParticipationRequestEntity;
import ru.practicum.ewm.main.model.UserEntity;
import ru.practicum.ewm.main.repository.EventRepository;
import ru.practicum.ewm.main.repository.RequestRepository;
import ru.practicum.ewm.main.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class RequestService {
    private final RequestRepository requests;
    private final UserRepository users;
    private final EventRepository events;

    public RequestService(RequestRepository requests, UserRepository users, EventRepository events) {
        this.requests = requests;
        this.users = users;
        this.events = events;
    }

    public ParticipationRequestDto add(long userId, long eventId) {
        UserEntity user = getUser(userId);
        EventEntity event = getEvent(eventId);
        if (event.getInitiator().getId().equals(userId)) {
            throw conflict("Initiator cannot request own event");
        }
        if (event.getState() != EventState.PUBLISHED) {
            throw conflict("Event is not published");
        }
        if (requests.existsByRequesterIdAndEventId(userId, eventId)) {
            throw conflict("Request already exists");
        }
        if (event.getParticipantLimit() > 0
                && requests.countByEventIdAndStatus(eventId, "CONFIRMED") >= event.getParticipantLimit()) {
            throw conflict("Participant limit reached");
        }
        ParticipationRequestEntity request = new ParticipationRequestEntity(event, user);
        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus("CONFIRMED");
        }
        return toDto(requests.save(request));
    }

    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getUserRequests(long userId) {
        getUser(userId);
        return requests.findByRequesterIdOrderByIdAsc(userId).stream().map(this::toDto).toList();
    }

    public ParticipationRequestDto cancel(long userId, long requestId) {
        getUser(userId);
        ParticipationRequestEntity request = requests.findById(requestId).orElseThrow(() -> notFound("Request"));
        if (!request.getRequester().getId().equals(userId)) {
            throw notFound("Request");
        }
        request.setStatus("CANCELED");
        return toDto(request);
    }

    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventRequests(long userId, long eventId) {
        EventEntity event = getEvent(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw notFound("Event");
        }
        return requests.findByEventIdOrderByIdAsc(eventId).stream().map(this::toDto).toList();
    }

    public EventRequestStatusUpdateResult updateStatuses(long userId, long eventId,
                                                         EventRequestStatusUpdateRequest dto) {
        EventEntity event = getEvent(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw notFound("Event");
        }
        if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
            throw conflict("Requests are not moderated");
        }
        if (!"CONFIRMED".equals(dto.status()) && !"REJECTED".equals(dto.status())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Unknown request status");
        }

        List<ParticipationRequestDto> confirmed = new ArrayList<>();
        List<ParticipationRequestDto> rejected = new ArrayList<>();
        long confirmedCount = requests.countByEventIdAndStatus(eventId, "CONFIRMED");
        for (Long requestId : dto.requestIds()) {
            ParticipationRequestEntity request = requests.findById(requestId)
                    .orElseThrow(() -> notFound("Request"));
            if (!request.getEvent().getId().equals(eventId) || !"PENDING".equals(request.getStatus())) {
                throw conflict("Request cannot be updated");
            }
            if ("REJECTED".equals(dto.status()) || confirmedCount >= event.getParticipantLimit()) {
                request.setStatus("REJECTED");
                rejected.add(toDto(request));
            } else {
                request.setStatus("CONFIRMED");
                confirmed.add(toDto(request));
                confirmedCount++;
            }
        }
        if (confirmedCount >= event.getParticipantLimit()) {
            requests.findByEventIdOrderByIdAsc(eventId).stream()
                    .filter(request -> "PENDING".equals(request.getStatus()))
                    .forEach(request -> request.setStatus("REJECTED"));
        }
        return new EventRequestStatusUpdateResult(confirmed, rejected);
    }

    private UserEntity getUser(long id) {
        return users.findById(id).orElseThrow(() -> notFound("User id=" + id));
    }

    private EventEntity getEvent(long id) {
        return events.findDetailed(id).orElseThrow(() -> notFound("Event id=" + id));
    }

    private ParticipationRequestDto toDto(ParticipationRequestEntity request) {
        return new ParticipationRequestDto(request.getCreated(), request.getEvent().getId(), request.getId(),
                request.getRequester().getId(), request.getStatus());
    }

    private ApiException notFound(String what) {
        return new ApiException(HttpStatus.NOT_FOUND, what + " was not found");
    }

    private ApiException conflict(String message) {
        return new ApiException(HttpStatus.CONFLICT, message);
    }
}
