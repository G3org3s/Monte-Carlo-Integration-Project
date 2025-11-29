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
    
        // FXML References
    
    @FXML
    private StackPane graphPane; // The Pane containing the LineChart

    @FXML
    private Label errorMessage; // Label displaying errors

    @FXML
    private Label endpointsLabel; //Label for endpoint selection (visible only for Riemann Sum method)

    @FXML
    private ComboBox<String> endpointCombo; //ComboBox for selecting "Left" or "Right" endpoints (Riemann Sum)

    @FXML
    private TextField equationText; // The equation TextField

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

        // Variables
    
    private LineChart<Number, Number> chart; // Line chart that displays f(x)

    private NumberAxis xAxis; // X-axis (domain) for the chart

    private NumberAxis yAxis; // Y-axis (range) for the chart

    private Expression currentExpression; // The current equation in expression form
    
    private double lowerBound; // The lower bound of the function
    
    private double upperBound; // The upper bound of the function
    
    private int numPoints; // The number of points of integration estimation
    
    /**
     * - Initialize ComboBoxes 
     * - Create and configure the chart 
     * - Bind the chart to the StackPane so it always fills it 
     * - Add listeners so that the graph updates automatically
     */
    @FXML
    private void initialize() {
        // Adds the ComboBox options
        methodCombo.getItems().addAll("Monte Carlo", "Riemann Sum");
        endpointCombo.getItems().addAll("Left", "Right");

        // Endpoints controls not visible until "Riemann Sum" method is selected
        endpointsLabel.setVisible(false);
        endpointCombo.setVisible(false);

        // Create axes and chart
        xAxis = new NumberAxis();
        xAxis.setLabel("x");
        xAxis.setAutoRanging(false);
        
        yAxis = new NumberAxis();
        yAxis.setLabel("f(x)");
        
        chart = new LineChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setCreateSymbols(false);

        // Make the chart fill the graphPane
        chart.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        chart.prefWidthProperty().bind(graphPane.widthProperty());
        chart.prefHeightProperty().bind(graphPane.heightProperty());
        graphPane.getChildren().add(chart);

        // When the equation changes, update the function and try to plot the graph
        equationText.textProperty().addListener((obs, oldValue, newValue) -> {
            buildAndVerify(newValue);
        });

        //When lower bound changes, update function and processes
        lowerBoundText.textProperty().addListener((obs, oldValue, newValue) -> {
            buildAndVerify(equationText.getText());
        });

        // When upper bound changes, update function and processes
        upperBoundText.textProperty().addListener((obs, oldValue, newValue) -> {
            buildAndVerify(equationText.getText());
        });
        
        // When the number of points change, update function and processes
        numPointsText.textProperty().addListener((obs, oldValue, newValue) -> {
            buildAndVerify(equationText.getText());
        });
        
        // When integration method is changed, update function and processes
        methodCombo.valueProperty().addListener((obs, oldValue, newValue) -> {
            buildAndVerify(equationText.getText());
        });
        
        // When Riemann Sum integration direction is changed, update function and processes
        endpointCombo.valueProperty().addListener((obs, oldValue, newValue) -> {
            buildAndVerify(equationText.getText());
        });
    }

    /**
     * Parses the user-entered equation into an Expression using exp4j. 
     * Tries to plot it immediately if bounds are present and valid.
     *
     * @param equation the string from equationText
     */
    private void buildAndVerify(String equation) {
            // Checking lower bound and number of points and integration type
            
        if (methodCombo.getValue() == null) {
            errorMessage.setText("Integration type not specified.");
            chart.getData().clear();
            return;
        } else if ("Riemann Sum".equals(methodCombo.getValue()) && endpointCombo.getValue() == null) {
            errorMessage.setText("Riemann Sum direction not specified.");
            chart.getData().clear();
            return;
        }
        
        // Validate lower bound
        try {
            lowerBound = Double.parseDouble(lowerBoundText.getText());
        } catch (NumberFormatException e) {
            errorMessage.setText("Lower bound must be a valid double.");
            chart.getData().clear();
            return;
        }

        // Validate upper bound
        try {
            upperBound = Double.parseDouble(upperBoundText.getText());
        } catch (NumberFormatException e) {
            errorMessage.setText("Upper bound must be a valid double.");
            chart.getData().clear();
            return;
        }

        // Validate number of points
        try {
            numPoints = Integer.parseInt(numPointsText.getText());
        } catch (NumberFormatException e) {
            errorMessage.setText("Number of points must be a valid integer.");
            chart.getData().clear();
            return;
        }
        
        // Order check
        if (lowerBound >= upperBound) {
            errorMessage.setText("Lower bound must be strictly less than upper bound.");
            chart.getData().clear();
            return;
        }
        
        // Checks to see if the upper and lower bounds are within their limits
        if (lowerBound < -1000 || upperBound > 1000) {
            errorMessage.setText("Upper bound has to be less than 1000 and lower bound has to be greater than -1000");
            chart.getData().clear();
            return;
        }
        
        // Checks to see if the number of points is a reasonable number
        if (numPoints <= 0 || numPoints > 1000000) {
            errorMessage.setText("Number of points must be between 1 and 1,000,000.");
            chart.getData().clear();
            return;
        }
        
            // Checking to see if equation is valid
            
        // If the equation box is empty, clear everything
        if (equation == null || equation.isBlank()) {
            currentExpression = null;
            chart.getData().clear();
            
            errorMessage.setText("No equation selected");
            return;
        }

        // Making sure the function doesn't contain tan or cot
        if (equation.contains("tan") || equation.contains("cot")) {
            currentExpression = null;
            chart.getData().clear();
            
            errorMessage.setText("Tangent and cotangent functions are not supported");
            return;
        }
        
        // Checking to see if equation syntax is valid
        try {
            ExpressionBuilder eb = new ExpressionBuilder(equation).variable("x");
            currentExpression = eb.build();

        } catch (Exception e) {
            currentExpression = null;
            chart.getData().clear();
            
            errorMessage.setText("Invalid function");
            return;
        }
        
        // Plot as many points as possible into the line chart to make it properly estimate the function
        for (double x = lowerBound; x <= upperBound; x += 0.001) { // TODO: Make the 0.0001 match Georges' function
            currentExpression.setVariable("x", Math.round(x * 1000) / 1000); // Round it to make sure changing it by a small number doesn't mess anything up
            
            double y;
            try {
                y = currentExpression.evaluate();
            } catch (Exception e) {
                currentExpression = null;
                chart.getData().clear();
            
                errorMessage.setText("Invalid function");
                return;
            }

            // Check to see if a point is NaN or infinity
            if (Double.isNaN(y) || Double.isInfinite(y)) {
                currentExpression = null;
                chart.getData().clear();
            
                errorMessage.setText("Invalid function");
                return;
            }
        }
        
        errorMessage.setText("");
        
        // Plot the function using the current bounds
        plotFunction();
            
        // TODO: call Georges' equations method
        
    }

    /**
     * Plots the given Expression on the LineChart, using the bounds in
     * lowerBoundText and upperBoundText. If bounds are missing or invalid, the
     * chart is cleared and an error message is shown
     *
     * @param expression a compiled exp4j Expression representing f(x)
     */
    private void plotFunction() {
        // Update upper and lower bounds
        xAxis.setLowerBound(lowerBound);
        xAxis.setUpperBound(upperBound);
        
        // Create a new series and sample the function between [a, b]
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        chart.getData().clear(); // remove any old series

        // Use a fixed number of sample points for a smooth curve
        double dx = (upperBound - lowerBound) / 1000; // TODO: Turn the 1000 (number of points) into whatever Georges used for his calculations

        // Plot as many points as possible into the line chart to make it properly estimate the function
        for (double x = lowerBound; x <= upperBound; x += dx) {
            currentExpression.setVariable("x", x);
            series.getData().add(new XYChart.Data<>(x, currentExpression.evaluate()));
        }

        // Add the series
        chart.getData().add(series);
        errorMessage.setText("");
    }
    
    private void riemannDisplay() {
        
    }
    
    private void monteCarloDisplay() {
        
    }
    
    /**
     * Called when the integration method ComboBox value changes 
     * Shows or hides the endpoint controls when "Riemann Sum" is selected
     */
    @FXML
    private void onMethodSelected(ActionEvent event) {
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
     * Resets all text fields, ComboBoxes, the chart and the error label
     */
    @FXML
    private void clearOnAction(ActionEvent event) {
        lowerBoundText.clear();
        upperBoundText.clear();
        numPointsText.clear();
        equationText.clear();
        methodCombo.getSelectionModel().clearSelection();
        endpointCombo.getSelectionModel().clearSelection();
        endpointsLabel.setVisible(false);
        endpointCombo.setVisible(false);

        currentExpression = null;
        chart.getData().clear();
        errorMessage.setText("");
        netAreaValue.setText("");
    }
}

