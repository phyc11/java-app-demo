package com.example.task.service;

import com.example.task.dto.TaskDTO;
import com.example.task.model.*;
import com.example.task.repository.*;
import org.junit.jupiter.api.BeforeEach; import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page; import org.springframework.orm.ObjectOptimisticLockingFailureException;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TaskServiceIntegrationTest {
 @Autowired TaskService service; @Autowired TaskRepository tasks; @Autowired TaskDependencyRepository deps; @Autowired TaskStatusHistoryRepository history;
 @BeforeEach void clean(){deps.deleteAll();history.deleteAll();tasks.deleteAll();}

 @Test void createsTaskWithScopeAssigneeAndWatchers(){TaskDTO d=request("One",1L,10L);d.setAssignee("bob");d.setWatchers(Set.of("alice","carol"));TaskDTO saved=service.createTask(d,"alice");assertEquals(1L,saved.getWorkspaceId());assertEquals(10L,saved.getProjectId());assertEquals("bob",saved.getAssignee());assertEquals(2,saved.getWatchers().size());assertNotNull(saved.getVersion());}

 @Test void paginatesAndFiltersByWorkspace(){for(int i=0;i<5;i++)service.createTask(request("Task "+i,1L,10L),"alice");service.createTask(request("Other",2L,20L),"bob");Page<TaskDTO> page=service.getTasks(1L,null,null,null,null,null,null,null,1,2,"position");assertEquals(2,page.getContent().size());assertEquals(5,page.getTotalElements());assertEquals(3,page.getTotalPages());}

 @Test void validatesSubtaskScope(){TaskDTO parent=service.createTask(request("Parent",1L,10L),"alice");TaskDTO child=request("Child",2L,10L);child.setParentTaskId(parent.getId());assertThrows(IllegalArgumentException.class,()->service.createTask(child,"alice"));child.setWorkspaceId(1L);assertEquals(parent.getId(),service.createTask(child,"alice").getParentTaskId());}

 @Test void preventsDependencyCycles(){TaskDTO a=service.createTask(request("A",1L,10L),"alice");TaskDTO b=service.createTask(request("B",1L,10L),"alice");service.addDependency(a.getId(),b.getId());assertThrows(IllegalArgumentException.class,()->service.addDependency(b.getId(),a.getId()));}

 @Test void rejectsStaleVersion(){TaskDTO saved=service.createTask(request("A",1L,10L),"alice");TaskDTO stale=request("Changed",1L,10L);stale.setVersion(saved.getVersion()+1);assertThrows(ObjectOptimisticLockingFailureException.class,()->service.updateTask(saved.getId(),stale));}

 @Test void recordsStatusHistory(){TaskDTO saved=service.createTask(request("A",1L,10L),"alice");service.updateTaskStatus(saved.getId(),Status.IN_PROGRESS,"bob");TaskStatusHistory item=service.getHistory(saved.getId()).get(0);assertEquals(Status.TODO,item.getFromStatus());assertEquals(Status.IN_PROGRESS,item.getToStatus());assertEquals("bob",item.getChangedBy());}

 @Test void cannotDeleteParentWithSubtasks(){TaskDTO parent=service.createTask(request("Parent",1L,10L),"alice");TaskDTO child=request("Child",1L,10L);child.setParentTaskId(parent.getId());service.createTask(child,"alice");assertThrows(IllegalArgumentException.class,()->service.deleteTask(parent.getId()));}

 private TaskDTO request(String title,Long workspace,Long project){TaskDTO d=new TaskDTO();d.setTitle(title);d.setWorkspaceId(workspace);d.setProjectId(project);return d;}
}
