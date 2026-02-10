package be.nicholasmeyers.demo.mcp.server.tool;

import be.nicholasmeyers.demo.mcp.server.client.TemperatureClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class TemperatureTool {

    private static final Logger log = LoggerFactory.getLogger(TemperatureTool.class);
    private final TemperatureClient temperatureClient;


    public TemperatureTool(TemperatureClient temperatureClient) {
        this.temperatureClient = temperatureClient;
    }

    @Tool
    public String getTemperature() {
        String temperature = temperatureClient.getTemperature();
        log.info("Temperature {}", temperature);
        return temperature;
    }
}
