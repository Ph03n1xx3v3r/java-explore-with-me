package ru.practicum.ewm.main.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.dto.NewUserRequest;
import ru.practicum.ewm.main.dto.UserDto;
import ru.practicum.ewm.main.exception.ApiException;
import ru.practicum.ewm.main.model.UserEntity;
import ru.practicum.ewm.main.repository.UserRepository;

import java.util.List;

@Service
@Transactional
public class UserService {
    private final UserRepository users;

    public UserService(UserRepository users) {
        this.users = users;
    }

    public UserDto add(NewUserRequest dto) {
        return toDto(users.save(new UserEntity(dto.name(), dto.email())));
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAll(List<Long> ids, int from, int size) {
        Pageable pageable = new OffsetPageRequest(from, size, Sort.by("id"));
        return (ids == null || ids.isEmpty() ? users.findAll(pageable)
                : users.findAll((root, query, builder) -> root.get("id").in(ids), pageable))
                .map(this::toDto).getContent();
    }

    public void delete(long id) {
        users.delete(users.findById(id).orElseThrow(() ->
                new ApiException(HttpStatus.NOT_FOUND, "User id=" + id + " was not found")));
    }

    private UserDto toDto(UserEntity user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }
}
