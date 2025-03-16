package com.keville.flummox.controllers.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.keville.flummox.DTO.SiteStatsDTO;
import com.keville.flummox.service.statsService.StatsService;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired
    private StatsService statsService;

    public StatsController(@Autowired StatsService statsService) {
      this.statsService = statsService;
    }

    @GetMapping("")
    public SiteStatsDTO stats() {
      return statsService.getStats();
    }

}
