package com.jmeterastra.agent.tool;

import org.junit.jupiter.api.Test;
import com.jmeterastra.agent.tool.handlers.AddElementHandler;
import com.jmeterastra.agent.tool.handlers.ApplyCorrelationHandler;
import com.jmeterastra.agent.tool.handlers.DeleteElementHandler;
import com.jmeterastra.agent.tool.handlers.DuplicateElementHandler;
import com.jmeterastra.agent.tool.handlers.FindCorrelationCandidatesHandler;
import com.jmeterastra.agent.tool.handlers.GetTestResultsHandler;
import com.jmeterastra.agent.tool.handlers.MoveElementHandler;
import com.jmeterastra.agent.tool.handlers.OpenPlanHandler;
import com.jmeterastra.agent.tool.handlers.ReadToolHandlers;
import com.jmeterastra.agent.tool.handlers.RenameElementHandler;
import com.jmeterastra.agent.tool.handlers.ReorderElementHandler;
import com.jmeterastra.agent.tool.handlers.RunTestHandler;
import com.jmeterastra.agent.tool.handlers.SavePlanHandler;
import com.jmeterastra.agent.tool.handlers.SetPropertyListHandler;
import com.jmeterastra.agent.tool.handlers.SetStructuredPropertyListHandler;
import com.jmeterastra.agent.tool.handlers.StopTestHandler;
import com.jmeterastra.agent.tool.handlers.ToggleElementHandler;
import com.jmeterastra.agent.tool.handlers.UpdateElementPropertyHandler;

import static org.junit.jupiter.api.Assertions.*;

/** Unit tests for {@link AgentToolRegistry}. */
class AgentToolRegistryTest {

    @Test
    void createDefault_registersReadAndWriteTools() {
        ToolRegistry registry = AgentToolRegistry.createDefault();

        assertTrue(registry.isRegistered(ReadToolHandlers.GET_TREE_STATE));
        assertTrue(registry.isRegistered(ReadToolHandlers.GET_ELEMENT_CONFIG));
        assertTrue(registry.isRegistered(ReadToolHandlers.GET_ELEMENT_SCHEMA));
        assertTrue(registry.isRegistered(AddElementHandler.ADD_ELEMENT));
        assertTrue(registry.isRegistered(UpdateElementPropertyHandler.UPDATE_ELEMENT_PROPERTY));
        assertTrue(registry.isRegistered(SetPropertyListHandler.SET_PROPERTY_LIST));
        assertTrue(registry.isRegistered(SetStructuredPropertyListHandler.SET_STRUCTURED_PROPERTY_LIST));
        assertTrue(registry.isRegistered(DeleteElementHandler.DELETE_ELEMENT));
        assertTrue(registry.isRegistered(ToggleElementHandler.TOGGLE_ELEMENT));
        assertTrue(registry.isRegistered(MoveElementHandler.MOVE_ELEMENT));
        assertTrue(registry.isRegistered(DuplicateElementHandler.DUPLICATE_ELEMENT));
        assertTrue(registry.isRegistered(RenameElementHandler.RENAME_ELEMENT));
        assertTrue(registry.isRegistered(ReorderElementHandler.REORDER_ELEMENT));
        assertTrue(registry.isRegistered(RunTestHandler.RUN_TEST));
        assertTrue(registry.isRegistered(StopTestHandler.STOP_TEST));
        assertTrue(registry.isRegistered(GetTestResultsHandler.GET_TEST_RESULTS));
        assertTrue(registry.isRegistered(SavePlanHandler.SAVE_PLAN));
        assertTrue(registry.isRegistered(OpenPlanHandler.OPEN_PLAN));
        assertTrue(registry.isRegistered(FindCorrelationCandidatesHandler.FIND_CORRELATION_CANDIDATES));
        assertTrue(registry.isRegistered(ApplyCorrelationHandler.APPLY_CORRELATION));
        assertTrue(registry.size() >= 21);
    }
}
