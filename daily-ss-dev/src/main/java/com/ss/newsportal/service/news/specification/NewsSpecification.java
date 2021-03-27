package com.ss.newsportal.service.news.specification;

import com.ss.newsportal.entity.News;
import com.ss.newsportal.entity.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * @author Aleksey Romodin
 */
@AllArgsConstructor
public class NewsSpecification implements Specification<News>{

    private final String operation;
    private final String value;

    @Override
    public Predicate toPredicate(@NonNull Root<News> root,
                                 @NonNull CriteriaQuery<?> cQuery,
                                 @NonNull CriteriaBuilder cBuilder
    ) {
        Join<News, Tag> newsTagJoin = root.join("tags", JoinType.LEFT);
        switch (operation) {
            case "tagComplete":
                return cBuilder.equal(cBuilder.lower(newsTagJoin.get("tagName")), value.toLowerCase());
            case "tagPart":
                return cBuilder.like(cBuilder.lower(newsTagJoin.get("tagName")), "%" + value.toLowerCase() + "%");
            case "title":
                return cBuilder.like(cBuilder.lower(root.get("title")), "%" + value.toLowerCase() + "%");
        }
        return null;
    }

}

