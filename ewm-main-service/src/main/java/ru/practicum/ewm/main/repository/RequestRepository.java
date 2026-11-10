package ru.practicum.ewm.main.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main.model.ParticipationRequestEntity;
import java.util.List;
    public interface RequestRepository extends JpaRepository<ParticipationRequestEntity, Long> {

 List<ParticipationRequestEntity>  findByRequesterIdOrderByIdAsc(Long requesterId);
 List<ParticipationRequestEntity>  findByEventIdOrderByIdAsc(Long eventId);
 boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);
 long countByEventIdAndStatus(Long eventId, String status);
 @org.springframework.data.jpa.repository.Query("select r.event.id, count(r) from ParticipationRequestEntity r where r.event.id in :ids and r.status = 'CONFIRMED' group by r.event.id")
 List < Object[] >  countConfirmedByEventIds(@org.springframework.data.repository.query.Param("ids") List<Long>  ids);

}
