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

    private Image introImage;

    @FXML
    private Label projectTitle;

    @FXML
    private Button startBtn;

    @FXML
    private ImageView introImageView;

    @FXML
    private void initialize() {
        loadIntroImage();
    }

    private void loadIntroImage() {
        introImage = new Image("file:images/MainMenu.png");
        introImageView.setImage(introImage);
    }

    @FXML
    void startOnAction(ActionEvent event) {
        startBtn.setVisible(false);
        introImageView.setVisible(false);
        projectTitle.setVisible(false);
        try {
            App.setRoot("MainViewFXML");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
