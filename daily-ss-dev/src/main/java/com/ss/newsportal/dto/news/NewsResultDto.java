package com.ss.newsportal.dto.news;

import com.ss.newsportal.dto.media.MediaShortDto;
import com.ss.newsportal.dto.tag.TagShortDto;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.Set;


@Data
public class NewsResultDto {

    private Long id;
    private String title;
    private String textNews;
    private long accountId;
    private String authorName;
    private String url;

    private long countLikes;
    private long countDislikes;
    private long countComments;
    private long countMedia;

    private Set<MediaShortDto> mediaSet;
    private Set<TagShortDto> tags;

    private ZonedDateTime dateTimeCreated;
    private ZonedDateTime dateTimeModified;
}
