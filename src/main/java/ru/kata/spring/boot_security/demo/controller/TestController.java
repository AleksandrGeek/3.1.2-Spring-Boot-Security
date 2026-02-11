package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class TestController {

    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "Test page - доступно всем";
    }

    @GetMapping("/test-secure")
    @ResponseBody
    public String testSecure() {
        return "Secure page - только для аутентифицированных";
    }
}

