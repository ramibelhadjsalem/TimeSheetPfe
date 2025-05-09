package com.tunisys.TimeSheetPfe.services.taskService;

import com.tunisys.TimeSheetPfe.models.Task;
import com.tunisys.TimeSheetPfe.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository repository;

    public Task Save(Task task) {
        return repository.save(task);
    }

    public Task getById(Long id) {
        return repository.findByIdWithDetails(id);
    }

    public List<Task> getAll() {
        return repository.findAllWithDetails();
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public void deleteTaskSafely(Long id) {
        Task task = getById(id);
        task.prepareForDeletion(); // Détache la tâche des utilisateurs et du projet
        repository.save(task); // Sauvegarde les changements de relations
        repository.delete(task); // Supprime la tâche et les feuilles de temps associées (grâce à
                                 // CascadeType.ALL)
    }

    public List<Task> findByDeadline(java.time.LocalDate today) {
        return repository.findByDeadline(today);
    }

    public List<Task> findByDeadlineBetween(LocalDate today, LocalDate endDate) {
        return repository.findByDeadlineBetween(today, endDate);
    }

    public List<Task> getTasksByUserIdAndProjectId(Long userId, Long projectId) {

        return repository.findByProjectIdAndEmployeeId(userId, projectId);
    }

    public Task save(Task task) {
        return repository.save(task);
    }
}
