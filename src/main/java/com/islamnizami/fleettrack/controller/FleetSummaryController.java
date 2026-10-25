package com.islamnizami.fleettrack.controller;

import com.islamnizami.fleettrack.model.dto.summary.FleetSummaryResponse;
import com.islamnizami.fleettrack.service.FleetSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/summary")
@RequiredArgsConstructor
public class FleetSummaryController {

    private final FleetSummaryService service;

    @GetMapping
    public FleetSummaryResponse summary() {
        return service.getSummary();
    }
}
