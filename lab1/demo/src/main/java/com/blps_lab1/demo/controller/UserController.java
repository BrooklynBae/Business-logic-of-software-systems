package com.blps_lab1.demo.controller;

import com.blps_lab1.demo.dto.CreateUserRequest;
import com.blps_lab1.demo.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.blps_lab1.demo.services.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody CreateUserRequest request) {
        UserDto response = userService.create(request);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable Long id) {
        UserDto response = userService.findById(id);
        return ResponseEntity.ok(response);
    } //find user or users res?

    @PatchMapping("/{id}/photo")
    public ResponseEntity<UserDto> updatePhoto(
            @PathVariable Long id,
            @RequestBody String photo
    ) {
        UserDto response = userService.updatePhoto(id, photo);
        return ResponseEntity.ok(response);
    }
}
