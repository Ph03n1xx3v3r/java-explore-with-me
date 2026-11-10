package ru.practicum.ewm.main.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.ewm.main.model.CompilationEntity;

import java.util.Optional;
    public interface CompilationRepository extends JpaRepository<CompilationEntity, Long> , 
        JpaSpecificationExecutor<CompilationEntity>  {

    @EntityGraph(attributePaths = {
"events", "events.category", "events.initiator"
})
    @Override
    Page<CompilationEntity>  findAll(Pageable pageable);

    @EntityGraph(attributePaths = {
"events", "events.category", "events.initiator"
})
    @Override
    Page<CompilationEntity>  findAll(org.springframework.data.jpa.domain.Specification<CompilationEntity>  specification, 
                                    Pageable pageable);

    @EntityGraph(attributePaths = {
"events", "events.category", "events.initiator"
})
    @Override
    Optional<CompilationEntity>  findById(Long id);

}
