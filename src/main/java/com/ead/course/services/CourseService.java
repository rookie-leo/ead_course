package com.ead.course.services;

import com.ead.course.dtos.CourseRecordDto;
import com.ead.course.models.CourseModel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.UUID;

public interface CourseService {

    void delete(CourseModel courseModel);

    CourseModel save(@Valid CourseRecordDto courseRecordDto);

    boolean existsByName(@NotBlank String name);

    Page<CourseModel> findAll(Specification<CourseModel> spec, Pageable pageable);

    Optional<CourseModel> findById(UUID courseId);

    CourseModel update(@Valid CourseRecordDto courseRecordDto, CourseModel courseModel);
}
