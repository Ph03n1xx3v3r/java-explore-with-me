package ru.practicum.ewm.main.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
    public final class ApiDtos {


    private ApiDtos() {}


    public record Location(float lat, float lon) {}


    public record NewCategoryDto(@NotBlank String name) {}


    public record CategoryDto(Long id, @NotBlank String name) {}


    public record NewUserRequest(@NotBlank String name, @Email @NotBlank String email) {}


    public record UserDto(Long id, String name, String email) {}


    public record UserShortDto(Long id, String name) {}


    public record NewEventDto(@NotBlank @Size(min = 20, max = 2000) String annotation, 
            @NotNull Long category, @NotBlank @Size(min = 20, max = 7000) String description, 
            @NotNull @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime eventDate, 
            @NotNull Location location, Boolean paid, Integer participantLimit, 
            Boolean requestModeration, @NotBlank @Size(min = 3, max = 120) String title) {}


    public record UpdateEventUserRequest(String annotation, Long category, String description, 
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime eventDate, 
            Location location, Boolean paid, Integer participantLimit, 
            Boolean requestModeration, String stateAction, String title) {}


    public record UpdateEventAdminRequest(String annotation, Long category, String description, 
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime eventDate, 
            Location location, Boolean paid, Integer participantLimit, 
            Boolean requestModeration, String stateAction, String title) {}


    public record EventShortDto(String annotation, CategoryDto category, Long confirmedRequests, 
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime eventDate, 
            Long id, UserShortDto initiator, Boolean paid, String title, Long views) {}


    public record EventFullDto(String annotation, CategoryDto category, Long confirmedRequests, 
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime createdOn, 
            String description, @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime eventDate, 
            Long id, UserShortDto initiator, Location location, Boolean paid, 
            Integer participantLimit, @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime publishedOn, 
            Boolean requestModeration, String state, String title, Long views) {}


    public record NewCompilationDto(List<Long>  events, Boolean pinned, @NotBlank String title) {}


    public record UpdateCompilationRequest(List<Long>  events, Boolean pinned, String title) {}


    public record CompilationDto(List<EventShortDto>  events, Long id, Boolean pinned, String title) {}


    public record ParticipationRequestDto(@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime created, 
            Long event, Long id, Long requester, String status) {}


    public record EventRequestStatusUpdateRequest(@NotNull List<Long>  requestIds, @NotBlank String status) {}


    public record EventRequestStatusUpdateResult(List<ParticipationRequestDto>  confirmedRequests, 
            List<ParticipationRequestDto>  rejectedRequests) {}

}
