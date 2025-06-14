package com.tunisys.TimeSheetPfe.services.userService;

import com.tunisys.TimeSheetPfe.DTOs.request.StaffDTO;
import com.tunisys.TimeSheetPfe.models.ERole;
import com.tunisys.TimeSheetPfe.models.UserModel;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

public interface IUserService {
    UserModel saveUser(UserModel user);

    boolean existsById(Long userId);

    Boolean existsByEmail(String email);

    UserModel findById(Long id);

    UserModel findByEmail(String email);

    UserModel createUserAndSendEmail(
            String email,
            ERole role,
            String firstName,
            String phone,
            String lastName,
            String cin,
            String department,
            Integer experience);

    List<UserModel> getUsersByRoles(List<ERole> roleNames);

    /**
     * Deletes a user by their ID
     * 
     * @param id The ID of the user to delete
     * @return true if the user was successfully deleted, false otherwise
     */
    boolean deleteUser(Long id);
}
