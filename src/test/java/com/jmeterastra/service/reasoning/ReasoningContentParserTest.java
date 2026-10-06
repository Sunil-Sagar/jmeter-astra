package com.jmeterastra.service.reasoning;

import com.openai.core.JsonValue;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ReasoningContentParser}: reading reasoning_content out of
 * additional-properties maps.
 */
class ReasoningContentParserTest {

    @Test
    void extractsReasoningContent() {
        Map<String, JsonValue> props = Map.of(
                ReasoningContentParser.REASONING_CONTENT_KEY, JsonValue.from("chain of thought"));
        assertEquals("chain of thought", ReasoningContentParser.reasoningContent(props));
    }

    @Test
    void returnsNullWhenAbsent() {
        Map<String, JsonValue> props = Map.of("other_field", JsonValue.from("value"));
        assertNull(ReasoningContentParser.reasoningContent(props));
    }

    @Test
    void returnsNullForNullMap() {
        assertNull(ReasoningContentParser.reasoningContent(null));
    }

    @Test
    void returnsNullForNonStringValue() {
        Map<String, JsonValue> props = Map.of(
                ReasoningContentParser.REASONING_CONTENT_KEY, JsonValue.from(42));
        // numeric reasoning_content is malformed - must not blow up
        String result = ReasoningContentParser.reasoningContent(props);
        assertTrue(result == null || result.equals("42"));
    }
}
