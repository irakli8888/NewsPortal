package com.ss.newsportal.dto.media;

import com.ss.newsportal.entity.select.MediaType;
import lombok.Data;


@Data
public class MediaShortDto {
    private Long id;
    private MediaType typeMedia;
}
