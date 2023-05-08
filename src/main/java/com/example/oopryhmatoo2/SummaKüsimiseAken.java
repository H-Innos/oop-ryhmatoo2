package com.example.oopryhmatoo2;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SummaKüsimiseAken extends Stage {
    private double sisend;
    public SummaKüsimiseAken(String sõnum) {
        Label juhis = new Label(sõnum);
        TextField tekstiriba = new TextField();
        Button kinnita = new Button("Kinnita");
        Label veateade = new Label("");

        kinnita.setOnAction(event -> {
            String sisse = tekstiriba.getText();
            try {
                sisend = Double.parseDouble(sisse);
                this.hide();
            }catch (NumberFormatException e) {
                veateade.setText("Palun sisestage number");
                tekstiriba.clear();
            }

        });

        VBox vbox = new VBox(10, juhis, tekstiriba, kinnita, veateade);
        Scene scene = new Scene(vbox, 300, 250);
        this.setScene(scene);
        this.setResizable(true);
        this.show();
    }

    public double getSisend() {
        return sisend;
    }
}
