package com.ss.newsportal.dto.news;

import com.ss.newsportal.dto.tag.TagShortDto;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.Set;

@Getter
@Setter
public class NewsByLinkResponse {

    private String title;
    private String url;
    private String description;
    private String image;
    private String siteName;
    private ZonedDateTime datePublication;

    private Set<TagShortDto> selectTags;
}
