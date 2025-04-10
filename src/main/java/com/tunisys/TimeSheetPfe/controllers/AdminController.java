package com.tunisys.TimeSheetPfe.controllers;

import com.tunisys.TimeSheetPfe.DTOs.request.CreateUserDto;
import com.tunisys.TimeSheetPfe.DTOs.response.MessageResponse;
import com.tunisys.TimeSheetPfe.services.userService.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    UserService userService ;

    @PostMapping("/create-user")
    // todo add role
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserDto dto) {

        if (userService.existsByEmail(dto.getEmail())){
            return MessageResponse.badRequest("User exist with that email");
        }
        userService.createUserAndSendEmail(dto.getEmail(), dto.getRole(),dto.getName(),dto.getPhoneNumber());
        return MessageResponse.ok("User created successfully");
    }
}
