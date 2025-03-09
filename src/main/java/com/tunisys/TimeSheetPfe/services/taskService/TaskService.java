package com.tunisys.TimeSheetPfe.services.taskService;

import com.tunisys.TimeSheetPfe.exceptions.EntityNotFoundException;
import com.tunisys.TimeSheetPfe.models.Task;
import com.tunisys.TimeSheetPfe.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired private TaskRepository repository ;
    public Task Save(Task task){
        return repository.save(task);
    }

    public Task getById(Long id){
        return repository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("NO task found with id"+id));
    }

    public List<Task> getAll(){
        return repository.findAll();
    }

    public void deleteById(Long id){
        repository.deleteById(id);
    }
}
