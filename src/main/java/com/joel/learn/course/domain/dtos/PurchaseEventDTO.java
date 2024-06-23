package com.joel.learn.course.domain.dtos;

import lombok.*;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Service
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseEventDTO {

    private UUID userId;
    private UUID courseId;
    private String title;
    private Double price;
    private OffsetDateTime orderDate;
}
