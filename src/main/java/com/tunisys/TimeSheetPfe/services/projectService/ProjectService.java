package com.tunisys.TimeSheetPfe.services.projectService;

import com.tunisys.TimeSheetPfe.models.Project;
import com.tunisys.TimeSheetPfe.repositories.ProjectRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository repository;

    public Project save(Project project) {
        return repository.save(project);
    }

    public Project getById(Long id) {
        return repository.findByIdWithDetails(id);
    }

    public List<Project> getAll() {
        return repository.findAllWithDetails();
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public List<Project> findByDeadline(LocalDate today) {
        return repository.findByDeadline(today);
    }

    public List<Project> findByDeadlineBetween(LocalDate startDate, LocalDate endDate) {
        return repository.findByDeadlineBetween(startDate, endDate);
    }

    public List<Project> findByManagerId(Long managerId) {
        return repository.findByManagerId(managerId);
    }

    @Transactional
    public void deleteProject(Long projectId) {
        // Find the project
        Project project = repository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with ID: " + projectId));

        // Prepare the project for deletion (detach relationships)
        project.prepareForDeletion();

        // Delete the project (cascades to tasks and timesheets)
        repository.delete(project);
    }

}
