package com.ss.newsportal.mapper.media;

import com.ss.newsportal.entity.Media;
import com.ss.newsportal.entity.select.MediaType;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.io.File;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public interface MediaMapper {

    String PATH_IMAGE = System.getProperty("user.dir") + "/img/";
    String SMALL_PATH_IMAGE = "/img/";

    @Named("toDto")
    default String toDto(Media media) {
        if (media == null) {
            return null;
        }
        // Медиа не фото
        if (media.getTypeMedia() != MediaType.IMAGE) {
            return null;
        }
        String mediaDto = null;
        File file = new File(PATH_IMAGE + media.getId() + ".jpeg");
        if (file.exists()) {
            // фото присутствует
            mediaDto = SMALL_PATH_IMAGE + media.getId() + ".jpeg";
        }
        return mediaDto;
    }

    @IterableMapping(qualifiedByName = "toDto")
    Set<String> toDto(Set<Media> media);

    @Named("withoutNull")
    default Set<String> toDtoWithoutNull(Set<Media> media) {
        if (media == null) {
            return null;
        }
        Set<String> dto = toDto(media);
        return dto.stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }
}
