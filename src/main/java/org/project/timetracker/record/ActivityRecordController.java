package org.project.timetracker.record;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class ActivityRecordController {
    private final ActivityRecordService activityRecordService;

    @PostMapping
    public ResponseEntity<ActivityRecordResponse> create(@RequestBody ActivityRecordCreateRequest request) {
        ActivityRecordResponse response = activityRecordService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/delete")
    public ResponseEntity<ActivityRecordResponse> delete(@RequestBody ActivityRecordDeleteRequest request) {
        ActivityRecordResponse response = activityRecordService.deleteSchedule(request);
        return ResponseEntity.ok(response);
    }
}
