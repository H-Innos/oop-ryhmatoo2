package com.example.oopryhmatoo2;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SaajaKüsimiseAken extends Stage {
    private int sisend; // sisestatud arv
    TextField tekstiriba = new TextField();
    Label veateade = new Label("");
    Label juhis = new Label();
    Button kinnita = new Button("Kinnita");

    public SaajaKüsimiseAken(String sõnum) {
        juhis.setText(sõnum);
        tekstiriba.requestFocus();

        // töötab nii "kinnita" nuppu, kui ka enterit vajutades
        kinnita.setOnAction(event -> {
            loeArv();
        });
        tekstiriba.setOnKeyPressed( event -> {
            if (event.getCode() == KeyCode.ENTER) {
                loeArv();
            }
        });

        VBox vbox = new VBox(10, juhis, tekstiriba, kinnita, veateade);
        Scene scene = new Scene(vbox, 300, 250);
        this.setScene(scene);
        this.setResizable(true);
        this.show();
    }

    private void loeArv(){
        String sisse = tekstiriba.getText();
        try {
            sisend = Integer.parseInt(sisse);
            this.hide();
        } catch (NumberFormatException e) {
            veateade.setText("Palun sisestage number");
            tekstiriba.clear();
        }
    }

    public int getSisend() {
        return sisend;
    }
}

