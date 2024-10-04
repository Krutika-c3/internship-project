package com.tep.repository.impl.JDBC;

import com.tep.model.Hashtag;
import com.tep.model.Topic;
import com.tep.util.ConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TopicRepository implements com.tep.repository.TopicRepository {

    final HashtagRepository hashtagRepository = new HashtagRepository();
    final Connection connection = ConnectionProvider.Connector(true);

    @Override
    public Optional<Topic> insert(Topic topic) {
        int insert = 0;
        AtomicInteger insertIntoMap = new AtomicInteger();
        final String query = "INSERT INTO Topic VALUES (?,?,?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, topic.getId().toString());
            preparedStatement.setString(2, topic.getTitle());
            preparedStatement.setString(3, topic.getDescription());
            insert = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Topic not created");
        }

        topic.getHashtags().forEach(hashtag ->
        {
            insertHashtag(topic);
            final String mapQuery = "Insert Into TopicHashtagMap Values(?,?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(mapQuery)) {
                preparedStatement.setString(1, topic.getId().toString());
                preparedStatement.setString(2, hashtag.getName());
                insertIntoMap.set(preparedStatement.executeUpdate());
            } catch (SQLException e) {
                System.out.println("Topic not associated");
            }
        });
        if (insert == 1 && insertIntoMap.get() == 1) {
            return Optional.of(topic);
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<Topic>> getAll() {
        List<Topic> topics = new ArrayList<>();
        try {
            final String sql = "Select * from Topic";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                Topic topic = new Topic();
                topic.setId(UUID.fromString(resultSet.getString(1)));
                topic.setTitle(resultSet.getString(2));
                topic.setDescription(resultSet.getString(3));
                topics.add(topic);
            }
        } catch (SQLException e) {
            System.out.println("Unable to display Topic");
        }
        return Optional.of(topics);
    }

    @Override
    public Optional<Topic> getById(UUID uuid) {
        final String query = "SELECT * FROM Topic WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Topic topic = new Topic();
                topic.setId(UUID.fromString(resultSet.getString(1)));
                topic.setTitle(resultSet.getString(2));
                topic.setDescription(resultSet.getString(3));
                return Optional.of(topic);
            }
        } catch (SQLException e) {
            System.out.println("\nTopic Not Found !!");
        }
        return Optional.empty();
    }

    public Optional<Set<Topic>> getByHashtag(String hashtag) {
        Map<String, Topic> topicsFromDB = new HashMap<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT distinct t.id, t.title, t.description FROM TopicHashtagMap thm\n" +
                    "INNER JOIN Topic t ON thm.topicId = t.id WHERE thm.hashtagName = ? ");
            preparedStatement.setString(1, hashtag);
            final ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                topicsFromDB.put(rs.getString(1),
                        new Topic(
                                UUID.fromString(rs.getString(1)),
                                rs.getString(2),
                                rs.getString(3),
                                new HashSet<>()));
            }
            setTopicHashtags(topicsFromDB);
            //setTopicSlices(topicsFromDB);
            return Optional.of(new HashSet<>(topicsFromDB.values()));
        } catch (SQLException e) {
            System.out.println("Error while getting topic");
        }
        return Optional.empty();
    }

    private void setTopicHashtags(Map<String, Topic> topics) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT thm.topicId, h.name FROM TopicHashtagMap thm " +
                    "INNER JOIN Hashtag h ON h.name=thm.hashtagName;");
            final ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                if (topics.containsKey(rs.getString(1))) {
                    topics.get(rs.getString(1)).getHashtags()
                            .add(new Hashtag(rs.getString(2)));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while getting topic hashtags..");
        }
    }

    /*public Set<Topic> getByHashtag(Set<Hashtag> hashtags) {
        Set<Topic> topic = new HashSet<>();
        return hashtags.stream().forEach(hashtag -> {
            try {
                String query = "SELECT * FROM Topic WHERE title = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, title);
                ResultSet resultset = statement.executeQuery();
                return resultset.next();
            } catch (SQLException e) {
                System.out.println("Unable to check Topic");
            }
            });
    }*/

    @Override
    public Set<Topic> getByHashtag(Set<Hashtag> hashtags) {
        return null;
    }

    @Override
    public boolean isTitleExists(String title) {
        try {
            String query = "SELECT * FROM Topic WHERE title = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, title);
            ResultSet resultset = statement.executeQuery();
            return resultset.next();
        } catch (SQLException e) {
            System.out.println("Unable to check Topic");
        }
        return false;
    }

    public void insertHashtag(Topic topic) {
        topic.getHashtags().forEach(hashtagRepository::insert);
    }

    public Optional<Set<Topic>> getByIds(Set<UUID> ids) {
        Set<Topic> idTopics = new HashSet<>();
        ids.forEach(id -> {
            Topic topic = getById(id).get();
            idTopics.add(topic);
        });
        return Optional.of(idTopics);
    }
}