package ru.practicum.ewm.main.service;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.dto.CategoryDto;
import ru.practicum.ewm.main.dto.NewCategoryDto;
import ru.practicum.ewm.main.exception.ApiException;
import ru.practicum.ewm.main.model.CategoryEntity;
import ru.practicum.ewm.main.repository.CategoryRepository;
import ru.practicum.ewm.main.repository.EventRepository;

import java.util.List;

@Service
@Transactional
public class CategoryService {
    private final CategoryRepository categories;
    private final EventRepository events;

    public CategoryService(CategoryRepository categories, EventRepository events) {
        this.categories = categories;
        this.events = events;
    }

    public CategoryDto add(NewCategoryDto dto) {
        return toDto(categories.save(new CategoryEntity(dto.name())));
    }

    public CategoryDto update(long id, CategoryDto dto) {
        CategoryEntity category = getEntity(id);
        category.setName(dto.name());
        return toDto(category);
    }

    public void delete(long id) {
        CategoryEntity category = getEntity(id);
        if (events.exists((root, query, builder) -> builder.equal(root.get("category").get("id"), id))) {
            throw new ApiException(HttpStatus.CONFLICT, "The category is not empty");
        }
        categories.delete(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getAll(int from, int size) {
        return categories.findAll(new OffsetPageRequest(from, size, Sort.by("id")))
                .map(this::toDto).getContent();
    }

    @Transactional(readOnly = true)
    public CategoryDto get(long id) {
        return toDto(getEntity(id));
    }

    private CategoryEntity getEntity(long id) {
        return categories.findById(id).orElseThrow(() ->
                new ApiException(HttpStatus.NOT_FOUND, "Category id=" + id + " was not found"));
    }

    private CategoryDto toDto(CategoryEntity category) {
        return new CategoryDto(category.getId(), category.getName());
    }
}
