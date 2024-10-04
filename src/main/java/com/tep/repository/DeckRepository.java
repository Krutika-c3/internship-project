package com.tep.repository;

import com.tep.model.Deck;

import java.util.UUID;

public interface DeckRepository extends Repository<Deck, UUID>
{
    boolean isTitleExists(String title);
}
