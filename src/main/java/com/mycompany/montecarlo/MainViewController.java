/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.montecarlo;

import java.util.HashMap;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
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
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Main controller for the Monte Carlo / Riemann Sum visualization UI
 *
 * - Sets up a LineChart inside a StackPane - Parses the user-inputted function
 * f(x) using exp4j - Automatically re-plots the function when the equation or
 * bounds change - Validates input and reports error messages
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
    private ComboBox<String> endpointCombo; // ComboBox for selecting "Left" or "Right" endpoints (Riemann Sum)

    @FXML
    private TextField equationText; // The equation TextField

    @FXML
    private TextField lowerBoundText; // The TextField for the lower bound

    @FXML
    private TextField upperBoundText; // The TextField for the upper bound

    @FXML
    private ComboBox<String> methodCombo; // Dropdown for the integration method selection

    @FXML
    private TextField numPointsText; // The TextField for the number of points to compute

    @FXML
    private Label netAreaValue; // Label that displays the final area calculation

    // Variables
    private LineChart<Number, Number> chart; // Line chart that displays f(x)

    private NumberAxis xAxis; // X-axis (domain) for the chart

    private NumberAxis yAxis; // Y-axis (range) for the chart

    private Expression currentExpression; // The current equation in expression form

    private double lowerBound; // The lower bound of the function

    private double upperBound; // The upper bound of the function

    private int numPoints; // The number of points of integration estimation

    private HashMap<Double, Double> plotPoints = new HashMap<>();

    // NOTE: A Group basically just keeps things together without any Layout (e.g. GridPane positions) on its children
    private Group graphingGroup; // The group within the StackPane that contains both the points and the LineChart

    /**
     * - Initialize ComboBoxes - Create and configure the chart - Bind the chart
     * to the StackPane so it always fills it - Add listeners so that the graph
     * updates automatically
     */
    @FXML
    private void initialize() {
        // Creates the group
        graphingGroup = new Group();

        // Adds the ComboBox options
        methodCombo.getItems().addAll("Monte Carlo", "Riemann Sum");
        endpointCombo.getItems().addAll("Left", "Right");

        // Endpoints controls not visible until "Riemann Sum" method is selected
        endpointsLabel.setVisible(false);
        endpointCombo.setVisible(false);

        // Creates axes and chart
        xAxis = new NumberAxis();
        xAxis.setLabel("x");
        xAxis.setAutoRanging(false);

        yAxis = new NumberAxis();
        yAxis.setLabel("f(x)");
        yAxis.setAutoRanging(true);

        chart = new LineChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setCreateSymbols(false);
        chart.setAnimated(false);

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
     * Parses the user-entered equation into an Expression using exp4j. Tries to
     * plot it immediately if bounds are present and valid.
     *
     * @param equation the string from equationText
     */
    private void buildAndVerify(String equation) {
        undoGraphing();

        // Checking lower bound and number of points and integration type
        if (methodCombo.getValue() == null) {
            errorMessage.setText("Integration type not specified");
            chart.getData().clear();
            return;
        } else if ("Riemann Sum".equals(methodCombo.getValue()) && endpointCombo.getValue() == null) {
            errorMessage.setText("Riemann Sum direction not specified");
            chart.getData().clear();
            return;
        }

        // Validate lower bound
        try {
            lowerBound = Double.parseDouble(lowerBoundText.getText());
        } catch (NumberFormatException e) {
            errorMessage.setText("Lower bound must be a valid double");
            chart.getData().clear();
            return;
        }

        // Validate upper bound
        try {
            upperBound = Double.parseDouble(upperBoundText.getText());
        } catch (NumberFormatException e) {
            errorMessage.setText("Upper bound must be a valid double");
            chart.getData().clear();
            return;
        }

        // Validate number of points
        try {
            numPoints = Integer.parseInt(numPointsText.getText());
        } catch (NumberFormatException e) {
            errorMessage.setText("Number of points must be a valid integer");
            chart.getData().clear();
            return;
        }

        // Order check
        if (lowerBound >= upperBound) {
            errorMessage.setText("Lower bound must be strictly less than upper bound");
            chart.getData().clear();
            return;
        }

        // Checks to see if the upper and lower bounds are within their limits
        if (lowerBound < -1000 || upperBound > 1000) {
            errorMessage.setText("Bounds must be in between [-1000 to 1000]");
            chart.getData().clear();
            return;
        }

        // Checks to see if the number of points is a reasonable number
        if (numPoints <= 0 || numPoints > 100000) {
            errorMessage.setText("Number of points must be between 1 and 100,000");
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
        
        if (equation.contains("/")) {
            currentExpression = null;
            chart.getData().clear();

            errorMessage.setText("Rational functions aren't supported");
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
        for (double x = lowerBound; x <= upperBound; x += 0.001) {
            currentExpression.setVariable("x", (double)(Math.round(x * 1000)) / 1000); // Round it to make sure changing it by a small number doesn't mess anything up

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

                errorMessage.setText("Function is not continuous on the interval");
                return;
            }
        }

        errorMessage.setText("");

        if ("Riemann Sum".equals(methodCombo.getValue())) {
            netAreaValue.setText(App.integrateRiem(currentExpression, lowerBound, upperBound, numPoints, endpointCombo.getValue()) + "");
        } else {
            double min = App.getMin(currentExpression, lowerBound, upperBound);
            double max = App.getMax(currentExpression, lowerBound, upperBound);
            plotPoints = App.plotPoints(lowerBound, upperBound, min, max, numPoints);

            netAreaValue.setText(App.integrateMonteCarlo(currentExpression, lowerBound, upperBound, plotPoints) + "");
        }

        // Plot the function using the current bounds
        plotFunction();
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
        double dx = (upperBound - lowerBound) / 1000;

        // Plot as many points as possible into the line chart to make it properly estimate the function
        for (double x = lowerBound; x <= upperBound; x += dx) {
            currentExpression.setVariable("x", x);
            series.getData().add(new XYChart.Data<>(x, currentExpression.evaluate()));
        }

        // Add the series
        chart.getData().add(series);
        errorMessage.setText("");

        chart.applyCss();
        chart.layout();

        if ("Riemann Sum".equals(methodCombo.getValue())) {
            riemannDisplay();
        } else {
            monteCarloDisplay();
        }
    }

    /**
     * Displays the Riemann Sum method of integration
     */
    private void riemannDisplay() {
        // Checking to see which endpoint is used
        String endpointChoice = endpointCombo.getValue();
        boolean useRight = "Right".equals(endpointChoice);

        // Creating all the variables to help with the coordinate conversion
        Node plotArea = chart.lookup(".chart-plot-background"); // The visual area behind the chart
        Pane plotContent = (Pane) plotArea.getParent(); // Gets the StackPane (Parent of chart) and treats it as a pane

        // Creates graphingGroup which will contain graphing points and the chart
        graphingGroup = new Group();
        plotContent.getChildren().add(graphingGroup);

        double dx = (upperBound - lowerBound) / numPoints; // Width of the rectangle

        // Cycles through all the points to be rendered
        for (int x = 0; x < numPoints; x++) {
            double x0 = lowerBound + x * dx; // Bottom left point of the rectangle
            double x1 = x0 + dx; // Bottom right point of the rectangle

            double sampleX = useRight ? x1 : x0; // Where the rectangle is rendered is dependent on whether it's left or right endpoint

            // X Position calculations
            // Converting the math coordinates to Scene Coordinates
            double axisX0 = xAxis.getDisplayPosition(x0); // Gets the bottom left scene position of the rectangle
            double axisX1 = xAxis.getDisplayPosition(x1); // Gets the bottom right scene position of the rectangle

            // Converting the Local Coordinates to Scene Coordinates
            Point2D x0Scene = xAxis.localToScene(axisX0, 0);
            Point2D x1Scene = xAxis.localToScene(axisX1, 0);

            // Converting the Scene Coordinates into the plotContent (Pane) coordinates
            Point2D x0Local = plotContent.sceneToLocal(x0Scene);
            Point2D x1Local = plotContent.sceneToLocal(x1Scene);

            // Getting the raw positions
            double X0 = x0Local.getX();
            double X1 = x1Local.getX();

            // Y Position calculations
            currentExpression.setVariable("x", sampleX);
            double y = currentExpression.evaluate();

            // Converting the math coordinates to Scene Coordinates
            double axisY0 = yAxis.getDisplayPosition(0); // The bottom of the rectangle will always be at 0
            double axisY1 = yAxis.getDisplayPosition(y); // The top of the rectangle will be wherever the y position is

            // Converting the Local Coordinates to Scene Coordinates
            Point2D y0Scene = yAxis.localToScene(0, axisY0);
            Point2D y1Scene = yAxis.localToScene(0, axisY1);

            // Converting the Scene Coordinates into the plotContent (Pane) coordinates
            Point2D y0Local = plotContent.sceneToLocal(y0Scene);
            Point2D y1Local = plotContent.sceneToLocal(y1Scene);

            // Getting the raw positions
            double Y0 = y0Local.getY(); // Bottom of the rectangle
            double Y1 = y1Local.getY(); // Top of the rectangle

            // Creating the rectangle
            Rectangle rect;
            if (Y0 > Y1) {
                rect = new Rectangle(X0, Y1, X1 - X0, Y0 - Y1); // Dealing with positive values
            } else {
                rect = new Rectangle(X0, Y0, X1 - X0, Y1 - Y0); // Dealing with negative values
            }

            rect.setFill(Color.web("#FFD700", 0.35));
            rect.setStroke(Color.web("#FFD700"));
            rect.setStrokeWidth(0.5);

            graphingGroup.getChildren().add(rect);
        }
    }

    /**
     * Displays the Monte Carlo estimation method
     */
    private void monteCarloDisplay() {
        // Creating all the variables to help with the coordinate conversion
        Node plotArea = chart.lookup(".chart-plot-background"); // The visual area behind the chart
        Pane plotContent = (Pane) plotArea.getParent(); // Gets the StackPane (Parent of chart) and treats it as a pane

        // Creates graphingGroup which will contain graphing points and the chart
        graphingGroup = new Group();
        plotContent.getChildren().add(graphingGroup);

        for (Double num : plotPoints.keySet()) {
            // X Position calculations

            // Converting the math coordinates to Scene Coordinates
            double axisX = xAxis.getDisplayPosition(num); // Gets the bottom left scene position of the rectangle

            // Converting the Local Coordinates to Scene Coordinates
            Point2D xScene = xAxis.localToScene(axisX, 0);

            // Converting the Scene Coordinates into the plotContent (Pane) coordinates
            Point2D xLocal = plotContent.sceneToLocal(xScene);

            // Getting the raw positions
            double X = xLocal.getX();

            // Y Position calculations
            // Converting the math coordinates to Scene Coordinates
            double axisY = yAxis.getDisplayPosition(plotPoints.get(num)); // The bottom of the rectangle will always be at 0

            // Converting the Local Coordinates to Scene Coordinates
            Point2D yScene = yAxis.localToScene(0, axisY);

            // Converting the Scene Coordinates into the plotContent (Pane) coordinates
            Point2D yLocal = plotContent.sceneToLocal(yScene);

            // Getting the raw positions
            double Y = yLocal.getY();

            // Evaluate the real value of the function at this x
            currentExpression.setVariable("x", num);
            double fAtPoint = currentExpression.evaluate();

            // Determine if point is inside the area
            boolean inside = false;
            if (fAtPoint > 0 && fAtPoint >= plotPoints.get(num) && plotPoints.get(num) > 0) {
                inside = true;
            }
            if (fAtPoint < 0 && fAtPoint <= plotPoints.get(num) && plotPoints.get(num) < 0) {
                inside = true;
            }

            Rectangle rect = new Rectangle(X, Y, 2, 2);

            if (inside) {
                rect.setFill(Color.web("#00FF00", 0.55));
            } else {
                rect.setFill(Color.RED);
            }
            rect.setStrokeWidth(0.5);

            graphingGroup.getChildren().add(rect);
        }
    }

    /**
     * Removes all the shapes displayed when called
     */
    private void undoGraphing() {
        ObservableList<Node> nodes = FXCollections.observableArrayList(graphingGroup.getChildren());

        for (Node node : nodes) {
            graphingGroup.getChildren().remove(node);
        }
    }

    /**
     * Called when the integration method ComboBox value changes Shows or hides
     * the endpoint controls when "Riemann Sum" is selected
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
     * Called when the "Clear" button is pressed Resets all text fields,
     * ComboBoxes, the chart and the error label
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
    
    @FXML
    void exitOnAction(ActionEvent event) {
        Platform.exit();
    }
}
