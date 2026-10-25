package com.islamnizami.fleettrack.model.entity;


import com.islamnizami.fleettrack.model.enums.VehicleStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length = 100)
    private String make;

    @Column(nullable = false,length = 100)
    private String model;

    @Column(name = "model_year",nullable = false)
    private Integer modelYear;

    @Column(name = "license_plate",nullable = false,unique = true,length = 20)
    private String licensePlate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 30)
    private VehicleStatus status = VehicleStatus.AVAILABLE;

    private Double latitude;
    private Double longitude;

    @Column(name = "location_updated_at")
    private OffsetDateTime locationUpdatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @Column(name = "created_at",insertable = false,updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at",insertable = false,updatable = false)
    private OffsetDateTime updatedAt;

}
