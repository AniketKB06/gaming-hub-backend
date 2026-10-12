package com.gaminghub.service;

import com.gaminghub.dto.SportRequest;
import com.gaminghub.dto.SportResponse;
import com.gaminghub.entity.Sport;
import com.gaminghub.repository.SportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SportService {

    private final SportRepository sportRepository;

    public SportService(SportRepository sportRepository) {
        this.sportRepository = sportRepository;
    }

    @Transactional(readOnly = true)
    public List<SportResponse> getAllSports() {
        return sportRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SportResponse getSport(UUID id) {
        Sport sport = sportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sport not found with id: " + id));
        return toResponse(sport);
    }

    @Transactional
    public SportResponse createSport(SportRequest request) {
        if (sportRepository.existsByNameIgnoreCase(request.name())) {
            throw new IllegalArgumentException("Sport with this name already exists");
        }
        String slug = request.name().trim().toLowerCase().replaceAll("[^a-z0-9]+", "-");
        Sport sport = new Sport(request.name().trim(), slug, request.description());
        sport.setActive(request.active() == null || request.active());
        sport = sportRepository.save(sport);
        return toResponse(sport);
    }

    private SportResponse toResponse(Sport sport) {
        return new SportResponse(
                sport.getId(),
                sport.getName(),
                sport.getSlug(),
                sport.getDescription(),
                sport.isActive(),
                sport.getCreatedAt(),
                sport.getUpdatedAt()
        );
    }
}
