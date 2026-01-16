// LAB 12 - REQUIREMENT 6: Database connection and queries
package com.uvt.newcomerassistant.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM PasswordResetToken p WHERE p.user = ?1")
    void deleteByUser(User user);
}
