package com.ss.newsportal.mapper;

import java.util.List;


public interface ParentMapper<D, E> {
    D toDto(E e);
    E toEntity(D d);
    List<D> toDtoList(List<E> eList);
}
