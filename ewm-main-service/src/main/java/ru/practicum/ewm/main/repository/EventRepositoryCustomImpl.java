package ru.practicum.ewm.main.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.ewm.main.model.EventEntity;

import java.util.List;

public class EventRepositoryCustomImpl implements EventRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Long> findIds(Specification<EventEntity> specification) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<EventEntity> root = query.from(EventEntity.class);
        query.select(root.get("id"));
        query.where(specification.toPredicate(root, query, builder));
        query.orderBy(builder.asc(root.get("eventDate")));
        return entityManager.createQuery(query).getResultList();
    }
}
