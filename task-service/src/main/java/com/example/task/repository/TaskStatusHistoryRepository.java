package com.example.task.repository;
import com.example.task.model.TaskStatusHistory; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List; import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable;
public interface TaskStatusHistoryRepository extends JpaRepository<TaskStatusHistory,Long>{List<TaskStatusHistory> findByTaskIdOrderByChangedAtDesc(Long taskId);Page<TaskStatusHistory> findByTaskId(Long taskId,Pageable pageable);}
