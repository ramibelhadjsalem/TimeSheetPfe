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
    UserService userService;

    @PostMapping("/create-user")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserDto dto) {

        if (userService.existsByEmail(dto.getEmail())) {
            return MessageResponse.badRequest("User exist with that email");
        }
        userService.createUserAndSendEmail(
                dto.getEmail(),
                dto.getRole(),
                dto.getFirstName(),
                dto.getPhoneNumber(),
                dto.getLastName(),
                dto.getCin(),
                dto.getDepartment(),
                dto.getExperience());
        return MessageResponse.ok("User created successfully");
    }

    /**
     * Deletes a user by their ID
     * 
     * @param id The ID of the user to delete
     * @return A response entity with a success or error message
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        // Check if the user exists
        if (!userService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // Delete the user
        boolean deleted = userService.deleteUser(id);

        if (deleted) {
            return ResponseEntity.ok(new MessageResponse("Utilisateur supprimé avec succès"));
        } else {
            return ResponseEntity.internalServerError()
                    .body(new MessageResponse("Échec de la suppression de l'utilisateur"));
        }
    }
}
