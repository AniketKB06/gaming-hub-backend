package com.gaminghub.repository;

import com.gaminghub.entity.VenueOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VenueOwnerRepository extends JpaRepository<VenueOwner, UUID> {
    Optional<VenueOwner> findByUserId(UUID userId);
}
