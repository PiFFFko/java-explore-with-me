package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.exception.EndBeforeStartException;
import ru.practicum.mapper.HitMapper;
import ru.practicum.model.Hit;
import ru.practicum.model.hit.dto.HitDto;
import ru.practicum.model.hit.dto.ViewHitStatsDto;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final StatsRepository statsRepository;

    public void postHit(HitDto hitDto) {
        Hit hit = HitMapper.toHit(hitDto);
        statsRepository.save(hit);
    }

    public List<ViewHitStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        List<ViewHitStatsDto> result;
        if (end.isBefore(start)) {
            throw new EndBeforeStartException();
        }

        if (uris == null) {
            if (unique) {
                result = statsRepository.getUniqueStats(start, end);
            } else {
                result = statsRepository.getStats(start, end);
            }
        } else {
            if (unique) {
                result = statsRepository.getUniqueStatsByUris(start, end, uris);
            } else
                result = statsRepository.getStatsByUris(start, end, uris);
        }
        return result;
    }

}
