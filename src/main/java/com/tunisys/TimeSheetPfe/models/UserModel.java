package com.tunisys.TimeSheetPfe.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.tunisys.TimeSheetPfe.DTOs.view.View;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "users")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(View.Base.class)
    private Long id;

    @JsonView(View.Base.class)
    private String name;

    @JsonIgnore
    private String password;

    @JsonView(View.Base.class)
    private String phone;

    @JsonView(View.Base.class)
    private String email;

    @JsonView(View.Base.class)
    private String imgUrl;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @JsonView(View.Base.class)
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(mappedBy = "employees", fetch = FetchType.LAZY)
    @JsonView(View.Base.class)
    @JsonBackReference()
    private Set<Task> tasks = new HashSet<>();

    public UserModel(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
