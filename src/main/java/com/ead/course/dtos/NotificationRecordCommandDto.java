package com.ead.course.dtos;

import java.util.UUID;

public record NotificationRecordCommandDto(
        String title,
        String message,
        UUID userId
) {
}
