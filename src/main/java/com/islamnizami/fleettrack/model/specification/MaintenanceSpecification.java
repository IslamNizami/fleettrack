package com.islamnizami.fleettrack.model.specification;

import com.islamnizami.fleettrack.model.entity.MaintenanceRecord;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;

public final class MaintenanceSpecification {

    private MaintenanceSpecification() {}

    public static Specification<MaintenanceRecord> hasVehicle(Long vehicleId) {
        return (root, query, cb) ->
                vehicleId == null ? cb.conjunction()
                        : cb.equal(root.get("vehicle").get("id"), vehicleId);
    }

    public static Specification<MaintenanceRecord> hasCompleted(Boolean completed) {
        return (root, query, cb) ->
                completed == null ? cb.conjunction()
                        : cb.equal(root.get("completed"), completed);
    }

    public static Specification<MaintenanceRecord> isOverdue(Boolean overdue) {
        return (root, query, cb) -> {
            if (overdue == null || !overdue) {
                return cb.conjunction();
            }
            return cb.and(
                    cb.isFalse(root.<Boolean>get("completed")),
                    cb.lessThan(root.<OffsetDateTime>get("dueAt"), OffsetDateTime.now()));
        };
    }

    public static Specification<MaintenanceRecord> fetchVehicle() {
        return (root, query, cb) -> {
            if (query != null && !Long.class.equals(query.getResultType())
                    && !long.class.equals(query.getResultType())) {
                root.fetch("vehicle", JoinType.LEFT);
            }
            return cb.conjunction();
        };
    }
}
