package com.ead.course.controllers;

import com.ead.course.dtos.LessonRecordDto;
import com.ead.course.models.LessonModel;
import com.ead.course.services.LessonService;
import com.ead.course.services.ModuleService;
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
public class LessonController {

    final LessonService lessonService;
    final ModuleService moduleService;

    public LessonController(LessonService lessonService, ModuleService moduleService) {
        this.lessonService = lessonService;
        this.moduleService = moduleService;
    }

    @PostMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<Object> saveLesson(
            @PathVariable(value = "moduleId") UUID moduleId,
            @RequestBody @Valid LessonRecordDto lessonRecordDto
    ) {
        log.debug("POST saveLesson moduleId: {} - lessonRecordDto: {}", moduleId, lessonRecordDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            lessonService.save(lessonRecordDto, moduleService.findById(moduleId).get())
        );
    }

    @GetMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<Page<LessonModel>> getAllLessons(
            @PathVariable(value = "moduleId") UUID moduleId,
            SpecificationTemplate.LessonSpec spec,
            Pageable pageable
            ) {
        Page<LessonModel> lessonModelPage = lessonService.findAllLessonsIntoModule(SpecificationTemplate.lessonModuleId(moduleId).and(spec), pageable);
        if (!lessonModelPage.isEmpty()) {
            lessonModelPage.forEach(lesson ->
                    lesson.add(linkTo(methodOn(LessonController.class).getOneLesson(moduleId, lesson.getLessonId())).withSelfRel())
                    );
        }
        return ResponseEntity.status(HttpStatus.OK).body(lessonModelPage);
    }

    @GetMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> getOneLesson(
            @PathVariable(value = "moduleId") UUID moduleId,
            @PathVariable(value = "lessonId") UUID lessonId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(lessonService.findLessonIntoModule(lessonId, moduleId).get());
    }

    @DeleteMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> deleteLesson(
            @PathVariable(value = "moduleId") UUID moduleId,
            @PathVariable(value = "lessonId") UUID lessonId
    ) {
        log.debug("DELETE deleteLesson moduleId: {} - lessonId: {}", moduleId, lessonId);
        lessonService.delete(lessonService.findLessonIntoModule(lessonId, moduleId).get());
        return ResponseEntity.status(HttpStatus.OK).body("Lesson deleted successfully");
    }

    @PutMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> updateLesson(
            @PathVariable(value = "moduleId") UUID moduleId,
            @PathVariable(value = "lessonId") UUID lessonId,
            @RequestBody @Valid LessonRecordDto lessonRecordDto
    ) {
        log.debug("PUT updateLesson moduleId: {} - lessonId: {}", moduleId, lessonId);
        return ResponseEntity.status(HttpStatus.OK).body(
                lessonService.update(lessonRecordDto, lessonService.findLessonIntoModule(lessonId, moduleId).get())
        );
    }
}
