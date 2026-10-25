package com.islamnizami.fleettrack.repository;

import com.islamnizami.fleettrack.model.entity.MaintenanceRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface MaintenanceRecordRepository
        extends JpaRepository<MaintenanceRecord, Long>, JpaSpecificationExecutor<MaintenanceRecord> {

    long countByCompletedFalse();

    long countByCompletedFalseAndDueAtBefore(OffsetDateTime time);

    @Query("""
            select m from MaintenanceRecord m join fetch m.vehicle
            where m.completed = false
              and m.dueSoonNotifiedAt is null
              and m.dueAt > :now and m.dueAt <= :limit
            """)
    List<MaintenanceRecord> findDueSoonNotNotified(@Param("now") OffsetDateTime now,
                                                   @Param("limit") OffsetDateTime limit);

    @Query("""
            select m from MaintenanceRecord m join fetch m.vehicle
            where m.completed = false
              and m.overdueNotifiedAt is null
              and m.dueAt <= :now
            """)
    List<MaintenanceRecord> findOverdueNotNotified(@Param("now") OffsetDateTime now);

    @Query("select m from MaintenanceRecord m join fetch m.vehicle order by m.dueAt desc")
    List<MaintenanceRecord> findRecentWithVehicle(Pageable pageable);

    @Modifying
    @Query("delete from MaintenanceRecord m where m.completed = true and m.completedAt < :cutoff")
    int deleteCompletedBefore(@Param("cutoff") OffsetDateTime cutoff);
}
