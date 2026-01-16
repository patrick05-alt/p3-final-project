package com.uvt.newcomerassistant.demo.service;

import com.uvt.newcomerassistant.demo.User;
import com.uvt.newcomerassistant.demo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class AsyncUserService {

    private final UserRepository userRepository;

    @Autowired
    public AsyncUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Asynchronously fetch all users.
     * This demonstrates multi-threading support for concurrent operations.
     */
    @Async("taskExecutor")
    public CompletableFuture<List<User>> getAllUsersAsync() {
        List<User> users = userRepository.findAll();
        return CompletableFuture.completedFuture(users);
    }

    /**
     * Asynchronously create a user.
     * Multiple user creations can be processed concurrently.
     */
    @Async("taskExecutor")
    public CompletableFuture<User> createUserAsync(User user) {
        User savedUser = userRepository.save(user);
        return CompletableFuture.completedFuture(savedUser);
    }

    /**
     * Asynchronously delete a user.
     * Allows non-blocking deletion operations.
     */
    @Async("taskExecutor")
    public CompletableFuture<Void> deleteUserAsync(Long userId) {
        userRepository.deleteById(userId);
        return CompletableFuture.completedFuture(null);
    }
}
