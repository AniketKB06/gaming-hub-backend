package com.gaminghub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "SportRequest", description = "Request payload for creating or updating a sport")
public record SportRequest(
        @NotBlank(message = "Sport name is required")
        @Size(min = 2, max = 80, message = "Sport name must be between 2 and 80 characters")
        String name,

        String description,
        Boolean active
) {
}
