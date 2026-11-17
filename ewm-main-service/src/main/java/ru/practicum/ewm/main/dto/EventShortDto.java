package ru.practicum.ewm.main.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record EventShortDto(String annotation, CategoryDto category, Long confirmedRequests,
                                @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime eventDate,
                                Long id, UserShortDto initiator, Boolean paid, String title, Long views) {
}
