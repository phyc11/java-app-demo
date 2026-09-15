package com.example.project.controller;
import com.example.common.dto.ApiResponse; import org.springframework.http.*; import org.springframework.orm.ObjectOptimisticLockingFailureException; import org.springframework.web.bind.annotation.*;
@RestControllerAdvice public class ProjectExceptionHandler {
 @ExceptionHandler(ObjectOptimisticLockingFailureException.class) ResponseEntity<ApiResponse<Void>> conflict(Exception e){return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error("Project was updated by another user. Reload and try again."));}
 @ExceptionHandler(SecurityException.class) ResponseEntity<ApiResponse<Void>> forbidden(Exception e){return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(e.getMessage()));}
 @ExceptionHandler({IllegalArgumentException.class,IllegalStateException.class}) ResponseEntity<ApiResponse<Void>> bad(Exception e){return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));}
}
