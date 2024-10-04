package com.tep.repository.impl.JDBC;

import com.tep.model.Deck;
import com.tep.model.Slice;
import com.tep.model.Topic;
import com.tep.util.ConnectionProvider;

import java.util.Optional;
import java.util.UUID;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class DeckRepository implements com.tep.repository.DeckRepository {

    final Connection connection = ConnectionProvider.Connector(true);
    final TopicRepository topicRepository = new TopicRepository();
    final SliceRepository sliceRepository = new SliceRepository();

    String id = null, title = null, description = null, topicId, sliceId, mapDeckId;
    LinkedHashSet<UUID> sliceIds = new LinkedHashSet<>();
    Optional<LinkedHashSet<Slice>> slices = Optional.empty();

    @Override
    public Optional<Deck> insert(Deck deck) {
        AtomicInteger sortOrder = new AtomicInteger();
        int insert = 0;
        AtomicInteger insertIntoMap = new AtomicInteger();
        final String query = "INSERT INTO Deck (id, title, description,topicId) VALUES (?,?,?,?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, deck.getId().toString());
            preparedStatement.setString(2, deck.getTitle());
            preparedStatement.setString(3, deck.getDescription());
            preparedStatement.setString(4, deck.getTopic().getId().toString());
            insert = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Deck not created");
        }

        deck.getSlices().forEach(slice -> {
            sortOrder.addAndGet(1);
            final String mapQuery = "INSERT INTO DeckSliceMap VALUES (?,?,?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(mapQuery)) {
                preparedStatement.setString(1, deck.getId().toString());
                preparedStatement.setString(2, slice.getId().toString());
                preparedStatement.setInt(3, sortOrder.get());
                insertIntoMap.set(preparedStatement.executeUpdate());
            } catch (SQLException e) {
                System.out.println("Deck not associated");
            }
        });
        if (insert == 1 && insertIntoMap.get() == 1) {
            return Optional.of(deck);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Deck> getById(UUID uuid) {
        final String query = "SELECT id,title,description,topicId,sliceId FROM Deck " +
                "INNER JOIN DeckSliceMap AS map ON Deck.id = map.deckId " +
                "WHERE map.deckId = ? ORDER BY deckId, sort ASC";

        Topic topic = null;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                id = resultSet.getString(1);
                title = resultSet.getString(2);
                description = resultSet.getString(3);
                topicId = resultSet.getString(4);
                topic = topicRepository.getById(UUID.fromString(topicId)).stream().findFirst().orElse(null);
                sliceId = resultSet.getString(5);
                sliceIds.add(UUID.fromString(sliceId));
                if (sliceRepository.getByIds(sliceIds).isPresent()) {
                    slices = sliceRepository.getByIds(sliceIds);
                } else {
                    slices = Optional.empty();
                }
            }
        } catch (SQLException e) {
            System.out.println("\nDeck not found !!");
        }
        if (id == null || title == null || description == null || topic == null || slices.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new Deck(UUID.fromString(id), title, description, topic, slices.get()));
    }

    @Override
    public Optional<List<Deck>> getAll() {
        Map<String, Deck> decks = new HashMap<>();
        List<String> deckIds = new ArrayList<>();

        final String query = "SELECT * from Deck";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            Topic topic = null;
            while (resultSet.next()) {
                id = resultSet.getString(1);
                deckIds.add(id);
                title = resultSet.getString(2);
                description = resultSet.getString(3);
                topicId = resultSet.getString(4);
                topic = topicRepository.getById(UUID.fromString(topicId)).stream().findFirst().orElse(null);
                Deck deck = new Deck(UUID.fromString(id), title, description, topic);
                decks.put(id, deck);
            }
            if (id == null || title == null || description == null || topic == null) {
                return Optional.empty();
            }

            Set<String> ids = decks.keySet();

            final String mapQuery = "SELECT * FROM DeckSliceMap WHERE deckId IN (";
            StringBuilder queryBuilder = new StringBuilder(mapQuery);
            for (int i = 0; i < ids.size(); i++) {
                queryBuilder.append(" ?");
                if (i != ids.size() - 1)
                    queryBuilder.append(",");
            }
            queryBuilder.append(") ORDER BY deckId, sort ASC");

            PreparedStatement preparedStatement = connection.prepareStatement(String.valueOf(queryBuilder));

            AtomicInteger parameterIndex = new AtomicInteger(1);
            ids.forEach(i -> {
                try {
                    preparedStatement.setString(parameterIndex.get(), i);
                    parameterIndex.getAndIncrement();
                } catch (SQLException e) {
                    System.out.println("\nUnable to display Deck");
                }
            });

            ResultSet mapResultSet = preparedStatement.executeQuery();
            final Map<String, LinkedHashSet<String>> mapDeckIdsToSliceIds = new HashMap<>();
            while (mapResultSet.next()) {
                mapDeckId = mapResultSet.getString(1);
                sliceId = mapResultSet.getString(2);
                if (mapDeckIdsToSliceIds.containsKey(mapDeckId)) {
                    mapDeckIdsToSliceIds.get(mapDeckId).add(sliceId);
                } else {
                    LinkedHashSet<String> sliceIds = new LinkedHashSet<>();
                    sliceIds.add(sliceId);
                    mapDeckIdsToSliceIds.put(mapDeckId, sliceIds);
                }
            }

            decks.forEach((key, deck) -> {
                final LinkedHashSet<String> sliceIds = mapDeckIdsToSliceIds.get(key);
                final LinkedHashSet<UUID> uuidSliceIds = sliceIds.stream().map(UUID::fromString).collect(Collectors.toCollection(LinkedHashSet::new));
                Optional<LinkedHashSet<Slice>> slices = sliceRepository.getByIds(uuidSliceIds);
                slices.ifPresent(deck::setSlices);
            });

            List<Deck> Decks = new ArrayList<>(decks.values());
            return Optional.of(Decks);
        } catch (SQLException e) {
            System.out.println("\nUnable to display Deck");
        }
        return Optional.empty();
    }

    @Override
    public boolean isTitleExists(String title) {
        final String query = "SELECT 1 FROM Deck WHERE Title = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, title);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.out.println("Unable to check title");
        }
        return false;
    }
}