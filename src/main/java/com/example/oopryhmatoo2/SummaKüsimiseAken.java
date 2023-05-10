package com.example.oopryhmatoo2;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SummaKüsimiseAken extends Stage {
    private double sisend = 0;
    Label juhis = new Label();
    TextField tekstiriba = new TextField();
    Button kinnita = new Button("Kinnita");
    Label veateade = new Label("");
    public SummaKüsimiseAken(String sõnum) {
        juhis.setText(sõnum);
        tekstiriba.requestFocus();

        // intellij + http://www.java2s.com/example/java/javafx/require-the-javafx-text-field-to-contain-numeric-digits-only.html
        // ei lase kirjutada tähti kasti
        tekstiriba.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[\\d\\.]*")) {
                tekstiriba.setText(newValue.replaceAll("[^\\.\\d]", ""));
            }
        });

        kinnita.setOnAction(event -> loeSumma());
        tekstiriba.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                loeSumma();
        });

        VBox vbox = new VBox(10, juhis, tekstiriba, kinnita, veateade);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(5);

        Scene scene = new Scene(vbox, 300, 250);
        this.setScene(scene);
        this.setResizable(false);
        this.show();
    }

    private void loeSumma() {
        String sisse = tekstiriba.getText();
        try {
            sisend = Double.parseDouble(sisse);
            this.hide();
        } catch (NumberFormatException e) {
            veateade.setText("Palun sisestage number");
            tekstiriba.clear();
        }
    }

    public double getSisend() {
        return sisend;
    }
}
