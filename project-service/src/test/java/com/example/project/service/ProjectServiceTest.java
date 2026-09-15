package com.example.project.service;
import com.example.project.dto.*; import com.example.project.model.*; import com.example.project.repository.*; import org.junit.jupiter.api.*; import org.springframework.orm.ObjectOptimisticLockingFailureException;
import java.util.Optional; import static org.junit.jupiter.api.Assertions.*; import static org.mockito.ArgumentMatchers.*; import static org.mockito.Mockito.*;
class ProjectServiceTest {
 private ProjectRepository projects;private ProjectMemberRepository members;private ProjectService service;
 @BeforeEach void setup(){projects=mock(ProjectRepository.class);members=mock(ProjectMemberRepository.class);service=new ProjectService(projects,members,mock(SprintRepository.class),mock(MilestoneRepository.class),mock(ProjectTagRepository.class),mock(ProjectTemplateRepository.class),"http://localhost:8082");}
 @Test void createUsesTrustedWorkspaceAndUser(){when(projects.save(any())).thenAnswer(i->{Project p=i.getArgument(0);p.setId(7L);return p;});ProjectRequestDto r=new ProjectRequestDto();r.setName("Demo");r.setProjectKey("demo");Project p=service.createProject(r,2L,"alice");assertEquals(2L,p.getWorkspaceId());assertEquals("alice",p.getOwnerUsername());verify(members).save(argThat(m->m.getProjectId().equals(7L)&&m.getRole().equals("OWNER")));}
 @Test void memberCannotWriteWithoutLeadRole(){Project p=project();when(projects.findById(7L)).thenReturn(Optional.of(p));when(members.findByProjectIdAndUsername(7L,"bob")).thenReturn(Optional.of(new ProjectMember(7L,"bob","MEMBER")));assertThrows(SecurityException.class,()->service.archive(7L,0L,2L,"bob","MEMBER"));}
 @Test void staleUpdateIsRejected(){Project p=project();p.setVersion(3L);when(projects.findById(7L)).thenReturn(Optional.of(p));ProjectRequestDto r=new ProjectRequestDto();r.setVersion(2L);assertThrows(ObjectOptimisticLockingFailureException.class,()->service.update(7L,r,2L,"admin","ADMIN"));}
 private Project project(){Project p=new Project(2L,"Demo","DEMO",null,"alice");p.setId(7L);return p;}
}
