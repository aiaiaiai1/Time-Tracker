package org.project.timetracker.record;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ActivityRecordRepository extends JpaRepository<ActivityRecord, Long> {
    List<ActivityRecord> findByUserIdAndStartTimeBetweenOrderByStartTimeAsc(Long userId, LocalDateTime start, LocalDateTime end);

    Optional<ActivityRecord> findByUserIdAndStartTime(Long userId, LocalDateTime startTime);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END FROM ActivityRecord r WHERE r.user.id = :userId AND r.startTime < :newEndTime AND r.endTime > :newStartTime")
    boolean existsOverlappingRecords(@Param("userId") Long userId, @Param("newStartTime") LocalDateTime newStartTime, @Param("newEndTime") LocalDateTime newEndTime);
}
