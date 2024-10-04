package com.tep.repository;

import com.tep.model.Slice;

import java.util.LinkedHashSet;
import java.util.UUID;
import java.util.Optional;

public interface SliceRepository extends Repository<Slice, UUID>
{

    Slice update(Slice slice);

    Optional<LinkedHashSet<Slice>> getByIds(LinkedHashSet<UUID> ids);
}
