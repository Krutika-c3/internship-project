package com.tep.repository.impl.InMemory;

import com.tep.model.Hashtag;

import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

public class HashtagRepository implements com.tep.repository.HashtagRepository{

   Set<Hashtag> hashtags = new HashSet<>();

    public Hashtag insert(Hashtag hashtag) {
        hashtags.add(hashtag);
        return hashtag;
    }

    public Optional<Hashtag> getExistingHashTag(Hashtag hashtag) {
        return hashtags.stream().filter(hashTag -> hashtag.equals(hashtag)).findFirst();
    }
}