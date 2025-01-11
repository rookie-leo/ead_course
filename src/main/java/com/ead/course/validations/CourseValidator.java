package com.ead.course.validations;

import com.ead.course.configs.security.AuthenticationCurrentUserService;
import com.ead.course.configs.security.UserDetailsImpl;
import com.ead.course.dtos.CourseRecordDto;
import com.ead.course.models.UserModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;
import java.util.UUID;

import static com.ead.course.enums.UserType.STUDENT;
import static com.ead.course.enums.UserType.USER;

@Log4j2
@Component
public class CourseValidator implements Validator {

    final AuthenticationCurrentUserService authenticationCurrentUserService;
    private final Validator validator;
    final CourseService courseService;
    final UserService userService;

    public CourseValidator(AuthenticationCurrentUserService authenticationCurrentUserService, @Qualifier("defaultValidator") Validator validator, CourseService courseService, UserService userService) {
        this.authenticationCurrentUserService = authenticationCurrentUserService;
        this.validator = validator;
        this.courseService = courseService;
        this.userService = userService;
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
        UserDetailsImpl userDetails = authenticationCurrentUserService.getCurrentUser();

        if (userDetails.getUserId().equals(userInstructor) || userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            Optional<UserModel> userModelOptional = userService.findById(userInstructor);
            var userType = userModelOptional.get().getUserType();

            if (userType.equals(STUDENT.toString()) || userType.equals(USER.toString())) {
                errors.rejectValue("userInstructor", "UserInstructorError", "User must be INSTRUCTOR or ADMIN");
                log.error("Error validation userInstructor: {}", userInstructor);
            }
        } else {
            throw new AccessDeniedException("Forbidden");
        }
    }
}
