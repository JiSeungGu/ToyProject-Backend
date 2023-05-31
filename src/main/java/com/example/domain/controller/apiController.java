package com.example.domain.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping
public class apiController {

    @GetMapping("/input")
    public void InputTest(@RequestParam(name="string") String string) {
        log.info("CALL INPUT TEST");

        System.out.println("InputTest :"+string);
    }
}

