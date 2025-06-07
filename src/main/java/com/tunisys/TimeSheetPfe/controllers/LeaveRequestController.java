package com.tunisys.TimeSheetPfe.controllers;

import com.tunisys.TimeSheetPfe.DTOs.request.LeaveRequestDto;
import com.tunisys.TimeSheetPfe.DTOs.response.LeaveResponseDto;
import com.tunisys.TimeSheetPfe.services.leaveService.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-requests")
public class LeaveRequestController {
    @Autowired
    private LeaveRequestService leaveRequestService;

    @PostMapping
    public ResponseEntity<LeaveResponseDto> create(@RequestBody LeaveRequestDto dto) {
        return ResponseEntity.ok(leaveRequestService.createLeaveRequest(dto));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<LeaveResponseDto> approve(@PathVariable Long id) {
        return ResponseEntity.ok(leaveRequestService.approveLeaveRequest(id));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<LeaveResponseDto> reject(@PathVariable Long id, @RequestParam String rejectionMessage) {
        return ResponseEntity.ok(leaveRequestService.rejectLeaveRequest(id, rejectionMessage));
    }

    @GetMapping("/my")
    public ResponseEntity<List<LeaveResponseDto>> getMyRequests() {
        return ResponseEntity.ok(leaveRequestService.getCurrentUserRequestsOrderedByUpdatedAt());
    }

    @GetMapping("/project")
    public ResponseEntity<List<LeaveResponseDto>> getProjectRequests() {
        return ResponseEntity.ok(leaveRequestService.getProjectUsersLeaveRequestsForManager());
    }
}
