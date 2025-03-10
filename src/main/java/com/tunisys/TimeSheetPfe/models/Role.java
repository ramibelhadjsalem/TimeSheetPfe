package com.tunisys.TimeSheetPfe.models;

import com.fasterxml.jackson.annotation.JsonView;
import com.tunisys.TimeSheetPfe.DTOs.view.View;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(length = 25)
    @JsonView(View.Base.class)
    private ERole name;
}
