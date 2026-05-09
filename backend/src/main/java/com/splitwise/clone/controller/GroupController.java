package com.splitwise.clone.controller;

import com.splitwise.clone.dto.GroupRequest;
import com.splitwise.clone.model.Group;
import com.splitwise.clone.model.User;
import com.splitwise.clone.repository.GroupRepository;
import com.splitwise.clone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> createGroup(@RequestBody GroupRequest request) {
        String currentUserEmail = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User creator = userRepository.findByEmail(currentUserEmail).orElseThrow();

        Group group = new Group();
        group.setName(request.getName());
        group.setCreatedBy(creator);

        Set<User> members = new HashSet<>();
        members.add(creator);
        
        if (request.getMemberIds() != null) {
            for (Long id : request.getMemberIds()) {
                userRepository.findById(id).ifPresent(members::add);
            }
        }
        group.setMembers(members);
        groupRepository.save(group);

        return ResponseEntity.ok(group);
    }

    @GetMapping
    public ResponseEntity<?> getUserGroups() {
        String currentUserEmail = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User currentUser = userRepository.findByEmail(currentUserEmail).orElseThrow();

        List<Group> groups = groupRepository.findByMembersContaining(currentUser);
        return ResponseEntity.ok(groups);
    }
}
