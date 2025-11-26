package com.mycompany.montecarlo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.ValidationResult;
import net.objecthunter.exp4j.tokenizer.Tokenizer;

// The primary class for creating and evaluating expressions
import tk.pratanumandal.expr4j.ExpressionEvaluator;
import tk.pratanumandal.expr4j.ExpressionEvaluator.Evaluator;
import tk.pratanumandal.expr4j.OperatorRepository;
import tk.pratanumandal.expr4j.common.Expr4jConstants;
import tk.pratanumandal.expr4j.token.Function;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        
//        double xValue1 = 5;
//        
//        ExpressionBuilder eb = new ExpressionBuilder("0.1*x^5 - 0.8x^4 - 1x^3 + 7x^2 - 6");
//        eb.variable("x");
//        Expression exp = eb.build();
//        exp.setVariable("x", xValue1);
//        double e = exp.evaluate(); 
//        System.out.println(e);
//        
//        Expression test = eb.build();
//        double min = getMin(test, -4, 40);
//        double max = getMax(test, 0, 7);
//        System.out.println(min);
//        System.out.println(max);
        
        
        double left = -15;
        double right = 25;
        
        ExpressionBuilder f = new ExpressionBuilder("2x - 2");
        f.variable("x");
        Expression g = f.build();
        g.setVariable("x", 0);
        System.out.println("g at point:" + g.evaluate());
        
        double mini = getMin(g, left, right);
        double maxi = getMax(g, left, right);
        System.out.println(mini);
        System.out.println(maxi);
        
        double rn = 0;
        for (int i = 0; i < 10; i ++) {
            HashMap<Double, Double> pointss = plotPoints(left, right, mini, maxi, 5000);
            double area = integrateMonteCarlo(g, left, right, pointss);
            rn += area;
        }
        HashMap<Double, Double> pointss = plotPoints(left, right, mini, maxi, 10000);
        
        double area2 = integrateRiem(g, left, right, 100, "left");
        
        HashMap<Double, Double> pointss2 = plotPoints(left, right, mini, maxi, 50000);
        double area3 = integrateMonteCarlo(g, left, right, pointss2);
        System.out.println(rn / 10);
        System.out.println(area2);
        System.out.println(area3);
        
            
        scene = new Scene(loadFXML("primary"), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
    
    public static double integrateMonteCarlo(Expression function, double leftBound, double rightBound, HashMap<Double, Double> randPoints) {
        double minValue = getMin(function, leftBound, rightBound);
        double maxValue = getMax(function, leftBound, rightBound);
        
        double counterPos = 0;
        double counterNeg = 0;
        
        for(Map.Entry<Double, Double> point : randPoints.entrySet()) {
            Double x = point.getKey();
            Double y = point.getValue();
            
            function.setVariable("x", x);
            double fAtPoint = function.evaluate();
            
            if (fAtPoint > 0 && fAtPoint >= y) {
                counterPos += 1;
            } else if (fAtPoint < 0 && fAtPoint <= y)  {
                counterNeg += 1;
            }
        }
        
        double rectArea = (rightBound - leftBound) * (maxValue - minValue);

        long numPoints = randPoints.size();
        
        double posArea = (counterPos / numPoints) * rectArea;
        double negArea = (counterNeg / numPoints) * rectArea;
        return posArea - negArea;
    }
    
    public static double getMin(Expression function, double leftBound, double rightBound){
        double min = Double.POSITIVE_INFINITY;
        
        for(double i = leftBound; i <= rightBound; i += 0.0001) {
            function.setVariable("x", i);
            double value = function.evaluate();
            
            if (value < min) {
                min = value;
            }
        }
        
        return min;
    }
    
    public static double getMax(Expression function, double leftBound, double rightBound){
        double max = Double.NEGATIVE_INFINITY;
        
        for(double i = leftBound; i <= rightBound; i += 0.0001) {
            function.setVariable("x", i);
            double value = function.evaluate();
            
            if (value > max) {
                max = value;
            }
        }
        
        return max;
    }
    
    public static HashMap<Double, Double> plotPoints(double leftBound, double rightBound, double min, double max, long numPoints) {
        HashMap<Double, Double> points = new HashMap();
        
        for (int i = 0; i < numPoints; i++) {
            double x = leftBound + (Math.random() * (rightBound - leftBound));
            double y = min + (Math.random() * (max - min));
            points.put(x, y);
        }
        
        return points;
    } 
    
    public static double integrateRiem(Expression function, double leftBound, double rightBound, long numPoints, String endPoints) {
        double range = rightBound - leftBound;
        double dx = range / numPoints;
        double area = 0;
        
        if (endPoints.equalsIgnoreCase("right")) {
            for (double x = leftBound; x <= rightBound; x += dx) {
                function.setVariable("x", x);
                double h = function.evaluate();
                area += h * dx;
            }
        } else if(endPoints.equalsIgnoreCase("left")) {
            for (double x = rightBound; x >= leftBound; x -= dx) {
                function.setVariable("x", x);
                double h = function.evaluate();
                area += h * dx;
            }
        }
        
        return area;
    }
}
