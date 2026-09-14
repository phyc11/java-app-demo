package com.example.task.controller;
import com.example.common.dto.ApiResponse;
import org.springframework.http.*; import org.springframework.orm.ObjectOptimisticLockingFailureException; import org.springframework.web.bind.annotation.*;
@RestControllerAdvice
public class TaskExceptionHandler {
 @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
 public ResponseEntity<ApiResponse<Void>> conflict(ObjectOptimisticLockingFailureException ex){return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error("Task was updated by another user. Reload and try again."));}
}
