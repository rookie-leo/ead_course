package com.ead.course.services;

import com.ead.course.dtos.ModuleRecordDto;
import com.ead.course.models.CourseModel;
import com.ead.course.models.ModuleModel;
import jakarta.validation.Valid;

public interface ModuleService {

    void delete(ModuleModel moduleModel);

    ModuleModel save(@Valid ModuleRecordDto moduleRecordDto, CourseModel courseModel);
}
