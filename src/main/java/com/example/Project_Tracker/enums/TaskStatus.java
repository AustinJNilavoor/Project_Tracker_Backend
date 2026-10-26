package com.example.Project_Tracker.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents the specific status of a task.")
public enum TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    ON_HOLD
}