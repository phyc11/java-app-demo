package com.example.task.service;

import com.example.common.exception.ResourceNotFoundException;
import com.example.task.dto.TaskDTO;
import com.example.task.dto.TaskStatsDTO;
import com.example.task.model.Category;
import com.example.task.model.Priority;
import com.example.task.model.Status;
import com.example.task.model.Task;
import com.example.task.repository.CategoryRepository;
import com.example.task.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, CategoryRepository categoryRepository) {
        this.taskRepository = taskRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<TaskDTO> getAllTasks(Status status, Priority priority, Long categoryId, String search) {
        List<Task> tasks = taskRepository.findFilteredTasks(status, priority, categoryId, search);
        return tasks.stream().map(TaskDTO::new).collect(Collectors.toList());
    }

    public TaskDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
        return new TaskDTO(task);
    }

    public TaskDTO createTask(TaskDTO taskDTO, String username) {
        Task task = new Task();
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus() != null ? taskDTO.getStatus() : Status.TODO);
        task.setPriority(taskDTO.getPriority() != null ? taskDTO.getPriority() : Priority.MEDIUM);
        task.setDueDate(taskDTO.getDueDate());
        task.setCreatedBy(username != null ? username : "System");

        if (taskDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(taskDTO.getCategoryId()).orElse(null);
            task.setCategory(category);
        }

        Task savedTask = taskRepository.save(task);
        return new TaskDTO(savedTask);
    }

    public TaskDTO updateTask(Long id, TaskDTO taskDTO) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));

        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        if (taskDTO.getStatus() != null) task.setStatus(taskDTO.getStatus());
        if (taskDTO.getPriority() != null) task.setPriority(taskDTO.getPriority());
        task.setDueDate(taskDTO.getDueDate());

        if (taskDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(taskDTO.getCategoryId()).orElse(null);
            task.setCategory(category);
        } else {
            task.setCategory(null);
        }

        Task updatedTask = taskRepository.save(task);
        return new TaskDTO(updatedTask);
    }

    public TaskDTO updateTaskStatus(Long id, Status status) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));

        task.setStatus(status);
        Task updatedTask = taskRepository.save(task);
        return new TaskDTO(updatedTask);
    }

    public TaskDTO moveTask(Long id, Status status, Integer position) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));

        task.setStatus(status);
        if (position != null) {
            task.setPosition(position);
        }

        Task updatedTask = taskRepository.save(task);
        return new TaskDTO(updatedTask);
    }

    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
        taskRepository.delete(task);
    }

    public TaskStatsDTO getStats() {
        long total = taskRepository.count();
        long todo = taskRepository.countByStatus(Status.TODO);
        long inProgress = taskRepository.countByStatus(Status.IN_PROGRESS);
        long completed = taskRepository.countByStatus(Status.COMPLETED);
        return new TaskStatsDTO(total, todo, inProgress, completed);
    }
}
