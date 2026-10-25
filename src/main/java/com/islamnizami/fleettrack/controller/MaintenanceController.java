package com.islamnizami.fleettrack.controller;

import com.islamnizami.fleettrack.model.dto.maintenance.MaintenanceRequest;
import com.islamnizami.fleettrack.model.dto.maintenance.MaintenanceResponse;
import com.islamnizami.fleettrack.model.dto.maintenance.MaintenanceUpdateRequest;
import com.islamnizami.fleettrack.service.MaintenanceService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
public class MaintenanceController {

    private final MaintenanceService service;

    @GetMapping
    public Page<MaintenanceResponse> findAll(
            @RequestParam(required = false) Long vehicleId,
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) Boolean overdue,
            @PageableDefault(size = 20, sort = "dueAt", direction = Sort.Direction.ASC) Pageable pageable) {
        return service.findAll(vehicleId, completed, overdue, pageable);
    }

    @GetMapping("/{id}")
    public MaintenanceResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public MaintenanceResponse create(@Valid @RequestBody MaintenanceRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public MaintenanceResponse update(@PathVariable Long id,
                                      @Valid @RequestBody MaintenanceUpdateRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/complete")
    public MaintenanceResponse complete(@PathVariable Long id) {
        return service.complete(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
