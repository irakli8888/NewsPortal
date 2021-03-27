package com.ss.newsportal.mapper.tag;

import com.ss.newsportal.dto.tag.TagShortDto;
import com.ss.newsportal.entity.Tag;
import com.ss.newsportal.entity.view.TagShort;
import com.ss.newsportal.mapper.ParentMapper;
import org.mapstruct.Mapper;

import java.util.Set;

/**
 * @author Aleksey Romodin
 */
@Mapper
public interface TagShortMapper extends ParentMapper<TagShortDto, TagShort> {

    Set<TagShortDto> toSetDto(Set<Tag> tags);
}
