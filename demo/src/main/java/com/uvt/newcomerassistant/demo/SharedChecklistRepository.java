// LAB 12 - REQUIREMENT 6: Database connection and queries
package com.uvt.newcomerassistant.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

public interface SharedChecklistRepository extends JpaRepository<SharedChecklist, Long> {
    List<SharedChecklist> findByOwnerId(Long ownerId);
    List<SharedChecklist> findBySharedWithId(Long sharedWithId);
    Optional<SharedChecklist> findByOwnerIdAndSharedWithId(Long ownerId, Long sharedWithId);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM SharedChecklist s WHERE s.owner.id = ?1")
    void deleteByOwnerId(Long ownerId);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM SharedChecklist s WHERE s.sharedWith.id = ?1")
    void deleteBySharedWithId(Long sharedWithId);
}
