/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.montecarlo;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class IntroUIController {

    private Image introGIF;

    @FXML
    private Label projectTitle;
    
    @FXML
    private Button startBtn;

    @FXML
    private ImageView introImageView;

    @FXML
    private void initialize() {
        loadIntroGIF();
    }

    private void loadIntroGIF() {
        introGIF = new Image("file:images/IntroMonteCarlo.gif");
        introImageView.setImage(introGIF);
    }

    @FXML
    void startOnAction(ActionEvent event) {
        startBtn.setVisible(false);
        introImageView.setVisible(false);
        projectTitle.setVisible(false);
        try {
            App.setRoot("MainViewFXML");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
