package be.nicholasmeyers.demo.temperature.api.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/temperature")
public class TemperatureController {

    private static final Logger log = LoggerFactory.getLogger(TemperatureController.class);

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    double getTemperature() {
        double temperature = 18 + (Math.random() * 16);
        temperature = Math.round(temperature * 100.0) / 100.0;
        log.info("Temperature {}", temperature);
        return temperature;
    }

}
