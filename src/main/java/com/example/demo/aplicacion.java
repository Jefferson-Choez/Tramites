package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.TimeZone;

@SpringBootApplication
public class aplicacion {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));
        SpringApplication.run(aplicacion.class, args);
    }


    @GetMapping("/Login")
    public String mostrarLogin() {
        return "Index";
    }
}

