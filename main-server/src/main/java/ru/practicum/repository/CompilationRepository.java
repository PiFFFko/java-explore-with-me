package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.compilation.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Integer> {

    @Query("select c " +
            "from Compilation as c " +
            "left join fetch c.events as e " +
            "where (?1 is null or c.pinned = ?1)")
    List<Compilation> findByPinned(Boolean pinned, Pageable pageable);
}
