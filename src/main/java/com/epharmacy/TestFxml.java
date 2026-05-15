package com.epharmacy;

import javafx.fxml.FXMLLoader;
import javafx.application.Platform;

public class TestFxml {
    public static void main(String[] args) {
        Platform.startup(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(TestFxml.class.getResource("/fxml/PatientBrowse.fxml"));
                loader.load();
                System.out.println("LOADED SUCCESSFULLY!");
            } catch (Exception e) {
                e.printStackTrace();
                if (e.getCause() != null) {
                    System.out.println("CAUSE: ");
                    e.getCause().printStackTrace();
                }
            }
            System.exit(0);
        });
    }
}
