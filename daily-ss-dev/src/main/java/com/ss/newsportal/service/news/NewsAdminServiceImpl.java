package com.ss.newsportal.service.news;

import com.ss.newsportal.dto.news.NewsByLinkRequest;
import com.ss.newsportal.dto.news.NewsByLinkResponse;
import com.ss.newsportal.dto.tag.TagShortDto;
import com.ss.newsportal.entity.Media;
import com.ss.newsportal.entity.News;
import com.ss.newsportal.entity.Tag;
import com.ss.newsportal.entity.select.MediaType;
import com.ss.newsportal.exception.BadUrlException;
import com.ss.newsportal.exception.NoMarkupAvailableException;
import com.ss.newsportal.exception.NotNullColumnException;
import com.ss.newsportal.mapper.tag.TagSelectedMapper;
import com.ss.newsportal.mapper.tag.TagShortMapper;
import com.ss.newsportal.repository.AccountRepository;
import com.ss.newsportal.repository.MediaRepository;
import com.ss.newsportal.repository.NewsRepository;
import com.ss.newsportal.repository.view.TagShortRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class NewsAdminServiceImpl implements NewsAdminService{

    private final TagShortRepository tagShortRepository;
    private final TagShortMapper tagShortMapper;
    private final TagSelectedMapper tagSelectedMapper;
    private final AccountRepository accountRepository;
    private final NewsRepository newsRepository;
    private final MediaRepository mediaRepository;

    private static final String PATH_IMAGE = System.getProperty("user.dir") + "/img/";

    @Autowired
    public NewsAdminServiceImpl(TagShortRepository tagShortRepository,
                                TagShortMapper tagSmallMapper,
                                TagSelectedMapper tagSelectedMapper,
                                AccountRepository accountRepository,
                                NewsRepository newsRepository,
                                MediaRepository mediaRepository) {
        this.tagShortRepository = tagShortRepository;
        this.tagShortMapper = tagSmallMapper;
        this.tagSelectedMapper = tagSelectedMapper;
        this.accountRepository = accountRepository;
        this.newsRepository = newsRepository;
        this.mediaRepository = mediaRepository;
    }

    @Override
    public NewsByLinkResponse parseLink(NewsByLinkRequest request) {
        return getBigData(request);
    }

    @Override
    @Transactional
    public void saveNewsByLink(NewsByLinkRequest request) {
        News news = new News();
        NewsByLinkResponse newsByLink = getBigData(request);

        Set<Tag> tags = tagSelectedMapper.toSetTag(
                Optional.ofNullable(newsByLink.getSelectTags())
                        .orElseThrow(()-> new NotNullColumnException("Не выбраны теги")));

        news.setTitle(newsByLink.getTitle());
        news.setTextNews(newsByLink.getDescription());
        news.setAccount(accountRepository.findByUsername("admin"));
        news.setAuthorName(newsByLink.getSiteName());
        news.setCountMedia(1L);
        news.setDateTimeCreated(newsByLink.getDatePublication());
        news.setDateTimeModified(newsByLink.getDatePublication());
        news.setUrl(newsByLink.getUrl());
        news.setTags(tags);

        newsRepository.save(news);

        //Сохраняем контент
        Media media = new Media();
        media.setNews(news);
        media.setTypeMedia(MediaType.IMAGE);
        mediaRepository.save(media);


        //Сохраняем фото
        long contentId = media.getId();
        try {
            URL url = new URL(newsByLink.getImage());
            File dir = new File(PATH_IMAGE);

            if (!dir.exists()) {
                if (!dir.mkdir()) {
                    throw new RuntimeException("Не удалось создать директорию.");
                }
            }

            InputStream in = url.openStream();
            Files.copy(in, Paths.get(PATH_IMAGE + contentId + ".jpeg"), StandardCopyOption.REPLACE_EXISTING);
            in.close();

        } catch (Exception e) {
            e.getStackTrace();
        }

    }

    private String getContent(Document doc, String tagName) {
        Elements temporaryData = doc.select("meta[property$=" + tagName + "], meta[name$=" + tagName + "]");
        return temporaryData.first().attr("content");
    }

    private NewsByLinkResponse getBigData(NewsByLinkRequest request) {
        Document doc;
        String url = Optional.ofNullable(request.getUrl()).orElse(null);

        NewsByLinkResponse dataNews = new NewsByLinkResponse();

        //Парсинг страницы по ссылке
        try {
            doc = Jsoup.connect(url).get();
        } catch (Exception e) {
            throw new BadUrlException("Неверная ссылка");
        }

        if (doc != null) {
            //Парсинг OG тегов
            try {
                dataNews.setTitle(getContent(doc, "title"));
                dataNews.setDescription(getContent(doc, "description"));
                dataNews.setImage(getContent(doc, "image"));
                dataNews.setUrl(getContent(doc, "url"));
                dataNews.setSiteName(getContent(doc, "site_name"));
                dataNews.setDatePublication(ZonedDateTime.now());
            } catch (Exception exception) {
                throw new NoMarkupAvailableException("Разметка не найдена");
            }

        } else {
            throw new BadUrlException("Неверная ссылка");
        }

        Long[] idTags = Optional.ofNullable(request.getIdTags()).orElse(null);

        if (idTags != null) {
            Set<TagShortDto> selectedTags = new HashSet<>();
            for (long id : idTags) {
                tagShortRepository.findById(id).ifPresent(tagShort -> selectedTags.add(tagShortMapper.toDto(tagShort)));
            }
            dataNews.setSelectTags(selectedTags);
        }

        return dataNews;
    }
}
