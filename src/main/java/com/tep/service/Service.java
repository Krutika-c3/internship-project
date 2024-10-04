package com.tep.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public interface Service {
    void displayOption() throws IOException;

    void save() throws SQLException;

    void display();

    Optional displayAll();

    void recreateOption();

}