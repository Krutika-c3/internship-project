package com.tep.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ConnectionProvider {

    static Connection connection = null;

    public static Connection Connector(boolean isURLWithDB) {
        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle("application");
            String loadDriver = resourceBundle.getString("driver");
            String dbURL = isURLWithDB ? resourceBundle.getString("urlWithDB"):resourceBundle.getString("url");
            String dbUSERNAME = resourceBundle.getString("userName");
            String dbPASSWORD = resourceBundle.getString("password");
            Class.forName(loadDriver);
            connection = DriverManager.getConnection(dbURL, dbUSERNAME, dbPASSWORD);
        }
        catch (ClassNotFoundException e){
            System.out.println("Failed to establish connection");
        }
        catch (SQLException e){
            System.out.println("Failed to establish connection");
            System.exit(0);
        }
        return connection;
    }
}
