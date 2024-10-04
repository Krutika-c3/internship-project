package com.tep.repository.impl.InMemory;

import com.tep.model.Slice;

import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Objects;
import java.util.stream.Collectors;

public class SliceRepository implements com.tep.repository.SliceRepository {

    Map<UUID, Slice> slices = new HashMap<>();

    @Override
    public Optional<Slice> insert(Slice slice) {
        return Optional.ofNullable(slices.put(slice.getId(), slice));
    }

    @Override
    public Optional<Slice> getById(UUID id) {
        return Optional.of(slices.get(id));
    }

    @Override
    public Optional<List<Slice>> getAll() {
        return Optional.of(new ArrayList<>(slices.values()));
    }

    public Slice update(Slice slice) {
        return slices.put(slice.getId(), slice);
    }

    public Optional<LinkedHashSet<Slice>> getByIds(LinkedHashSet<UUID> ids) {
        LinkedHashSet<Slice> sliceList = ids.stream().map(id -> slices.get(id)).filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new));
        return Optional.of(sliceList);
    }
}
