/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.montecarlo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Main controller for the Monte Carlo / Riemann Sum visualization UI
 *
 * - Sets up a LineChart inside a StackPane 
 * - Parses the user-inputted function f(x) using exp4j 
 * - Automatically replots the function when the equation or bounds change
 * - Validates input and reports error messages
 */
public class MainViewController {

    //Line chart that displays f(x)
    private LineChart<Number, Number> chart;

    //X-axis (domain) for the chart
    private NumberAxis xAxis;

    //Y-axis (range) for the chart
    private NumberAxis yAxis;

    private Expression currentFunction;

    @FXML
    private StackPane graphPane;

    @FXML
    private Label errorMessage;

    //Label for endpoint selection (visible only for Riemann Sum method)
    @FXML
    private Label endpointsLabel;

    //ComboBox for selecting "Left" or "Right" endpoints (Riemann Sum)
    @FXML
    private ComboBox<String> endpointCombo;

    @FXML
    private TextField equationText;

    @FXML
    private TextField lowerBoundText;

    @FXML
    private TextField upperBoundText;

    @FXML
    private ComboBox<String> methodCombo;

    @FXML
    private TextField numPointsText;

    @FXML
    private Label netAreaValue;

    /**
     * - Initialize combo boxes 
     * - Create and configure the chart 
     * - Bind the chart to the StackPane so it always fills it 
     * - Add listeners so that the graph updates automatically
     */
    @FXML
    private void initialize() {
        methodCombo.getItems().addAll("Monte Carlo", "Riemann Sum");
        endpointCombo.getItems().addAll("Left", "Right");

        //Endpoints controls not visible until "Riemann Sum" method is selected
        endpointsLabel.setVisible(false);
        endpointCombo.setVisible(false);

        //Create axes and chart
        xAxis = new NumberAxis();
        xAxis.setLabel("x");
        yAxis = new NumberAxis();
        yAxis.setLabel("f(x)");
        chart = new LineChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setCreateSymbols(false);   // smooth line, no circles

        //Make the chart fill the graphPane
        chart.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        chart.prefWidthProperty().bind(graphPane.widthProperty());
        chart.prefHeightProperty().bind(graphPane.heightProperty());
        graphPane.getChildren().add(chart);

        //When the equation changes, update the function and try to plot the graph
        equationText.textProperty().addListener((obs, oldValue, newValue) -> {
            buildFunctionAndGraph(newValue);
        });

        //When lower bound changes, replot if we have a valid function
        lowerBoundText.textProperty().addListener((obs, oldValue, newValue) -> {
            inputValidation();
            if (currentFunction != null) {
                plotFunction(currentFunction);
            }
        });

        //When upper bound changes, replot if we have a valid function
        upperBoundText.textProperty().addListener((obs, oldValue, newValue) -> {
            inputValidation();
            if (currentFunction != null) {
                plotFunction(currentFunction);
            }
        });
    }

    /**
     * Called when the integration method ComboBox value changes 
     * Shows or hides the endpoint controls when "Riemann Sum" is selected
     */
    @FXML
    void onMethodSelected(ActionEvent event) {
        String selected = methodCombo.getValue();

        if (selected == null) {
            return;
        }

        if (selected.equals("Riemann Sum")) {
            endpointsLabel.setVisible(true);
            endpointCombo.setVisible(true);
        } else {
            endpointsLabel.setVisible(false);
            endpointCombo.setVisible(false);
        }
    }

    /**
     * Called when the "Clear" button is pressed
     * Resets all text fields, comboboxes, the chart and the error label
     */
    @FXML
    void clearOnAction(ActionEvent event) {
        lowerBoundText.clear();
        upperBoundText.clear();
        numPointsText.clear();
        equationText.clear();
        methodCombo.getSelectionModel().clearSelection();
        endpointCombo.getSelectionModel().clearSelection();
        endpointsLabel.setVisible(false);
        endpointCombo.setVisible(false);

        currentFunction = null;
        chart.getData().clear();
        errorMessage.setText("");
        netAreaValue.setText("");

    }

    /**
     * Parses the user-entered equation into an Expression using exp4j. 
     * Tries to plot it immediately if bounds are present and valid.
     *
     * @param equation the string from equationText
     */
    private void buildFunctionAndGraph(String equation) {
        // If the equation box is empty, just clear everything
        if (equation == null || equation.isBlank()) {
            currentFunction = null;
            chart.getData().clear();
            errorMessage.setText("");
            return;
        }

        try {
            //Build an expression with a single variable 'x'
            ExpressionBuilder eb = new ExpressionBuilder(equation).variable("x");
            currentFunction = eb.build();

            //If we get here, parsing is successful
            errorMessage.setText("");

            // Try to plot the function using the current bounds
            plotFunction(currentFunction);

        } catch (Exception e) {
            // Any exception here means the function is invalid
            currentFunction = null;
            chart.getData().clear();
            errorMessage.setText("Invalid function");
        }
    }

    /**
     * Plots the given Expression on the LineChart, using the bounds in
     * lowerBoundText and upperBoundText. If bounds are missing or invalid, the
     * chart is cleared and an error message is shown
     *
     * @param function a compiled exp4j Expression representing f(x)
     */
    private void plotFunction(Expression function) {

        //Bounds must be filled
        if (lowerBoundText.getText().isBlank()
                || upperBoundText.getText().isBlank()) {
            chart.getData().clear();
            errorMessage.setText("Enter both lower and upper bounds to see the graph.");
            return;
        }

        //Parse bounds as doubles
        double a, b;
        try {
            a = Double.parseDouble(lowerBoundText.getText());
            b = Double.parseDouble(upperBoundText.getText());
        } catch (NumberFormatException e) {
            chart.getData().clear();
            errorMessage.setText("Bounds must be numeric values ");
            return;
        }

        //Validate bounds order
        if (a >= b) {
            chart.getData().clear();
            errorMessage.setText("Lower bound must be less than upper bound.");
            return;
        }

        //Create a new series and sample the function between [a, b]
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        chart.getData().clear(); // remove any old series

        // Use a fixed number of sample points for a smooth curve
        final int SAMPLES = 200;
        double step = (b - a) / SAMPLES;

        for (double x = a; x <= b; x += step) {
            function.setVariable("x", x);
            double y;
            try {
                y = function.evaluate();
            } catch (Exception e) {
                //If evaluation fails at some x, just skip that point
                continue;
            }

            //Ignore NaN or infinite values so they don't break the chart
            if (Double.isNaN(y) || Double.isInfinite(y)) {
                continue;
            }

            series.getData().add(new XYChart.Data<>(x, y));
        }

        //If we didn't get any valid points, update the error message
        if (series.getData().isEmpty()) {
            chart.getData().clear();
            errorMessage.setText("No valid points to plot for this function and interval.");
            return;
        }

        //Success: add the series and clear any previous error
        chart.getData().add(series);
        errorMessage.setText("");
    }

    /**
     * Validates the user inputs
     *
     * @return true if all inputs are valid, false otherwise.
     */
    public boolean inputValidation() {
        double lowerBound;
        double upperBound;
        int numPoints;

        //Validate lower bound
        try {
            lowerBound = Double.parseDouble(lowerBoundText.getText());
        } catch (NumberFormatException e) {
            errorMessage.setText("Lower bound must be a valid number.");
            return false;
        }

        //Validate upper bound
        try {
            upperBound = Double.parseDouble(upperBoundText.getText());
        } catch (NumberFormatException e) {
            errorMessage.setText("Upper bound must be a valid number.");
            return false;
        }

        //Order check
        if (lowerBound >= upperBound) {
            errorMessage.setText("Lower bound must be strictly less than upper bound.");
            return false;
        }

        //Validate number of points
        try {
            numPoints = Integer.parseInt(numPointsText.getText());
        } catch (NumberFormatException e) {
            errorMessage.setText("Number of points must be an integer.");
            return false;
        }

        if (numPoints <= 0 || numPoints > 1000000) {
            errorMessage.setText("Number of points must be between 1 and 1 000 000.");
            return false;
        }

        //If we got here, everything is valid
        errorMessage.setText("");
        return true;
    }
}

