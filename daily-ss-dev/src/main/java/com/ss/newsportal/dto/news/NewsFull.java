package com.ss.newsportal.dto.news;

import com.ss.newsportal.dto.tag.TagShortDto;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.Set;


@Data
public class NewsFull {

    private Long id;
    private String title;
    private String url;
    private String textNews;
    private Set<String> mediaSet;
    private String authorName;
    private ZonedDateTime dateTimeCreated;

    private Set<TagShortDto> tags;
}
