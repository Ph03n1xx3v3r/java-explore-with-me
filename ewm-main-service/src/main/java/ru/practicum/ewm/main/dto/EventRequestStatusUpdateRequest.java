package ru.practicum.ewm.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record EventRequestStatusUpdateRequest(@NotNull List<Long> requestIds, @NotBlank String status) {
}
