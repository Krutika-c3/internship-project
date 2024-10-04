package com.tep.repository.impl.JDBC;

import com.tep.model.Hashtag;
import com.tep.model.Slice;
import com.tep.model.Topic;
import com.tep.util.ConnectionProvider;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SliceRepository implements com.tep.repository.SliceRepository {
    final Connection connection = ConnectionProvider.Connector(true);
    final HashtagRepository hashtagRepository = new HashtagRepository();
    final TopicRepository topicRepository = new TopicRepository();
    final Map<String, Set<String>> mapSliceIdsToTopicIds = new HashMap<>();

    @Override
    public Optional<Slice> insert(Slice slice) {
        int insert = 0;
        final String query = "insert into Slice values (?,?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, slice.getId().toString());
            preparedStatement.setString(2, slice.getNote());
            insert = preparedStatement.executeUpdate();
            insertHashtag(slice);
            mapInsert(slice);
            associateTopicWithSlice(slice);
        } catch (SQLException e) {
            System.out.println("Slice not created");
        }
        if (insert == 1) {
            return Optional.of(slice);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Slice> getById(UUID uuid) {
        final String query = "SELECT * from Slice WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            String id = null, note = null, topicId, mapSliceId;
            while (resultSet.next()) {
                id = resultSet.getString(1);
                note = resultSet.getString(2);
            }

            if (id == null || note == null) {
                return Optional.empty();
            }

            Slice slice = new Slice();
            slice.setId(UUID.fromString(id));
            slice.setNote(note);

            final String mapQuery = "SELECT * FROM SliceTopicMap WHERE sliceId = ?";
            PreparedStatement mapPreparedStatement = connection.prepareStatement(mapQuery);
            mapPreparedStatement.setString(1, slice.getId().toString());
            ResultSet mapResultSet = mapPreparedStatement.executeQuery();

            Set<String> topicIds = new HashSet<>();

            while (mapResultSet.next()) {
                mapSliceId = mapResultSet.getString(1);
                topicId = mapResultSet.getString(2);
                if (mapSliceIdsToTopicIds.containsKey(mapSliceId)) {
                    mapSliceIdsToTopicIds.get(mapSliceId).add(topicId);
                } else {
                    topicIds.add(topicId);
                    mapSliceIdsToTopicIds.put(mapSliceId, topicIds);
                }
            }
            Optional<Set<Topic>> topic;
            final Set<String> topicIDs = mapSliceIdsToTopicIds.get(slice.getId().toString());
            if (topicIDs != null) {
                final Set<UUID> uuidTopicIds = topicIDs.stream().map(UUID::fromString).collect(Collectors.toSet());
                topic = topicRepository.getByIds(uuidTopicIds);
                topic.ifPresent(slice::setTopics);
            }
            return Optional.of(slice);
        } catch (SQLException e) {
            System.out.println("\nSlice Not Found !!");
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<Slice>> getAll() {
        Map<String, Slice> slices = new HashMap<>();
        List<String> sliceIds = new ArrayList<>();
        String query = "SELECT * from Slice";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            String id = null, note = null, topicId, mapSliceId;

            while (resultSet.next()) {
                id = resultSet.getString(1);
                sliceIds.add(id);
                note = resultSet.getString(2);
                Slice slice = new Slice(UUID.fromString(id), note);
                slices.put(id, slice);
            }

            if (id == null || note == null) {
                return Optional.empty();
            }

            Set<String> sliceId = slices.keySet();

            final String mapQuery = "SELECT * FROM SliceTopicMap WHERE sliceId IN (";
            StringBuilder queryBuilder = new StringBuilder(mapQuery);
            for (int i = 0; i < sliceId.size(); i++) {
                queryBuilder.append(" ?");
                if (i != sliceId.size() - 1)
                    queryBuilder.append(",");
            }
            queryBuilder.append(")");
            PreparedStatement mapPreparedStatement = connection.prepareStatement(String.valueOf(queryBuilder));

            ResultSet mapResultSet;
            AtomicInteger parameterIndex = new AtomicInteger(1);
            sliceId.forEach(i -> {
                try {
                    mapPreparedStatement.setString(parameterIndex.get(), i);
                    parameterIndex.getAndIncrement();
                } catch (SQLException e) {
                    System.out.println("Failed to Display All Slice");
                }
            });

            mapResultSet = mapPreparedStatement.executeQuery();

            final Map<String, Set<String>> mapSliceIdsToTopicIds = new HashMap<>();
            while (mapResultSet.next()) {
                mapSliceId = mapResultSet.getString(1);
                topicId = mapResultSet.getString(2);
                if (mapSliceIdsToTopicIds.containsKey(mapSliceId)) {
                    mapSliceIdsToTopicIds.get(mapSliceId).add(topicId);
                } else {
                    Set<String> topicIds = new HashSet<>();
                    topicIds.add(topicId);
                    mapSliceIdsToTopicIds.put(mapSliceId, topicIds);
                }
            }
            slices.forEach((key, slice) -> {
                final Set<String> topicIDs = mapSliceIdsToTopicIds.get(key);
                if (topicIDs != null) {
                    final Set<UUID> uuidTopicIds = topicIDs.stream().map(UUID::fromString).collect(Collectors.toSet());
                    Optional<Set<Topic>> topics = topicRepository.getByIds(uuidTopicIds);
                    topics.ifPresent(slice::setTopics);
                }
            });
        } catch (SQLException e) {
            System.out.println("Failed to Display All Slice");
        }
        List<Slice> sliceValues = new ArrayList<>(slices.values());
        return Optional.of(sliceValues);
    }

    @Override
    public Slice update(Slice slice) {
        final String query = "UPDATE Slice SET note = ? WHERE id = ?";
        UUID uuid = slice.getId();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.setString(1, slice.getNote());
            preparedStatement.executeUpdate();

            mapDelete(slice);
            insertHashtag(slice);
            mapInsert(slice);
            deleteTopicWithSlice(slice);
            associateTopicWithSlice(slice);
        } catch (SQLException e) {
            System.out.println("Failed to update Slice");
        }
        return slice;
    }

    @Override
    public Optional<LinkedHashSet<Slice>> getByIds(LinkedHashSet<UUID> ids) {
        Optional<LinkedHashSet<Slice>> sliceIds = Optional.of(new LinkedHashSet<>());
        ids.forEach(id -> {
            Optional<Slice> slice = getById(id);
            if (slice.isPresent()) {
                sliceIds.get().add(slice.get());
            } else {
                Optional.empty();
            }
        });
        if (sliceIds.isEmpty()) {
            return Optional.empty();
        }
        return sliceIds;
    }

    public void mapDelete(Slice slice) {
        final String query = "DELETE FROM SliceHashtagMap WHERE sliceId=?";
        UUID uuid = slice.getId();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to delete Slice");
        }
    }

    public void insertHashtag(Slice slice) {
        slice.getHashtags().forEach(hashtagRepository::insert);
    }

    public void mapInsert(Slice slice) {
        slice.getHashtags().forEach(hashtag -> {
            final String mapQuery = "insert into SliceHashtagMap values (?,?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(mapQuery)) {
                preparedStatement.setString(1, slice.getId().toString());
                preparedStatement.setString(2, hashtag.getName());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Slice not associated with hashtag");
            }
        });
    }

    public Slice associateTopicWithSlice(Slice slice) {
        final String associate = "INSERT IGNORE INTO SliceTopicMap SELECT SliceHashtagMap.sliceId, TopicHashtagMap.topicId\n" +
                "FROM SliceHashtagMap\n" +
                "INNER JOIN TopicHashtagMap ON SliceHashtagMap.hashtagName = TopicHashtagMap.hashtagName;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(associate)) {
            preparedStatement.executeUpdate();

            final String getTopic = "SELECT * FROM SliceTopicMap WHERE sliceId = ?";
            String topicId, sliceId;
            PreparedStatement mapPrepareStatement = connection.prepareStatement(getTopic);
            mapPrepareStatement.setString(1, slice.getId().toString());
            ResultSet resultset = mapPrepareStatement.executeQuery();

            Set<String> topicIds = new HashSet<>();
            final Map<String, Set<String>> associateSliceIdsToTopicIds = new HashMap<>();
            while (resultset.next()) {
                sliceId = resultset.getString(1);
                topicId = resultset.getString(2);
                if (associateSliceIdsToTopicIds.containsKey(sliceId)) {
                    associateSliceIdsToTopicIds.get(sliceId).add(topicId);
                } else {
                    topicIds.add(topicId);
                    associateSliceIdsToTopicIds.put(sliceId, topicIds);
                }
            }

            Set<Topic> topics = new HashSet<>();
            topicIds.forEach(id -> topics.add(topicRepository.getById(UUID.fromString(id)).get()));
            slice.setTopics(topics);
        } catch (SQLException e) {
            System.out.println("Slice not associated with Topic");
        }
        return slice;
    }

    public void deleteTopicWithSlice(Slice slice) {
        final String deleteQuery = "DELETE FROM SliceTopicMap WHERE sliceId=?";
        UUID deleteId = slice.getId();
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setString(1, deleteId.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to delete Slice Topic association");
        }
    }

    public Optional<Set<Slice>> getByHashtag(String hashtag) {
        Map<String, Slice> slicesFromDB = new HashMap<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT distinct s.id, s.note FROM SliceHashtagMap shm\n" +
                    "INNER JOIN Slice s ON shm.sliceId = s.id \n" +
                    "INNER JOIN SliceTopicMap stm ON s.id = stm.sliceId\n" +
                    "WHERE shm.hashtagName = ?");
            preparedStatement.setString(1, hashtag);
            final ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                slicesFromDB.put(rs.getString(1),
                        new Slice(
                                UUID.fromString(rs.getString(1)),
                                rs.getString(2),
                                new HashSet<>(),new HashSet<>())
                );
            }
            setSliceHashtags(slicesFromDB);
            return Optional.of(new HashSet<>(slicesFromDB.values()));
        } catch (SQLException e) {
            System.out.println("Error while getting topic");
        }
        return Optional.empty();
    }

    private void setSliceHashtags(Map<String, Slice> slices) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT shm.sliceId, h.name FROM SliceHashtagMap shm " +
                    "INNER JOIN Hashtag h ON h.name=shm.hashtagName");
            final ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                if (slices.containsKey(rs.getString(1))) {
                    slices.get(rs.getString(1)).getHashtags()
                            .add(new Hashtag(rs.getString(2)));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while getting topic hashtags..");
        }
    }
}