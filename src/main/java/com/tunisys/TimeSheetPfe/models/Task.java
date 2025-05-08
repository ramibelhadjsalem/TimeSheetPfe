package com.tunisys.TimeSheetPfe.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import com.tunisys.TimeSheetPfe.DTOs.view.View;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tasks")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(View.Base.class)
    private Long id;

    @JsonView(View.Base.class)
    private String title;

    @JsonView(View.Base.class)
    private String description;

    @Enumerated(EnumType.STRING)
    @JsonView(View.Base.class)
    @Builder.Default
    private EStatus status = EStatus.NOT_STARTED;

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH })
    @JoinTable(name = "task_employees", joinColumns = @JoinColumn(name = "task_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    @JsonView(View.External.class)
    @JsonManagedReference // Prevent recursion from Task to UserModel
    @Builder.Default
    private Set<UserModel> employees = new HashSet<>(); // Employees working on the task

    @Enumerated(EnumType.STRING)
    @JsonView(View.Base.class)
    @Builder.Default
    private EPriority priority = EPriority.LOW;

    @Enumerated(EnumType.STRING)
    @JsonView(View.Base.class)
    @Builder.Default
    private EDifficulty difficulty = EDifficulty.EASY;

    @ElementCollection
    @CollectionTable(name = "task_attachments", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "attachment_url")
    @JsonView(View.Base.class)
    @Builder.Default
    private List<String> attachments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @JsonView(View.Base.class)
    private Project project;

    @JsonView(View.Base.class)
    private LocalDate deadline;

    @JsonView(View.Base.class)
    private LocalDate finishedAt;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TimeSheet> timesheetEntries;

    public void addEmployee(UserModel user) {
        this.employees.add(user);
        user.getTasks().add(this); // Update the other side of the relationship
    }

    public void prepareForDeletion() {
        // Detach from employees
        for (UserModel employee : employees) {
            employee.getTasks().remove(this); // Remove this task from user's tasks
        }
        employees.clear();

        // // Detach from manager
        // if (manager != null) {
        // manager = null;
        // }

        // Detach from project
        if (project != null) {
            project.getTasks().remove(this);
            project = null;
        }

        // Note: timesheetEntries are automatically deleted due to cascade =
        // CascadeType.ALL
    }
}
