package com.tep.repository.impl.JDBC;

import com.tep.model.Hashtag;
import com.tep.util.ConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class HashtagRepository implements com.tep.repository.HashtagRepository {
    final Connection connection = ConnectionProvider.Connector(true);

    @Override
    public Hashtag insert(Hashtag hashtag) {
        final String query = "insert ignore into Hashtag values (?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, hashtag.getName());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to insert Hashtag");
        }
        return hashtag;
    }

    @Override
    public Optional<Hashtag> getExistingHashTag(Hashtag hashtag) {
        final String query = "SELECT * FROM Hashtag WHERE name = ?";
        String name = hashtag.getName();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            ResultSet resultset = preparedStatement.executeQuery();

            if (resultset.next()) {
                name = resultset.getString(1);
            }
        } catch (SQLException e) {
            System.out.println("Failed to get Hashtag");
        }
        return Optional.of(new Hashtag(name));
    }

}
