// LAB 12 - REQUIREMENT 6: Database connection and queries
package com.uvt.newcomerassistant.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChecklistItemStatusRepository extends JpaRepository<ChecklistItemStatus, Long> {
    List<ChecklistItemStatus> findByUserId(Long userId);
    Optional<ChecklistItemStatus> findByUserIdAndChecklistItemId(Long userId, String checklistItemId);
    List<ChecklistItemStatus> findByUserIdAndIsChecked(Long userId, boolean isChecked);
}
