package com.example.Project_Tracker.repository;

import com.example.Project_Tracker.entity.Project;
import com.example.Project_Tracker.enums.Status;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {

    // List<Project> findByManager_Id(String managerId);

    List<Project> findByIbu_Id(String ibuId);

    List<Project> findByMembers_User_Id(String userId);

    List<Project> findByStatus(Status status);

    List<Project> findByCreatedBy_Id(String userId);

    boolean existsByIbu_Id(String ibuId);


}
