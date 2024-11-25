package com.ead.course.dtos;

import java.util.UUID;

public record CourseUserRecordDto(UUID courseId, UUID userId) {
}
