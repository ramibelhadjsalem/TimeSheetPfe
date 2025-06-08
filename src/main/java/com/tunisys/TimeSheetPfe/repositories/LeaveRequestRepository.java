package com.tunisys.TimeSheetPfe.repositories;

import com.tunisys.TimeSheetPfe.models.LeaveRequest;
import com.tunisys.TimeSheetPfe.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByEmployee(UserModel employee);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.employee = :employee ORDER BY lr.id DESC")
    List<LeaveRequest> findByEmployeeOrderByIdDesc(@Param("employee") UserModel employee);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.employee.currentProject.id = :projectId ORDER BY lr.id DESC")
    List<LeaveRequest> findByProjectIdOrderByIdDesc(@Param("projectId") Long projectId);
}
