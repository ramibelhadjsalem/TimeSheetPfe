package com.tunisys.TimeSheetPfe.services.userService;


import com.tunisys.TimeSheetPfe.models.ERole;
import com.tunisys.TimeSheetPfe.models.Role;
import com.tunisys.TimeSheetPfe.models.UserModel;
import com.tunisys.TimeSheetPfe.repositories.UserRepository;
import com.tunisys.TimeSheetPfe.services.roleService.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService implements IUserService{
    @Autowired
    private UserRepository userRepository;
     @Autowired
    private PasswordService passwordService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private RoleService roleService;

    @Override
    public UserModel saveUser(UserModel user) {
        return userRepository.save(user);
    }

    @Override
    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserModel findById(Long id) {
        UserModel user=userRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("User not found with id : "+id));
        return user;
    }

    @Override
    public UserModel findByEmail(String email) {
        UserModel user=userRepository.findByEmail(email)
                .orElseThrow(()-> new NoSuchElementException("User not found with email : "+email));
        return user;
    }

    @Override
    public UserModel createUserAndSendEmail(String email, ERole role) {
        String generatedPassword =passwordService.generateRandomPassword();
        String encodedPassword = passwordService.encodePassword(generatedPassword);
        UserModel user = new UserModel(email,encodedPassword);
        Role userRole = roleService.findRoleByName(role);
        user.getRoles().add(userRole);


        try {
            // Assuming the sender email is "no-reply@example.com"
            emailService.sendEmail("no-reply@example.com", email,
                    "Welcome to the Platform",
                    "User", email, generatedPassword);
        } catch (Exception e) {
            e.printStackTrace();  // Log the exception or handle it as needed
            throw new RuntimeException("Failed to send email", e);
        }

        // Step 7: Return the created user
        return userRepository.save(user);

    }

    @Override
    public List<UserModel> getUsersByRoles(List<ERole> roleNames) {
        return userRepository.findByRoles_NameIn(roleNames);
    }

}





