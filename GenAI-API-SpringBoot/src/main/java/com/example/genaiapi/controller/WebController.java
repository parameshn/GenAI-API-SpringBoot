package com.example.genaiapi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

public class WebController {
    @GetMapping("/")
    public String index() {
        return "index";
    }
}
