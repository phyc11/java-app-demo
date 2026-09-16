package com.example.task.repository;
import com.example.task.model.TaskDependency; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface TaskDependencyRepository extends JpaRepository<TaskDependency,Long>{
 List<TaskDependency> findByTaskId(Long taskId); boolean existsByTaskIdAndDependsOnTaskId(Long taskId,Long dependsOnTaskId);
 void deleteByTaskIdAndDependsOnTaskId(Long taskId,Long dependsOnTaskId);
 void deleteByTaskId(Long taskId);
}
