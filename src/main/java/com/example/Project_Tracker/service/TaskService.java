package com.example.Project_Tracker.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.Project_Tracker.dto.TaskDto;
import com.example.Project_Tracker.entity.Milestone;
import com.example.Project_Tracker.entity.Project;
import com.example.Project_Tracker.entity.Task;
import com.example.Project_Tracker.entity.User;
import com.example.Project_Tracker.enums.ProjectRole;
import com.example.Project_Tracker.enums.TaskStatus;
import com.example.Project_Tracker.exceptions.ResourceNotFoundException;
import com.example.Project_Tracker.repository.MilestoneRepository;
import com.example.Project_Tracker.repository.ProjectMemberRepository;
import com.example.Project_Tracker.repository.ProjectRepository;
import com.example.Project_Tracker.repository.TaskRepository;
import com.example.Project_Tracker.repository.UserRepository;
import com.example.Project_Tracker.utils.MapperUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final MilestoneRepository milestoneRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final MapperUtils mapperUtils;

    // 🔹 Get tasks by project (no restriction)
    public List<TaskDto> getTasksByProject(String projectId) {
        return taskRepository.findByProject_Id(projectId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 🔹 Get tasks by user (for dashboards)
    public List<TaskDto> getTasksByUser(String userId) {
        return taskRepository.findByAssignedTo_Id(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<TaskDto> getTasksByMilestone(String milestoneId) {
        return taskRepository.findByMilestone_Id(milestoneId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 🔹 Create task — only MANAGER or PROJECT_MANAGER
    public TaskDto createTask(TaskDto taskDto) {
        authorizeProjectManagerOrManager(taskDto.getProjectId());
        if (taskDto.getAssignedToId() != null) {
            boolean isProjectMember = projectMemberRepository
                    .existsById_ProjectIdAndUser_Id(taskDto.getProjectId(), taskDto.getAssignedToId());

            if (!isProjectMember) {
                throw new AccessDeniedException("Operation failed: User is not a member of the required project.");
            }
        }

        Task task = convertToEntity(taskDto);
        // task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        // task.getCreatedBy().setId(
        // userRepository.findByEmail(
        // SecurityContextHolder.getContext().getAuthentication().getName()
        // ).orElseThrow(() -> new ResourceNotFoundException("User not found")).getId()
        // );
        Task savedTask = taskRepository.save(task);
        return convertToDto(savedTask);
    }

    // 🔹 Update task — only MANAGER or PROJECT_MANAGER
    public TaskDto updateTask(String id, TaskDto updatedTaskDto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        authorizeProjectManagerOrManager(task.getProject().getId());

// Only update the title if the DTO provided a non-null value
        if (updatedTaskDto.getTitle() != null) {
            task.setTitle(updatedTaskDto.getTitle());
        }

        // Only update the description if the DTO provided a non-null value
        if (updatedTaskDto.getDescription() != null) {
            task.setDescription(updatedTaskDto.getDescription());
        }

        // Only update the status if the DTO provided a non-null value
        if (updatedTaskDto.getStatus() != null) {
            task.setStatus(updatedTaskDto.getStatus());
        }

        // Only update the priority if the DTO provided a non-null value
        if (updatedTaskDto.getPriority() != null) {
            task.setPriority(updatedTaskDto.getPriority());
        }

        // Only update the due date if the DTO provided a non-null value
        if (updatedTaskDto.getDueDate() != null) {
            task.setDueDate(updatedTaskDto.getDueDate());
        }

        // Optional reassignment
        if (updatedTaskDto.getAssignedToId() != null) {
            User user = userRepository.findById(updatedTaskDto.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            task.setAssignedTo(user);
        }

        task.setUpdatedAt(LocalDateTime.now());
        Task updated = taskRepository.save(task);
        return convertToDto(updated);
    }

    // 🔹 Update task status — only assigned user OR MANAGER or PROJECT_MANAGER
    public TaskDto updateTaskStatus(String id, TaskStatus status) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        User currentUser = userRepository.findByEmail(
                SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isManager = currentUser.getRole().equalsIgnoreCase("MANAGER");
        boolean isProjectManager = projectMemberRepository
                .existsById_ProjectIdAndUser_IdAndRoleInProject(task.getProject().getId(), currentUser.getId(),
                        ProjectRole.valueOf("PROJECT_MANAGER"));

        boolean isAssignedUser = task.getAssignedTo() != null &&
                task.getAssignedTo().getId().equals(currentUser.getId());

        if (!(isManager || isProjectManager || isAssignedUser)) {
            throw new AccessDeniedException("You are not authorized to update this task's status");
        }

        task.setStatus(status);
        task.setUpdatedAt(LocalDateTime.now());
        Task updated = taskRepository.save(task);
        return convertToDto(updated);
    }

    // 🔹 Delete task — only MANAGER or PROJECT_MANAGER
    public boolean deleteTask(String id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        authorizeProjectManagerOrManager(task.getProject().getId());

        taskRepository.deleteById(id);
        return true;
    }

    // 🔒 Common authorization logic for MANAGER or PROJECT_MANAGER
    private void authorizeProjectManagerOrManager(String projectId) {
        User currentUser = userRepository.findByEmail(
                SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isProjectManager = projectMemberRepository
                .existsById_ProjectIdAndUser_IdAndRoleInProject(projectId, currentUser.getId(),
                        ProjectRole.valueOf("PROJECT_MANAGER"));
        boolean isSameIBU = false;
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        if (currentUser.getIbu() != null && project.getIbu() != null) {
            isSameIBU = currentUser.getIbu().getId().equals(project.getIbu().getId());
        }
        if ((!currentUser.getRole().equalsIgnoreCase("MANAGER") || !isSameIBU) && !isProjectManager) {
            throw new AccessDeniedException("You are not authorized to modify tasks in this project");
        }
    }

    // 🔹 Convert Entity → DTO
    private TaskDto convertToDto(Task task) {
        TaskDto dto = mapperUtils.map(task, TaskDto.class);

        if (task.getProject() != null) {
            dto.setProjectId(task.getProject().getId());
            dto.setProjectName(task.getProject().getName());
        }

        if (task.getAssignedTo() != null) {
            dto.setAssignedToId(task.getAssignedTo().getId());
            dto.setAssignedToName(task.getAssignedTo().getName());
        }
        if (task.getMilestone() != null) {
            dto.setMilestoneID(task.getMilestone().getId());
        }
        return dto;
    }

    // 🔹 Convert DTO → Entity
    private Task convertToEntity(TaskDto dto) {
        Task task = mapperUtils.map(dto, Task.class);

        if (dto.getProjectId() != null) {
            Project project = projectRepository.findById(dto.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
            task.setProject(project);
        }

        if (dto.getAssignedToId() != null) {
            User user = userRepository.findById(dto.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assigned user not found"));
            task.setAssignedTo(user);
        }

        if (dto.getMilestoneID() != null) {

            Milestone milestone = milestoneRepository.findById(dto.getMilestoneID())
                    .orElseThrow(() -> new ResourceNotFoundException("Milestone not found"));
            task.setMilestone(milestone);
            ;
        }
        return task;
    }
}
