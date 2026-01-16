// LAB 12 - REQUIREMENT 6: Database connection and queries
package com.uvt.newcomerassistant.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

public interface EventRSVPRepository extends JpaRepository<EventRSVP, Long> {
    List<EventRSVP> findByUserId(Long userId);
    List<EventRSVP> findByEventId(Long eventId);
    Optional<EventRSVP> findByEventIdAndUserId(Long eventId, Long userId);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM EventRSVP e WHERE e.event.id = ?1")
    void deleteByEventId(Long eventId);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM EventRSVP e WHERE e.user.id = ?1")
    void deleteByUserId(Long userId);
}
