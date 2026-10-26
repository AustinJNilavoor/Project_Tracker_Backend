package com.example.Project_Tracker.service;

import com.example.Project_Tracker.dto.ProjectMemberDto;
import com.example.Project_Tracker.entity.Ibu;
import com.example.Project_Tracker.entity.Project;
import com.example.Project_Tracker.entity.ProjectMember;
import com.example.Project_Tracker.entity.ProjectMemberId;
import com.example.Project_Tracker.entity.User;
import com.example.Project_Tracker.enums.ProjectRole;
import com.example.Project_Tracker.exceptions.MemberAlreadyExistsException;
import com.example.Project_Tracker.exceptions.ResourceNotFoundException;
import com.example.Project_Tracker.repository.ProjectMemberRepository;
import com.example.Project_Tracker.repository.ProjectRepository;
import com.example.Project_Tracker.repository.UserRepository;
import com.example.Project_Tracker.repository.TaskRepository;
import com.example.Project_Tracker.utils.MapperUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final MapperUtils mapperUtils;

    // ✅ Get all members of a project
    public List<ProjectMemberDto> getMembersByProject(String projectId) {
        return projectMemberRepository.findByProject_Id(projectId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // ✅ Add a new project member (Manager or Project Manager only)
    public ProjectMemberDto addMember(ProjectMemberDto dto) {
        User currentUser = getCurrentUser();

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        // 🔒 Allow only Manager or Project Manager
        if (!hasManagerPrivileges(currentUser, project.getId())) {
            throw new AccessDeniedException("You are not authorized to add members to this project");
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Ibu projectIbu = project.getIbu();
        Ibu userToAddIbu = user.getIbu(); // Using 'userToAdd' instead of 'user'
        Ibu currentUserIbu = currentUser.getIbu();

        if (!projectIbu.equals(userToAddIbu)) {
            throw new AccessDeniedException("The user (" + user.getName() +
                    ") cannot be added as they belong to a different IBU (" + userToAddIbu.getName() +
                    ") than the Project's IBU (" + projectIbu.getName() + ")");
        }
        if (!projectIbu.getId().equals(currentUserIbu.getId())) {
            throw new AccessDeniedException("You are not authorized to add members to a project outside your IBU");
        }
        boolean alreadyMember = projectMemberRepository.existsById_ProjectIdAndUser_Id(
                project.getId(),
                user.getId());

        if (alreadyMember) {
            throw new MemberAlreadyExistsException(
                    "The user (" + user.getName() + ") is already a member of this project.");
        }
        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setUser(user);

        member.setRoleInProject(dto.getRole());
        member.setJoinedAt(LocalDateTime.now());

        ProjectMember saved = projectMemberRepository.save(member);
        return convertToDto(saved);
    }

    // ✅ Remove a project member (Manager or Project Manager only)
    public void removeMember(String projectId, String userId) {
        User currentUser = getCurrentUser();

        // 🔒 Allow only Manager or Project Manager
        if (!hasManagerPrivileges(currentUser, projectId)) {
            throw new AccessDeniedException("You are not authorized to remove members from this project");
        }
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        if (project.getIbu() == null || currentUser.getIbu() == null ||
                !project.getIbu().getId().equals(currentUser.getIbu().getId())) {
            throw new AccessDeniedException("You are not authorized to remove members from a project outside your IBU");
        }
            boolean hasAssignedTasks = taskRepository.existsByProject_IdAndAssignedTo_Id(projectId, userId);

    if (hasAssignedTasks) {
        throw new IllegalStateException("User cannot be removed because they are assigned to active tasks.");
    }
        ProjectMemberId id = new ProjectMemberId(projectId, userId);
        if (!projectMemberRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project member not found");
        }
        projectMemberRepository.deleteById(id);
    }

    // ✅ Convert entity → DTO
    private ProjectMemberDto convertToDto(ProjectMember member) {
        ProjectMemberDto dto = mapperUtils.map(member, ProjectMemberDto.class);

        if (member.getProject() != null) {
            dto.setProjectId(member.getProject().getId());
            dto.setProjectName(member.getProject().getName());
        }

        if (member.getUser() != null) {
            dto.setUserId(member.getUser().getId());
            dto.setUserName(member.getUser().getName());
        }
        if (member.getRoleInProject() != null) {
            dto.setRole(member.getRoleInProject());
        }

        if (member.getJoinedAt() != null) {
            dto.setJoinedAt(member.getJoinedAt());
        }

        return dto;
    }

    // ✅ Utility — get current logged-in user
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    // ✅ Utility — check if user is Manager or Project Manager for given project
    private boolean hasManagerPrivileges(User user, String projectId) {
        // Allow if global MANAGER role
        if ("MANAGER".equalsIgnoreCase(user.getRole())) {
            return true;
        }
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return false; // project not found
        }

        Project project = projectOpt.get();

        // Check if user belongs to same IBU as project
        if (user.getIbu() == null || project.getIbu() == null ||
                !user.getIbu().getId().equals(project.getIbu().getId())) {
            return false;
        }
        // Otherwise, check if Project Member with Project Manager role
        return projectMemberRepository
                .existsById_ProjectIdAndUser_IdAndRoleInProject(projectId, user.getId(),
                        ProjectRole.valueOf("PROJECT_MANAGER"));
    }
}
