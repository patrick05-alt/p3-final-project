package com.uvt.newcomerassistant.demo.controller;

import com.uvt.newcomerassistant.demo.User;
import com.uvt.newcomerassistant.demo.UserRepository;
import com.uvt.newcomerassistant.demo.ChecklistItemStatus;
import com.uvt.newcomerassistant.demo.ChecklistItemStatusRepository;
import com.uvt.newcomerassistant.demo.AppData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/view")
public class ViewController {

    private final UserRepository userRepository;
    private final ChecklistItemStatusRepository checklistItemStatusRepository;
    private final AppData appData;

    @Autowired
    public ViewController(UserRepository userRepository, 
                         ChecklistItemStatusRepository checklistItemStatusRepository,
                         AppData appData) {
        this.userRepository = userRepository;
        this.checklistItemStatusRepository = checklistItemStatusRepository;
        this.appData = appData;
    }

    // Home page
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("userCount", userRepository.count());
        model.addAttribute("checklistCount", checklistItemStatusRepository.count());
        return "index";
    }

    // User management views
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("newUser", new User());
        return "users";
    }

    @PostMapping("/users/create")
    public String createUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        // Input validation
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Username is required");
            return "redirect:/view/users";
        }
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            redirectAttributes.addFlashAttribute("error", "Valid email is required");
            return "redirect:/view/users";
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            redirectAttributes.addFlashAttribute("error", "Password must be at least 6 characters");
            return "redirect:/view/users";
        }
        
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Username already exists");
            return "redirect:/view/users";
        }
        
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "User created successfully");
        return "redirect:/view/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return userRepository.findById(id)
            .map(user -> {
                model.addAttribute("user", user);
                return "user-edit";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("error", "User not found");
                return "redirect:/view/users";
            });
    }

    @PostMapping("/users/update/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user, 
                            RedirectAttributes redirectAttributes) {
        return userRepository.findById(id)
            .map(existingUser -> {
                if (user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
                    existingUser.setUsername(user.getUsername());
                }
                if (user.getEmail() != null && user.getEmail().contains("@")) {
                    existingUser.setEmail(user.getEmail());
                }
                if (user.getPassword() != null && user.getPassword().length() >= 6) {
                    existingUser.setPassword(user.getPassword());
                }
                userRepository.save(existingUser);
                redirectAttributes.addFlashAttribute("success", "User updated successfully");
                return "redirect:/view/users";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("error", "User not found");
                return "redirect:/view/users";
            });
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "User not found");
        }
        return "redirect:/view/users";
    }

    // Checklist management views
    @GetMapping("/checklist/{userId}")
    public String userChecklist(@PathVariable Long userId, Model model, RedirectAttributes redirectAttributes) {
        return userRepository.findById(userId)
            .map(user -> {
                List<ChecklistItemStatus> statuses = checklistItemStatusRepository.findByUserId(userId);
                model.addAttribute("user", user);
                model.addAttribute("statuses", statuses);
                model.addAttribute("checklistItems", appData.getChecklistItems());
                return "checklist";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("error", "User not found");
                return "redirect:/view/users";
            });
    }

    @PostMapping("/checklist/{userId}/toggle/{itemId}")
    public String toggleChecklistItem(@PathVariable Long userId, @PathVariable String itemId,
                                     RedirectAttributes redirectAttributes) {
        return userRepository.findById(userId)
            .map(user -> {
                ChecklistItemStatus status = checklistItemStatusRepository
                    .findByUserIdAndChecklistItemId(userId, itemId)
                    .orElse(new ChecklistItemStatus(user, itemId, false));
                
                status.setChecked(!status.isChecked());
                checklistItemStatusRepository.save(status);
                redirectAttributes.addFlashAttribute("success", "Checklist item updated");
                return "redirect:/view/checklist/" + userId;
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("error", "User not found");
                return "redirect:/view/users";
            });
    }

    // Search view
    @GetMapping("/search")
    public String search(@RequestParam(required = false) String q, Model model) {
        if (q != null && !q.trim().isEmpty()) {
            model.addAttribute("query", q);
            model.addAttribute("results", appData.getSearchableItems().stream()
                .filter(s -> s.matches(q))
                .toList());
        }
        return "search";
    }

    // Information views
    @GetMapping("/contacts")
    public String contacts(Model model) {
        model.addAttribute("contacts", appData.getAllContacts());
        return "contacts";
    }

    @GetMapping("/events")
    public String events(Model model) {
        model.addAttribute("events", appData.getAllEvents());
        return "events";
    }

    @GetMapping("/locations")
    public String locations(Model model) {
        model.addAttribute("locations", appData.getAllLocations());
        return "locations";
    }
}
