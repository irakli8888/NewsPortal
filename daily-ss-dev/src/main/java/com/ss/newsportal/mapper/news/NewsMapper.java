package com.ss.newsportal.mapper.news;

import com.ss.newsportal.dto.news.NewsDto;
import com.ss.newsportal.entity.News;
import com.ss.newsportal.mapper.ParentMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NewsMapper extends ParentMapper<NewsDto, News> {
}
