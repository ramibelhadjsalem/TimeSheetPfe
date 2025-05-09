package com.tunisys.TimeSheetPfe.repositories;

import com.tunisys.TimeSheetPfe.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT DISTINCT t FROM Task t LEFT JOIN FETCH t.employees LEFT JOIN FETCH t.project WHERE t.deadline = ?1")
    List<Task> findByDeadline(LocalDate today);

    @Query("SELECT DISTINCT t FROM Task t LEFT JOIN FETCH t.employees LEFT JOIN FETCH t.project WHERE t.deadline BETWEEN ?1 AND ?2")
    List<Task> findByDeadlineBetween(LocalDate today, LocalDate endDate);

    @Query("SELECT DISTINCT t FROM Task t LEFT JOIN FETCH t.employees e LEFT JOIN FETCH t.project WHERE e.id = :userId AND t.project.id = :projectId")
    List<Task> findByProjectIdAndEmployeeId(@Param("userId") Long userId, @Param("projectId") Long projectId);

    @Query("SELECT DISTINCT t FROM Task t LEFT JOIN FETCH t.employees LEFT JOIN FETCH t.project")
    List<Task> findAllWithDetails();

    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.employees LEFT JOIN FETCH t.project WHERE t.id = :id")
    Task findByIdWithDetails(@Param("id") Long id);
}
