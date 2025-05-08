package com.tunisys.TimeSheetPfe.controllers;

import com.tunisys.TimeSheetPfe.DTOs.response.EmployeesGetAllDto;
import com.tunisys.TimeSheetPfe.models.ERole;
import com.tunisys.TimeSheetPfe.models.UserModel;
import com.tunisys.TimeSheetPfe.services.userService.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class EmployeesController {

    @Autowired
    private UserService userService;

    @GetMapping
    public Object getEmployees() {
        List<ERole> roles = Arrays.asList(ERole.ROLE_MANAGER, ERole.ROLE_EMPLOYEE);
        List<UserModel> users = userService.getUsersByRoles(roles);

        return users.stream()
                .map(EmployeesGetAllDto::fromUser)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(EmployeesGetAllDto.fromUser(userService.findById(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteUserById(@PathVariable("id") Long id) {
        // Check if the user exists
        if (!userService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // Delete the user
        boolean deleted = userService.deleteUser(id);

        if (deleted) {
            return ResponseEntity.ok().body(
                    new com.tunisys.TimeSheetPfe.DTOs.response.MessageResponse("Utilisateur supprimé avec succès"));
        } else {
            return ResponseEntity.internalServerError().body(
                    new com.tunisys.TimeSheetPfe.DTOs.response.MessageResponse(
                            "Échec de la suppression de l'utilisateur"));
        }
    }

}
