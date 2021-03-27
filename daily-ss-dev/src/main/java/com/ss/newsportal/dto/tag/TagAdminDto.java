package com.ss.newsportal.dto.tag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TagAdminDto {

    private Long id;

    @Pattern(regexp = "^([A-zА-я]{1,16}(?:\\s[A-zА-я]{1,16}){0,2})$",
            message = "Длина слова(min-max) 1-16 букв. Количество слов(min-max): 1-3.")
    private String tagName;

    private long countNews;
}
