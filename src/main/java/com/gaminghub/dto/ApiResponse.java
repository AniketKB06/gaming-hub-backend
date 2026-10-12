package com.gaminghub.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ApiResponse", description = "Generic API response wrapper")
public record ApiResponse<T>(
        String message,
        T data
) {
}
