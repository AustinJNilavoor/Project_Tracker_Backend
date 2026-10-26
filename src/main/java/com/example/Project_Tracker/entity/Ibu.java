package com.example.Project_Tracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ibus")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ibu {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ibu_id")
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private final LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "ibu", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Project> projects;
}
