package com.tunisys.TimeSheetPfe.models;

import jakarta.persistence.*;
import lombok.*;

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
    private Long id;

    private String title;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private UserModel employee; // Primary assigned employee

    @Enumerated(EnumType.STRING)
    private EStatus status = EStatus.NOT_STARTED;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "task_employees",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<UserModel> employees = new HashSet<>(); // Employees working on the task

    @Enumerated(EnumType.STRING)
    private EPriority priority = EPriority.LOW;

    @Enumerated(EnumType.STRING)
    private EDifficulty difficulty = EDifficulty.EASY;

    @ElementCollection
    @CollectionTable(name = "task_attachments", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "attachment_url")
    private List<String> attachments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;
}





