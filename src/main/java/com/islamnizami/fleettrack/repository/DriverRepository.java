package com.islamnizami.fleettrack.repository;

import com.islamnizami.fleettrack.model.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DriverRepository extends JpaRepository<Driver,Long>, JpaSpecificationExecutor<Driver> {

    boolean existsByEmail(String email);
    boolean existsByLicenseNumber(String licenseNumber);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByLicenseNumberAndIdNot(String licenseNumber, Long id);

    long countByActive(boolean active);
}
