package com.ss.newsportal.dto.news;

import com.ss.newsportal.dto.tag.TagShortDto;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class NewsSearchOptionResponse {
    private int pageNumber;
    private int pageSize;
    private int totalPage;
    private long totalNews;
    private String searchTitle;
    private Set<String> filterTags;
    private List<TagShortDto> tagsForFilter;
    private List<NewsResultDto> listNews;
}
