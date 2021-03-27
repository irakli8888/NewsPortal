package com.ss.newsportal.controller;

import com.ss.newsportal.dto.news.NewsDto;
import com.ss.newsportal.dto.news.NewsFull;
import com.ss.newsportal.dto.news.NewsSearchOptionRequest;
import com.ss.newsportal.entity.Feedback;
import com.ss.newsportal.service.feedback.FeedbackService;
import com.ss.newsportal.service.news.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;


@Controller
@RequestMapping(value = "")
@RequiredArgsConstructor
public class NewsController {

    //Количество популярных новостей
    private static final Integer QUANTITY_POPULAR_NEWS = 5;

    private static final String PAGE_MAIN = "index";
    private static final String PAGE_NEWS_DETAIL = "news/news_detail";
    private static final String PAGE_NEWS_ADD = "news/news_post";

    private final FeedbackService feedbackService;
    private final NewsService newsService;

    /**
     * Главная страница
     */
    @GetMapping(value = "")
    public String getMainPage(NewsSearchOptionRequest request, Model model) {

        model.addAttribute("response", newsService.getAllNewsByFilter(request));

        model.addAttribute("popularNews", newsService.getPopularNews(QUANTITY_POPULAR_NEWS));

        return PAGE_MAIN;
    }

    /**
     * Подробный просмотр новости
     */
    @GetMapping(value = "/news/{id}")
    public String getNewsDetails(@PathVariable("id") long newsId, Model model) {
        NewsFull news = newsService.getOneNews(newsId);
        if (news == null) {
            return "redirect:/";
        }
        model.addAttribute("news", news);
        List<Feedback> feedbacks = feedbackService.getFeedbacksByNewsId(newsId);
        model.addAttribute("feedbacks", feedbacks);
        model.addAttribute("popularNews", newsService.getPopularNews(QUANTITY_POPULAR_NEWS));

        return PAGE_NEWS_DETAIL;
    }

    /**
     * Сохранение комментария
     */
    @PostMapping(value = "/news/{id}/addFeedback")
    public String addFeedback(@PathVariable("id") long newsId,
                              @RequestParam("text") String comment,
                              @RequestParam("authorName") String authorName,
                              Principal principal
    ) {
        feedbackService.addFeedback(newsId, comment, authorName, principal);
        return "redirect:/news/" + newsId;
    }

    /**
     * Удаление комментария
     */
    @PostMapping(value = "/news/{id}/deleteFeedback")
    public String deleteFeedback(@PathVariable("id") long newsId,
                                 @RequestParam("feedbackId") long feedbackId
    ) {
        feedbackService.deleteFeedback(newsId, feedbackId);
        return "redirect:";
    }

    /**
     * Форма добавления новости
     */
    @GetMapping("/news/add")
    public String getPostNews(Model model) {
        model.addAttribute("newsDto", new NewsDto());

        model.addAttribute("popularNews", newsService.getPopularNews(QUANTITY_POPULAR_NEWS));

        return PAGE_NEWS_ADD;
    }

    /**
     * Сохранение новости в БД
     */
    @PostMapping("/news/add")
    public String addNews(@ModelAttribute("newsDto") NewsDto newsDto) {
        long newsId = newsService.saveNews(newsDto);
        return "redirect:/news/" + newsId;
    }
}
