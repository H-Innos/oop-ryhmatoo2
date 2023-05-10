package com.example.oopryhmatoo2;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class TegevusteAken extends Stage {

    private final Pangakonto kasutajaKonto;
    private final Pank pank;
    TextArea sõnum2 = new TextArea(""); // siia tulevad väljastatavad sõnumid

    public TegevusteAken(Pank pank, Pangakonto pangakonto) {
        this.pank = pank;
        this.kasutajaKonto = pangakonto;


        Label tegevusedLabel = new Label("Tegevused:");
        sõnum2.setWrapText(true);

        // tegevuste nupud
        Button lisaNupp = new Button("Lisa");
        Button väljastaNupp = new Button("Sula");
        Button saadaNupp = new Button("Saada");
        Button jääkNupp = new Button("Jääk");
        Button kokkuvõteNupp = new Button("Kokkuvõte");
        Button tehingudNupp = new Button("Tehingud");
        Button sulgeNupp = new Button("Sulge");

        // lisame raha
        lisaNupp.setOnAction(e -> {
            rahaSisestamine(pank);
        });

        väljastaNupp.setOnAction(e -> {
            rahaVäljastamine("Kui palju raha soovid välja võtta?");
        });

        // saadame raha
        saadaNupp.setOnAction(e -> {
            // küsime saaja kontonumbrit
            küsiSaajat("Kellele soovid raha saata?");
        });

        // väljastame kontojäägi
        jääkNupp.setOnAction(e -> {
            sõnum2.setText("Kontojääk: " + kasutajaKonto.getKontoJääk());
        });

        // väljastame konto kokkuvõtte
        kokkuvõteNupp.setOnAction(e -> {
            sõnum2.setText(kasutajaKonto.bilanss());
        });

        // väljastame kõik tehingud
        tehingudNupp.setOnAction(e -> {
            sõnum2.setText(pank.näitaTehinguid(kasutajaKonto));
        });

        // salvestame kontod peaklassi stop-meetodis ja sulgeme programmi
        sulgeNupp.setOnAction(e -> {
            Platform.exit();
        });



        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);

        grid.add(tegevusedLabel, 0, 0);
        grid.add(lisaNupp, 0, 1);
        grid.add(väljastaNupp, 0, 2);
        grid.add(saadaNupp, 0, 3);
        grid.add(jääkNupp, 0, 4);
        grid.add(kokkuvõteNupp, 0, 5);
        grid.add(tehingudNupp, 0, 6);
        grid.add(sulgeNupp, 0, 7);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(sõnum2);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(grid, scrollPane);

        Scene scene = new Scene(hBox, 600, 300);
        this.setScene(scene);
        this.setTitle("Vali tegevus - Sisse logitud: " + kasutajaKonto.getKlient().getNimi());
        this.setResizable(false);
        this.show();

        // arvutab objektide asukohad õigeks
        ColumnConstraints constraint = new ColumnConstraints(kokkuvõteNupp.getWidth());
        grid.getColumnConstraints().add(constraint);

        sõnum2.setMinHeight(scene.getHeight()-2);
        sõnum2.setPrefWidth(scene.getWidth()-constraint.getPrefWidth());
    }

    private void rahaSisestamine(Pank pank) {
        SummaKüsimiseAken rahaLisamine = new SummaKüsimiseAken("Kui palju raha soovid lisada?");
        rahaLisamine.setOnHidden(event -> {
            double summa = rahaLisamine.getSisend();
            if (summa > 0.0) {
                pank.lisaRaha(kasutajaKonto, summa);
                sõnum2.setText("Raha lisatud!");
            }
        });
    }

    private void rahaVäljastamine(String teade) {
        // küsime väljastatavat summat
        SummaKüsimiseAken rahaVäljastamine = new SummaKüsimiseAken(teade);
        rahaVäljastamine.setOnHidden(event -> {
            double summa = rahaVäljastamine.getSisend();
            try {
                if (summa > 0.0) {
                    pank.väljastaRaha(kasutajaKonto, summa);
                    sõnum2.setText("Raha välja antud!");
                }
            } catch (PolePiisavaltRahaErind ex) {
                // avab sama akna uuesti, veateade tekstikastis
                rahaVäljastamine(ex.getMessage());
            }
        });
    }

    private void küsiSaajat(String teade) {
        SaajaKüsimiseAken saajaKüsimine = new SaajaKüsimiseAken(teade);
        saajaKüsimine.setOnHidden(event -> {
            int saaja = saajaKüsimine.getSisend();
            Pangakonto konto = null;
            try {
                if (saaja != 0)
                    konto = pank.kasKontoEksisteerib(saaja);
            } catch (KontotEiEksisteeriErind ex) {
                // nagu lõpmatu tsükkel, kuna avab uuesti sama akna teise sõnumiga
                küsiSaajat(ex.getMessage());
            }

            // küsime summat siis, kui õige saaja on sisestatud
            if (konto != null)
                küsiSummat(konto, "Kui palju raha soovid saata isikule " + konto.getKlient().getNimi() + "?");
        });
    }

    private void küsiSummat(Pangakonto konto, String teade) {
        SummaKüsimiseAken summaKüsimine = new SummaKüsimiseAken(teade);

        summaKüsimine.setOnHidden(event2 -> {
            double summa = summaKüsimine.getSisend();
            try {
                if (summa > 0.0) {
                    pank.teeTehing(kasutajaKonto, konto, summa);
                    sõnum2.setText("Tehing sooritatud!");
                }
            } catch (PolePiisavaltRahaErind ex) {
                // nagu lõpmatu tsükkel
                küsiSummat(konto, ex.getMessage());
            }
        });
    }
}
