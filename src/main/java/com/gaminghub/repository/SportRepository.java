package com.gaminghub.repository;

import com.gaminghub.entity.Sport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SportRepository extends JpaRepository<Sport, UUID> {
    Optional<Sport> findBySlug(String slug);

    boolean existsByNameIgnoreCase(String name);
}
