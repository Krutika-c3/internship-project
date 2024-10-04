package com.tep.factory;

import java.util.Optional;

import com.tep.service.Service;
import com.tep.service.impl.DeckService;
import com.tep.service.impl.SliceService;
import com.tep.service.impl.TopicService;
import com.tep.service.impl.SearchService;

public class ServiceFactory {
    public Optional<Service> getInstance(Integer i) {
        switch (i) {
            case 1:
                return Optional.of(TopicService.getInstance());
            case 2:
                return Optional.of(SliceService.getInstance());
            case 3:
                return Optional.of(DeckService.getInstance());
            case 4:
                return Optional.of(SearchService.getInstance());
            default:
                System.out.println("\nOOPS!!! You selected the wrong option");
                return Optional.empty();
        }
    }
}
