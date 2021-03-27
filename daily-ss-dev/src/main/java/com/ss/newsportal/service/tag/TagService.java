package com.ss.newsportal.service.tag;

import com.ss.newsportal.dto.tag.TagAdminDto;
import com.ss.newsportal.dto.tag.TagShortDto;

import java.util.List;


public interface TagService {

    List<TagAdminDto> getAll();
    void deleteTag(Long id);
    List<TagAdminDto> getAllApplyFilter(String search);
    void saveTag(TagAdminDto tagAdminDto);
    TagAdminDto getTagAdminDto(Long id);
    List<TagShortDto> getShortTags();
}
