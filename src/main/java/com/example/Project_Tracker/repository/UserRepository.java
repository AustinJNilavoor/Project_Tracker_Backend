package com.example.Project_Tracker.repository;

import com.example.Project_Tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    List<User> findByRole(String role);

    List<User> findByIbu_Id(String ibuId);

    List<User> findByRoleAndIbu_Id(String role, String ibuId);

    boolean existsByEmail(String email);

    boolean existsByIbu_Id(String ibuId);
}
