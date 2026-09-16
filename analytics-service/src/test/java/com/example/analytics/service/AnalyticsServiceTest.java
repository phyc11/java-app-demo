package com.example.analytics.service;
import com.example.analytics.dto.AnalyticsDTO; import com.example.analytics.dto.TaskRecord; import com.example.analytics.model.AnalyticsSnapshot; import com.example.analytics.repository.AnalyticsSnapshotRepository;
import org.junit.jupiter.api.Test; import org.springframework.data.domain.PageImpl; import java.time.LocalDateTime; import java.util.Arrays; import java.util.Collections;
import org.mockito.ArgumentCaptor; import static org.mockito.ArgumentMatchers.any; import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
class AnalyticsServiceTest {
 @Test void snapshotCalculatesOverdueAndWorkload(){AnalyticsSnapshotRepository repo=mock(AnalyticsSnapshotRepository.class);when(repo.save(any())).thenAnswer(i->i.getArgument(0));AnalyticsService service=new AnalyticsService(repo,"http://localhost:8082",200,90);
  TaskRecord overdue=new TaskRecord();overdue.workspaceId=1L;overdue.projectId=2L;overdue.status="IN_PROGRESS";overdue.assignee="alice";overdue.dueDate=LocalDateTime.now().minusDays(1);
  TaskRecord todo=new TaskRecord();todo.workspaceId=1L;todo.projectId=2L;todo.status="TODO";todo.assignee="alice";
  service.save(1L,2L,Arrays.asList(overdue,todo));ArgumentCaptor<AnalyticsSnapshot> captor=ArgumentCaptor.forClass(AnalyticsSnapshot.class);verify(repo).save(captor.capture());AnalyticsSnapshot saved=captor.getValue();
  assertEquals(2,saved.getTotalTasks());assertEquals(1,saved.getOverdueTasks());assertTrue(saved.getWorkloadJson().contains("\"alice\":2"));
 }
 @Test void dashboardRequiresWorkspace(){AnalyticsService service=new AnalyticsService(mock(AnalyticsSnapshotRepository.class),"http://localhost:8082",200,90);assertThrows(IllegalArgumentException.class,()->service.getAnalytics(null,null));}
 @Test void historyIsPaginatedByWorkspace(){AnalyticsSnapshotRepository repo=mock(AnalyticsSnapshotRepository.class);AnalyticsSnapshot snapshot=new AnalyticsSnapshot();snapshot.setWorkspaceId(1L);when(repo.findByWorkspaceIdAndProjectIdIsNull(eq(1L),any())).thenReturn(new PageImpl<>(Collections.singletonList(snapshot)));AnalyticsService service=new AnalyticsService(repo,"http://localhost:8082",200,90);assertEquals(1,service.getHistory(1L,null,0,20).getTotalElements());}
 @Test void cleanupUsesConfiguredRetention(){AnalyticsSnapshotRepository repo=mock(AnalyticsSnapshotRepository.class);when(repo.deleteByCapturedAtBefore(any())).thenReturn(3L);AnalyticsService service=new AnalyticsService(repo,"http://localhost:8082",200,30);assertEquals(3L,service.cleanupSnapshots());verify(repo).deleteByCapturedAtBefore(any());}
 @Test void csvExportContainsRealAnalytics(){AnalyticsDTO dto=new AnalyticsDTO();dto.setWorkspaceId(7L);dto.setTotalTasks(12);dto.setCompletedTasks(8);String csv=new String(new ExportService().exportAnalyticsToCsv(dto));assertTrue(csv.contains("\"Workspace ID\",7"));assertTrue(csv.contains("\"Total tasks\",12"));}
}
