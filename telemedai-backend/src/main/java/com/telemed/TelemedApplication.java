package com.telemed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Punto de entrada de la aplicación de telemedicina. */
@SpringBootApplication
public class TelemedApplication {
    public static void main(String[] args) {
        SpringApplication.run(TelemedApplication.class, args);
    }
}
