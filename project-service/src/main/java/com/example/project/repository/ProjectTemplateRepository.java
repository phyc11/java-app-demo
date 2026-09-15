package com.example.project.repository;
import com.example.project.model.ProjectTemplate; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface ProjectTemplateRepository extends JpaRepository<ProjectTemplate,Long>{List<ProjectTemplate> findByWorkspaceIdOrderByName(Long workspaceId);boolean existsByWorkspaceIdAndNameIgnoreCase(Long workspaceId,String name);}
