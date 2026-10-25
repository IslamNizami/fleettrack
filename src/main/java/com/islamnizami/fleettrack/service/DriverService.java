package com.islamnizami.fleettrack.service;

import com.islamnizami.fleettrack.model.dto.driver.DriverRequest;
import com.islamnizami.fleettrack.model.dto.driver.DriverResponse;
import com.islamnizami.fleettrack.model.entity.Driver;
import com.islamnizami.fleettrack.model.mapper.DriverMapper;
import com.islamnizami.fleettrack.model.specification.DriverSpecification;
import com.islamnizami.fleettrack.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper mapper;

    @Transactional(readOnly = true)
    public Page<DriverResponse> findAll(String search, Boolean active, Pageable pageable) {
        Specification<Driver> spec = Specification
                .where(DriverSpecification.matchesText(search))
                .and(DriverSpecification.hasActive(active));

        return driverRepository.findAll(spec, pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "drivers", key = "#id")
    public DriverResponse findById(Long id) {
        return mapper.toResponse(getDriver(id));
    }

    public DriverResponse create(DriverRequest driverRequest) {
        if (driverRepository.existsByEmail(driverRequest.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Email already exists");
        }

        if(driverRepository.existsByLicenseNumber(driverRequest.getLicenseNumber())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "License number already exists");
        }

        return mapper.toResponse(driverRepository.save(mapper.toEntity(driverRequest)));
    }

    // vehicle responses contain the driver's name, so they must be evicted too
    @Caching(evict = {
            @CacheEvict(value = "drivers", key = "#id"),
            @CacheEvict(value = "vehicles", allEntries = true)
    })
    public DriverResponse update(Long id, DriverRequest request) {
        Driver driver = getDriver(id);

        if (driverRepository.existsByEmailAndIdNot(request.getEmail(), id)
                || driverRepository.existsByLicenseNumberAndIdNot(
                request.getLicenseNumber(), id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email or license number already exists");
        }

        mapper.updateEntity(request, driver);
        return mapper.toResponse(driverRepository.save(driver));
    }

    @Caching(evict = {
            @CacheEvict(value = "drivers", key = "#id"),
            @CacheEvict(value = "vehicles", allEntries = true)
    })
    public void delete(Long id) {
        driverRepository.delete(getDriver(id));
    }

    private Driver getDriver(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Driver not found: " + id));
    }

}
