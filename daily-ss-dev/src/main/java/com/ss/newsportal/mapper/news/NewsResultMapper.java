package com.ss.newsportal.mapper.news;

import com.ss.newsportal.dto.news.NewsResultDto;
import com.ss.newsportal.entity.News;
import com.ss.newsportal.mapper.media.MediaShortMapper;
import com.ss.newsportal.mapper.ParentMapper;
import com.ss.newsportal.mapper.tag.TagShortMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(uses = {TagShortMapper.class, MediaShortMapper.class})
public interface NewsResultMapper extends ParentMapper<NewsResultDto, News> {

    @Mapping(target = "accountId", source = "account.id")
    NewsResultDto toDto(News news);
}
