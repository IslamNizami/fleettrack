package com.islamnizami.fleettrack.model.mapper;

import com.islamnizami.fleettrack.model.dto.vehicle.VehicleRequest;
import com.islamnizami.fleettrack.model.dto.vehicle.VehicleResponse;
import com.islamnizami.fleettrack.model.entity.Driver;
import com.islamnizami.fleettrack.model.entity.Vehicle;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "latitude", ignore = true)
    @Mapping(target = "longitude", ignore = true)
    @Mapping(target = "locationUpdatedAt", ignore = true)
    @Mapping(target = "driver", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Vehicle toEntity(VehicleRequest request);

    @Mapping(target = "driverId", source = "driver.id")
    @Mapping(target = "driverName", source = "driver", qualifiedByName = "driverFullName")
    VehicleResponse toResponse(Vehicle vehicle);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "latitude", ignore = true)
    @Mapping(target = "longitude", ignore = true)
    @Mapping(target = "locationUpdatedAt", ignore = true)
    @Mapping(target = "driver", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(VehicleRequest request, @MappingTarget Vehicle vehicle);

    @Named("driverFullName")
    default String driverFullName(Driver driver) {
        return driver == null ? null : driver.getFirstName() + " " + driver.getLastName();
    }
}
