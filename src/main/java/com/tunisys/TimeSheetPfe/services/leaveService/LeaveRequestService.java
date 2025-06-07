package com.tunisys.TimeSheetPfe.services.leaveService;

import com.tunisys.TimeSheetPfe.DTOs.request.LeaveRequestDto;
import com.tunisys.TimeSheetPfe.DTOs.response.LeaveResponseDto;
import com.tunisys.TimeSheetPfe.models.*;
import com.tunisys.TimeSheetPfe.repositories.LeaveRequestRepository;
import com.tunisys.TimeSheetPfe.services.notificationService.NotificationService;
import com.tunisys.TimeSheetPfe.services.userService.UserService;
import com.tunisys.TimeSheetPfe.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveRequestService {
    @Autowired
    private LeaveRequestRepository leaveRequestRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private TokenUtils tokenUtils;
    @Autowired
    private UserService userService;

    @Transactional
    public LeaveResponseDto createLeaveRequest(LeaveRequestDto dto) {
        UserModel employee = userService.findById(dto.getEmployeeId());

        
        LeaveRequest request = new LeaveRequest();
        request.setEmployee(employee);
        request.setStartDate(dto.getStartDate());
        request.setEndDate(dto.getEndDate());
        request.setLeaveType(dto.getLeaveType());
        request.setStatus(LeaveStatus.PENDING);
        request.setReason(dto.getReason());
        leaveRequestRepository.save(request);
        notificationService.sendNotification(employee, "Demande de congé créée", "Votre demande de congé est en attente.", NotificationType.INFO);
        return toDto(request);
    }

    @Transactional
    public LeaveResponseDto approveLeaveRequest(Long requestId) {
        LeaveRequest request = leaveRequestRepository.findById(requestId).get();
        request.setStatus(LeaveStatus.APPROVED);
        leaveRequestRepository.save(request);
        notificationService.sendNotification(request.getEmployee(), "Demande de congé approuvée", "Votre demande de congé a été approuvée.", NotificationType.INFO);
        return toDto(request);
    }

    @Transactional
    public LeaveResponseDto rejectLeaveRequest(Long requestId, String rejectionMessage) {
        LeaveRequest request = leaveRequestRepository.findById(requestId).get();
        if (rejectionMessage == null || rejectionMessage.isEmpty()) {
            throw new IllegalArgumentException("Le motif du refus est obligatoire");
        }
        request.setStatus(LeaveStatus.REJECTED);
        request.setRejectionMessage(rejectionMessage);
        leaveRequestRepository.save(request);
        notificationService.sendNotification(request.getEmployee(), "Demande de congé refusée", rejectionMessage, NotificationType.INFO);
        return toDto(request);
    }

    public List<LeaveResponseDto> getCurrentUserRequestsOrderedByUpdatedAt() {
        Long userId = tokenUtils.ExtractId();
        UserModel user = userService.findById(userId);
        return leaveRequestRepository.findByEmployeeOrderByUpdatedAtDesc(user)
            .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<LeaveResponseDto> getProjectUsersLeaveRequestsForManager() {
        Long managerId = tokenUtils.ExtractId();
        UserModel manager = userService.findById(managerId);
        Project project = manager.getCurrentProject();
        if (project == null) return List.of();
        return leaveRequestRepository.findByProjectIdOrderByUpdatedAtDesc(project.getId())
            .stream().map(this::toDto).collect(Collectors.toList());
    }

    private LeaveResponseDto toDto(LeaveRequest request) {
        UserModel emp = request.getEmployee();
        LeaveResponseDto.UserInfo userInfo = LeaveResponseDto.UserInfo.builder()
            .id(emp.getId())
            .name(emp.getName())
            .email(emp.getEmail())
            .phone(emp.getPhone())
            .imgUrl(emp.getImgUrl())
            .currentProject(emp.getCurrentProject() != null ? emp.getCurrentProject().getId() : null)
            .build();
        return LeaveResponseDto.builder()
            .id(request.getId())
            .employee(userInfo)
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .leaveType(request.getLeaveType())
            .status(request.getStatus())
            .reason(request.getReason())
            .rejectionMessage(request.getRejectionMessage())
            .build();
    }
}
