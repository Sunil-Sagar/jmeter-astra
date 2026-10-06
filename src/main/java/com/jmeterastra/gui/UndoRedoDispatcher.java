package com.jmeterastra.gui;

import com.jmeterastra.lint.LintCommandHandler;
import com.jmeterastra.service.AiService;
import com.jmeterastra.wrap.WrapUndoRedoHandler;

import javax.swing.SwingWorker;
import java.util.concurrent.ExecutionException;

/**
 * Handles undo and redo operations for lint (rename) and wrap commands,
 * running each operation in a background SwingWorker.
 */
public class UndoRedoDispatcher {

    private final CommandCallback cb;

    public UndoRedoDispatcher(CommandCallback callback) {
        this.cb = callback;
    }

    /**
     * Undoes the last rename operation performed by the ElementRenamer.
     */
    public void undoLastRename() {
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                String selectedModel = cb.getSelectedModel();
                if (selectedModel == null) {
                    return "Please select a model first.";
                }
                AiService serviceToUse = cb.resolveAiService(selectedModel);
                return new LintCommandHandler(serviceToUse).undoLastRename();
            }

            @Override
            protected void done() {
                try {
                    cb.appendMessageToChat(get());
                } catch (InterruptedException | ExecutionException e) {
                    cb.appendErrorMessageToChat("Error undoing rename operation", e);
                }
            }
        }.execute();
    }

    /**
     * Redoes the last undone rename operation performed by the ElementRenamer.
     */
    public void redoLastUndo() {
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                String selectedModel = cb.getSelectedModel();
                AiService serviceToUse = selectedModel != null
                        ? cb.resolveAiService(selectedModel)
                        : cb.resolveAiService("");
                return new LintCommandHandler(serviceToUse).redoLastUndo();
            }

            @Override
            protected void done() {
                try {
                    cb.appendMessageToChat(get());
                } catch (InterruptedException | ExecutionException e) {
                    cb.appendErrorMessageToChat("Error redoing rename operation", e);
                }
            }
        }.execute();
    }

    /**
     * Undoes the last wrap operation performed by the WrapCommandHandler.
     */
    public void undoLastWrap() {
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return WrapUndoRedoHandler.getInstance().undoLastWrap();
            }

            @Override
            protected void done() {
                try {
                    cb.appendMessageToChat(get());
                } catch (InterruptedException | ExecutionException e) {
                    cb.appendErrorMessageToChat("Error undoing wrap operation", e);
                }
            }
        }.execute();
    }
}
