package com.ss.newsportal.service.news;

import com.ss.newsportal.dto.news.NewsByLinkRequest;
import com.ss.newsportal.dto.news.NewsByLinkResponse;


public interface NewsAdminService {
    NewsByLinkResponse parseLink(NewsByLinkRequest request);
    void saveNewsByLink(NewsByLinkRequest request);
}
