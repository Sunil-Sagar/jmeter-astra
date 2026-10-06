package com.jmeterastra.record;

/**
 * Marker for transaction start/end boundaries in the step-markers.json file.
 */
public record StepMarker(
    String name,
    String type,
    long timestamp
) {}
