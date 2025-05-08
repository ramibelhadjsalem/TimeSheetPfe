package com.tunisys.TimeSheetPfe.repositories;

import com.tunisys.TimeSheetPfe.models.Task;
import com.tunisys.TimeSheetPfe.models.TimeSheet;
import com.tunisys.TimeSheetPfe.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TimeSheetRepository extends JpaRepository<TimeSheet,Long> {

    @Query("select t from TimeSheet t where t.user.id = ?1")
    List<TimeSheet> findByUserId(Long userId);

    @Query("select (count(t) > 0) from TimeSheet t where t.user = ?1 and t.task = ?2 and t.date = ?3")
    boolean existsByUserAndTaskAndDate(UserModel user, Task task, LocalDate today);


    @Query("select t from TimeSheet t where t.user.id = ?1 and t.task.project.id = ?2")
    List<TimeSheet> findByUserIdAndProjectId(Long userId, Long projectId);
}
