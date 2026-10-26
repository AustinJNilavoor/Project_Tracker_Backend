package com.example.Project_Tracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 25)
    private String role; // ADMIN, MANAGER, EMPLOYEE

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ibu_id")
    private Ibu ibu; // The department/IBU they belong to

    private final LocalDateTime createdAt = LocalDateTime.now();
    private  LocalDateTime updatedAt ;



    @OneToMany(mappedBy = "assignedTo")
    private List<Task> assignedTasks;
}
