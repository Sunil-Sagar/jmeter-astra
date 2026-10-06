package com.jmeterastra.gui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import com.jmeterastra.service.AiServiceHolder;
import com.jmeterastra.service.BedrockAiService;
import com.jmeterastra.service.ClaudeService;
import com.jmeterastra.service.GoogleAiService;
import com.jmeterastra.service.GrokAiService;
import com.jmeterastra.service.MetaMuseAiService;
import com.jmeterastra.service.OllamaAiService;
import com.jmeterastra.service.OpenAiService;
import com.jmeterastra.service.attach.AttachmentRegistry;
import com.jmeterastra.service.attach.FileContentPreparer;
import com.jmeterastra.utils.AiConfig;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the attachment-marker resolution in {@link AiResponseRouter}:
 * prepared content is substituted into the conversation before it is forwarded
 * to a provider service.
 */
@ExtendWith(MockitoExtension.class)
class AiResponseRouterAttachmentTest {

    @Mock private ClaudeService claudeService;
    @Mock private OpenAiService openAiService;
    @Mock private OllamaAiService ollamaService;
    @Mock private GoogleAiService googleService;
    @Mock private GrokAiService grokService;
    @Mock private MetaMuseAiService metaMuseService;
    @Mock private BedrockAiService bedrockService;

    private MockedStatic<AiConfig> aiConfigMockedStatic;
    private AiResponseRouter router;
    private AttachmentRegistry registry;

    @BeforeEach
    void setUp() {
        aiConfigMockedStatic = mockStatic(AiConfig.class);
        aiConfigMockedStatic.when(() -> AiConfig.getProperty(anyString(), anyString()))
                .thenAnswer(invocation -> invocation.getArgument(1));
        AiServiceHolder holder = new AiServiceHolder();
        holder.setClaudeService(claudeService);
        holder.setOpenAiService(openAiService);
        holder.setOllamaService(ollamaService);
        holder.setGoogleService(googleService);
        holder.setGrokService(grokService);
        holder.setMetaMuseService(metaMuseService);
        holder.setBedrockService(bedrockService);
        router = new AiResponseRouter(holder);
        registry = new AttachmentRegistry();
    }

    @AfterEach
    void tearDown() {
        aiConfigMockedStatic.close();
    }

    @Test
    void markersAreResolvedBeforeForwarding() {
        registry.register("notes.txt", "file body", FileContentPreparer.Mode.SMART);
        router.setAttachmentRegistry(registry);
        when(claudeService.generateResponse(anyList())).thenReturn("answer");

        router.getAiResponse("claude-sonnet-4-6", List.of("check this [file:f1]"));

        verify(claudeService).generateResponse(argThat(conversation ->
                conversation.size() == 1
                        && conversation.get(0).contains("<attached file=\"notes.txt\"")
                        && conversation.get(0).contains("file body")
                        && !conversation.get(0).contains("[file:")));
    }

    @Test
    void noRegistryLeavesConversationUntouched() {
        when(claudeService.generateResponse(anyList())).thenReturn("answer");

        router.getAiResponse("claude-sonnet-4-6", List.of("check this [file:f1]"));

        verify(claudeService).generateResponse(List.of("check this [file:f1]"));
    }

    @Test
    void streamPathResolvesMarkers() {
        registry.register("notes.txt", "file body", FileContentPreparer.Mode.SMART);
        router.setAttachmentRegistry(registry);
        when(claudeService.generateStreamResponse(anyList(), anyString(),
                any(), any(), any(), any())).thenReturn(() -> {});

        router.generateStreamResponse("claude-sonnet-4-6", List.of("check this [file:f1]"),
                token -> {}, () -> {}, e -> {});

        verify(claudeService).generateStreamResponse(argThat(conversation ->
                conversation.size() == 1 && !conversation.get(0).contains("[file:")),
                anyString(), any(), any(), any(), any());
    }
}
