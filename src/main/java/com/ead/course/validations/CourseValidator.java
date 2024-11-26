package com.ead.course.validations;

import com.ead.course.clients.AuthUserClient;
import com.ead.course.dtos.CourseRecordDto;
import com.ead.course.dtos.UserRecordDto;
import com.ead.course.enums.UserType;
import com.ead.course.services.CourseService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.UUID;

import static com.ead.course.enums.UserType.STUDENT;
import static com.ead.course.enums.UserType.USER;

@Log4j2
@Component
public class CourseValidator implements Validator {

    private final Validator validator;
    final CourseService courseService;
    final AuthUserClient authUserClient;

    public CourseValidator(@Qualifier("defaultValidator") Validator validator, CourseService courseService, AuthUserClient authUserClient) {
        this.validator = validator;
        this.courseService = courseService;
        this.authUserClient = authUserClient;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {
        CourseRecordDto courseRecordDto = (CourseRecordDto) target;
        validator.validate(courseRecordDto, errors);

        if (!errors.hasErrors()) {
            validateCourseName(courseRecordDto, errors);
            validateUserInstructor(courseRecordDto.userInstructor(), errors);
        }
    }

    private void validateCourseName(CourseRecordDto courseRecordDto, Errors errors) {
        if (courseService.existsByName(courseRecordDto.name())) {
            errors.rejectValue("name", "courseNameConflict", "Course Name is Already Taken!");
            log.error("Error validation ourseName: {}", courseRecordDto.name());
        }
    }

    private void validateUserInstructor(UUID userInstructor, Errors errors) {
        ResponseEntity<UserRecordDto> userRecordDto = authUserClient.getOneUsreById(userInstructor);
        UserType userType = userRecordDto.getBody().userType();

        if (userType.equals(STUDENT) || userType.equals(USER)) {
            errors.rejectValue("userInstructor", "UserInstructorError", "User must be INSTRUCTOR or ADMIN");
            log.error("Error validation userInstructor: {}", userInstructor);
        }
    }
}
