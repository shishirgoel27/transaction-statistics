package com.n26;

import com.n26.transaction.validator.JsonValidator;
import com.n26.transaction.validator.TimestampValidator;
import com.n26.transaction.validator.Validator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

import java.sql.Timestamp;

@SpringBootApplication
public class Application {

    @Bean @Order(1)
    public Validator jsonValidator() {
        return new JsonValidator();
    }

    @Bean @Order(2)
    public Validator timestampValidator() {
        return new TimestampValidator();
    }

    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }

}
