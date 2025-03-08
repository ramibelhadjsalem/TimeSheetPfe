package com.tunisys.TimeSheetPfe.securities.services;

import com.tunisys.TimeSheetPfe.models.UserModel;
import com.tunisys.TimeSheetPfe.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel user=userRepository.findByEmail(username)
                .orElseThrow(()->new UsernameNotFoundException("User not found with email "+username));

        return UserDetailsImpl.build(user);
    }
}
