package com.tep.model;

import java.util.UUID;
import java.util.Set;
import java.util.Objects;
import java.util.Collections;
import java.util.stream.Collectors;

public class Slice {
    private UUID id;
    private String note;
    private Set<Hashtag> hashtags;
    private Set<Topic> topics;

    public Slice(UUID id, String note, Set<Hashtag> hashtags, Set<Topic> topics) {
        this.id = id;
        this.note = note;
        this.hashtags = hashtags;
        this.topics = topics;
    }

    public Slice(UUID id, String note) {
        this.id = id;
        this.note = note;
    }

    public Slice() {

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Set<Hashtag> getHashtags() {
        return hashtags;
    }

    public void setHashtags(Set<Hashtag> hashtags) {
        this.hashtags = hashtags;
    }

    public Set<Topic> getTopics() {
        return topics;
    }

    public void setTopics(Set<Topic> topics) {
        this.topics = topics;
    }

    @Override
    public String toString() {
        Set<String> displayTopics = topics != null ? topics.stream().filter(Objects::nonNull).map(Topic::getTitle).collect(Collectors.toSet()) : Collections.EMPTY_SET;
        return "\nId : " + id + "\n" +
                "Note : " + note + "\n" +
                "Topic/s : " + displayTopics;
    }
}