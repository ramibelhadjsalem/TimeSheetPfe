package com.tunisys.TimeSheetPfe.securities.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tunisys.TimeSheetPfe.models.UserModel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


public class UserDetailsImpl implements UserDetails {
    private Long id;
    private String email;
    @JsonIgnore
    private  String password;
    private Collection<? extends GrantedAuthority> authorities;

    public  UserDetailsImpl(Long id,String email, String password,
                            Collection<? extends  GrantedAuthority > authorities){
        this.id=id;

        this.email=email;
        this.password=password;
        this.authorities=authorities;
    }
    public static UserDetailsImpl build(UserModel user){
        List<GrantedAuthority> authorities=user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
        return new UserDetailsImpl(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
    public static UserDetailsImpl create(UserModel user, Map<String, Object> attributes) {
        UserDetailsImpl userPrincipal = UserDetailsImpl.build(user);

        return userPrincipal;
    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    public Long getId(){
        return id;
    }
    public  String getEmail(){
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }




    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj == null || getClass() != obj.getClass())
            return false;
        UserDetailsImpl user=(UserDetailsImpl) obj;
        return Objects.equals(id ,user.id);
    }
}
