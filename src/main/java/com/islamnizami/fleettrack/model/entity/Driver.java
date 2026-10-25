package com.islamnizami.fleettrack.model.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "drivers")
@Getter
@Setter
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name",nullable = false,length = 100)
    private String firstName;

    @Column(name = "last_name",nullable = false,length = 100)
    private String lastName;

    @Column(nullable = false,unique = true)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(name = "license_number",nullable = false,unique = true)
    private String licenseNumber;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at",insertable = false,updatable = false)
    private OffsetDateTime createdAt;
}
