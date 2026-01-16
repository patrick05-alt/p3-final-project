// LAB 12 - REQUIREMENT 6: Database connection and queries
package com.uvt.newcomerassistant.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

public interface FavoriteLocationRepository extends JpaRepository<FavoriteLocation, Long> {
    List<FavoriteLocation> findByUserId(Long userId);
    List<FavoriteLocation> findByLocationId(Long locationId);
    Optional<FavoriteLocation> findByLocationIdAndUserId(Long locationId, Long userId);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM FavoriteLocation f WHERE f.location.id = ?1")
    void deleteByLocationId(Long locationId);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM FavoriteLocation f WHERE f.user.id = ?1")
    void deleteByUserId(Long userId);
}
