package com.ss.newsportal.service.tag;

import com.ss.newsportal.dto.tag.TagAdminDto;
import com.ss.newsportal.dto.tag.TagShortDto;
import com.ss.newsportal.entity.Tag;
import com.ss.newsportal.exception.DeleteRelatedDataException;
import com.ss.newsportal.exception.NotUniqueValueException;
import com.ss.newsportal.mapper.tag.TagAdminMapper;
import com.ss.newsportal.mapper.tag.TagShortMapper;
import com.ss.newsportal.repository.TagRepository;
import com.ss.newsportal.repository.view.TagShortRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Service
public class TagServiceImpl implements TagService {

    @PersistenceContext
    private EntityManager entityManager;

    private final TagRepository tagRepository;
    private final TagShortRepository tagShortRepository;
    private final TagAdminMapper tagAdminMapper;
    private final TagShortMapper tagShortMapper;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository,
                          TagShortRepository tagShortRepository, TagAdminMapper tagMapper,
                          TagShortMapper tagShortMapper
    ) {
        this.tagRepository = tagRepository;
        this.tagShortRepository = tagShortRepository;
        this.tagAdminMapper = tagMapper;
        this.tagShortMapper = tagShortMapper;
    }

    @Override
    public List<TagAdminDto> getAll() {
        return tagAdminMapper.toDtoList(tagRepository.findAll());
    }

    @Override
    public void deleteTag(Long id) {
        Tag tag = notFoundTag(id);
        if (tag.getNews().size() > 0) {
            throw new DeleteRelatedDataException("Невозможно удалить тег, он присвоен одной из новостей.");
        }else {
            tagRepository.delete(tag);
        }
    }

    /**
     * Поиск тегов с параметрами
     * @param search текст в названии тега
     * @return список найденных тегов
     */
    @Override
    public List<TagAdminDto> getAllApplyFilter(String search) {

        List<Predicate> predicates = new ArrayList<>();

        CriteriaBuilder cBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tag> cQuery = cBuilder.createQuery(Tag.class);
        Root<Tag> tag = cQuery.from(Tag.class);

        if (!search.isBlank()) {
            predicates.add(cBuilder.like(
                    cBuilder.lower(tag.get("tagName")), "%" + search.toLowerCase() + "%")
            );
        }

        if (!predicates.isEmpty()) {
            cQuery.where(predicates.toArray(new Predicate[0]));
        }

        return tagAdminMapper.toDtoList(entityManager.createQuery(cQuery).getResultList());
    }

    @Override
    public void saveTag(TagAdminDto tagAdminDto) {
        try {
            tagRepository.save(tagAdminMapper.toEntity(tagAdminDto));
        }catch (DataIntegrityViolationException ex) {
            if (((SQLException) ex.getMostSpecificCause()).getSQLState().equals("23505")) {
                throw new NotUniqueValueException("Тег с таким наименованием уже существует в БД");
            }
        }

    }

    @Override
    public TagAdminDto getTagAdminDto(Long id) {
        return tagAdminMapper.toDto(notFoundTag(id));
    }

    @Override
    public List<TagShortDto> getShortTags() {
        return tagShortMapper.toDtoList(tagShortRepository.findAll());
    }

    private Tag notFoundTag(Long id) {
        return tagRepository.findById(id).orElseThrow(() -> new NotFoundException("Тег не найден."));
    }
}
