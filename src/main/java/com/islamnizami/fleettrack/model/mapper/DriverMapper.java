package com.islamnizami.fleettrack.model.mapper;

import com.islamnizami.fleettrack.model.dto.driver.DriverRequest;
import com.islamnizami.fleettrack.model.dto.driver.DriverResponse;
import com.islamnizami.fleettrack.model.entity.Driver;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel="spring")
public interface DriverMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Driver toEntity(DriverRequest request);

    DriverResponse toResponse(Driver driver);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(DriverRequest request, @MappingTarget Driver driver);
}
