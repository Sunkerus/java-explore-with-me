package ru.practicum.stats.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.StatsServiceThings;
import ru.practicum.common.structures.HitDto;
import ru.practicum.common.structures.ViewStats;
import ru.practicum.stats.sevice.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;


    @GetMapping("/stats")
    public List<ViewStats> getStats(
            @DateTimeFormat(pattern = StatsServiceThings.DATE_TIME_FORMAT) @RequestParam LocalDateTime start,
            @DateTimeFormat(pattern = StatsServiceThings.DATE_TIME_FORMAT) @RequestParam LocalDateTime end,
            @RequestParam(required = false) List<String> uri,
            @RequestParam(required = false) boolean unique) {
        log.info("Get request [/stats]. Params: {}, {}, {}, {}.", start, end, uri, unique);
        return statsService.getStats(start, end, uri, unique);
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public HitDto addHit(@Valid @RequestBody HitDto hitDto) {
        log.info("Post request [/hit.]");
        return statsService.addHit(hitDto);
    }
}
