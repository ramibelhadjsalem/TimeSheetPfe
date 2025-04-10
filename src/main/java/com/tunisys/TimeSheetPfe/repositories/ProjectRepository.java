package com.tunisys.TimeSheetPfe.repositories;

import com.tunisys.TimeSheetPfe.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project,Long> {


    @Query("select p from Project p where p.deadline = ?1")
    List<Project> findByDeadline(LocalDate today);

    @Query("SELECT p FROM Project p WHERE p.deadline BETWEEN :startDate AND :endDate")
    List<Project> findByDeadlineBetween(LocalDate startDate, LocalDate endDate);
}
