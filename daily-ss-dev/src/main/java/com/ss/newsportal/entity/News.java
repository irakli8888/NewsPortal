package com.ss.newsportal.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Setter
@Getter
@Table(name = "news")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "text_news")
    private String textNews;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "author_name")
    private String authorName;

    @Column(name = "count_likes")
    private long countLikes;

    @Column(name = "count_dislikes")
    private long countDislikes;

    @Column(name = "count_comments")
    private long countComments;

    @Column(name = "count_media")
    private long countMedia;

    @Column(name = "date_time_created")
    private ZonedDateTime dateTimeCreated;

    @Column(name = "date_time_modified")
    private ZonedDateTime dateTimeModified;

    @Column(name = "url")
    private String url;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "news_tag",
            joinColumns = @JoinColumn(name = "news_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id")
    )
    private Set<Tag> tags;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "news_id")
    private Set<Media> mediaSet;


    public News() {
        dateTimeCreated = ZonedDateTime.now();
        dateTimeModified = ZonedDateTime.now();
    }
}
