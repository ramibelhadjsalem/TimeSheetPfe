package com.tunisys.TimeSheetPfe.repositories;

import com.tunisys.TimeSheetPfe.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Long> {

    @Query("select t from Task t where t.deadline = ?1")
    List<Task> findByDeadline(LocalDate today);

    @Query("select t from Task t where t.deadline between ?1 and ?2")
    List<Task> findByDeadlineBetween(LocalDate today, LocalDate endDate);

    @Query("SELECT t FROM Task t JOIN t.employees e WHERE e.id = :userId AND t.project.id = :projectId")
    List<Task> findByProjectIdAndEmployeeId(@Param("userId") Long userId, @Param("projectId") Long projectId);
}
