package ru.practicum.ewm.main.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.main.model.EventEntity;

import java.util.List;
import java.util.Optional;
    public interface EventRepository extends JpaRepository<EventEntity, Long> , JpaSpecificationExecutor<EventEntity>  {

    @EntityGraph(attributePaths = {
"category", "initiator"
})
    Page<EventEntity>  findByInitiatorId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {
"category", "initiator"
})
    @Override
    Page<EventEntity>  findAll(org.springframework.data.jpa.domain.Specification<EventEntity>  specification, 
                              Pageable pageable);

    @EntityGraph(attributePaths = {
"category", "initiator"
})
    @Override
    List<EventEntity>  findAll(org.springframework.data.jpa.domain.Specification<EventEntity>  specification, 
                              Sort sort);

    @Query("select e from EventEntity e join fetch e.category join fetch e.initiator where e.id = :id")
    Optional<EventEntity>  findDetailed(@Param("id") Long id);

    @Query("select count(r) from ParticipationRequestEntity r where r.event.id = :eventId and r.status = 'CONFIRMED'")
    long countConfirmed(@Param("eventId") Long eventId);

}
