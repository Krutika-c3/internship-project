package com.tep.service.impl;

import com.tep.model.Slice;
import com.tep.model.Topic;
import com.tep.service.Service;
import com.tep.util.ScannerUtil;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

public class SearchService implements Service {

    final TopicService topicService = TopicService.getInstance();
    final HashtagService hashtagService = HashtagService.getInstance();
    final SliceService sliceService = SliceService.getInstance();
    private static SearchService searchService = null;

    private SearchService() {
    }

    public static SearchService getInstance() {
        if (searchService == null) {
            searchService = new SearchService();
        }
        return searchService;
    }

    public void displayOption() {

        String sanitisedHashtag = "";
        System.out.println("\nEnter the hashtag you want to get the Topic for : ");

        String hashtagString = ScannerUtil.getInstance().nextLine();
        String hashtag = hashtagString.trim().toLowerCase();
        if (hashtag.charAt(0) == '#') {
            hashtag = hashtag.substring(1);
        }

        Optional<Set<Topic>> topics = searchTopic(hashtag);
        Optional<Set<Slice>> slices = searchSlice(hashtag);

        if (topics.get().isEmpty()) {
            System.out.println("\nNo topic/s associated with this hashtag");
        } else {
            System.out.println("\nThese are the associated topic/s : ");
            topics.get().stream().forEach(System.out::println);
        }

        if (slices.get().isEmpty()) {
            System.out.println("\nNo slice/s associated with this hashtag");
        } else {
            System.out.println("\nThese are the associated slice/s : ");

            for (Slice slice : slices.get()) {
                System.out.println("\nId : " + slice.getId() +
                        "\nNote : " + slice.getNote());
            }
        }
    }

    @Override
    public void save() throws SQLException {

    }

    @Override
    public void display() {

    }

    @Override
    public Optional displayAll() {
        return Optional.empty();
    }

    @Override
    public void recreateOption() {

    }

    public Optional<Set<Topic>> searchTopic(String hashtag) {
        return topicService.getTopicsByHashtag(hashtag);
    }

    public Optional<Set<Slice>> searchSlice(String hashtag) {
        return sliceService.getSlicesByHashtag(hashtag);
    }
}
