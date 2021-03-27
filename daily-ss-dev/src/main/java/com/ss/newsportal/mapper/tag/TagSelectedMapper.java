package com.ss.newsportal.mapper.tag;

import com.ss.newsportal.dto.tag.TagShortDto;
import com.ss.newsportal.entity.Tag;
import org.mapstruct.Mapper;

import java.util.Set;

/**
 * @author Aleksey Romodin
 */
@Mapper
public interface TagSelectedMapper{
    Set<Tag> toSetTag(Set<TagShortDto> tags);
}
