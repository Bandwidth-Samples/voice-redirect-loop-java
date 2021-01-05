package com.bandwidth;

import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Main {

    static Logger logger = LoggerFactory.getLogger(Main.class);

    public static final Set<String> activeCalls = new HashSet<>();

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        logger.info("Server has started...");
    }






}
