package com.ss.newsportal.service.feedback;

import com.ss.newsportal.entity.Account;
import com.ss.newsportal.entity.Feedback;
import com.ss.newsportal.entity.News;
import com.ss.newsportal.entity.Person;
import com.ss.newsportal.repository.AccountRepository;
import com.ss.newsportal.repository.FeedbackRepository;
import com.ss.newsportal.repository.NewsRepository;
import com.ss.newsportal.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackServiceImpl implements FeedbackService {

    private final AccountRepository accountRepository;
    private final NewsRepository newsRepository;
    private final FeedbackRepository feedbackRepository;
    private final PersonRepository personRepository;

    @Override
    public void addFeedback(long newsId, String feedbackText, String authorName, Principal principal) {
        News news = newsRepository.findById(newsId).orElse(null);
        // Проверка существования новости
        if (news == null) {
            log.debug("feedback not added. news with id {} not exist", newsId);
            return;
        }
        // Проверка пустоты имени
        if (authorName.matches("\\s*")) {
            authorName = "Anonim";
        }
        // Проверка пустоты комментария
        if (feedbackText.matches("\\s*")) {
            log.debug("feedback not added. feedback text is Empty");
            return;
        }
        Feedback feedback = new Feedback();
        if (principal != null) {
            Account account = accountRepository.findByUsername(principal.getName());
            feedback.setAccount(account);
            Person person = personRepository.findByAccount(account);
            if (person == null) {
                authorName = account.getUsername();
            } else {
                authorName = person.getFirstName() + person.getLastName();
            }
        }
        feedback.setComment(feedbackText);
        feedback.setAuthorName(authorName);
        feedback.setNews(news);
        feedback.setCountLikes(0);
        feedback.setCountDislikes(0);
        feedback.setParentId(0L);
        feedbackRepository.save(feedback);

        log.info("added feedback {} from {} in news with id {}", feedbackText, authorName, newsId);
    }
    @Override
    public List<Feedback> getFeedbacksByNewsId(long newsId) {
        return feedbackRepository.findByNews_Id(newsId);
    }

    @Override
    @Transactional
    public void deleteFeedback(long newsId, long feedbackId) {
        News news = newsRepository.findById(newsId).orElse(null);
        // Проверка существования новости
        if (news == null) {
            log.debug("feedback not deleted. news with id {} not exist", newsId);
            return;
        }
        Feedback feedback = feedbackRepository.findById(feedbackId).orElse(null);
        if (feedback == null) {
            log.debug("feedback not deleted. feedback with id {} not exist", newsId);
            return;
        }
        feedbackRepository.delete(feedback);
        log.info("feedback with id {} deleted", feedbackId);
    }
}
