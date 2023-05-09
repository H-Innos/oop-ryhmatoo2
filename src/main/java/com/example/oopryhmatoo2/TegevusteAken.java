package com.example.oopryhmatoo2;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
    Label sõnum = new Label("");    // siia tulevad väljastatavad sõnumid

    public TegevusteAken(Pank pank, Pangakonto pangakonto) {
        this.pank = pank;
        this.kasutajaKonto = pangakonto;


        Label tegevusedLabel = new Label("Tegevused:");
        sõnum.setWrapText(true);
        sõnum.setMaxWidth(500);
        sõnum.setLineSpacing(10);

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
            DoubleProperty summa = new SimpleDoubleProperty(0.0);
            SummaKüsimiseAken rahaLisamine = new SummaKüsimiseAken("Kui palju raha soovid lisada?");
            rahaLisamine.setOnHidden(event -> {
                summa.set(rahaLisamine.getSisend());
                if (summa.getValue() > 0.0) {
                    pank.lisaRaha(kasutajaKonto, summa.get());
                    sõnum.setText("Raha lisatud!");
                }
            });
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
            sõnum.setText("Kontojääk: " + kasutajaKonto.getKontoJääk());
        });

        // väljastame konto kokkuvõtte
        kokkuvõteNupp.setOnAction(e -> {
            sõnum.setText(kasutajaKonto.bilanss());
        });

        // väljastame kõik tehingud
        tehingudNupp.setOnAction(e -> {
            sõnum.setText(pank.näitaTehinguid(kasutajaKonto));
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

        ColumnConstraints constraint = new ColumnConstraints(80);
        grid.getColumnConstraints().add(constraint);

        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(grid, sõnum);


        Scene scene = new Scene(hBox, 300, 300);
        this.setScene(scene);
        this.setTitle("Vali tegevus");
        this.setResizable(true);
        this.show();
    }

    private void rahaVäljastamine(String teade) {
        DoubleProperty summa = new SimpleDoubleProperty(0.0);
        // küsime väljastatavat summat
        SummaKüsimiseAken rahaVäljastamine = new SummaKüsimiseAken(teade);
        // siin võiks pmst veateade ilmuda küsimise akna sees
        rahaVäljastamine.setOnHidden(event -> {
            summa.set(rahaVäljastamine.getSisend());
            try {
                if (summa.getValue() > 0.0) {
                    pank.väljastaRaha(kasutajaKonto, summa.get());
                    sõnum.setText("Raha välja antud!");
                }
            } catch (PolePiisavaltRahaErind ex) {
                rahaVäljastamine(ex.getMessage());
            }
        });
    }

    private void küsiSaajat(String teade) {
        IntegerProperty saaja = new SimpleIntegerProperty(0);   // saaja kontonumber

        SaajaKüsimiseAken saajaKüsimine = new SaajaKüsimiseAken(teade);
        saajaKüsimine.setOnHidden(event -> {
            saaja.set(saajaKüsimine.getSisend());
            Pangakonto konto = null;
            try {
                konto = pank.kasKontoEksisteerib(saaja.getValue());
            } catch (KontotEiEksisteeriErind ex) {
                // nagu lõpmatu tsükkel, kuna avab uuesti sama akna teise sõnumiga
                küsiSaajat(ex.getMessage());
            }

            // küsime summat siis, kui õige saaja on sisestatud
            if (konto != null)
                küsiSummat(konto, "Palju raha soovid saata isikule " + konto.getKlient().getNimi() + "?");
        });
    }

    private void küsiSummat(Pangakonto konto, String teade) {
        DoubleProperty summa = new SimpleDoubleProperty(0.0);   // saadetav summa

        SummaKüsimiseAken summaKüsimine = new SummaKüsimiseAken(teade);

        summaKüsimine.setOnHidden(event2 -> {
            summa.set(summaKüsimine.getSisend());
            try {
                if (summa.getValue() > 0.0) {
                    pank.teeTehing(kasutajaKonto, konto, summa.doubleValue());
                    sõnum.setText("Tehing sooritatud!");
                }
            } catch (PolePiisavaltRahaErind ex) {
                // lõpmatu tsükkel
                küsiSummat(konto, ex.getMessage());
            }
        });
    }
}
