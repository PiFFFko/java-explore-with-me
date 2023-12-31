package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Hit;
import ru.practicum.model.hit.dto.ViewHitStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Hit, Long> {

    @Query("select new ru.practicum.model.hit.dto.ViewHitStatsDto(s.app, s.uri, count(s.ip)) " +
            "from Hit as s " +
            "where s.date between :start and :end " +
            "group by s.app, s.uri " +
            "order by count(s.ip) desc")
    List<ViewHitStatsDto> getStats(@Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end);


    @Query("select new ru.practicum.model.hit.dto.ViewHitStatsDto(s.app, s.uri, count(distinct s.ip)) " +
            "from Hit as s " +
            "where s.date between ?1 and ?2 " +
            "group by s.app, s.uri " +
            "order by count(s.ip) desc")
    List<ViewHitStatsDto> getUniqueStats(@Param("start") LocalDateTime start,
                                         @Param("end") LocalDateTime end);

    @Query("select new ru.practicum.model.hit.dto.ViewHitStatsDto(s.app, s.uri, count(s.ip)) " +
            "from Hit as s " +
            "where s.date between :start and :end and s.uri in :uris " +
            "group by s.app, s.uri " +
            "order by count(s.ip) desc")
    List<ViewHitStatsDto> getStatsByUris(@Param("start") LocalDateTime start,
                                         @Param("end") LocalDateTime end,
                                         @Param("uris") List<String> uris);

    @Query("select new ru.practicum.model.hit.dto.ViewHitStatsDto(s.app, s.uri, count(distinct s.ip)) " +
            "from Hit as s " +
            "where s.date between :start and :end and s.uri in :uris " +
            "group by s.app, s.uri " +
            "order by count(s.ip) desc")
    List<ViewHitStatsDto> getUniqueStatsByUris(@Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end,
                                               @Param("uris") List<String> uris);

}
