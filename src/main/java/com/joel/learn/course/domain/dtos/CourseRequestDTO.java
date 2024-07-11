package com.joel.learn.course.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Service
public class CourseRequestDTO {

    @Size(min = 6, max = 50)
    private String title;

    @Size(min = 8, max = 150)
    private String subtitle;

    @NotNull
    @PositiveOrZero
    private Double price;
}
