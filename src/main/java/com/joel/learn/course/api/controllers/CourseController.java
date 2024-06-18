package com.joel.learn.course.api.controllers;

import com.joel.learn.course.domain.dtos.CourseDTO;
import com.joel.learn.course.domain.dtos.CourseRequestDTO;
import com.joel.learn.course.domain.services.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public Page<CourseDTO> getAll(
            @PageableDefault(page = 0, size = 10, sort = "courseId", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return courseService.findAll(pageable);
    }

    @GetMapping("/{courseId}")
    public CourseDTO getOne(@PathVariable UUID courseId) {
        return courseService.findById(courseId);
    }

    @PutMapping("/{courseId}")
    public CourseDTO update(@PathVariable UUID courseId, @RequestBody @Valid CourseRequestDTO courseRequestDTO) {
        return courseService.update(courseId, courseRequestDTO);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseDTO save(@RequestBody @Valid CourseRequestDTO courseRequestDTO) {
        return courseService.save(courseRequestDTO);
    }

    @DeleteMapping("/{courseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID courseId) {
        courseService.delete(courseId);
    }


}
