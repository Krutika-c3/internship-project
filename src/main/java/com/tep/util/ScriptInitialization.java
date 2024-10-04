package com.tep.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class ScriptInitialization {
    public void initializeScript() {
        try (Connection connection = ConnectionProvider.Connector(false))  {
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("tep.sql")) {
                Scanner scanner = new Scanner(inputStream);
                scanner.useDelimiter(";");
                try (Statement statement = connection.createStatement()) {
                    while (scanner.hasNext()) {
                        String line = scanner.next();
                        if (line.trim().length() > 0) {
                            statement.execute(line);
                        } else
                            break;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to load Database");
        } catch (SQLException ex) {
            System.out.println("Failed to create Database");
        }
    }
}