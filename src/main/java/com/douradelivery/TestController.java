package com.douradelivery;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String home() {
        return "API online.";
    }

    @GetMapping("/test")
    public String test() {
        return "OK";
    }
}
