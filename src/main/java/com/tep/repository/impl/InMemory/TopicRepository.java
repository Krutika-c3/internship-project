package com.tep.repository.impl.InMemory;

import com.tep.model.Hashtag;
import com.tep.model.Topic;

import java.util.UUID;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import java.util.stream.Collectors;

public class TopicRepository implements com.tep.repository.TopicRepository {

    Map<UUID, Topic> topics = new HashMap<>();

    @Override
    public Optional<Topic> insert(Topic topic) {
        return Optional.ofNullable(topics.put(topic.getId(), topic));
    }

    @Override
    public Optional<Topic> getById(UUID id) {
        return Optional.of(topics.get(id));
    }

    @Override
    public Optional<List<Topic>> getAll() {
        return Optional.of(new ArrayList<>(topics.values()));
    }

    public Set<Topic> getByHashtag(Set<Hashtag> hashtags) {
        return hashtags.stream().flatMap(hashtag ->
                topics.values().stream().filter(t -> t.getHashtags().contains(hashtag))
        ).collect(Collectors.toSet());
    }

    public boolean isTitleExists(String title) {
        List<Topic> topics = getAll().get();
        for (Topic checkTopic : topics) {
            if (title.equals(checkTopic.getTitle())) {
                return true;
            }
        }
        return false;
    }
}