package com.splitwise.clone.controller;

import com.splitwise.clone.model.User;
import com.splitwise.clone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/friends")
public class FriendController {

    @Autowired
    UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getFriends() {
        String currentUserEmail = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User currentUser = userRepository.findByEmail(currentUserEmail).orElseThrow();

        List<User> friends = currentUser.getFriends().stream()
            .map(friend -> {
                // Create a copy without sensitive data
                User friendCopy = new User();
                friendCopy.setId(friend.getId());
                friendCopy.setName(friend.getName());
                friendCopy.setEmail(friend.getEmail());
                return friendCopy;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(friends);
    }

    @PostMapping("/{friendId}")
    public ResponseEntity<?> addFriend(@PathVariable Long friendId) {
        String currentUserEmail = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User currentUser = userRepository.findByEmail(currentUserEmail).orElseThrow();

        if (currentUser.getId().equals(friendId)) {
            return ResponseEntity.badRequest().body("Cannot add yourself as a friend");
        }

        User friend = userRepository.findById(friendId).orElse(null);
        if (friend == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (currentUser.getFriends().contains(friend)) {
            return ResponseEntity.badRequest().body("Already friends with this user");
        }

        currentUser.getFriends().add(friend);
        friend.getFriends().add(currentUser);

        userRepository.save(currentUser);
        userRepository.save(friend);

        return ResponseEntity.ok("Friend added successfully");
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<?> removeFriend(@PathVariable Long friendId) {
        String currentUserEmail = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User currentUser = userRepository.findByEmail(currentUserEmail).orElseThrow();

        User friend = userRepository.findById(friendId).orElse(null);
        if (friend == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!currentUser.getFriends().contains(friend)) {
            return ResponseEntity.badRequest().body("Not friends with this user");
        }

        currentUser.getFriends().remove(friend);
        friend.getFriends().remove(currentUser);

        userRepository.save(currentUser);
        userRepository.save(friend);

        return ResponseEntity.ok("Friend removed successfully");
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam String query) {
        String currentUserEmail = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User currentUser = userRepository.findByEmail(currentUserEmail).orElseThrow();

        List<User> users = userRepository.findAll().stream()
            .filter(user -> !user.getId().equals(currentUser.getId()))
            .filter(user -> user.getName().toLowerCase().contains(query.toLowerCase()) ||
                           user.getEmail().toLowerCase().contains(query.toLowerCase()))
            .map(user -> {
                // Create a copy without sensitive data
                User userCopy = new User();
                userCopy.setId(user.getId());
                userCopy.setName(user.getName());
                userCopy.setEmail(user.getEmail());
                return userCopy;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }
}