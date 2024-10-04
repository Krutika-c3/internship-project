package com.tep.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T, ID> {

    Optional<T> insert(T object);

    Optional<T> getById(ID id);

    Optional<List<T>> getAll();
}
