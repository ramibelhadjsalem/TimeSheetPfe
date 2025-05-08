package com.tunisys.TimeSheetPfe.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.tunisys.TimeSheetPfe.DTOs.request.ResetPasswordDto;
import com.tunisys.TimeSheetPfe.DTOs.response.MessageResponse;
import com.tunisys.TimeSheetPfe.DTOs.view.View;
import com.tunisys.TimeSheetPfe.models.UserModel;
import com.tunisys.TimeSheetPfe.services.userService.EmailService;
import com.tunisys.TimeSheetPfe.services.userService.PasswordService;
import com.tunisys.TimeSheetPfe.services.userService.UserService;
import com.tunisys.TimeSheetPfe.utils.TokenUtils;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordService passwordService;
    @Autowired
    private TokenUtils tokenUtils;

    @PostMapping("/reset-password")
    @PermitAll
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDto dto) {

        UserModel user = userService.findByEmail(dto.getEmail());
        String newPassword = passwordService.generateRandomPassword();
        String encodedPassword = passwordService.encodePassword(newPassword);

        try {
            emailService.sendResetPassword(
                    "no-reply@example.com",
                    dto.getEmail(),
                    "Password reseted successfully",
                    user.getName(),
                    user.getEmail(),
                    newPassword);
            user.setPassword(encodedPassword);
            userService.saveUser(user);
            return MessageResponse.ok("Password rested successfully ,check the new password in the e-mail ");
        } catch (Exception e) {
            return MessageResponse.badRequest("SomeThing went wrong");
        }

    }

    @GetMapping
    @JsonView(View.External.class)
    public ResponseEntity<?> getProfile() {
        UserModel user = userService.findById(tokenUtils.extractUser().getId());

        return ResponseEntity.ok(user);
    }
}
