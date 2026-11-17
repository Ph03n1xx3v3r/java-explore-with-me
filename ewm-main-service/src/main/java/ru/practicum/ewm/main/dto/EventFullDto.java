package ru.practicum.ewm.main.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record EventFullDto(String annotation, CategoryDto category, Long confirmedRequests,
                               @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime createdOn,
                               String description, @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime eventDate,
                               Long id, UserShortDto initiator, Location location, Boolean paid,
                               Integer participantLimit,
                               @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime publishedOn,
                               Boolean requestModeration, ru.practicum.ewm.main.model.EventState state, String title, Long views) {
}
