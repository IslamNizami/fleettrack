package com.islamnizami.fleettrack.service;


import com.islamnizami.fleettrack.model.dto.location.LocationUpdateRequest;
import com.islamnizami.fleettrack.model.dto.location.VehicleLocationMessage;
import com.islamnizami.fleettrack.model.dto.vehicle.VehicleRequest;
import com.islamnizami.fleettrack.model.dto.vehicle.VehicleResponse;
import com.islamnizami.fleettrack.model.entity.Driver;
import com.islamnizami.fleettrack.model.entity.Vehicle;
import com.islamnizami.fleettrack.model.enums.NotificationType;
import com.islamnizami.fleettrack.model.enums.VehicleStatus;
import com.islamnizami.fleettrack.model.mapper.VehicleMapper;
import com.islamnizami.fleettrack.model.specification.VehicleSpecification;
import com.islamnizami.fleettrack.notification.NotificationPublisher;
import com.islamnizami.fleettrack.repository.DriverRepository;
import com.islamnizami.fleettrack.repository.VehicleRepository;
import com.islamnizami.fleettrack.websocket.VehicleLocationUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
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
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final VehicleMapper mapper;
    private final NotificationPublisher notificationPublisher;
    private final ApplicationEventPublisher eventPublisher;


    @Transactional(readOnly = true)
    public Page<VehicleResponse> findAll(String search, VehicleStatus status,
                                         Integer modelYear, Long driverId,
                                         Pageable pageable){
        Specification<Vehicle> spec = Specification
                .where(VehicleSpecification.fetchDriver())
                .and(VehicleSpecification.matchesText(search))
                .and(VehicleSpecification.hasStatus(status))
                .and(VehicleSpecification.hasModelYear(modelYear))
                .and(VehicleSpecification.hasDriver(driverId));

        return vehicleRepository.findAll(spec,pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "vehicles",key = "#id")
    public VehicleResponse findById(Long id){
        return mapper.toResponse(getVehicle(id));
    }

    public VehicleResponse create(VehicleRequest request){
        if(vehicleRepository.existsByLicensePlate(request.getLicensePlate())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "License plate already exists");
        }
        Vehicle vehicle = mapper.toEntity(request);

        if(vehicle.getStatus() == null){
            vehicle.setStatus(VehicleStatus.AVAILABLE);
        }

        return mapper.toResponse(vehicleRepository.save(vehicle));
    }

    @CacheEvict(value = "vehicles",key = "#id")
    public VehicleResponse update(Long id,VehicleRequest request){
        Vehicle vehicle = getVehicle(id);
        VehicleStatus previousStatus = vehicle.getStatus();

        vehicleRepository.findByLicensePlate(request.getLicensePlate())
                .filter(existing-> !existing.getId().equals(id))
                .ifPresent(existing->{
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT, "License plate already exists"
                    );
                });
        mapper.updateEntity(request, vehicle);
        Vehicle saved = vehicleRepository.save(vehicle);

        if (saved.getStatus() == VehicleStatus.OFFLINE && previousStatus != VehicleStatus.OFFLINE) {
            notificationPublisher.publish(NotificationType.VEHICLE_OFFLINE,
                    "Vehicle " + saved.getLicensePlate() + " went offline", saved.getId());
        }

        return mapper.toResponse(saved);
    }

    @CacheEvict(value = "vehicles",key = "#id")
    public void delete(Long id){
        vehicleRepository.delete(getVehicle(id));
    }



    @CacheEvict(value = "vehicles", key = "#id")
    public VehicleResponse updateLocation(Long id, LocationUpdateRequest request) {
        Vehicle vehicle = getVehicle(id);
        vehicle.setLatitude(request.getLatitude());
        vehicle.setLongitude(request.getLongitude());
        vehicle.setLocationUpdatedAt(OffsetDateTime.now());

        Vehicle saved = vehicleRepository.save(vehicle);

        eventPublisher.publishEvent(new VehicleLocationUpdatedEvent(
                new VehicleLocationMessage(
                        saved.getId(),
                        saved.getLicensePlate(),
                        saved.getLatitude(),
                        saved.getLongitude(),
                        saved.getStatus().name(),
                        saved.getLocationUpdatedAt().toString())));

        return mapper.toResponse(saved);
    }


    @CacheEvict(value = "vehicles", key = "#id")
    public VehicleResponse assignDriver(Long id, Long driverId) {
        Vehicle vehicle = getVehicle(id);
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Driver not found: " + driverId));

        if (!driver.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Driver is not active and cannot be assigned");
        }

        vehicle.setDriver(driver);
        return mapper.toResponse(vehicleRepository.save(vehicle));
    }

    @CacheEvict(value = "vehicles", key = "#id")
    public VehicleResponse unassignDriver(Long id) {
        Vehicle vehicle = getVehicle(id);
        vehicle.setDriver(null);
        return mapper.toResponse(vehicleRepository.save(vehicle));
    }

    private Vehicle getVehicle(Long id){
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,"Vehicle not found: " + id
                ));
    }
}
