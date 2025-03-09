package com.tunisys.TimeSheetPfe.services.projectService;

import com.tunisys.TimeSheetPfe.exceptions.EntityNotFoundException;
import com.tunisys.TimeSheetPfe.models.Project;
import com.tunisys.TimeSheetPfe.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    @Autowired private ProjectRepository repository;


    public Project save(Project project){
        return repository.save(project);
    }

    public Project getById(Long id){
        return repository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("NO project found with id"+id));
    }

    public List<Project> getAll(){
        return repository.findAll();
    }

    public void deleteById(Long id){
         repository.deleteById(id);
    }

}
