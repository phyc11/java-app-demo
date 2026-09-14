package com.example.task.service;

import com.example.common.exception.ResourceNotFoundException;
import com.example.task.dto.*;
import com.example.task.model.*;
import com.example.task.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final TaskRepository taskRepository; private final CategoryRepository categoryRepository;
    private final TaskDependencyRepository dependencyRepository; private final TaskStatusHistoryRepository historyRepository;
    public TaskService(TaskRepository tr,CategoryRepository cr,TaskDependencyRepository dr,TaskStatusHistoryRepository hr){taskRepository=tr;categoryRepository=cr;dependencyRepository=dr;historyRepository=hr;}

    public Page<TaskDTO> getTasks(Long workspaceId,Long projectId,String assignee,Long parentTaskId,Status status,Priority priority,Long categoryId,String search,int page,int size,String sort){
        if(page<0||size<1||size>100) throw new IllegalArgumentException("page must be >= 0 and size must be 1-100");
        Sort order="dueDate".equals(sort)?Sort.by("dueDate").ascending():Sort.by("position").ascending().and(Sort.by("createdAt").descending());
        return taskRepository.findFilteredTasks(workspaceId,projectId,blankToNull(assignee),parentTaskId,status,priority,categoryId,blankToNull(search),PageRequest.of(page,size,order)).map(this::toDto);
    }
    public TaskDTO getTaskById(Long id){return toDto(find(id));}

    @Transactional public TaskDTO createTask(TaskDTO dto,String username){
        if(dto.getWorkspaceId()==null) throw new IllegalArgumentException("workspaceId is required");
        validateParent(dto.getParentTaskId(),dto.getWorkspaceId(),dto.getProjectId(),null);
        Task t=new Task(); copy(dto,t); t.setCreatedBy(username==null?"System":username); Task saved=taskRepository.save(t);
        addDependencies(saved,dto.getDependencyIds()); return toDto(saved);
    }

    @Transactional public TaskDTO updateTask(Long id,TaskDTO dto){
        Task t=find(id); if(dto.getVersion()==null||!dto.getVersion().equals(t.getVersion())) throw new org.springframework.orm.ObjectOptimisticLockingFailureException(Task.class,id);
        validateParent(dto.getParentTaskId(),dto.getWorkspaceId()==null?t.getWorkspaceId():dto.getWorkspaceId(),dto.getProjectId(),id);
        Status old=t.getStatus(); copy(dto,t); Task saved=taskRepository.saveAndFlush(t);
        if(old!=saved.getStatus()) historyRepository.save(new TaskStatusHistory(id,old,saved.getStatus(),dto.getCreatedBy()));
        return toDto(saved);
    }

    @Transactional public TaskDTO updateTaskStatus(Long id,Status status,String actor){Task t=find(id);Status old=t.getStatus();t.setStatus(status);Task saved=taskRepository.save(t);if(old!=status)historyRepository.save(new TaskStatusHistory(id,old,status,actor));return toDto(saved);}
    @Transactional public TaskDTO moveTask(Long id,Status status,Integer position,String actor){Task t=find(id);Status old=t.getStatus();t.setStatus(status);if(position!=null)t.setPosition(position);Task saved=taskRepository.save(t);if(old!=status)historyRepository.save(new TaskStatusHistory(id,old,status,actor));return toDto(saved);}
    @Transactional public TaskDTO addWatcher(Long id,String username){Task t=find(id);t.getWatchers().add(required(username,"username"));return toDto(taskRepository.save(t));}
    @Transactional public TaskDTO removeWatcher(Long id,String username){Task t=find(id);t.getWatchers().remove(username);return toDto(taskRepository.save(t));}
    @Transactional public TaskDTO addDependency(Long id,Long dependsOnId){Task task=find(id);Task depends=find(dependsOnId);if(id.equals(dependsOnId))throw new IllegalArgumentException("Task cannot depend on itself");if(!task.getWorkspaceId().equals(depends.getWorkspaceId()))throw new IllegalArgumentException("Dependencies must belong to the same workspace");if(hasPath(dependsOnId,id,new HashSet<>()))throw new IllegalArgumentException("Dependency would create a cycle");if(!dependencyRepository.existsByTaskIdAndDependsOnTaskId(id,dependsOnId))dependencyRepository.save(new TaskDependency(id,dependsOnId));return toDto(task);}
    @Transactional public void removeDependency(Long id,Long dependsOnId){find(id);dependencyRepository.deleteByTaskIdAndDependsOnTaskId(id,dependsOnId);}
    public List<TaskStatusHistory> getHistory(Long id){find(id);return historyRepository.findByTaskIdOrderByChangedAtDesc(id);}
    @Transactional public void deleteTask(Long id){Task t=find(id);if(!taskRepository.findByParentTaskId(id).isEmpty())throw new IllegalArgumentException("Delete or move subtasks before deleting their parent");taskRepository.delete(t);}
    public TaskStatsDTO getStats(Long workspaceId){long total=workspaceId==null?taskRepository.count():taskRepository.countByWorkspaceId(workspaceId);return new TaskStatsDTO(total,count(workspaceId,Status.TODO),count(workspaceId,Status.IN_PROGRESS),count(workspaceId,Status.COMPLETED));}

    private long count(Long w,Status s){return w==null?taskRepository.countByStatus(s):taskRepository.countByWorkspaceIdAndStatus(w,s);}
    private Task find(Long id){return taskRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Task","id",id));}
    private void copy(TaskDTO d,Task t){t.setTitle(required(d.getTitle(),"title"));t.setDescription(d.getDescription());t.setWorkspaceId(d.getWorkspaceId()==null?t.getWorkspaceId():d.getWorkspaceId());t.setProjectId(d.getProjectId());t.setParentTaskId(d.getParentTaskId());t.setAssignee(blankToNull(d.getAssignee()));t.setWatchers(d.getWatchers());t.setStatus(d.getStatus()==null?Status.TODO:d.getStatus());t.setPriority(d.getPriority()==null?Priority.MEDIUM:d.getPriority());t.setDueDate(d.getDueDate());if(d.getPosition()!=null)t.setPosition(d.getPosition());t.setCategory(d.getCategoryId()==null?null:categoryRepository.findById(d.getCategoryId()).orElseThrow(()->new ResourceNotFoundException("Category","id",d.getCategoryId())));}
    private TaskDTO toDto(Task t){TaskDTO d=new TaskDTO(t);d.setDependencyIds(dependencyRepository.findByTaskId(t.getId()).stream().map(TaskDependency::getDependsOnTaskId).collect(Collectors.toCollection(LinkedHashSet::new)));return d;}
    private void validateParent(Long parentId,Long workspaceId,Long projectId,Long self){if(parentId==null)return;if(parentId.equals(self))throw new IllegalArgumentException("Task cannot be its own parent");Task p=find(parentId);if(!Objects.equals(workspaceId,p.getWorkspaceId())||!Objects.equals(projectId,p.getProjectId()))throw new IllegalArgumentException("Subtask must use the parent workspace and project");}
    private void addDependencies(Task task,Set<Long> ids){if(ids!=null)for(Long id:ids)addDependency(task.getId(),id);}
    private boolean hasPath(Long from,Long target,Set<Long> seen){if(from.equals(target))return true;if(!seen.add(from))return false;for(TaskDependency d:dependencyRepository.findByTaskId(from))if(hasPath(d.getDependsOnTaskId(),target,seen))return true;return false;}
    private String blankToNull(String s){return s==null||s.trim().isEmpty()?null:s.trim();} private String required(String s,String f){String v=blankToNull(s);if(v==null)throw new IllegalArgumentException(f+" is required");return v;}
}
