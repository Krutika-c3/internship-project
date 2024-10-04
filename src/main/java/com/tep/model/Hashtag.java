package com.tep.model;

import java.util.Objects;

public class Hashtag {

    private String name;

    public Hashtag(final String name) {
        this.name = name;
    }

    public Hashtag() {

    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Hashtag " + "\n" +
                "Name : " + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hashtag hashtag = (Hashtag) o;
        return name.equals(hashtag.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
