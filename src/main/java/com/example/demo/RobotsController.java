package com.example.demo;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;

@RestController
public class RobotsController {

    @GetMapping("/robots.txt")
    public ResponseEntity<String> getRobotsTxt() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/robots.txt");
        String content = new String(Files.readAllBytes(resource.getFile().toPath()));
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/plain; charset=utf-8");
        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }
}
