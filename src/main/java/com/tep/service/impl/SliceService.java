package com.tep.service.impl;

import com.tep.model.Hashtag;
import com.tep.model.Slice;
import com.tep.model.Topic;
import com.tep.repository.impl.JDBC.SliceRepository;
import com.tep.service.Service;
import com.tep.util.HashtagUtil;
import com.tep.util.ScannerUtil;
import com.tep.util.Validation;

import java.util.Set;
import java.util.UUID;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.stream.Collectors;

public class SliceService implements Service {

    private static SliceService sliceService = null;

    SliceService() {
    }

    public static SliceService getInstance() {
        if (sliceService == null) {
            sliceService = new SliceService();
        }
        return sliceService;
    }

    final SliceRepository sliceRepository = new SliceRepository();
    final HashtagService hashtagService = HashtagService.getInstance();
    final Validation validation = new Validation();
    final int minLength = 0, maxLength = 200;

    @Override
    public void displayOption() {
        while (true) {
            try {
                System.out.println("\nEnter the operation you want to perform on Slice");
                System.out.println("Press 1 to Insert");
                System.out.println("Press 2 to Display with a particular ID ");
                System.out.println("Press 3 to Display All");
                System.out.println("Press 4 to Update");
                System.out.println("Press 5 to Exit");

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
                        update();
                        break;

                    case 5:
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
        System.out.println("\nEnter note :");
        String note = ScannerUtil.getInstance().nextLine();

        if (validation.notValidLength(note, minLength, maxLength)) {
            System.out.println("\nNote should not be empty or should not exceed 200 characters");
            return;
        }

        Set<String> hashtagNames = HashtagUtil.extractHashtags(note);
        Set<Hashtag> savedHashTags = hashtagNames.stream().map(h -> hashtagService.save(new Hashtag(h))).collect(Collectors.toSet());
        Slice slice = new Slice(UUID.randomUUID(), note, savedHashTags, null);
        sliceRepository.insert(slice);
        displayAll();
        recreateOption();
    }

    @Override
    public void display() {
        try {
            System.out.println("\nEnter ID you want to display : ");
            UUID id = UUID.fromString(ScannerUtil.getInstance().nextLine().trim());
            Optional<Slice> slice = sliceRepository.getById(id);
            if (slice.isEmpty()) {
                System.out.println("\nSlice not found !");
                return;
            }
            System.out.println(slice.get());
        } catch (IllegalArgumentException e) {
            System.out.println("\nInvalid ID");
        }
    }

    @Override
    public Optional displayAll() {
        Optional<List<Slice>> slices = sliceRepository.getAll();
        if (slices.isEmpty()) {
            System.out.println("\nNo Slice present");
            return Optional.empty();
        }
        System.out.println("\nAll Slices :");
        slices.ifPresent(slice -> slice.forEach(System.out::println));
        return slices;
    }

    @Override
    public void recreateOption() {
        while (true) {
            System.out.println("\nDo you want to create slice again. Press 'Y' for Yes and Press 'N' to exit");
            String select = ScannerUtil.getInstance().nextLine();
            if (select.equalsIgnoreCase("Y")) {
                save();
                break;
            } else if (select.equalsIgnoreCase("N")) {
                return;
            } else {
                System.out.println("\nPlease enter valid option");
            }
        }
    }

    public void update() {
        try {
            System.out.println("\nEnter ID you want to update : ");
            UUID id = UUID.fromString(ScannerUtil.getInstance().nextLine().trim());
            Optional<Slice> existingSlice = sliceRepository.getById(id);

            if(existingSlice.isEmpty()) {
                System.out.println("\nSlice not found !");
                return;
            }

            System.out.println("Update note : ");

            String note = ScannerUtil.getInstance().nextLine();

            if (validation.notValidLength(note, minLength, maxLength)) {
                System.out.println("\nNote should not be empty or should not exceed 200 characters");
                return;
            }

            Set<String> hashtagNames = HashtagUtil.extractHashtags(note);
            Set<Hashtag> savedHashTags = hashtagNames.stream().map(h -> hashtagService.save(new Hashtag(h))).collect(Collectors.toSet());
            Slice updatedSlice = new Slice(id, note, savedHashTags, associateTopic(savedHashTags));
            sliceRepository.update(updatedSlice);
            System.out.print("\nUpdated slice is : ");
            System.out.println(updatedSlice);

        } catch (IllegalArgumentException e) {
            System.out.println("\nInvalid ID");
        }
    }

    public Set<Topic> associateTopic(Set<Hashtag> hashTags) {
        return null;
    }

    public Optional<LinkedHashSet<Slice>> getSliceById(LinkedHashSet<UUID> id) {
        return sliceRepository.getByIds(id);
    }

    public Optional<Set<Slice>> getSlicesByHashtag(String hashtag) {
        return sliceRepository.getByHashtag(hashtag);
    }
}