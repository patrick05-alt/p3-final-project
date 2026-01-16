// LAB 12 - REQUIREMENT 2: Functional tests for API endpoints
package com.uvt.newcomerassistan.demo;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.uvt.newcomerassistant.demo.AppData;
import com.uvt.newcomerassistant.demo.ChecklistItemStatus;
import com.uvt.newcomerassistant.demo.ChecklistItemStatusRepository;
import com.uvt.newcomerassistant.demo.Contact;
import com.uvt.newcomerassistant.demo.ContactRepository;
import com.uvt.newcomerassistant.demo.EventRepository;
import com.uvt.newcomerassistant.demo.LocationRepository;
import com.uvt.newcomerassistant.demo.Searchable;
import com.uvt.newcomerassistant.demo.User;
import com.uvt.newcomerassistant.demo.UserRepository;
import com.uvt.newcomerassistant.demo.controller.ApiController;

class ApiControllerFunctionalTests {

    @Mock
    private AppData appData;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChecklistItemStatusRepository checklistItemStatusRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ApiController apiController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test 1: User creation workflow
    @Test
    void testCreateUserWorkflow() {
        // Given: A new user with valid data
        User newUser = new User("johndoe", "password123", "john@example.com");
        User savedUser = new User("johndoe", "password123", "john@example.com");
        savedUser.setId(1L);
        
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        // When: Creating the user
        ResponseEntity<User> response = apiController.createUser(newUser);
        
        // Then: User should be created successfully
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("johndoe", response.getBody().getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    // Test 2: User retrieval and update workflow
    @Test
    void testGetAndUpdateUserWorkflow() {
        // Given: An existing user
        User existingUser = new User("janedoe", "password456", "jane@example.com");
        existingUser.setId(2L);
        
        when(userRepository.findById(2L)).thenReturn(Optional.of(existingUser));
        
        // When: Getting the user
        ResponseEntity<User> getResponse = apiController.getUser(2L);
        
        // Then: User should be retrieved
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals("janedoe", getResponse.getBody().getUsername());
        
        // When: Updating the user
        User updatedData = new User();
        updatedData.setEmail("jane.new@example.com");
        
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        ResponseEntity<User> updateResponse = apiController.updateUser(2L, updatedData);
        
        // Then: User should be updated
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        verify(userRepository, times(1)).save(any(User.class));
    }

    // Test 3: Checklist item check/uncheck workflow
    @Test
    void testChecklistItemCheckUncheckWorkflow() {
        // Given: A user and checklist item
        User user = new User("testuser", "password", "test@example.com");
        user.setId(1L);
        String itemId = "item-1";
        
        ChecklistItemStatus uncheckedStatus = new ChecklistItemStatus(user, itemId, false);
        ChecklistItemStatus checkedStatus = new ChecklistItemStatus(user, itemId, true);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(checklistItemStatusRepository.findByUserIdAndChecklistItemId(1L, itemId))
            .thenReturn(Optional.of(uncheckedStatus));
        when(checklistItemStatusRepository.save(any(ChecklistItemStatus.class)))
            .thenReturn(checkedStatus);
        
        // When: Checking the item
        ResponseEntity<ChecklistItemStatus> checkResponse = apiController.checkItem(1L, itemId);
        
        // Then: Item should be checked
        assertEquals(HttpStatus.OK, checkResponse.getStatusCode());
        verify(checklistItemStatusRepository, times(1)).save(any(ChecklistItemStatus.class));
        
        // When: Unchecking the item
        when(checklistItemStatusRepository.findByUserIdAndChecklistItemId(1L, itemId))
            .thenReturn(Optional.of(checkedStatus));
        when(checklistItemStatusRepository.save(any(ChecklistItemStatus.class)))
            .thenReturn(uncheckedStatus);
        
        ResponseEntity<ChecklistItemStatus> uncheckResponse = apiController.uncheckItem(1L, itemId);
        
        // Then: Item should be unchecked
        assertEquals(HttpStatus.OK, uncheckResponse.getStatusCode());
        verify(checklistItemStatusRepository, times(2)).save(any(ChecklistItemStatus.class));
    }

    // Test 4: Input validation for user creation
    @Test
    void testUserCreationInputValidation() {
        // Test null user
        ResponseEntity<User> response1 = apiController.createUser(null);
        assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        
        // Test empty username
        User user2 = new User("", "password123", "test@example.com");
        ResponseEntity<User> response2 = apiController.createUser(user2);
        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        
        // Test invalid email
        User user3 = new User("testuser", "password123", "invalidemail");
        ResponseEntity<User> response3 = apiController.createUser(user3);
        assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        
        // Test short password
        User user4 = new User("testuser", "123", "test@example.com");
        ResponseEntity<User> response4 = apiController.createUser(user4);
        assertEquals(HttpStatus.BAD_REQUEST, response4.getStatusCode());
    }

    // Test 5: User deletion workflow
    @Test
    void testDeleteUserWorkflow() {
        // Given: An existing user
        User user = new User("deleteuser", "password", "delete@example.com");
        user.setId(5L);
        
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(5L);
        
        // When: Deleting the user
        ResponseEntity<Void> response = apiController.deleteUser(5L);
        
        // Then: User should be deleted
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userRepository, times(1)).deleteById(5L);
    }

    // Test 6: Get all users functionality
    @Test
    void testGetAllUsersWorkflow() {
        // Given: Multiple users in the system
        User user1 = new User("user1", "pass1", "user1@example.com");
        user1.setId(1L);
        User user2 = new User("user2", "pass2", "user2@example.com");
        user2.setId(2L);
        
        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(users);
        
        // When: Getting all users
        ResponseEntity<List<User>> response = apiController.getAllUsers();
        
        // Then: All users should be returned
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(userRepository, times(1)).findAll();
    }

    // Test 7: Invalid ID validation
    @Test
    void testInvalidIdValidation() {
        // Test null ID
        ResponseEntity<User> response1 = apiController.getUser(null);
        assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        
        // Test negative ID
        ResponseEntity<User> response2 = apiController.getUser(-1L);
        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        
        // Test zero ID
        ResponseEntity<User> response3 = apiController.getUser(0L);
        assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
    }

    // Test 8: Search functionality
    @Test
    void testSearchFunctionality() {
        // Given: Searchable items
        Contact contact = new Contact();
        contact.setName("John Doe");
        contact.setEmail("john@example.com");
        contact.setPhone("123-456-7890");
        
        when(eventRepository.findAll()).thenReturn(Arrays.asList());
        when(locationRepository.findAll()).thenReturn(Arrays.asList());
        when(contactRepository.findAll()).thenReturn(Arrays.asList(contact));
        when(appData.getSearchableItems()).thenReturn(Arrays.asList(contact));
        
        // When: Searching with valid query
        List<Searchable> results = apiController.search("john");
        
        // Then: Results should be returned
        assertEquals(1, results.size());
        
        // When: Searching with empty query
        List<Searchable> emptyResults = apiController.search("");
        
        // Then: Empty list should be returned
        assertEquals(0, emptyResults.size());
    }

    // Test 9: ChecklistItemStatus CRUD operations
    @Test
    void testChecklistStatusCRUDWorkflow() {
        // Given: A user and checklist status
        User user = new User("testuser", "password", "test@example.com");
        user.setId(1L);
        
        ChecklistItemStatus status = new ChecklistItemStatus(user, "item-1", false);
        status.setId(10L);
        
        when(checklistItemStatusRepository.findById(10L)).thenReturn(Optional.of(status));
        
        // When: Getting checklist status
        ResponseEntity<ChecklistItemStatus> getResponse = apiController.getChecklistStatus(10L);
        
        // Then: Status should be retrieved
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(10L, getResponse.getBody().getId());
        
        // When: Deleting checklist status
        doNothing().when(checklistItemStatusRepository).deleteById(10L);
        ResponseEntity<Void> deleteResponse = apiController.deleteChecklistStatus(10L);
        
        // Then: Status should be deleted
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
        verify(checklistItemStatusRepository, times(1)).deleteById(10L);
    }

    // Test 10: Duplicate username conflict
    @Test
    void testDuplicateUsernameConflict() {
        // Given: An existing user with username "duplicate"
        User existingUser = new User("duplicate", "password", "existing@example.com");
        when(userRepository.findByUsername("duplicate")).thenReturn(Optional.of(existingUser));
        
        // When: Trying to create a new user with the same username
        User newUser = new User("duplicate", "newpassword", "new@example.com");
        ResponseEntity<User> response = apiController.createUser(newUser);
        
        // Then: Should return conflict status
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(userRepository, never()).save(any(User.class));
    }
}
