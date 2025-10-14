package org.project.timetracker.record;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.timetracker.auth.User;

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

    @Builder
    public ActivityRecord(User user, LocalDateTime startTime, LocalDateTime endTime, String category, String memo) {
        this.user = user;
        this.startTime = startTime;
        this.endTime = endTime;
        this.category = category;
        this.memo = memo;
    }

    public static ActivityRecord create(User user, LocalDateTime startTime, LocalDateTime endTime, String category, String memo) {
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
                .build();
    }

}
