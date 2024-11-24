package com.ead.course.services;

import com.ead.course.models.CourseModel;
import com.ead.course.models.CourseUserModel;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public interface CourseUserService {
    boolean existsByCourseAndUserId(CourseModel courseModel, UUID uuid);

    CourseUserModel saveAndSendSubscriptionUserInCourse(CourseUserModel courseUserModel);
}
