package com.tunisys.TimeSheetPfe.controllers;

import com.tunisys.TimeSheetPfe.DTOs.request.TimeSheetCreateDTO;
import com.tunisys.TimeSheetPfe.DTOs.request.TimesheetUpdateDto;
import com.tunisys.TimeSheetPfe.DTOs.response.MessageResponse;
import com.tunisys.TimeSheetPfe.DTOs.response.TimeSheetResponseDto;
import com.tunisys.TimeSheetPfe.models.Task;
import com.tunisys.TimeSheetPfe.models.TimeSheet;
import com.tunisys.TimeSheetPfe.models.UserModel;
import com.tunisys.TimeSheetPfe.services.taskService.TaskService;
import com.tunisys.TimeSheetPfe.services.timeSheetService.TimeSheetService;
import com.tunisys.TimeSheetPfe.services.userService.UserService;
import com.tunisys.TimeSheetPfe.utils.TokenUtils;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/timesheet")
public class TimeSheetController {
    @Autowired private UserService userService;
    @Autowired private TokenUtils tokenUtils;
    @Autowired private TaskService taskService ;
    @Autowired private TimeSheetService timeSheetService;
    @Autowired private ModelMapper modelMapper;


    @GetMapping("/task/{id}")
    public List<TimeSheetResponseDto> findByTaskId(@PathVariable Long id) {
        return taskService.getById(id).getTimesheetEntries().stream()
                .map(ts -> modelMapper.map(ts, TimeSheetResponseDto.class)
                ).collect(Collectors.toList());
    }
    @GetMapping("/user/{id}")
    public List<TimeSheetResponseDto> findByUserId(@PathVariable Long id){
        return timeSheetService.findByUserId(id).stream()
                .map(ts -> modelMapper.map(ts, TimeSheetResponseDto.class)
                ) .collect(Collectors.toList());

    }
    @GetMapping("/current")
    public List<TimeSheetResponseDto> findByCurrentUser(){
        Long id = tokenUtils.ExtractId();
        return timeSheetService.findByUserId(id).stream()
                .map(ts -> modelMapper.map(ts, TimeSheetResponseDto.class)
                ) .collect(Collectors.toList());

    }


    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_EMPLOYEE')")
    public ResponseEntity<?> createTimeSheet(@Valid @RequestBody TimeSheetCreateDTO dto) {

        UserModel user = userService.findById(tokenUtils.ExtractId());
        Task task = taskService.getById(dto.getTaskId());

        int startMins = parseTimeToMinutes(dto.getStartTime());
        int endMins = parseTimeToMinutes(dto.getEndTime());
        if (endMins <= startMins) {
            return MessageResponse.badRequest("End time must be after start time");
        }

        if (!user.getCurrentProject().getId().equals(task.getProject().getId())) {
            return MessageResponse.notAllowed("not allowed to do this action");
        }
        TimeSheet timeSheet = TimeSheet
                .builder()
                .date(dto.getDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .description(dto.getDescription())
                .user(user)
                .task(task)
                .build();
        return ResponseEntity.ok(modelMapper.map(timeSheetService.save(timeSheet), TimeSheetResponseDto.class));
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTimeSheet(@PathVariable Long id,@Valid @RequestBody TimesheetUpdateDto dto){
        TimeSheet timeSheet = timeSheetService.findById(id);

        UserModel user = userService.findById(tokenUtils.ExtractId());

        if (!timeSheet.getTask().getEmployees().contains(user)){
            return MessageResponse.notAllowed("Not allowed to update this timesheet");
        }
        int startMins = parseTimeToMinutes(dto.getStartTime());
        int endMins = parseTimeToMinutes(dto.getEndTime());
        if (endMins <= startMins) {
            return MessageResponse.badRequest("End time must be after start time");
        }

        timeSheet.setEndTime(dto.getStartTime());
        timeSheet.setEndTime(dto.getEndTime());
        timeSheet.setDescription(dto.getDescription());

        return ResponseEntity.ok(modelMapper.map(timeSheetService.save(timeSheet), TimeSheetResponseDto.class));


    }


    private int parseTimeToMinutes(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes;
    }
}

