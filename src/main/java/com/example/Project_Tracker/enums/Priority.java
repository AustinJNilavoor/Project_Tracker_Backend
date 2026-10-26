package com.example.Project_Tracker.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents the priority level of a task or project.")
public enum Priority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}