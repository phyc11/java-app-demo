package com.example.task.controller;

import com.example.common.dto.ApiResponse;
import com.example.task.dto.TaskDTO;
import com.example.task.dto.TaskStatsDTO;
import com.example.task.model.Priority;
import com.example.task.model.Status;
import com.example.task.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ApiResponse<List<TaskDTO>> getAllTasks(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search) {
        List<TaskDTO> tasks = taskService.getAllTasks(status, priority, categoryId, search);
        return ApiResponse.ok("Tasks retrieved successfully", tasks);
    }

    @GetMapping("/stats")
    public ApiResponse<TaskStatsDTO> getStats() {
        TaskStatsDTO stats = taskService.getStats();
        return ApiResponse.ok("Task stats retrieved successfully", stats);
    }

    @GetMapping("/{id}")
    public ApiResponse<TaskDTO> getTaskById(@PathVariable Long id) {
        TaskDTO task = taskService.getTaskById(id);
        return ApiResponse.ok("Task retrieved successfully", task);
    }

    @PostMapping
    public ApiResponse<TaskDTO> createTask(@Valid @RequestBody TaskDTO taskDTO, Principal principal) {
        String username = principal != null ? principal.getName() : "User";
        TaskDTO createdTask = taskService.createTask(taskDTO, username);
        return ApiResponse.ok("Task created successfully", createdTask);
    }

    @PutMapping("/{id}")
    public ApiResponse<TaskDTO> updateTask(@PathVariable Long id, @Valid @RequestBody TaskDTO taskDTO) {
        TaskDTO updatedTask = taskService.updateTask(id, taskDTO);
        return ApiResponse.ok("Task updated successfully", updatedTask);
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<TaskDTO> updateTaskStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Status status = Status.valueOf(body.get("status"));
        TaskDTO updatedTask = taskService.updateTaskStatus(id, status);
        return ApiResponse.ok("Task status updated successfully", updatedTask);
    }

    @PatchMapping("/{id}/move")
    public ApiResponse<TaskDTO> moveTask(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Status status = Status.valueOf((String) body.get("status"));
        Integer position = body.get("position") != null ? (Integer) body.get("position") : null;
        TaskDTO updatedTask = taskService.moveTask(id, status, position);
        return ApiResponse.ok("Task moved successfully", updatedTask);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ApiResponse.ok("Task deleted successfully", null);
    }
}
