package com.tunisys.TimeSheetPfe.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.tunisys.TimeSheetPfe.DTOs.view.View;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@NamedQueries({
        @NamedQuery(name = "UserModel.findAll", query = "select u from UserModel u")
})
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
    @Column(name = "first_name")
    private String firstName;

    @JsonView(View.Base.class)
    @Column(name = "last_name")
    private String lastName;

    @Transient
    @JsonView(View.Base.class)
    public String getName() {
        return firstName + " " + lastName;
    }

    @JsonIgnore
    private String password;

    @JsonView(View.Base.class)
    private String phone;

    @JsonView(View.Base.class)
    @Column(unique = true)
    private String email;

    @JsonView(View.Base.class)
    @Column(name = "img_url")
    private String imgUrl;

    @JsonView(View.Base.class)
    @Column(name = "cin", unique = true, length = 8)
    private String cin;

    @JsonView(View.Base.class)
    private String department;

    @JsonView(View.Base.class)
    private Integer experience;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @JsonView(View.Base.class)
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(mappedBy = "employees", fetch = FetchType.EAGER)
    @JsonView(View.Base.class)
    @JsonBackReference()
    private Set<Task> tasks = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "current_project_id")
    @JsonView(View.Base.class)
    private Project currentProject;

    public UserModel(String email, String password) {
        this.email = email;
        this.password = password;
    }
}