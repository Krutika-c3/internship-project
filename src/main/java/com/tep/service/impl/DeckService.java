package com.tep.service.impl;

import com.tep.model.Deck;
import com.tep.model.Topic;
import com.tep.model.Slice;
import com.tep.repository.impl.JDBC.DeckRepository;
import com.tep.service.Service;
import com.tep.util.Validation;
import com.tep.util.ScannerUtil;

import java.util.Optional;
import java.util.UUID;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

public class DeckService implements Service {

    private static DeckService deckService = null;

    private DeckService() {
    }

    public static DeckService getInstance() {
        if (deckService == null) {
            deckService = new DeckService();
        }
        return deckService;
    }

    final DeckRepository deckRepository = new DeckRepository();
    final TopicService topicService = TopicService.getInstance();
    final SliceService sliceService = SliceService.getInstance();

    public void displayOption() {
        while (true) {
            try {
                System.out.println("\nEnter the operation you want to perform on Deck");
                System.out.println("Press 1 to Insert");
                System.out.println("Press 2 to Display with a particular ID ");
                System.out.println("Press 3 to Display All");
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
                        System.out.println("\nPlease select from the above options");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("\nPlease select from the above options");
            }
        }
    }

    @Override
    public void save() {
        System.out.println("\nEnter Title : ");
        String title = ScannerUtil.getInstance().nextLine();
        Validation validation = new Validation();
        final int minLength = 0, maxTitleLength = 100, maxDescriptionLength = 1000;

        if (validation.notValidLength(title, minLength, maxTitleLength)) {
            System.out.println("Title should not be empty or should not exceed 100 characters");
            return;
        }

        if (deckRepository.isTitleExists(title)) {
            System.out.println("\nDeck already exist !!!");
            return;
        }

        System.out.println("Enter Description :");
        String description = ScannerUtil.getInstance().nextLine();

        if (validation.notValidLength(description, minLength, maxDescriptionLength)) {
            System.out.println("Description should not be empty or should not exceed 1000 characters");
            return;
        }

        if (topicService.displayAll().isEmpty()) {
            return;
        }

        try {
            System.out.println("\nEnter Topic Id you want to add :");
            UUID topicId = UUID.fromString(ScannerUtil.getInstance().nextLine().trim());
            Optional<Topic> topic = getTopicId(topicId);

            if (topic.isEmpty()) {
                System.out.println("\nTopic not found !!");
                return;
            }

            if (sliceService.displayAll().isEmpty()) {
                return;
            }

            System.out.println("\nEnter Slice Id and separate it by ',' (Comma)  :");

            String sliceId = ScannerUtil.getInstance().nextLine().trim();

            String[] ids = sliceId.split(",", 0);
            LinkedHashSet<UUID> uuids = Arrays.stream(ids).map(UUID::fromString).collect(Collectors.toCollection(LinkedHashSet::new));
            Optional<LinkedHashSet<Slice>> slices = getSliceId(uuids);

            if (slices.get().isEmpty()) {
                System.out.println("\nSlice not found !!");
                return;
            }
            Deck deck = new Deck(UUID.randomUUID(), title, description, topic.get(), slices.get());
            deckRepository.insert(deck);
            displayAll();
            recreateOption();
        } catch (IllegalArgumentException e) {
            System.out.println("\nEnter valid Id from next time !!!");
        }
    }

    @Override
    public void display() {
        try {
            System.out.println("\nEnter Deck ID you want to display : ");
            UUID id = UUID.fromString(ScannerUtil.getInstance().nextLine().trim());
            Optional<Deck> deck = deckRepository.getById(id);
            if (deck.isEmpty()) {
                System.out.println("\nDeck not found !!");
                return;
            }
            System.out.println(deck.get());
        } catch (IllegalArgumentException e) {
            System.out.println("\nInvalid ID");
        }
    }

    @Override
    public Optional displayAll() {
        Optional<List<Deck>> decks = deckRepository.getAll();
        if (decks.isEmpty()) {
            System.out.println("\nNo Deck present");
            return Optional.empty();
        }
        System.out.println("\nAll Decks :");
        decks.ifPresent(deck -> deck.forEach(System.out::println));
        return decks;
    }

    @Override
    public void recreateOption() {
        while (true) {
            System.out.println("\nDo you want to create Deck again. Press 'Y' for Yes and Press 'N' to exit");
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

    public Optional<Topic> getTopicId(UUID id) {
        return topicService.getTopicById(id);
    }

    public Optional<LinkedHashSet<Slice>> getSliceId(LinkedHashSet<UUID> id) {
        if (sliceService.getSliceById(id).isPresent()) {
            return sliceService.getSliceById(id);
        } else {
            return Optional.empty();
        }
    }
}