package com.jmeterastra.agent.tool;

import com.jmeterastra.agent.TriageNotice;
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

/**
 * Assembles the default set of agent tools (read tools plus the currently
 * implemented write tools) into a {@link ToolRegistry}. Registration order is
 * preserved, so the generated tool schema is deterministic.
 */
public final class AgentToolRegistry {

    private AgentToolRegistry() {
    }

    /** Builds a registry wired to the live JMeter tree. */
    public static ToolRegistry createDefault() {
        return createDefault(null);
    }

    /**
     * Builds a registry wired to the live JMeter tree, with an optional sink for Jev
     * failure-triage notices emitted by {@code get_test_results}.
     */
    public static ToolRegistry createDefault(java.util.function.Consumer<TriageNotice> triageNotice) {
        ToolRegistry registry = new ToolRegistry();
        for (Tool tool : new ReadToolHandlers().tools()) {
            registry.register(tool);
        }
        registry.register(new AddElementHandler().tool());
        registry.register(new UpdateElementPropertyHandler().tool());
        registry.register(new SetPropertyListHandler().tool());
        registry.register(new SetStructuredPropertyListHandler().tool());
        registry.register(new DeleteElementHandler().tool());
        registry.register(new ToggleElementHandler().tool());
        registry.register(new MoveElementHandler().tool());
        registry.register(new DuplicateElementHandler().tool());
        registry.register(new RenameElementHandler().tool());
        registry.register(new ReorderElementHandler().tool());
        registry.register(new RunTestHandler().tool());
        registry.register(new StopTestHandler().tool());
        registry.register(new GetTestResultsHandler(triageNotice).tool());
        registry.register(new SavePlanHandler().tool());
        registry.register(new OpenPlanHandler().tool());
        registry.register(new FindCorrelationCandidatesHandler().tool());
        registry.register(new ApplyCorrelationHandler().tool());
        return registry;
    }
}
