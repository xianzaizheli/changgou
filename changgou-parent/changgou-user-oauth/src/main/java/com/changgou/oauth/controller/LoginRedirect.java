package com.changgou.oauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/oauth")
public class LoginRedirect {

    @GetMapping("/login")
    public String login(@RequestParam(required = false ,defaultValue = "" ,value = "from")String from , Model model){
        model.addAttribute("from",from);
        return "login";
    }

}
