package com.example.demo.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
public class TestService {
    public String getMessage(String message) {
        return format("wrapped original message: %s", message);
    }
}
