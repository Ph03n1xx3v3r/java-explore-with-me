package ru.practicum.ewm.main.dto;

import java.util.List;

public record EventRequestStatusUpdateResult(List<ParticipationRequestDto> confirmedRequests,
                                                 List<ParticipationRequestDto> rejectedRequests) {
}
