package com.tunisys.TimeSheetPfe.services.userService;


import com.tunisys.TimeSheetPfe.DTOs.request.StaffDTO;
import com.tunisys.TimeSheetPfe.exceptions.BadRequestException;
import com.tunisys.TimeSheetPfe.exceptions.EmailNotFoundException;
import com.tunisys.TimeSheetPfe.exceptions.EntityNotFoundException;
import com.tunisys.TimeSheetPfe.models.ERole;
import com.tunisys.TimeSheetPfe.models.Role;
import com.tunisys.TimeSheetPfe.models.UserModel;
import com.tunisys.TimeSheetPfe.repositories.UserRepository;
import com.tunisys.TimeSheetPfe.services.roleService.RoleService;
import com.tunisys.TimeSheetPfe.utils.Mapper;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService{
    @Autowired
    private UserRepository userRepository;
     @Autowired
    private PasswordService passwordServicer;
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
                .orElseThrow(()-> new EntityNotFoundException("User not found with id : "+id));
        return user;
    }

    @Override
    public UserModel findByEmail(String email) {
        UserModel user=userRepository.findByEmail(email)
                .orElseThrow(()-> new EntityNotFoundException("User not found with email : "+email));
        return user;
    }

//    @Override
//    public UserModel AddStaff(StaffDTO kitchenStaffDTO, Principal principal) throws MessagingException, IOException {
//        if(this.existsByEmail(kitchenStaffDTO.getEmail())) {
//            throw new BadRequestException("Email is already exist !!");
//        }
//        Set<Role> roles = new HashSet<>();
//        String from=principal.getName();
//        String rawPassword = passwordServicer.generateRandomPassword();
//        Role userRole = roleService.findRoleByName(ERole.MANAGER);
//
//        roles.add(userRole);
//        UserModel user = UserModel.builder()
//                .firstname(kitchenStaffDTO.getFirstname())
//                .lastname(kitchenStaffDTO.getLastname())
//                .adresse(kitchenStaffDTO.getAdresse())
//                .phone(kitchenStaffDTO.getPhone())
//                .email(kitchenStaffDTO.getEmail())
//                .roles(roles)
//                .build();
//        UserModel savedUser = this.saveUser(user);
//
//        try {
//
//            emailService.sendEmail(from, savedUser.getEmail(), "Vos informations de connexion", savedUser.getFirstname(), savedUser.getEmail(), rawPassword);
//            return savedUser;
//        } catch (EmailNotFoundException e) {
//            throw new BadRequestException("The admin email is not valid: " + from);
//        } catch (MessagingException | IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException("An error occurred while sending the email.");
//        }
//    }
//
//    @Override
//    public List<StaffDTO> getAllEmployer() {
//        List<UserModel> users =userRepository.findByRole(ERole.EMPLOYER);
//        return users.stream()
//                .map(Mapper::toDto)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<StaffDTO> getAllMANAGER() {
//        List<UserModel> users =userRepository.findByRole(ERole.MANAGER);
//        return users.stream()
//                .map(Mapper::toDto)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public void deleteById(Long kitchenStaffId) {
//        if( !existsById(kitchenStaffId)) {
//            throw new EntityNotFoundException("user not found");
//        }
//        userRepository.deleteById(kitchenStaffId);
//    }
//
//    @Override
//    public void deleteKitchenStaffById(Long kitchenStaffId) {
//
//    }


}
