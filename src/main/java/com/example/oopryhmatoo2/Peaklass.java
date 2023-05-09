package com.example.oopryhmatoo2;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class Peaklass extends Application {
    Pangakonto aktiivneKonto;
    Pank pank = new Pank();

    @Override
    public void start(Stage peaLava) {

        Pane juur = new Pane();

        // näitame mugavuse pärast kõiki kontosid konsoolis
        pank.näitaKontod();

        // loome peastseeni
        Scene peastseen = new Scene(juur, 300, 200);
        peaLava.setScene(peastseen);
        peaLava.setTitle("Parim pank maailmas");
        peaLava.show();

        Button sisseLogimine = new Button("Logi sisse/ ava konto"); // sisse logimise nupp
        juur.getChildren().add(sisseLogimine);

        // Vajutades sisse logimise nupule loome uue sisse logimise akna, et kasutaja saaks sisestada oma info
        sisseLogimine.setOnAction(event -> {
            // Sisse logimine
            SisseLogimiseAken sisseLogimiseAken = new SisseLogimiseAken(pank);
            // tegevused kui sisse logimise aken suletakse
            sisseLogimiseAken.setOnHidden(event1 -> {
                aktiivneKonto = sisseLogimiseAken.getPangakonto(); // konto millesse logiti sisse või mis loodi
                // kui sisse logimise aken suletakse enneaegselt, siis aktiivne konto on null ning me ei tee midagi
                if (aktiivneKonto != null) {
                    peaLava.hide();
                    // avame tegevuste akna
                    new TegevusteAken(pank, aktiivneKonto);
                }
            });
        });
    }

    @Override
    public void stop(){
        // toimub nii peaakna sulgemisel kui ka sulgemisnupul
        try {
            pank.salvestaKontod();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}