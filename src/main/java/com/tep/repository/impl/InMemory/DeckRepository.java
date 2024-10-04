package com.tep.repository.impl.InMemory;

import com.tep.model.Deck;

import java.util.Optional;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class DeckRepository implements com.tep.repository.DeckRepository {
    Map<UUID, Deck> decks = new HashMap<>();

    @Override
    public Optional<Deck> insert(Deck deck) {
        return Optional.ofNullable(decks.put(deck.getId(), deck));
    }

    @Override
    public Optional<Deck> getById(UUID id) {
        return Optional.of(decks.get(id));
    }

    @Override
    public Optional<List<Deck>> getAll() {
        return Optional.of(new ArrayList<>(decks.values()));
    }

    public boolean isTitleExists(String title) {
        List<Deck> decks = getAll().get();
        for (Deck checkDeck : decks) {
            if (title.equals(checkDeck.getTitle())) {
                return true;
            }
        }
        return false;
    }
}
