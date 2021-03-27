package com.ss.newsportal.dto.news;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class NewsByLinkRequest {
    private String url;
    private Long[] idTags;
}
