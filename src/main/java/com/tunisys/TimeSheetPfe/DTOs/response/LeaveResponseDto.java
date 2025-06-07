package com.tunisys.TimeSheetPfe.DTOs.response;

import com.tunisys.TimeSheetPfe.models.LeaveStatus;
import com.tunisys.TimeSheetPfe.models.LeaveType;
import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveResponseDto {
    private Long id;
    private UserInfo employee;
    private LocalDate startDate;
    private LocalDate endDate;
    private LeaveType leaveType;
    private LeaveStatus status;
    private String reason;
    private String rejectionMessage;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfo {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private String imgUrl;
        private Long currentProject;
    }
}
