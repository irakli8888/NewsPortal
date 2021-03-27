package com.ss.newsportal.controller.admin;

import com.ss.newsportal.dto.news.NewsByLinkRequest;
import com.ss.newsportal.exception.BadUrlException;
import com.ss.newsportal.exception.NoMarkupAvailableException;
import com.ss.newsportal.exception.NotNullColumnException;
import com.ss.newsportal.service.news.NewsAdminService;
import com.ss.newsportal.service.tag.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;


@Controller
@RequestMapping(AdminNewsController.BASE_URL)
public class AdminNewsController {

    public static final String BASE_URL = "/admin/news/by_link/add";

    private static final String PAGE_ADD_BY_LINK = "admin/news/add_by_link";

    private final NewsAdminService newsAdminService;
    private final TagService tagService;

    @Autowired
    public AdminNewsController(NewsAdminService newsAdminService, TagService tagService) {
        this.newsAdminService = newsAdminService;
        this.tagService = tagService;
    }

    @GetMapping(value = "")
    public String getAddByLinkPage(@ModelAttribute("request") NewsByLinkRequest request, Model model) {

        if (Optional.ofNullable(request.getUrl()).isPresent()) {
            try {
                model.addAttribute("dataNews", newsAdminService.parseLink(request));
                model.addAttribute("allTags", tagService.getShortTags());
            } catch (BadUrlException | NoMarkupAvailableException exception) {
                model.addAttribute("exception", exception.getMessage());
            }
        }

        model.addAttribute("base_url", BASE_URL);

        return PAGE_ADD_BY_LINK;
    }

    @PostMapping(value = "")
    public String saveNewsByLink(NewsByLinkRequest request, Model model) {
        try {
            newsAdminService.saveNewsByLink(request);
        } catch (NotNullColumnException exception) {
            model.addAttribute("noTagsException", exception.getMessage());
            return PAGE_ADD_BY_LINK;
        }

        return "redirect:" + BASE_URL;
    }
}