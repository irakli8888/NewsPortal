package com.ss.newsportal.controller.auth;

import com.ss.newsportal.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

@RequiredArgsConstructor
@Controller
public class LoginController {
    @GetMapping(value = "/login")
    public ModelAndView getLoginPage(ModelMap model, @ModelAttribute("activatedAccount") Account activatedAccount) {
        model.addAttribute("activatedAccount", activatedAccount);
        return new ModelAndView("auth/login", model);
    }
}
