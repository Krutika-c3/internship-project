package com.tep.model;

import java.util.Set;
import java.util.UUID;

public class Topic {
    private UUID id;
    private String title;
    private String description;
    private Set<Hashtag> hashtags;

    public Topic(UUID id, String title, String description, Set<Hashtag> hashtags) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.hashtags = hashtags;
    }

    public Topic() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Hashtag> getHashtags() {
        return hashtags;
    }

    public void setHashtags(Set<Hashtag> hashtags) {
        this.hashtags = hashtags;
    }

    @Override
    public String toString() {
        return "\nId : " + id +
                "\nTitle : " + title +
                "\nDescription : " + description ;
              }
}
