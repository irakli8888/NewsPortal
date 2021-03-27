package com.ss.newsportal.mapper.news;

import com.ss.newsportal.dto.news.NewsFull;
import com.ss.newsportal.entity.News;
import com.ss.newsportal.mapper.media.MediaMapper;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;


@Mapper(componentModel = "spring", uses = {MediaMapper.class})
public interface NewsFullMapper {

    @Named("withMedia")
    @Mapping(target = "mediaSet", qualifiedByName = "withoutNull")
    NewsFull toDto(News news);

    @Named("withoutMedia")
    @IterableMapping(qualifiedByName = "withMedia")
    List<NewsFull> toDto(List<News> news);
}
