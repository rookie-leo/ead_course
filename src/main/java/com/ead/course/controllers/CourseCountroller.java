package com.ead.course.controllers;

import com.ead.course.dtos.CourseRecordDto;
import com.ead.course.models.CourseModel;
import com.ead.course.services.CourseService;
import com.ead.course.specifications.SpecificationTemplate;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Log4j2
@RestController
@RequestMapping("/courses")
public class CourseCountroller {

    final CourseService courseService;

    public CourseCountroller(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    public ResponseEntity<Object> saveCourse(@RequestBody @Valid CourseRecordDto courseRecordDto) {
        log.debug("POST saveCourse courseRecordDto: {}", courseRecordDto);
        if (courseService.existsByName(courseRecordDto.name())) {
            log.warn("Course Name is Already Taken: {}", courseRecordDto.name());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Course Name is Already Taken!");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.save(courseRecordDto));
    }

    @GetMapping
    public ResponseEntity<Page<CourseModel>> getAllCourses(
            SpecificationTemplate.CourseSpec spec, Pageable pageable
    ) {
        Page<CourseModel> courseModelPage = courseService.findAll(spec, pageable);

        if (!courseModelPage.isEmpty()) {
            courseModelPage.forEach(course ->
                    course.add(linkTo(methodOn(CourseCountroller.class).getOneCourse(course.getCourseId())).withSelfRel())
            );
        }

        return ResponseEntity.status(HttpStatus.OK).body(courseModelPage);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<Object> getOneCourse(@PathVariable("courseId") UUID courseId) {
        return ResponseEntity.status(HttpStatus.OK).body(courseService.findById(courseId).get());
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Object> deleteCourse(@PathVariable("courseId") UUID courseId) {
        log.debug("DELETE deleteCourse courseId: {}", courseId);
        courseService.delete(courseService.findById(courseId).get());
        return ResponseEntity.status(HttpStatus.OK).body("Course deleted successfully");
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<Object> updateCourse(
            @PathVariable("courseId") UUID courseId,
            @RequestBody @Valid CourseRecordDto courseRecordDto
    ) {
        log.debug("PUT updateCourse courseRecordDto: {}", courseRecordDto);
        return ResponseEntity.status(HttpStatus.OK).body(courseService.update(
                courseRecordDto, courseService.findById(courseId).get()
        ));
    }

    @GetMapping("/logs")
    public String index() {
        log.trace("TRACE");
        log.debug("DEBUG");
        log.info("INFO");
        log.warn("WARN");
        log.error("ERROR");
        return "Logging Spring Boot...";
    }
}
