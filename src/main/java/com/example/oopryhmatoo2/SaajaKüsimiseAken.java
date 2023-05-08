package com.example.oopryhmatoo2;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SaajaKüsimiseAken extends Stage {
    private int sisend; // sisestatud arv
    public SaajaKüsimiseAken(String sõnum) {
        Label juhis = new Label(sõnum);
        TextField tekstiriba = new TextField();
        Button kinnita = new Button("Kinnita");
        Label veateade = new Label("");


        kinnita.setOnAction(event -> {
            String sisse = tekstiriba.getText();
            try {
                sisend = Integer.parseInt(sisse);
                this.hide();
            } catch (NumberFormatException e) {
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

    public int getSisend() {
        return sisend;
    }
}

