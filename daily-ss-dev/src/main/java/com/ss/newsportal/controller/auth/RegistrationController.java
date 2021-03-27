package com.ss.newsportal.controller.auth;

import com.ss.newsportal.dto.RegisterDto;
import com.ss.newsportal.entity.Account;
import com.ss.newsportal.exception.RegistrationUniqueConstraintsException;
import com.ss.newsportal.service.auth.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;

@RequiredArgsConstructor
@Controller
public class RegistrationController {
    private final RegistrationService registrationService;

    private Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    @GetMapping("/registration")
    public String getRegistrationPage(Model model) {
        RegisterDto registerDTO = new RegisterDto();
        model.addAttribute("registerDto", registerDTO);
        return "auth/registration";
    }

    @PostMapping("/registration")
    public ModelAndView registerUser(@ModelAttribute("registerDto") @Valid RegisterDto registerDTO) {
        try {
            registrationService.registerAccount(registerDTO);
        } catch (RegistrationUniqueConstraintsException ex) {
            logger.debug("registration canceled");
            ModelAndView mav = new ModelAndView("auth/registration", "registerDto", registerDTO);
            mav.addObject("errors", ex.getErrors());
            return mav;
        }

        logger.debug("registration ok");
        return new ModelAndView("auth/check_email", "registerDto", registerDTO);
    }

    @GetMapping("/activate/{code}")
    public RedirectView activate(@PathVariable String code, RedirectAttributes attributes) {
        Account activatedAccount = registrationService.activateAccount(code);

        if (activatedAccount != null) {
            attributes.addFlashAttribute("activatedAccount", activatedAccount);
            return new RedirectView("/login");
        } else {
            return new RedirectView("/invalid_or_expired_code");
        }
    }

    @GetMapping("/invalid_or_expired_code")
    public String getInvalidOrExpiredCodePage() {
        return "auth/invalid_or_expired_code";
    }
}
