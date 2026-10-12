package com.gaminghub.controller;

import com.gaminghub.dto.ApiResponse;
import com.gaminghub.dto.SportRequest;
import com.gaminghub.dto.SportResponse;
import com.gaminghub.service.SportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Sports", description = "Sports management endpoints")
public class SportController {

    private final SportService sportService;

    public SportController(SportService sportService) {
        this.sportService = sportService;
    }

    @GetMapping("/sports")
    @Operation(summary = "List sports", description = "Returns all available sports")
    public ResponseEntity<List<SportResponse>> listSports() {
        return ResponseEntity.ok(sportService.getAllSports());
    }

    @GetMapping("/sports/{id}")
    @Operation(summary = "Get sport by id", description = "Returns a single sport")
    public ResponseEntity<SportResponse> getSport(@PathVariable UUID id) {
        return ResponseEntity.ok(sportService.getSport(id));
    }

    @PostMapping("/admin/sports")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a sport", description = "Admin-only creation of a sport", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<SportResponse>> createSport(@Valid @RequestBody SportRequest request) {
        SportResponse response = sportService.createSport(request);
        return ResponseEntity.ok(new ApiResponse<>("Sport created successfully", response));
    }
}
