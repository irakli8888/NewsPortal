package com.ss.newsportal.repository;

import com.ss.newsportal.dto.news.PopularNewsShort;
import com.ss.newsportal.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long>, JpaSpecificationExecutor<News> {

    /**
     * Получаем популярные новости из БД.
     * Алгоритм: сортируем по отзываем -> если лайки превышают дизлайки -> количество лайков
     * @param quantity количество новостей
     * @return список новостей(id, заголовок)
     */
    @Query(value = "SELECT * FROM popular_news(:quantity)",nativeQuery = true)
    List<PopularNewsShort> getPopularNews(Integer quantity);
}