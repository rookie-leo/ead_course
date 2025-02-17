package com.ead.course.controllers;

import com.ead.course.dtos.ModuleRecordDto;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.ModuleService;
import com.ead.course.specifications.SpecificationTemplate;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Log4j2
@RestController
public class ModuleController {

    final ModuleService moduleService;
    final CourseService courseService;

    public ModuleController(ModuleService moduleService, CourseService courseService) {
        this.moduleService = moduleService;
        this.courseService = courseService;
    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @PostMapping("/courses/{courseId}/modules")
    public ResponseEntity<Object> saveModule(
            @PathVariable(value = "courseId") UUID courseId,
            @RequestBody @Valid ModuleRecordDto moduleRecordDto
    ) {
        log.debug("POST saveModule courseId: {} - moduleRecorDto: {}", courseId, moduleRecordDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                moduleService.save(moduleRecordDto, courseService.findById(courseId).get())
        );
    }

    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/courses/{courseId}/modules")
    public ResponseEntity<Page<ModuleModel>> getAllModels(
            @PathVariable(value = "courseId") UUID courseId,
            SpecificationTemplate.ModuleSpec spec,
            Pageable pageable
    ) {
        Page<ModuleModel> moduleModelPage = moduleService.findAllModulesIntoCourse(SpecificationTemplate.moduleCourseId(courseId).and(spec), pageable);

        if (!moduleModelPage.isEmpty()) {
            moduleModelPage.forEach(module ->
                    module.add(linkTo(methodOn(ModuleController.class).getOneModel(courseId, module.getModuleId())).withSelfRel()));
        }

        return ResponseEntity.status(HttpStatus.OK).body(moduleModelPage);
    }

    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> getOneModel(
            @PathVariable(value = "courseId") UUID courseId,
            @PathVariable(value = "moduleId") UUID moduleId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(moduleService.findModuleIntoCourse(courseId, moduleId));
    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @DeleteMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> deleteModule(
            @PathVariable("courseId") UUID courseId,
            @PathVariable(value = "moduleId") UUID moduleId
    ) {
        log.debug("DELETE deleteModule courseId: {} - moduleId: {}", courseId, moduleId);
        moduleService.delete(moduleService.findModuleIntoCourse(courseId, moduleId).get());
        return ResponseEntity.status(HttpStatus.OK).body("Module deleted successfully");
    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @PutMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> updateModule(
            @PathVariable("courseId") UUID courseId,
            @PathVariable(value = "moduleId") UUID moduleId,
            @RequestBody @Valid ModuleRecordDto moduleRecordDto
    ) {
        log.debug("PUT updateModule courseId: {} - moduleRecordDto: {}", courseId, moduleRecordDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                moduleService.update(moduleRecordDto, moduleService.findModuleIntoCourse(courseId, moduleId).get())
        );
    }
}
