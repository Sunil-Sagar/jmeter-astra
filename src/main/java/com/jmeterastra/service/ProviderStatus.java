package com.jmeterastra.service;

import com.jmeterastra.claudecode.ClaudeCodeCliProvider;
import com.jmeterastra.utils.AiConfig;
import com.jmeterastra.utils.GatewayConfig;

/**
 * Read-only snapshot of which AI providers look configured from JMeter
 * properties. No network calls: Ollama is treated as "available to try"
 * without probing, and is mentioned in the empty-state welcome when no
 * cloud keys are present.
 */
public final class ProviderStatus {

    private final boolean anyCloudReady;
    private final boolean ollamaPreferred;
    private final boolean claudeCodeCliPreferred;

    private ProviderStatus(boolean anyCloudReady, boolean ollamaPreferred, boolean claudeCodeCliPreferred) {
        this.anyCloudReady = anyCloudReady;
        this.ollamaPreferred = ollamaPreferred;
        this.claudeCodeCliPreferred = claudeCodeCliPreferred;
    }

    /** Loads status from the current {@link AiConfig} / JMeter properties. */
    public static ProviderStatus fromConfig() {
        boolean cloud = isApiKeyConfigured("anthropic.api.key")
                || isApiKeyConfigured("openai.api.key")
                || isApiKeyConfigured("google.api.key")
                || isApiKeyConfigured("grok.api.key")
                || isApiKeyConfigured("meta.api.key")
                || isBedrockConfigured()
                || GatewayConfig.hasOpenAiGatewayCredentials()
                || GatewayConfig.hasAnthropicGatewayCredentials();
        String serviceType = AiConfig.getProperty("jmeter.ai.service.type", "");
        boolean ollamaPreferred = "ollama".equalsIgnoreCase(serviceType == null ? "" : serviceType.trim());
        boolean claudeCodeCliPreferred = isClaudeCodeCliPreferred();
        return new ProviderStatus(cloud, ollamaPreferred, claudeCodeCliPreferred);
    }

    /**
     * Declarative only, like {@link #isOllamaPreferred()} - no PATH/filesystem
     * probing here, so this stays fast and deterministic in tests. Reads the
     * same property {@link ClaudeCodeCliProvider#isEnabled()} does, which now
     * defaults to {@code true}: an authenticated Claude Code subscription
     * session is a usable fallback even with zero cloud API keys configured.
     */
    private static boolean isClaudeCodeCliPreferred() {
        return Boolean.parseBoolean(AiConfig.getProperty(ClaudeCodeCliProvider.ENABLED_KEY, "true"));
    }

    /**
     * True when the user has at least one cloud/API key (or Bedrock creds),
     * has explicitly chosen Ollama as the default service type, or has an
     * enabled (by default) Claude Code CLI subscription path available.
     */
    public boolean isReady() {
        return anyCloudReady || ollamaPreferred || claudeCodeCliPreferred;
    }

    /** True when at least one non-placeholder cloud/API key is set. */
    public boolean hasAnyCloudProvider() {
        return anyCloudReady;
    }

    /** True when {@code jmeter.ai.service.type=ollama}. */
    public boolean isOllamaPreferred() {
        return ollamaPreferred;
    }

    /**
     * True when a property looks like a real API key (non-empty, not a
     * YOUR_* placeholder from the sample file).
     */
    public static boolean isApiKeyConfigured(String propertyKey) {
        String value = AiConfig.getProperty(propertyKey, "");
        return isUsableSecret(value);
    }

    static boolean isBedrockConfigured() {
        if (isApiKeyConfigured("bedrock.api.key")) {
            return true;
        }
        String access = AiConfig.getProperty("bedrock.aws.access.key", "");
        String secret = AiConfig.getProperty("bedrock.aws.secret.key", "");
        return isUsableSecret(access) && isUsableSecret(secret);
    }

    /**
     * Rejects blank values and any sample-file placeholder starting with
     * {@code YOUR_} (case-insensitive), for example {@code YOUR_API_KEY},
     * {@code YOUR_GOOGLE_API_KEY}, {@code YOUR_TOKEN} or
     * {@code YOUR_AWS_ACCESS_KEY}.
     */
    static boolean isUsableSecret(String value) {
        if (value == null) {
            return false;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return false;
        }
        return !trimmed.regionMatches(true, 0, "YOUR_", 0, 5);
    }
}
