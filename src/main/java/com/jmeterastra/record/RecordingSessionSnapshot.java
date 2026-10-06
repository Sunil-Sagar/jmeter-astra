package com.jmeterastra.record;

/**
 * Immutable snapshot of the recording session state.
 */
public record RecordingSessionSnapshot(
    RecordingSessionState state,
    long startTime,
    SessionConfig config,
    String artifactDirectory,
    String errorMessage
) {}
