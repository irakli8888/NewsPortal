package com.ss.newsportal.repository.view;

import com.ss.newsportal.entity.view.TagShort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TagShortRepository extends JpaRepository<TagShort, Long> {
    TagShort findByTagNameIgnoreCase(String tagName);
}
