package com.example.Project_Tracker.exceptions;

/**
 * Thrown when a milestone cannot be marked as completed
 * because not all tasks under it are completed.
 */
public class MilestoneCompletionException extends RuntimeException {

    public MilestoneCompletionException(String message) {
        super(message);
    }
}
