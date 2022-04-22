package org.divulgit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("currentUsername","decioluckow");
        return "index";
    }

    @GetMapping("/start")
    public String start(Model model) {
        model.addAttribute("currentUsername","decioluckow");
        return "start";
    }
}
