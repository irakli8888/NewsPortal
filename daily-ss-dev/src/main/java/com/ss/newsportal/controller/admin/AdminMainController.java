package com.ss.newsportal.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping(value = "/admin")
public class AdminMainController {

    @GetMapping(value = "")
    public String getMainPage() {
        return "admin/main/main";
    }
}
