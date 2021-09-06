package org.divulgit.controller;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

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
