package com.ss.newsportal.service.news;

import com.ss.newsportal.dto.news.NewsDto;
import com.ss.newsportal.dto.news.NewsFull;
import com.ss.newsportal.dto.news.NewsSearchOptionRequest;
import com.ss.newsportal.dto.news.NewsSearchOptionResponse;
import com.ss.newsportal.dto.news.PopularNewsShort;
import com.ss.newsportal.entity.Media;
import com.ss.newsportal.entity.News;
import com.ss.newsportal.entity.Person;
import com.ss.newsportal.entity.select.MediaType;
import com.ss.newsportal.mapper.news.NewsFullMapper;
import com.ss.newsportal.mapper.news.NewsMapper;
import com.ss.newsportal.mapper.news.NewsResultMapper;
import com.ss.newsportal.repository.MediaRepository;
import com.ss.newsportal.repository.NewsRepository;
import com.ss.newsportal.repository.PersonRepository;
import com.ss.newsportal.repository.view.TagShortRepository;
import com.ss.newsportal.service.news.specification.NewsSpecification;
import com.ss.newsportal.service.tag.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private static final String PATH_IMAGE = System.getProperty("user.dir") + "/img/";
    private static final int PAGE_SIZE = 10;

    private final NewsRepository newsRepository;
    private final MediaRepository mediaRepository;
    private final NewsMapper newsMapper;
    private final NewsFullMapper newsFullMapper;
    private final PersonRepository personRepository;
    private final TagShortRepository tagShortRepository;
    private final NewsResultMapper newsResultMapper;
    private final TagService tagService;

    @Override
    public NewsSearchOptionResponse getAllNewsByFilter(NewsSearchOptionRequest request) {

        Specification<News> specification = null;

        String regexpSearchTag = "^([A-zА-я]{1,16}(?:\\s[A-zА-я]{1,16}){0,2})$";
        String searchTitle = request.getSearchTitle();

        NewsSearchOptionResponse response = new NewsSearchOptionResponse();

        List<String>  listSearchTag = Optional.ofNullable(request.getFilterTags()).orElse(null);

        //Проверяем список полученных тегов на соответствие паттерну
        if (listSearchTag != null) {


            Set<String> fSet = new HashSet<>();
            listSearchTag.stream().filter(t -> t.matches(regexpSearchTag)).forEach(fSet::add);
            response.setFilterTags(fSet);

            /*
             * Добавляем критерии фильтрации по тегам
             * Если тег имеет полное совпадение с тегом в БД -> добавляем полное сравнение (=)
             * Если тег не имеет совпадений в БД -> добавляем условие вхождения в наименовании (like)
             */
            if (!fSet.isEmpty()) {
                for (String tag : fSet) {
                    if (tagShortRepository.findByTagNameIgnoreCase(tag) != null) {
                        specification = draftSpec(specification, "tagComplete", tag);
                    } else {
                        specification = draftSpec(specification, "tagPart", tag);
                    }
                }

            }
        }

        //Поиск по заголовкам
        specification = draftSpec(specification, "title", searchTitle);

        long totalNews = newsRepository.count(specification);
        int pageNumber = Optional.of(request.getPageNumber()).orElse(0);

        if (pageNumber < 0) {
            pageNumber = 0;
        }

        int totalPage = (int) Math.ceil((double) totalNews / PAGE_SIZE);

        if (pageNumber > (totalPage - 1)) {
            pageNumber = totalPage - 1;
        }

        //Добавляем пагинацию и сортировку
        List<News> listNews = newsRepository.findAll(specification,
                PageRequest.of(pageNumber, PAGE_SIZE, Sort.by("dateTimeCreated").descending())).toList();


        response.setListNews(newsResultMapper.toDtoList(listNews));
        response.setTotalNews(totalNews);
        response.setTotalPage(totalPage);
        response.setPageSize(PAGE_SIZE);
        response.setPageNumber(pageNumber);
        response.setTagsForFilter(tagService.getShortTags());
        response.setSearchTitle(searchTitle);

        return response;
    }

    private Specification<News> draftSpec(Specification<News> specification, String operation, String value) {
        if (value != null) {
            if (specification == null) {
                specification = Specification.where(new NewsSpecification(operation, value));
            } else {
                specification = specification.and(new NewsSpecification(operation, value));
            }
        }
        return specification;
    }

    @Override
    public NewsFull getOneNews(long id) {
        return newsFullMapper.toDto(newsRepository.findById(id).orElse(null));
    }

    @Override
    public long saveNews(NewsDto newsDto) {
        News news = newsMapper.toEntity(newsDto);

        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        String username = principal.getUsername();
        Person person = personRepository.findByAccountUsername(username);

        news.setAuthorName(person.getFirstName());
        news.setAccount(person.getAccount());
        news.setCountMedia(newsDto.getFiles().length);
        news = newsRepository.save(news);
        saveMedia(news, newsDto.getFiles());
        return news.getId();
    }

    private void saveMedia(News news, MultipartFile[] files) {
        File dir = new File(PATH_IMAGE);
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                throw new RuntimeException("Не удалось создать каталог");
            }
        }

        for (int i = 1; i <= files.length; i++) {
            Media media = new Media();
            media.setNews(news);
            media.setTypeMedia(MediaType.IMAGE);
            mediaRepository.save(media);

            try {
                files[i - 1].transferTo(new File(PATH_IMAGE + media.getId() + ".jpeg"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public List<PopularNewsShort> getPopularNews(Integer quantity) {
        return newsRepository.getPopularNews(quantity);
    }
}
