package com.example.Project_Tracker.service;

import com.example.Project_Tracker.dto.ProjectDto;
import com.example.Project_Tracker.dto.ProjectResponseDto;
import com.example.Project_Tracker.entity.Project;
import com.example.Project_Tracker.entity.ProjectMember;
import com.example.Project_Tracker.entity.User;
import com.example.Project_Tracker.enums.ProjectRole;
import com.example.Project_Tracker.exceptions.ResourceNotFoundException;
import com.example.Project_Tracker.repository.ProjectMemberRepository;
import com.example.Project_Tracker.repository.ProjectRepository;
import com.example.Project_Tracker.repository.UserRepository;
import com.example.Project_Tracker.utils.MapperUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.method.P;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final MapperUtils mapperUtils;
    private final ProjectMemberRepository projectMemberRepository;

    // ✅ Get all projects based on role
    public List<ProjectResponseDto> getAllProjects() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Project> projects;
        switch (currentUser.getRole().toUpperCase()) {
            case "ADMIN":
                projects = projectRepository.findAll();
                break;

            case "MANAGER":
                if (currentUser.getIbu() == null)
                    throw new RuntimeException("Manager is not associated with any IBU");
                projects = projectRepository.findByIbu_Id(currentUser.getIbu().getId());
                break;

            default:
                projects = projectRepository.findByMembers_User_Id(currentUser.getId());
                break;
        }

        return projects.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public ProjectDto getProjectById(String id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        return convertToDto(project);
    }

    // ✅ Create project
    public ProjectDto createProjectFromDto(ProjectDto dto) {
        Project project = convertToEntity(dto);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (currentUser.getIbu() == null)
            throw new RuntimeException("User is not associated with any IBU");

        project.setIbu(currentUser.getIbu());
        project.setCreatedBy(currentUser);
        // project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());

        Project saved = projectRepository.save(project);
        return convertToDto(saved);
    }

    // ✅ Update project
    public ProjectDto updateProjectFromDto(String id, ProjectDto dto) {
        Project existing = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        // Only update the name if the DTO provided a non-null value
        if (dto.getName() != null) {
            existing.setName(dto.getName());
        }

        // Only update the description if the DTO provided a non-null value
        if (dto.getDescription() != null) {
            existing.setDescription(dto.getDescription());
        }

        // Only update the status if the DTO provided a non-null value
        if (dto.getStatus() != null) {
            existing.setStatus(dto.getStatus());
        }

        // Only update the priority if the DTO provided a non-null value
        if (dto.getPriority() != null) {
            existing.setPriority(dto.getPriority());
        }

        // Only update the end date if the DTO provided a non-null value
        if (dto.getEndDate() != null) {
            existing.setEndDate(dto.getEndDate());
        }

        existing.setUpdatedAt(LocalDateTime.now());

        Project updated = projectRepository.save(existing);
        return convertToDto(updated);
    }

    // ✅ Delete project
    public void deleteProject(String id) {
        if (!projectRepository.existsById(id))
            throw new ResourceNotFoundException("Project not found with id: " + id);
        projectRepository.deleteById(id);
    }

    // ✅ Entity → DTO
    private ProjectDto convertToDto(Project project) {
        ProjectDto dto = mapperUtils.map(project, ProjectDto.class);

        return dto;
    }

    private ProjectResponseDto convertToResponseDto(Project project) {
        List<ProjectMember> members = projectMemberRepository.findByProject_Id(project.getId());

        // Filter Manager roles and convert to names
        List<String> managerNames = members.stream()
                .filter(m -> m.getRoleInProject() != null && m.getRoleInProject().equals(ProjectRole.PROJECT_MANAGER))
                .map(m -> m.getUser().getName()) // Or getFullName()
                .collect(Collectors.toList());

        ProjectResponseDto dto = mapperUtils.map(project, ProjectResponseDto.class);
        dto.setManagers(managerNames);

        return dto;
    }

    // ✅ DTO → Entity
    private Project convertToEntity(ProjectDto dto) {
        Project project = mapperUtils.map(dto, Project.class);

        return project;
    }
}
