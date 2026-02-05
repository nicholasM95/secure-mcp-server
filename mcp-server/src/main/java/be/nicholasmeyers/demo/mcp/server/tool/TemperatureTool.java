package be.nicholasmeyers.demo.mcp.server.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class TemperatureTool {

    private static final Logger log = LoggerFactory.getLogger(TemperatureTool.class);

    @Tool
    public double getTemperature() {
        double temperature = 18 + (Math.random() * 16);
        temperature = Math.round(temperature * 100.0) / 100.0;
        log.info("Temperature {}", temperature);
        return temperature;
    }
}
