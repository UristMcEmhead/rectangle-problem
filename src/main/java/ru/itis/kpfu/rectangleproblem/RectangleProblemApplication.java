package ru.itis.kpfu.rectangleproblem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RectangleProblemApplication{

    private static Logger LOG = LoggerFactory.getLogger(RectangleProblemApplication.class);

    public static void main(String[] args) {
        LOG.info("Application started");
        SpringApplication.run(RectangleProblemApplication.class, args);
        LOG.info("Application finished");
    }
}