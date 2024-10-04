package com.tep.model;

import java.util.UUID;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

public class Deck {
    private UUID id;
    private String description;
    private String title;
    private Topic topic;
    private LinkedHashSet<Slice> slices;

    public Deck(UUID id, String title ,String description, Topic topic, LinkedHashSet<Slice> slices) {
        this.id = id;
        this.description = description;
        this.title = title;
        this.topic = topic;
        this.slices = slices;
    }

    public Deck(UUID id, String title, String description, Topic topic) {
        this.id = id;
        this.description = description;
        this.title = title;
        this.topic = topic;
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

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public LinkedHashSet<Slice> getSlices() {
        return slices;
    }

    public void setSlices(LinkedHashSet<Slice> slices) {
        this.slices = slices;
    }

    @Override
    public String toString() {
        return "\nId : " + id +
                "\nTitle : " + title +
                "\nDescription : " + description +
                "\nTopic : " + topic.getTitle() +
                "\nSlice/s : " + slices.stream().map(Slice::getId).collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
