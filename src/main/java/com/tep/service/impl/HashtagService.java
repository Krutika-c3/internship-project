package com.tep.service.impl;

import com.tep.model.Hashtag;
import com.tep.repository.impl.JDBC.HashtagRepository;

import java.util.Optional;
public class HashtagService {

    private static HashtagService hashtagService = new HashtagService();
    HashtagRepository hashtagRepository=new HashtagRepository();

    private HashtagService() {
    }

    public static HashtagService getInstance() {
        if (hashtagService == null) {
            hashtagService = new HashtagService();
        }
        return hashtagService;
    }
    public Hashtag save(Hashtag hashtag)
    {
        final Optional<Hashtag> existingHashTag = getExistingHash(hashtag);
        return existingHashTag.orElseGet(() -> hashtagRepository.insert(hashtag));
    }

   private Optional<Hashtag> getExistingHash(Hashtag hashtag) {
        return hashtagRepository.getExistingHashTag(hashtag);
    }
}
