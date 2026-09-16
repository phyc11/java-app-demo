package com.example.task.repository;
import com.example.task.model.TaskTemplate;import org.springframework.data.jpa.repository.JpaRepository;import java.util.List;
public interface TaskTemplateRepository extends JpaRepository<TaskTemplate,Long>{List<TaskTemplate> findByWorkspaceIdOrderByName(Long workspaceId);boolean existsByWorkspaceIdAndNameIgnoreCase(Long workspaceId,String name);}
