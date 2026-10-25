package com.islamnizami.fleettrack.repository;

import com.islamnizami.fleettrack.model.entity.Vehicle;
import com.islamnizami.fleettrack.model.enums.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle,Long>, JpaSpecificationExecutor<Vehicle> {

    boolean existsByLicensePlate(String licensePlate);

    Optional<Vehicle> findByLicensePlate(String licensePlate);

    long countByStatus(VehicleStatus status);

    long countByDriverIsNull();

    @Query("select v from Vehicle v left join fetch v.driver order by v.id")
    List<Vehicle> findAllWithDriver();

}
