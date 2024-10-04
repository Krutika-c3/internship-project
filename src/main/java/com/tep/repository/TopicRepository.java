package com.tep.repository;

import com.tep.model.Hashtag;
import com.tep.model.Topic;

import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;

public interface TopicRepository extends Repository<Topic, UUID>
{
    Set<Topic> getByHashtag(Set<Hashtag> hashtags);

    boolean isTitleExists(String title) throws SQLException;
}
