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
        scene = new Scene(loadFXML("IntroUIFXML"), 800, 600);
        stage.setResizable(false);
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

}
