package com.manav.cartservice.repository;

import com.manav.cartservice.model.Carts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CartRepository extends JpaRepository<Carts, UUID> {
    Carts findByUserIdAndArchivedFalse(UUID userId);
}
