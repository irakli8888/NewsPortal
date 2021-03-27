package com.ss.newsportal.dto.news;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class NewsDto {

    private String title;

    private String textNews;

    private MultipartFile[] files;
}
