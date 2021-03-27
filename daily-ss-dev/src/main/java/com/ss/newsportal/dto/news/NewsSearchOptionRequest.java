package com.ss.newsportal.dto.news;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class NewsSearchOptionRequest {
    private int pageNumber;
    private String searchTitle;
    private List<String> filterTags;
}
