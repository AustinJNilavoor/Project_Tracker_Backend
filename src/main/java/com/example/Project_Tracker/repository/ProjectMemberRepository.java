package com.example.Project_Tracker.repository;

import com.example.Project_Tracker.entity.ProjectMember;
import com.example.Project_Tracker.entity.ProjectMemberId;
import com.example.Project_Tracker.enums.ProjectRole;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMemberId> {

    List<ProjectMember> findByProject_Id(String projectId);

    List<ProjectMember> findByUser_Id(String userId);

    Optional<ProjectMember> findByProject_IdAndUser_Id(String projectId, String userId);

    boolean existsById_ProjectIdAndUser_IdAndRoleInProject(String projectId, String userId, ProjectRole roleInProject);
    boolean existsById_ProjectIdAndUser_Id(String projectId, String userId);

}
