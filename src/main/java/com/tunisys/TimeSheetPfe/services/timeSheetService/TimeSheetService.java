package com.tunisys.TimeSheetPfe.services.timeSheetService;

import com.tunisys.TimeSheetPfe.exceptions.EntityNotFoundException;
import com.tunisys.TimeSheetPfe.models.TimeSheet;
import com.tunisys.TimeSheetPfe.repositories.TimeSheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimeSheetService {

    @Autowired private TimeSheetRepository timeSheetRepository;

    public TimeSheet save(TimeSheet timeSheet){
        return timeSheetRepository.save(timeSheet);
    }

    public TimeSheet findById(Long id) {

        return timeSheetRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("TimeSheet not found with id :"+id));
    }

    public List<TimeSheet> findByUserId(Long userId){
        return timeSheetRepository.findByUserId(userId);
    }

    public List<TimeSheet> findByUserIdAndCurrentProject(Long userId,Long currentProjectId){
        return timeSheetRepository.findByUserIdAndProjectId(userId,currentProjectId);
    }
}
