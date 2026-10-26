package com.example.Project_Tracker.repository;

import com.example.Project_Tracker.entity.Task;
import com.example.Project_Tracker.enums.TaskStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {

    List<Task> findByProject_Id(String projectId);

    List<Task> findByMilestone_Id(String milestoneId);

    List<Task> findByAssignedTo_Id(String userId);

    List<Task> findByStatus(TaskStatus status);
    boolean existsByProject_IdAndAssignedTo_Id(String projectId, String userId);
// Checks if any task exists for a given milestone ID that is NOT in the COMPLETED status.
boolean existsByMilestone_IdAndStatusNot(String milestoneId, TaskStatus status);
}
