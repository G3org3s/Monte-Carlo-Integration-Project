package com.mycompany.montecarlo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
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
        
        double xValue1 = 5;
        
        ExpressionBuilder eb = new ExpressionBuilder("0.1*x^5 - 0.8x^4 - 1x^3 + 7x^2 - 6");
        eb.variable("x");
        Expression exp = eb.build();
        exp.setVariable("x", xValue1);
        ExpressionEvaluator ev = new ExpressionEvaluator();
        double e = exp.evaluate(); 
        System.out.println(e);
        
        Expression test = eb.build();
        double min = getMin(test, -4, 40);
        double max = getMax(test, 0, 7);
        System.out.println(min);
        System.out.println(max);
            
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
    
    public static double integrateMonteCarlo(Expression function, int leftBound, int rightBound) {
        double minValue = 0;
        double maxValue = 0;
        
        for (double i = leftBound; i < rightBound; i += 0.2) {
            
        }
        
        return 0;
    }
    
    public static double getMin(Expression function, int leftBound, int rightBound){
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
    
    public static double getMax(Expression function, int leftBound, int rightBound){
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
}
