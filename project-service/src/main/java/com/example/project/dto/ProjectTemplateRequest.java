package com.example.project.dto;
import javax.validation.constraints.NotBlank;
public class ProjectTemplateRequest {@NotBlank private String name; private String description; private Long sourceProjectId; public String getName(){return name;}public void setName(String v){name=v;}public String getDescription(){return description;}public void setDescription(String v){description=v;}public Long getSourceProjectId(){return sourceProjectId;}public void setSourceProjectId(Long v){sourceProjectId=v;}}
