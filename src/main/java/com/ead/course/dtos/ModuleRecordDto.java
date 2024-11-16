package com.ead.course.dtos;

import jakarta.validation.constraints.NotBlank;

public record ModuleRecordDto(
        @NotBlank
        String title,

        @NotBlank
        String description
) {
}
