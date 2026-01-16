// LAB 12 - REQUIREMENT 6: Database connection and queries
package com.uvt.newcomerassistant.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
}
