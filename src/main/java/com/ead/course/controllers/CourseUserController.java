package com.ead.course.controllers;

import com.ead.course.clients.AuthUserClient;
import com.ead.course.dtos.SubscriptionRecordDto;
import com.ead.course.dtos.UserRecordDto;
import com.ead.course.enums.UserStatus;
import com.ead.course.models.CourseModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.CourseUserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
public class CourseUserController {

    final AuthUserClient authUserClient;
    final CourseService courseService;
    final CourseUserService courseUserService;

    public CourseUserController(AuthUserClient authUserClient, CourseService courseService, CourseUserService courseUserService) {
        this.authUserClient = authUserClient;
        this.courseService = courseService;
        this.courseUserService = courseUserService;
    }

    @GetMapping("/courses/{courseId}/users")
    public ResponseEntity<Page<UserRecordDto>> getAllUsersByCourse(
            @PageableDefault(sort = "userId", direction = Sort.Direction.ASC) Pageable page,
            @PathVariable(value = "courseId") UUID courseId
    ) {
        courseService.findById(courseId);
        return ResponseEntity.status(HttpStatus.OK).body(authUserClient.getAllUsersByCourse(courseId, page));
    }

    @PostMapping("/courses/{courseId}/users/subscription")
    public ResponseEntity<Object> saveSubscriptionUserInCourse(
            @PathVariable(value = "courseId") UUID courseId,
            @RequestBody @Valid SubscriptionRecordDto subscriptionRecordDto
    ) {
        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        if (courseUserService.existsByCourseAndUserId(courseModelOptional.get(), subscriptionRecordDto.userId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Subscription already exists!");
        }

        ResponseEntity<UserRecordDto> responseUser = authUserClient.getOneUsreById(subscriptionRecordDto.userId());
        if (responseUser.getBody().userStatus().equals(UserStatus.BLOCKED)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User is blocked!");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(
                courseUserService.saveAndSendSubscriptionUserInCourse(
                        courseModelOptional
                                .get()
                                .convertToCourseUserModel(subscriptionRecordDto.userId())
                ));
    }
}
