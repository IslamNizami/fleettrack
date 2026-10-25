package com.islamnizami.fleettrack.service;

import com.islamnizami.fleettrack.model.dto.maintenance.MaintenanceRequest;
import com.islamnizami.fleettrack.model.dto.maintenance.MaintenanceResponse;
import com.islamnizami.fleettrack.model.dto.maintenance.MaintenanceUpdateRequest;
import com.islamnizami.fleettrack.model.entity.MaintenanceRecord;
import com.islamnizami.fleettrack.model.entity.Vehicle;
import com.islamnizami.fleettrack.model.mapper.MaintenanceMapper;
import com.islamnizami.fleettrack.model.specification.MaintenanceSpecification;
import com.islamnizami.fleettrack.repository.MaintenanceRecordRepository;
import com.islamnizami.fleettrack.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class MaintenanceService {

    private final MaintenanceRecordRepository repository;
    private final VehicleRepository vehicleRepository;
    private final MaintenanceMapper mapper;

    @Transactional(readOnly = true)
    public Page<MaintenanceResponse> findAll(Long vehicleId, Boolean completed,
                                             Boolean overdue, Pageable pageable) {
        Specification<MaintenanceRecord> spec = Specification
                .where(MaintenanceSpecification.fetchVehicle())
                .and(MaintenanceSpecification.hasVehicle(vehicleId))
                .and(MaintenanceSpecification.hasCompleted(completed))
                .and(MaintenanceSpecification.isOverdue(overdue));

        return repository.findAll(spec, pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public MaintenanceResponse findById(Long id) {
        return mapper.toResponse(getRecord(id));
    }

    public MaintenanceResponse create(MaintenanceRequest request) {
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Vehicle not found: " + request.getVehicleId()));

        MaintenanceRecord record = new MaintenanceRecord();
        record.setVehicle(vehicle);
        record.setDescription(request.getDescription());
        record.setDueAt(request.getDueAt());

        return mapper.toResponse(repository.save(record));
    }

    public MaintenanceResponse update(Long id, MaintenanceUpdateRequest request) {
        MaintenanceRecord record = getRecord(id);

        if (record.isCompleted()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Completed maintenance records cannot be edited");
        }

        // a new due date means reminders should be sent again
        if (!record.getDueAt().isEqual(request.getDueAt())) {
            record.setDueSoonNotifiedAt(null);
            record.setOverdueNotifiedAt(null);
        }

        record.setDescription(request.getDescription());
        record.setDueAt(request.getDueAt());

        return mapper.toResponse(repository.save(record));
    }

    public MaintenanceResponse complete(Long id) {
        MaintenanceRecord record = getRecord(id);

        if (record.isCompleted()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Maintenance record is already completed");
        }

        record.setCompleted(true);
        record.setCompletedAt(OffsetDateTime.now());

        return mapper.toResponse(repository.save(record));
    }

    public void delete(Long id) {
        repository.delete(getRecord(id));
    }

    private MaintenanceRecord getRecord(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Maintenance record not found: " + id));
    }
}
