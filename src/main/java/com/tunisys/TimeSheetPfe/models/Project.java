package com.tunisys.TimeSheetPfe.models;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonView;
import com.tunisys.TimeSheetPfe.DTOs.view.View;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "projects")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(View.Base.class)
    private Long id;
    @JsonView(View.Base.class)
    private String name;
    @JsonView(View.Base.class)
    private String description;
    @JsonView(View.Base.class)
    private LocalDate deadline;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manager_id")
    @JsonView(View.External.class)
    private UserModel manager;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "project_employees",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @JsonView(View.External.class)
    private Set<UserModel> employees = new HashSet<>();


    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Task> tasks = new HashSet<>();



}


