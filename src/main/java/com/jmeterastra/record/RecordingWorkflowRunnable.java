package com.jmeterastra.record;

import com.jmeterastra.gui.CommandCallback;

/**
 * Interface to trigger the recording workflow execution asynchronously.
 */
public interface RecordingWorkflowRunnable {
    void run(String prompt, CommandCallback cb);
}
