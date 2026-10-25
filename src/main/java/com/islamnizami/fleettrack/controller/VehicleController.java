package com.islamnizami.fleettrack.controller;

import com.islamnizami.fleettrack.model.dto.location.LocationUpdateRequest;
import com.islamnizami.fleettrack.model.dto.vehicle.VehicleRequest;
import com.islamnizami.fleettrack.model.dto.vehicle.VehicleResponse;
import com.islamnizami.fleettrack.model.enums.VehicleStatus;
import com.islamnizami.fleettrack.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService service;

    @Operation(summary = "List vehicles",
            description = "Filter by text, status, model year or assigned driver. "
                    + "Sort with ?sort=modelYear,desc (any vehicle field); page with ?page=0&size=20.")
    @GetMapping
    public Page<VehicleResponse> findAll(@RequestParam(required = false) String search,
                                         @RequestParam(required = false) VehicleStatus status,
                                         @RequestParam(required = false) Integer modelYear,
                                         @RequestParam(required = false) Long driverId,
                                         @PageableDefault(size = 20,sort = "id",
                                         direction = Sort.Direction.DESC) Pageable pageable) {
        return service.findAll(search, status, modelYear, driverId, pageable);
    }

    @GetMapping("/{id}")
    public VehicleResponse findById(@PathVariable long id) {
        return service.findById(id);
    }

    @PostMapping
    public VehicleResponse create(@Valid @RequestBody VehicleRequest request){
        return service.create(request);
    }

    @PutMapping("/{id}")
    public VehicleResponse update(
            @PathVariable Long id,
            @Valid @RequestBody VehicleRequest request
    ){
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        service.delete(id);
    }

    @Operation(summary = "Update the GPS position of a vehicle",
            description = "Saves the position and broadcasts it to WebSocket subscribers "
                    + "on /topic/vehicles/locations and /topic/vehicles/{id}/location.")
    @PutMapping("/{id}/location")
    public VehicleResponse updateLocation(@PathVariable Long id,
                                          @Valid @RequestBody LocationUpdateRequest request) {
        return service.updateLocation(id, request);
    }

    @Operation(summary = "Assign a driver to a vehicle")
    @PutMapping("/{id}/driver/{driverId}")
    public VehicleResponse assignDriver(@PathVariable Long id, @PathVariable Long driverId) {
        return service.assignDriver(id, driverId);
    }

    @Operation(summary = "Remove the driver from a vehicle")
    @DeleteMapping("/{id}/driver")
    public VehicleResponse unassignDriver(@PathVariable Long id) {
        return service.unassignDriver(id);
    }

}
