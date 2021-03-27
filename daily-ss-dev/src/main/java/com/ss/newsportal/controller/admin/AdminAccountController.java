package com.ss.newsportal.controller.admin;

import com.ss.newsportal.service.account.AccountAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(AdminAccountController.BASE_URL)
public class AdminAccountController {

    public static final String BASE_URL = "/admin/accounts";

    private final AccountAdminService accountService;

    @Autowired
    public AdminAccountController(AccountAdminService accountService) {
        this.accountService = accountService;
    }

    @GetMapping(value = "")
    public String getAccounts(@ModelAttribute("search") String search, Model model) {
        model.addAttribute("accounts", accountService.getAccountsAdmin(search));
        return "/admin/account/accounts";
    }
}