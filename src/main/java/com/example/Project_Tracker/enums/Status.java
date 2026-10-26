package com.example.Project_Tracker.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents the general status of a project or milestone.")
public enum Status {
    PENDING,
    IN_PROGRESS,
    COMPLETED
}