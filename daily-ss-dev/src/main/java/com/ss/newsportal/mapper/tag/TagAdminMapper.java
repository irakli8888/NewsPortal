package com.ss.newsportal.mapper.tag;

import com.ss.newsportal.dto.tag.TagAdminDto;
import com.ss.newsportal.entity.Tag;
import com.ss.newsportal.mapper.ParentMapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * @author Aleksey Romodin
 */
@Mapper
public interface TagAdminMapper extends ParentMapper<TagAdminDto, Tag> {

    @AfterMapping
    default void setCountNews(@MappingTarget TagAdminDto tagAdminDto, Tag tag) {
        tagAdminDto.setCountNews(tag.getNews().size());
    }

    List<TagAdminDto> toDtoList(List<Tag> tags);

}
