package com.tunisys.TimeSheetPfe.services.roleService;


import com.tunisys.TimeSheetPfe.exceptions.EntityNotFoundException;
import com.tunisys.TimeSheetPfe.models.ERole;
import com.tunisys.TimeSheetPfe.models.Role;
import com.tunisys.TimeSheetPfe.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public Role findRoleByName(ERole name){
        Role role=roleRepository.findRoleByName(name)
                .orElseThrow(()->new EntityNotFoundException("role not found with name :"+name));
        return role;
    }
}
