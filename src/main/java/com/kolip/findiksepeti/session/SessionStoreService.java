package com.kolip.findiksepeti.session;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public interface SessionStoreService<T> {

    void store(Long key, T value);

    T getValue(Long key);

    void delete(Long key);

    List<T> getAll();

    void deleteAll();
}
