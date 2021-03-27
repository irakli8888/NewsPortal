package com.ss.newsportal.mapper.media;

import com.ss.newsportal.dto.media.MediaShortDto;
import com.ss.newsportal.entity.Media;
import com.ss.newsportal.entity.select.MediaType;
import org.mapstruct.Mapper;

import java.io.File;
import java.util.HashSet;
import java.util.Set;


@Mapper
public abstract class MediaShortMapper {

    private static final String IMAGE_PATH = System.getProperty("user.dir") + "/img/";

    public MediaShortDto toDto(Media media) {
        MediaShortDto mediaShortDto = new MediaShortDto();
        mediaShortDto.setId(media.getId());
        mediaShortDto.setTypeMedia(MediaType.IMAGE);
        return mediaShortDto;
    }

    public Set<MediaShortDto> toSetDto(Set<Media> mediaSet) {
        if (mediaSet != null) {
            Set<MediaShortDto> newSet = new HashSet<>();

            mediaSet.stream().filter(media ->
                    (new File(IMAGE_PATH + media.getId() + ".jpeg").isFile()) && (media.getTypeMedia() == MediaType.IMAGE))
                    .forEach(media -> newSet.add(toDto(media)));

            return newSet;
        }
        return null;
    }
}
