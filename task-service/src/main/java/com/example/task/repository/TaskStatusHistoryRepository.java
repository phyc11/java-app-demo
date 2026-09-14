package com.example.task.repository;
import com.example.task.model.TaskStatusHistory; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface TaskStatusHistoryRepository extends JpaRepository<TaskStatusHistory,Long>{List<TaskStatusHistory> findByTaskIdOrderByChangedAtDesc(Long taskId);}
