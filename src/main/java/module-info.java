module com.epharmacy {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires javafx.graphics;
    requires java.sql;
    requires java.prefs;

    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;

    // BCrypt and MySQL JDBC are automatic modules
    requires jbcrypt;
    requires mysql.connector.j;

    // iText 7 PDF modules
    requires kernel;
    requires layout;
    requires io;

    // Export & open root package so javafx.graphics can instantiate App
    exports com.epharmacy;
    exports com.epharmacy.controllers;
    exports com.epharmacy.models;
    exports com.epharmacy.services;
    exports com.epharmacy.dao;
    exports com.epharmacy.database;
    exports com.epharmacy.utils;
    exports com.epharmacy.ui;
    exports com.epharmacy.patterns.singleton;
    exports com.epharmacy.patterns.factory;
    exports com.epharmacy.patterns.proxy;
    exports com.epharmacy.patterns.decorator;
    exports com.epharmacy.patterns.strategy;
    exports com.epharmacy.patterns.command;

    opens com.epharmacy            to javafx.fxml, javafx.graphics;
    opens com.epharmacy.controllers to javafx.fxml;
    opens com.epharmacy.models     to javafx.base, javafx.fxml;
    opens com.epharmacy.database   to javafx.fxml;
    opens com.epharmacy.utils      to javafx.fxml;
    opens com.epharmacy.ui         to javafx.fxml;
    opens com.epharmacy.services   to javafx.fxml;
    opens com.epharmacy.patterns.singleton to javafx.fxml;
    opens com.epharmacy.patterns.factory   to javafx.fxml;
    opens com.epharmacy.patterns.proxy     to javafx.fxml;
    opens com.epharmacy.patterns.decorator to javafx.fxml;
    opens com.epharmacy.patterns.strategy  to javafx.fxml;
    opens com.epharmacy.patterns.command   to javafx.fxml;
    opens com.epharmacy.dao to javafx.fxml;
}
