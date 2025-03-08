package com.tunisys.TimeSheetPfe.services.userService;


import com.tunisys.TimeSheetPfe.DTOs.request.StaffDTO;
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
//    UserModel AddStaff(StaffDTO StaffDTO, Principal principal) throws MessagingException, IOException;
//    List<StaffDTO> getAllEmployer();
//    List<StaffDTO> getAllMANAGER();
//    void deleteById(Long kitchenStaffId);
//    void deleteKitchenStaffById(Long kitchenStaffId);

}
