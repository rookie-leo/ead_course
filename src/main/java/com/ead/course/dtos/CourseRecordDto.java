package com.ead.course.dtos;

import com.ead.course.enums.CourseLevel;
import com.ead.course.enums.CourseStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CourseRecordDto(
        @NotBlank
        String name,

        @NotBlank
        String description,

        @NotNull
        CourseStatus courseStatus,

        @NotNull
        CourseLevel courseLevel,

        @NotNull
        UUID userInstructor,

        String imageUrl
) {
}
