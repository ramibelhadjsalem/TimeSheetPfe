package com.tunisys.TimeSheetPfe.repositories;

import com.tunisys.TimeSheetPfe.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.manager LEFT JOIN FETCH p.employees WHERE p.deadline = ?1")
    List<Project> findByDeadline(LocalDate today);

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.manager LEFT JOIN FETCH p.employees WHERE p.deadline BETWEEN :startDate AND :endDate")
    List<Project> findByDeadlineBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.employees LEFT JOIN FETCH p.tasks WHERE p.manager.id = :managerId")
    List<Project> findByManagerId(Long managerId);

    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN FETCH p.manager LEFT JOIN FETCH p.employees e WHERE e.id = :userId")
    List<Project> findProjectsByEmployeeId(Long userId);

    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN FETCH p.manager LEFT JOIN FETCH p.employees LEFT JOIN FETCH p.tasks")
    List<Project> findAllWithDetails();

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.manager LEFT JOIN FETCH p.employees LEFT JOIN FETCH p.tasks WHERE p.id = :id")
    Project findByIdWithDetails(Long id);
}
