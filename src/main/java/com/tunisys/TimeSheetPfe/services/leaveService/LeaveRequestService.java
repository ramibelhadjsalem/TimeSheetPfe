package com.tunisys.TimeSheetPfe.services.leaveService;

import com.tunisys.TimeSheetPfe.DTOs.request.LeaveRequestDto;
import com.tunisys.TimeSheetPfe.DTOs.response.LeaveResponseDto;
import com.tunisys.TimeSheetPfe.models.*;
import com.tunisys.TimeSheetPfe.repositories.LeaveRequestRepository;
import com.tunisys.TimeSheetPfe.services.notificationService.NotificationService;
import com.tunisys.TimeSheetPfe.services.userService.UserService;
import com.tunisys.TimeSheetPfe.utils.TokenUtils;
import com.tunisys.TimeSheetPfe.utils.NotificationMessages;
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
        // Get current user from token instead of using employeeId from request
        Long currentUserId = tokenUtils.ExtractId();
        UserModel employee = userService.findById(currentUserId);

        // Validate input data
        validateLeaveRequest(dto, employee);

        // Check for overlapping leave requests
        validateNoOverlappingLeaves(employee, dto.getStartDate(), dto.getEndDate());

        LeaveRequest request = new LeaveRequest();
        request.setEmployee(employee);
        request.setStartDate(dto.getStartDate());
        request.setEndDate(dto.getEndDate());
        request.setLeaveType(dto.getLeaveType());
        request.setStatus(LeaveStatus.PENDING);
        request.setReason(dto.getReason());
        leaveRequestRepository.save(request);
        notificationService.sendNotification(employee,
            NotificationMessages.Leaves.LEAVE_REQUEST_CREATED_TITLE,
            NotificationMessages.Leaves.LEAVE_REQUEST_CREATED_BODY,
            NotificationType.INFO);
        return toDto(request);
    }

    private void validateLeaveRequest(LeaveRequestDto dto, UserModel employee) {
        // Validate dates
        if (dto.getStartDate() == null) {
            throw new IllegalArgumentException("La date de début est obligatoire");
        }
        if (dto.getEndDate() == null) {
            throw new IllegalArgumentException("La date de fin est obligatoire");
        }
        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin");
        }
        if (dto.getStartDate().isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("La date de début ne peut pas être dans le passé");
        }

        // Validate leave type
        if (dto.getLeaveType() == null) {
            throw new IllegalArgumentException("Le type de congé est obligatoire");
        }

        // Validate reason
        if (dto.getReason() == null || dto.getReason().trim().isEmpty()) {
            throw new IllegalArgumentException("Le motif est obligatoire");
        }
        if (dto.getReason().length() > 500) {
            throw new IllegalArgumentException("Le motif ne peut pas dépasser 500 caractères");
        }

        // Validate duration (max 30 days for a single request)
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1;
        if (daysBetween > 30) {
            throw new IllegalArgumentException("Une demande de congé ne peut pas dépasser 30 jours");
        }

        // Validate minimum advance notice (at least 1 day in advance)
        if (dto.getStartDate().equals(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("Les demandes de congé doivent être soumises au moins 1 jour à l'avance");
        }
    }

    private void validateNoOverlappingLeaves(UserModel employee, java.time.LocalDate startDate, java.time.LocalDate endDate) {
        List<LeaveRequest> existingLeaves = leaveRequestRepository.findByEmployee(employee);

        for (LeaveRequest existingLeave : existingLeaves) {
            // Only check pending and approved leaves
            if (existingLeave.getStatus() == LeaveStatus.REJECTED) {
                continue;
            }

            // Check for overlap
            boolean hasOverlap = !(endDate.isBefore(existingLeave.getStartDate()) ||
                                 startDate.isAfter(existingLeave.getEndDate()));

            if (hasOverlap) {
                throw new IllegalArgumentException(
                    String.format("Cette période chevauche avec une demande existante du %s au %s",
                                existingLeave.getStartDate(), existingLeave.getEndDate())
                );
            }
        }
    }

    @Transactional
    public LeaveResponseDto approveLeaveRequest(Long requestId) {
        LeaveRequest request = leaveRequestRepository.findById(requestId)
            .orElseThrow(() -> new IllegalArgumentException("Demande de congé non trouvée"));

        // Only pending requests can be approved
        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalArgumentException("Seules les demandes en attente peuvent être approuvées");
        }

        // Check if the request is not in the past
        if (request.getStartDate().isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("Impossible d'approuver une demande dont la date de début est passée");
        }

        request.setStatus(LeaveStatus.APPROVED);
        leaveRequestRepository.save(request);
        notificationService.sendNotification(request.getEmployee(),
            NotificationMessages.Leaves.LEAVE_REQUEST_APPROVED_TITLE,
            NotificationMessages.Leaves.LEAVE_REQUEST_APPROVED_BODY,
            NotificationType.INFO);
        return toDto(request);
    }

    @Transactional
    public LeaveResponseDto rejectLeaveRequest(Long requestId, String rejectionMessage) {
        LeaveRequest request = leaveRequestRepository.findById(requestId)
            .orElseThrow(() -> new IllegalArgumentException("Demande de congé non trouvée"));

        // Only pending requests can be rejected
        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalArgumentException("Seules les demandes en attente peuvent être rejetées");
        }

        if (rejectionMessage == null || rejectionMessage.trim().isEmpty()) {
            throw new IllegalArgumentException("Le motif du refus est obligatoire");
        }

        if (rejectionMessage.length() > 500) {
            throw new IllegalArgumentException("Le motif du refus ne peut pas dépasser 500 caractères");
        }

        request.setStatus(LeaveStatus.REJECTED);
        request.setRejectionMessage(rejectionMessage);
        leaveRequestRepository.save(request);
        notificationService.sendNotification(request.getEmployee(),
            NotificationMessages.Leaves.LEAVE_REQUEST_REJECTED_TITLE,
            NotificationMessages.Leaves.leaveRequestRejectedBody(rejectionMessage),
            NotificationType.WARNING);
        return toDto(request);
    }

    public List<LeaveResponseDto> getCurrentUserRequestsOrderedByUpdatedAt() {
        Long userId = tokenUtils.ExtractId();
        UserModel user = userService.findById(userId);
        return leaveRequestRepository.findByEmployeeOrderByIdDesc(user)
            .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<LeaveResponseDto> getProjectUsersLeaveRequestsForManager() {
        Long managerId = tokenUtils.ExtractId();
        UserModel manager = userService.findById(managerId);

        // Check if user is admin - admins can see all leave requests
        boolean isAdmin = manager.getRoles().stream()
            .anyMatch(role -> role.getName() == ERole.ROLE_ADMIN);

        if (isAdmin) {
            // Admin can see all leave requests
            return leaveRequestRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        }

        // For managers, get leave requests from their current project
        Project currentProject = manager.getCurrentProject();
        if (currentProject == null) {
            return List.of();
        }

        // Get leave requests from the current project
        return leaveRequestRepository.findByProjectIdOrderByIdDesc(currentProject.getId())
            .stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    public List<LeaveResponseDto> getUserLeaveRequests(Long userId) {
        UserModel user = userService.findById(userId);
        return leaveRequestRepository.findByEmployeeOrderByIdDesc(user)
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
