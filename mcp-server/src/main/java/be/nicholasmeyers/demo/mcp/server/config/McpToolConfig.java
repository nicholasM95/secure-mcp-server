package be.nicholasmeyers.demo.mcp.server.config;

import be.nicholasmeyers.demo.mcp.server.tool.TemperatureTool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpToolConfig {

    @Bean
    public ToolCallbackProvider toolCallbackProvider(TemperatureTool temperatureTool) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(temperatureTool)
                .build();
    }
}