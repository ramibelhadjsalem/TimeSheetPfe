package com.tunisys.TimeSheetPfe.services.userService;

import com.tunisys.TimeSheetPfe.models.ERole;
import com.tunisys.TimeSheetPfe.models.Project;
import com.tunisys.TimeSheetPfe.models.Role;
import com.tunisys.TimeSheetPfe.models.Task;
import com.tunisys.TimeSheetPfe.models.UserModel;
import com.tunisys.TimeSheetPfe.repositories.ProjectRepository;
import com.tunisys.TimeSheetPfe.repositories.UserRepository;
import com.tunisys.TimeSheetPfe.services.roleService.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private PasswordService passwordService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private RoleService roleService;
    private UserModel user;

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
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found with id : " + id));
        return user;
    }

    @Override
    public UserModel findByEmail(String email) {
        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found with email : " + email));
        return user;
    }

    @Override
    public UserModel createUserAndSendEmail(
            String email,
            ERole role,
            String firstName,
            String phone,
            String lastName,
            String cin,
            String department,
            Integer experience) {
        String generatedPassword = passwordService.generateRandomPassword();
        String encodedPassword = passwordService.encodePassword(generatedPassword);
        user = new UserModel(email, encodedPassword);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone(phone);
        user.setCin(cin);
        user.setDepartment(department);
        user.setExperience(experience);
        Role userRole = roleService.findRoleByName(role);
        // Initialize roles set if it's null
        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }
        user.getRoles().add(userRole);

        try {
            // Assuming the sender email is "no-reply@example.com"
            emailService.sendEmail("no-reply@example.com", email,
                    "Welcome to the Platform",
                    "User", email, generatedPassword);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it as needed
            throw new RuntimeException("Failed to send email", e);
        }

        // Step 7: Return the created user
        return userRepository.save(user);

    }

    @Override
    public List<UserModel> getUsersByRoles(List<ERole> roleNames) {
        return userRepository.findByRoles_NameIn(roleNames);
    }

    @Override
    public boolean deleteUser(Long id) {
        try {
            // Check if the user exists
            if (!userRepository.existsById(id)) {
                return false;
            }

            // Get the user
            UserModel user = userRepository.findById(id).orElse(null);
            if (user == null) {
                return false;
            }

            // Find all projects that have this user as an employee
            List<Project> projects = projectRepository.findProjectsByEmployeeId(id);

            // Remove the user from all projects
            for (Project project : projects) {
                project.getEmployees().remove(user);
                projectRepository.save(project);
            }

            // Set current project to null
            if (user.getCurrentProject() != null) {
                user.setCurrentProject(null);
                userRepository.save(user);
            }

            // Clean up all other relationships
            user.prepareForDeletion();

            // Save the user with cleared relationships
            userRepository.save(user);

            // Now delete the user
            userRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
