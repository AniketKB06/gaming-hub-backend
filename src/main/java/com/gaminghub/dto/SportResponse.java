package com.gaminghub.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(name = "SportResponse", description = "Sport information returned by the API")
public record SportResponse(
        UUID id,
        String name,
        String slug,
        String description,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
