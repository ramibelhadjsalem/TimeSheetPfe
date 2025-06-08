package com.tunisys.TimeSheetPfe.controllers;

import com.tunisys.TimeSheetPfe.DTOs.request.LeaveRequestDto;
import com.tunisys.TimeSheetPfe.DTOs.response.LeaveResponseDto;
import com.tunisys.TimeSheetPfe.services.leaveService.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leave-requests")
public class LeaveRequestController {
    @Autowired
    private LeaveRequestService leaveRequestService;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody LeaveRequestDto dto) {
        LeaveResponseDto response = leaveRequestService.createLeaveRequest(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> approve(@PathVariable Long id) {
        try {
            LeaveResponseDto response = leaveRequestService.approveLeaveRequest(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Une erreur interne s'est produite"));
        }
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> reject(@PathVariable Long id, @RequestParam String rejectionMessage) {
        try {
            LeaveResponseDto response = leaveRequestService.rejectLeaveRequest(id, rejectionMessage);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Une erreur interne s'est produite"));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<List<LeaveResponseDto>> getMyRequests() {
        return ResponseEntity.ok(leaveRequestService.getCurrentUserRequestsOrderedByUpdatedAt());
    }

    @GetMapping("/project")
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<LeaveResponseDto>> getProjectRequests() {
        return ResponseEntity.ok(leaveRequestService.getProjectUsersLeaveRequestsForManager());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<LeaveResponseDto>> getUserRequests(@PathVariable Long userId) {
        return ResponseEntity.ok(leaveRequestService.getUserLeaveRequests(userId));
    }
}
