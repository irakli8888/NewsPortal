package com.ss.newsportal.service.news;

import com.ss.newsportal.dto.news.NewsDto;
import com.ss.newsportal.dto.news.NewsSearchOptionRequest;
import com.ss.newsportal.dto.news.NewsSearchOptionResponse;
import com.ss.newsportal.dto.news.NewsFull;
import com.ss.newsportal.dto.news.PopularNewsShort;

import java.util.List;


public interface NewsService {

    NewsSearchOptionResponse getAllNewsByFilter(NewsSearchOptionRequest request);
    NewsFull getOneNews(long id);
    List<PopularNewsShort> getPopularNews(Integer quantity);
    long saveNews(NewsDto newsDto);
}
