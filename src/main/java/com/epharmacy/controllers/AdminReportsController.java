package com.epharmacy.controllers;

import com.epharmacy.services.ReportService;
import com.epharmacy.utils.AlertHelper;
import com.epharmacy.utils.AsyncHelper;
import com.epharmacy.utils.Refreshable;
import com.epharmacy.utils.SceneNavigator;
import com.epharmacy.utils.ScreenUi;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/** Admin reports controller with charts. */
public class AdminReportsController implements Initializable, Refreshable {

    @FXML private BarChart<String,Number> revenueChart;
    @FXML private PieChart               categoryPie;
    @FXML private PieChart               statusPie;
    @FXML private BorderPane rootPane;
    @FXML private StackPane sidebarBrandMark;
    @FXML private Button btnBack;

    private final ReportService reportService = new ReportService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ScreenUi.setup(rootPane, sidebarBrandMark, btnBack,
                "Smart E-Pharmacy", "Reports & analytics");
    }

    @Override
    public void refresh() {
        AsyncHelper.supplyAsync(reportService::getMonthlyRevenue, revenue -> {
            if (revenueChart == null) return;
            revenueChart.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Monthly Revenue ($)");
            revenue.forEach((month, rev) ->
                    series.getData().add(new XYChart.Data<>(month, rev)));
            revenueChart.getData().add(series);
        });
        AsyncHelper.supplyAsync(reportService::getMedicinesByCategory, byCat -> {
            if (categoryPie == null) return;
            categoryPie.getData().clear();
            byCat.forEach((cat, cnt) -> categoryPie.getData().add(new PieChart.Data(cat, cnt)));
        });
        AsyncHelper.supplyAsync(reportService::getOrdersByStatus, byStatus -> {
            if (statusPie == null) return;
            statusPie.getData().clear();
            byStatus.forEach((status, cnt) -> statusPie.getData().add(new PieChart.Data(status, cnt)));
        });
    }

    @FXML private void goBack(ActionEvent e) {
        try { SceneNavigator.navigateToDashboard((Stage) revenueChart.getScene().getWindow()); }
        catch (Exception ex) { AlertHelper.showError("Error", ex.getMessage()); }
    }
}
