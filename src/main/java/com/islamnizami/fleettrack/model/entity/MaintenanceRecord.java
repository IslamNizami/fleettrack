package com.islamnizami.fleettrack.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "maintenance_records")
@Getter
@Setter
public class MaintenanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(name = "due_at", nullable = false)
    private OffsetDateTime dueAt;

    @Column(nullable = false)
    private boolean completed = false;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Column(name = "due_soon_notified_at")
    private OffsetDateTime dueSoonNotifiedAt;

    @Column(name = "overdue_notified_at")
    private OffsetDateTime overdueNotifiedAt;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;
}
