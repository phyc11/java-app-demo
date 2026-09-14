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
import org.springframework.data.domain.Page;
import com.example.task.model.TaskStatusHistory;

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
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long workspaceId,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String assignee,
            @RequestParam(required = false) Long parentTaskId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "position") String sort) {
        Page<TaskDTO> tasks = taskService.getTasks(workspaceId,projectId,assignee,parentTaskId,status,priority,categoryId,search,page,size,sort);
        return ApiResponse.okPage("Tasks retrieved successfully", tasks.getContent(), page, size, tasks.getTotalElements(), tasks.getTotalPages());
    }

    @GetMapping("/stats")
    public ApiResponse<TaskStatsDTO> getStats(@RequestParam(required=false) Long workspaceId) {
        TaskStatsDTO stats = taskService.getStats(workspaceId);
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
    public ApiResponse<TaskDTO> updateTaskStatus(@PathVariable Long id, @RequestBody Map<String, String> body, Principal principal) {
        Status status = Status.valueOf(body.get("status"));
        TaskDTO updatedTask = taskService.updateTaskStatus(id, status, principal == null ? "User" : principal.getName());
        return ApiResponse.ok("Task status updated successfully", updatedTask);
    }

    @PatchMapping("/{id}/move")
    public ApiResponse<TaskDTO> moveTask(@PathVariable Long id, @RequestBody Map<String, Object> body, Principal principal) {
        Status status = Status.valueOf((String) body.get("status"));
        Integer position = body.get("position") != null ? (Integer) body.get("position") : null;
        TaskDTO updatedTask = taskService.moveTask(id, status, position, principal == null ? "User" : principal.getName());
        return ApiResponse.ok("Task moved successfully", updatedTask);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ApiResponse.ok("Task deleted successfully", null);
    }

    @PostMapping("/{id}/watchers/{username}")
    public ApiResponse<TaskDTO> addWatcher(@PathVariable Long id,@PathVariable String username){return ApiResponse.ok("Watcher added",taskService.addWatcher(id,username));}
    @DeleteMapping("/{id}/watchers/{username}")
    public ApiResponse<TaskDTO> removeWatcher(@PathVariable Long id,@PathVariable String username){return ApiResponse.ok("Watcher removed",taskService.removeWatcher(id,username));}
    @PostMapping("/{id}/dependencies/{dependsOnId}")
    public ApiResponse<TaskDTO> addDependency(@PathVariable Long id,@PathVariable Long dependsOnId){return ApiResponse.ok("Dependency added",taskService.addDependency(id,dependsOnId));}
    @DeleteMapping("/{id}/dependencies/{dependsOnId}")
    public ApiResponse<Void> removeDependency(@PathVariable Long id,@PathVariable Long dependsOnId){taskService.removeDependency(id,dependsOnId);return ApiResponse.ok("Dependency removed",null);}
    @GetMapping("/{id}/history")
    public ApiResponse<List<TaskStatusHistory>> history(@PathVariable Long id){return ApiResponse.ok("History retrieved",taskService.getHistory(id));}
}
