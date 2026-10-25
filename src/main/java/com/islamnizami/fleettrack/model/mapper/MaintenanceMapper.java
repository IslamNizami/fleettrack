package com.islamnizami.fleettrack.model.mapper;

import com.islamnizami.fleettrack.model.dto.maintenance.MaintenanceResponse;
import com.islamnizami.fleettrack.model.entity.MaintenanceRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MaintenanceMapper {

    @Mapping(target = "vehicleId", source = "vehicle.id")
    @Mapping(target = "vehicleLicensePlate", source = "vehicle.licensePlate")
    @Mapping(target = "overdue",
            expression = "java(!maintenance.isCompleted() && maintenance.getDueAt().isBefore(java.time.OffsetDateTime.now()))")
    MaintenanceResponse toResponse(MaintenanceRecord maintenance);
}
