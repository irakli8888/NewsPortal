package com.ss.newsportal.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.ZonedDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "comment", nullable = false)
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", nullable = false)
    private News news;

    @Column(name = "count_likes", nullable = false)
    private long countLikes;

    @Column(name = "count_dislikes", nullable = false)
    private long countDislikes;

    @Column(name = "author_name")
    private String authorName;

    @Column(name = "parent_id", nullable = false)
    private Long parentId;

    @CreationTimestamp
    @Column(name = "date_time_created", nullable = false)
    private ZonedDateTime dateTimeCreated;

    @UpdateTimestamp
    @Column(name = "date_time_modified", nullable = false)
    private ZonedDateTime dateTimeModified;

}
