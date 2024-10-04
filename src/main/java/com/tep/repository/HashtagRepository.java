package com.tep.repository;

import com.tep.model.Hashtag;

import java.sql.SQLException;
import java.util.Optional;

public interface HashtagRepository{

    Hashtag insert(Hashtag hashtag) throws SQLException;

    Optional<Hashtag> getExistingHashTag(Hashtag hashtag);
}
