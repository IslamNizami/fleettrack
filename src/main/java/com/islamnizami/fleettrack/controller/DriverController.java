package com.islamnizami.fleettrack.controller;

import com.islamnizami.fleettrack.model.dto.driver.DriverRequest;
import com.islamnizami.fleettrack.model.dto.driver.DriverResponse;
import com.islamnizami.fleettrack.model.dto.vehicle.VehicleResponse;
import com.islamnizami.fleettrack.service.DriverService;
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
@RequiredArgsConstructor
@RequestMapping("/api/drivers")
public class DriverController {

    private final DriverService driverService;
    private final VehicleService vehicleService;

    @Operation(summary = "List drivers",
            description = "Filter by text (name, email, license number) and active flag. "
                    + "Sort with ?sort=lastName,asc; page with ?page=0&size=20.")
    @GetMapping
    public Page<DriverResponse> findAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 20,sort = "id",
            direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return driverService.findAll(search, active, pageable);
    }

    @GetMapping("/{id}")
    public DriverResponse findById(@PathVariable long id) {
        return driverService.findById(id);
    }

    @Operation(summary = "Vehicles assigned to a driver")
    @GetMapping("/{id}/vehicles")
    public Page<VehicleResponse> assignedVehicles(
            @PathVariable long id,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        driverService.findById(id); // 404 if the driver does not exist
        return vehicleService.findAll(null, null, null, id, pageable);
    }

    @PostMapping
    public DriverResponse create(@Valid @RequestBody DriverRequest request){
        return driverService.create(request);
    }

    @PutMapping("/{id}")
    public DriverResponse update(@PathVariable long id, @Valid @RequestBody DriverRequest request){
        return driverService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        driverService.delete(id);
    }


}
