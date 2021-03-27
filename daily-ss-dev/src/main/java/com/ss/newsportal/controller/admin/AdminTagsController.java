package com.ss.newsportal.controller.admin;

import com.ss.newsportal.dto.tag.TagAdminDto;
import com.ss.newsportal.exception.NotUniqueValueException;
import com.ss.newsportal.service.tag.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;


@Controller
@RequestMapping(AdminTagsController.BASE_URL)
public class AdminTagsController {

    //Базовый URL для раздела "Теги"
    public static final String BASE_URL = "/admin/tags";

    private final TagService tagService;

    private String tagNameBeforeUpgrade;

    @Autowired
    public AdminTagsController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping(value = "")
    public String getTags(@ModelAttribute("search") String search, Model model) {
        List<TagAdminDto> tags = tagService.getAllApplyFilter(search);
        model.addAttribute("tags", tags);
        model.addAttribute("search", search);
        return "/admin/tags/tags";
    }

    @PostMapping(value = "/delete")
    public String deleteTag(@RequestParam("tagId") Long id) {
        tagService.deleteTag(id);
        return "redirect:" + BASE_URL;
    }

    @GetMapping(value = "/add")
    public String addTag(Model model) {
        model.addAttribute("tag", new TagAdminDto());
        return "admin/tags/tag";
    }

    /**
     * Добавление/редактирование тега в БД
     * @param tagAdminDto новый тег
     * @param validResult валидация
     * @return получение списка тегов
     */
    @PostMapping(value = "/save")
    public String saveTag(
            @ModelAttribute("tag") @Valid TagAdminDto tagAdminDto,
            BindingResult validResult,
            Model model
    ) {
        //Ошибки валидации
        if (validResult.hasErrors()) {
            model.addAttribute("tagNameOld", tagNameBeforeUpgrade);
            return "/admin/tags/tag";
        }
        //Неуникальное значение поля tagName
        try {
            tagService.saveTag(tagAdminDto);
        } catch (NotUniqueValueException exception) {
            model.addAttribute("tagNameOld", tagNameBeforeUpgrade);
            validResult.rejectValue("tagName", "badUniqueTagName");
            return "/admin/tags/tag";
        }

        return "redirect:" + BASE_URL;
    }

    @GetMapping(value = "/edit")
    public String editTag(@RequestParam("tagId") Long tagId, Model model) {
        TagAdminDto tagAdminDto = tagService.getTagAdminDto(tagId);
        tagNameBeforeUpgrade = tagAdminDto.getTagName();
        model.addAttribute("tag", tagAdminDto);
        model.addAttribute("tagNameOld", tagNameBeforeUpgrade);
        return "/admin/tags/tag";
    }

}
