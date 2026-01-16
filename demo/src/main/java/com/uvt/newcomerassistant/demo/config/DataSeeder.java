package com.uvt.newcomerassistant.demo.config;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.uvt.newcomerassistant.demo.Contact;
import com.uvt.newcomerassistant.demo.ContactRepository;
import com.uvt.newcomerassistant.demo.Event;
import com.uvt.newcomerassistant.demo.EventRepository;
import com.uvt.newcomerassistant.demo.Location;
import com.uvt.newcomerassistant.demo.LocationRepository;
import com.uvt.newcomerassistant.demo.User;
import com.uvt.newcomerassistant.demo.UserRepository;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(
            UserRepository userRepository, 
            PasswordEncoder passwordEncoder,
            EventRepository eventRepository,
            LocationRepository locationRepository,
            ContactRepository contactRepository) {
        return args -> {
            // Create admin user if not exists
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setEmail("admin@uvt.ro");
                admin.setRole("ADMIN");
                userRepository.save(admin);
                System.out.println("Admin user created: admin/admin123");
            }

            // Create regular user if not exists
            if (userRepository.findByUsername("user").isEmpty()) {
                User user = new User();
                user.setUsername("user");
                user.setPassword(passwordEncoder.encode("user123"));
                user.setEmail("user@uvt.ro");
                user.setRole("USER");
                userRepository.save(user);
                System.out.println("Regular user created: user/user123");
            }
            
            // Create demo user if not exists
            if (userRepository.findByUsername("demo").isEmpty()) {
                User demo = new User();
                demo.setUsername("demo");
                demo.setPassword(passwordEncoder.encode("demo123"));
                demo.setEmail("demo@uvt.ro");
                demo.setRole("USER");
                userRepository.save(demo);
                System.out.println("Demo user created: demo/demo123");
            }
            
            // Seed sample events if database is empty
            if (eventRepository.count() == 0) {
                Event event1 = new Event();
                event1.setName("Orientation Day");
                event1.setDate(LocalDate.of(2026, 2, 1));
                event1.setLocation("Campus Main Hall");
                eventRepository.save(event1);
                
                Event event2 = new Event();
                event2.setName("Student Registration");
                event2.setDate(LocalDate.of(2026, 2, 3));
                event2.setLocation("Administration Building");
                eventRepository.save(event2);
                
                Event event3 = new Event();
                event3.setName("Welcome Party");
                event3.setDate(LocalDate.of(2026, 2, 5));
                event3.setLocation("Student Center");
                eventRepository.save(event3);
                
                System.out.println("Sample events created");
            }
            
            // Seed sample locations if database is empty
            if (locationRepository.count() == 0) {
                Location loc1 = new Location();
                loc1.setName("Campus Library");
                loc1.setCategory("Academic");
                loc1.setDetails("Open 8 AM - 10 PM, Ground floor has study rooms");
                locationRepository.save(loc1);
                
                Location loc2 = new Location();
                loc2.setName("Student Cafeteria");
                loc2.setCategory("Dining");
                loc2.setDetails("Affordable meals, opens at 7 AM");
                locationRepository.save(loc2);
                
                Location loc3 = new Location();
                loc3.setName("Sports Complex");
                loc3.setCategory("Recreation");
                loc3.setDetails("Gym, pool, tennis courts available");
                locationRepository.save(loc3);
                
                System.out.println("Sample locations created");
            }
            
            // Seed sample contacts if database is empty
            if (contactRepository.count() == 0) {
                Contact contact1 = new Contact();
                contact1.setName("Student Services");
                contact1.setEmail("services@uvt.ro");
                contact1.setPhone("+40-256-123-456");
                contactRepository.save(contact1);
                
                Contact contact2 = new Contact();
                contact2.setName("Academic Advisor");
                contact2.setEmail("advisor@uvt.ro");
                contact2.setPhone("+40-256-123-457");
                contactRepository.save(contact2);
                
                Contact contact3 = new Contact();
                contact3.setName("IT Support");
                contact3.setEmail("itsupport@uvt.ro");
                contact3.setPhone("+40-256-123-458");
                contactRepository.save(contact3);
                
                System.out.println("Sample contacts created");
            }
        };
    }
}
