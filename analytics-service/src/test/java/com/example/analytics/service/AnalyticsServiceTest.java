package com.example.analytics.service;
import com.example.analytics.dto.TaskRecord; import com.example.analytics.model.AnalyticsSnapshot; import com.example.analytics.repository.AnalyticsSnapshotRepository;
import org.junit.jupiter.api.Test; import java.time.LocalDateTime; import java.util.Arrays;
import org.mockito.ArgumentCaptor; import static org.mockito.ArgumentMatchers.any; import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
class AnalyticsServiceTest {
 @Test void snapshotCalculatesOverdueAndWorkload(){AnalyticsSnapshotRepository repo=mock(AnalyticsSnapshotRepository.class);when(repo.save(any())).thenAnswer(i->i.getArgument(0));AnalyticsService service=new AnalyticsService(repo,"http://localhost:8082",200);
  TaskRecord overdue=new TaskRecord();overdue.workspaceId=1L;overdue.projectId=2L;overdue.status="IN_PROGRESS";overdue.assignee="alice";overdue.dueDate=LocalDateTime.now().minusDays(1);
  TaskRecord todo=new TaskRecord();todo.workspaceId=1L;todo.projectId=2L;todo.status="TODO";todo.assignee="alice";
  service.save(1L,2L,Arrays.asList(overdue,todo));ArgumentCaptor<AnalyticsSnapshot> captor=ArgumentCaptor.forClass(AnalyticsSnapshot.class);verify(repo).save(captor.capture());AnalyticsSnapshot saved=captor.getValue();
  assertEquals(2,saved.getTotalTasks());assertEquals(1,saved.getOverdueTasks());assertTrue(saved.getWorkloadJson().contains("\"alice\":2"));
 }
 @Test void dashboardRequiresWorkspace(){AnalyticsService service=new AnalyticsService(mock(AnalyticsSnapshotRepository.class),"http://localhost:8082",200);assertThrows(IllegalArgumentException.class,()->service.getAnalytics(null,null));}
}
