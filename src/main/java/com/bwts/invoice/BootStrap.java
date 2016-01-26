package com.bwts.invoice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ComponentScan("com.bwts.common.kafka.producer,com.bwts.invoice")
@ImportResource("classpath:spring/spring-config.xml")
public class BootStrap extends SpringBootServletInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(BootStrap.class);

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(BootStrap.class);
    }

    public static void main(String[] args) {
        LOGGER.info("Starting application!");
        new BootStrap()
                .configure(new SpringApplicationBuilder(BootStrap.class))
                .run(args);
    }
}
