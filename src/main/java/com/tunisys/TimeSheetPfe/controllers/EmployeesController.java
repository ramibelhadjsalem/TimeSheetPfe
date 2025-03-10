package com.tunisys.TimeSheetPfe.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.tunisys.TimeSheetPfe.DTOs.response.EmployeesGetAllDto;
import com.tunisys.TimeSheetPfe.DTOs.view.View;
import com.tunisys.TimeSheetPfe.models.ERole;
import com.tunisys.TimeSheetPfe.models.UserModel;
import com.tunisys.TimeSheetPfe.services.userService.UserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class EmployeesController {

    @Autowired private UserService userService ;
    @Autowired private ModelMapper modelMapper;


    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Object getEmployees() {
        List<ERole> roles = Arrays.asList(ERole.ROLE_MANAGER, ERole.ROLE_EMPLOYEE);
        List<UserModel> users = userService.getUsersByRoles(roles);


        return users.stream()
                .map(employee -> modelMapper.map(employee, EmployeesGetAllDto.class))
                .collect(Collectors.toList());
    }


}
