package com.tep.service.impl;

import com.tep.model.Hashtag;
import com.tep.util.HashtagUtil;
import com.tep.util.Validation;
import com.tep.model.Topic;
import com.tep.repository.impl.JDBC.TopicRepository;
import com.tep.service.Service;
import com.tep.util.ScannerUtil;

import java.util.Set;
import java.util.List;
import java.util.UUID;
import java.util.Optional;
import java.util.HashSet;

public class TopicService implements Service {

    private static TopicService topicService = null;

    private TopicService() {
    }

    public static TopicService getInstance() {
        if (topicService == null) {
            topicService = new TopicService();
        }
        return topicService;
    }

    final TopicRepository topicRepository = new TopicRepository();
    final Validation validation = new Validation();

    @Override
    public void displayOption() {
        while (true) {
            try {
                System.out.println("\nEnter the operation you want to perform on Topic");
                System.out.println("Press 1 to Insert");
                System.out.println("Press 2 to Display with a particular ID");
                System.out.println("Press 3 to Display all topics");
                System.out.println("Press 4 to Exit");
                int choice = Integer.parseInt(ScannerUtil.getInstance().nextLine());

                switch (choice) {
                    case 1:
                        save();
                        break;

                    case 2:
                        display();
                        break;
                    case 3:
                        displayAll();
                        break;
                    case 4:
                        return;

                    default: {
                        System.out.println("Please select from the above option");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Please select from the above option");
            }
        }
    }

    @Override
    public void save() {
        System.out.println("\nEnter Title: ");
        String title = ScannerUtil.getInstance().nextLine();
        final int minLength = 0, maxTitleLength = 100, maxDescriptionLength = 1000;

        if (validation.notValidLength(title, minLength, maxTitleLength)) {
            System.out.println("Title should not be empty or should not exceed 100 characters");
            return;
        }
        if (topicRepository.isTitleExists(title)) {
            System.out.println("\nTopic already exist !!!");
            return;
        }
        System.out.println("Enter Description: ");
        String description = ScannerUtil.getInstance().nextLine();
        if (validation.notValidLength(description, minLength, maxDescriptionLength)) {
            System.out.println("Description should not be empty or should not exceed 100 characters");
            return;
        }

        Set<String> hashtagNames = HashtagUtil.extractHashtags(description);

        Set<Hashtag> hashTags = new HashSet<>();
        HashtagService hashtagService = HashtagService.getInstance();

        for (String hashTagString : hashtagNames) {
            final Hashtag hashtag = new Hashtag(hashTagString);
            final Hashtag savedHashTag = hashtagService.save(hashtag);
            hashTags.add(savedHashTag);
        }

        Topic topic = new Topic(UUID.randomUUID(), title, description, hashTags);
        topicRepository.insert(topic);
        displayAll();
        recreateOption();
    }

    @Override
    public void display() {
        try {
            System.out.println("\nEnter ID you want to display: ");
            UUID id = UUID.fromString(ScannerUtil.getInstance().nextLine().trim());
            Optional<Topic> topic = topicRepository.getById(id);
            if (topic.isEmpty()) {
                System.out.println("\nTopic not found !");
                return;
            }
            System.out.println(topic.get());
        } catch (IllegalArgumentException e) {
            System.out.println("\nInvalid ID");
        }
    }

    @Override
    public Optional displayAll() {
        Optional<List<Topic>> topics = topicRepository.getAll();
        if (topics.get().isEmpty()) {
            System.out.println("\nNo Topic present");
            return Optional.empty();
        }
        System.out.println("\nAll Topics :");
        topics.ifPresent(topicList -> topicList.forEach(System.out::println));
        return topics;
    }

    @Override
    public void recreateOption() {
        while (true) {
            System.out.println("\nDo you want to create topic again. Press 'Y' for Yes and Press 'N' to exit");
            String select = ScannerUtil.getInstance().nextLine();
            if (select.equalsIgnoreCase("Y")) {
                save();
                break;
            } else if (select.equalsIgnoreCase("N")) {
                return;
            } else {
                System.out.println("Please enter valid option");
            }
        }
    }

    public Set<Topic> getTopicsByHashtag(Set<Hashtag> hashTags) {
        return topicRepository.getByHashtag(hashTags);
    }

    public Optional<Set<Topic>> getTopicsByHashtag(String hashtag) {
        return topicRepository.getByHashtag(hashtag);
    }

    public Optional<Topic> getTopicById(UUID id) {
        return topicRepository.getById(id);
    }

    public Optional<Set<Topic>> getTopicById(Set<UUID> id) {
        return topicRepository.getByIds(id);
    }
}