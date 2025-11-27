package org.project.timetracker.record;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.timetracker.auth.User;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActivityRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private String category;

    private String memo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecordSource source = RecordSource.USER; //default 설정


    @Builder
    public ActivityRecord(User user,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String category,
            String memo,
            RecordSource source) {
        this.user = user;
        this.startTime = startTime;
        this.endTime = endTime;
        this.category = category;
        this.memo = memo;
        this.source = source != null ? source : RecordSource.USER;
    }

    public static ActivityRecord create(
            User user,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String category,
            String memo,
            RecordSource source) {
        String memoToSave = memo;
        if (memoToSave == null) {
            memoToSave = "";
        }

        return ActivityRecord.builder()
                .user(user)
                .startTime(startTime)
                .endTime(endTime)
                .category(category)
                .memo(memoToSave)
                .source(source)
                .build();
    }

    public long getSpanMinutes() {
        Duration span = Duration.between(startTime, endTime);
        return span.toMinutes();
    }

    public void updateTimeRange(LocalDateTime start, LocalDateTime end) {
        this.startTime = start;
        this.endTime = end;
    }

    public ActivityRecord copyWithNewTimeRange(LocalDateTime start, LocalDateTime end) {
        return ActivityRecord.builder()
                .user(this.user)
                .startTime(start)
                .endTime(end)
                .category(this.category)
                .memo(this.memo)
                .source(this.source)
                .build();
    }

}
