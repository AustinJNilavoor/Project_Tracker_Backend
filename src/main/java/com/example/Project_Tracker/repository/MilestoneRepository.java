package com.example.Project_Tracker.repository;

import com.example.Project_Tracker.entity.Milestone;
import com.example.Project_Tracker.enums.Status;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, String> {

    List<Milestone> findByProject_Id(String projectId);

    List<Milestone> findByStatus(Status status);
}
