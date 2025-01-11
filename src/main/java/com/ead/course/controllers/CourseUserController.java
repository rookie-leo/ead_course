package com.ead.course.controllers;

import com.ead.course.dtos.SubscriptionRecordDto;
import com.ead.course.enums.UserStatus;
import com.ead.course.models.CourseModel;
import com.ead.course.models.UserModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.UserService;
import com.ead.course.specifications.SpecificationTemplate;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

import static com.ead.course.specifications.SpecificationTemplate.UserSpec;

@RestController
public class CourseUserController {

    final CourseService courseService;
    final UserService userService;

    public CourseUserController(CourseService courseService, UserService userService) {
        this.courseService = courseService;
        this.userService = userService;
    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @GetMapping("/courses/{courseId}/users")
    public ResponseEntity<Object> getAllUsersByCourse(
            @PageableDefault(sort = "userId", direction = Sort.Direction.ASC) Pageable page,
            @PathVariable(value = "courseId") UUID courseId,
            UserSpec spec
    ) {
        courseService.findById(courseId);
        return ResponseEntity.status(HttpStatus.OK).body(
                userService.findAll(SpecificationTemplate.userCourseId(courseId).and(spec), page)
        );
    }

    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping("/courses/{courseId}/users/subscription")
    public ResponseEntity<Object> saveSubscriptionUserInCourse(
            @PathVariable(value = "courseId") UUID courseId,
            @RequestBody @Valid SubscriptionRecordDto subscriptionRecordDto
    ) {
        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        Optional<UserModel> userModelOptional = userService.findById(subscriptionRecordDto.userId());

        if (courseService.existsByCourseAndUser(courseId, subscriptionRecordDto.userId()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Subscription already exists!");

        if (userModelOptional.get().getUserStatus().equals(UserStatus.BLOCKED.toString()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User is blocked!");

        courseService.saveSubscriptionUserInCourse(courseModelOptional.get(), userModelOptional.get());

        return ResponseEntity.status(HttpStatus.CREATED).body("Subscription created successfully");
    }
}
