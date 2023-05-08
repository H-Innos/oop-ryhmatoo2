package com.example.oopryhmatoo2;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class TegevusteAken extends Stage {

    private Pangakonto kasutajaKonto;
    private Pank pank;

    public TegevusteAken(Pank pank, Pangakonto pangakonto) {
        this.pank = pank;
        this.kasutajaKonto = pangakonto;


        Label tegevusedLabel = new Label("Tegevused:");
        Label sõnum = new Label("");    // siia tulevad väljastatavad sõnumid
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
                pank.lisaRaha(kasutajaKonto, summa.get());
            });
        });

        väljastaNupp.setOnAction(e -> {
            DoubleProperty summa = new SimpleDoubleProperty(0.0);
            // küsime väljastatavat summat
            SummaKüsimiseAken rahaVäljastamine = new SummaKüsimiseAken("Kui palju raha soovid välja võtta?");
            // siin võiks pmst veateade ilmuda küsimise akna sees
            rahaVäljastamine.setOnHidden(event -> {
                summa.set(rahaVäljastamine.getSisend());
                try {
                    pank.väljastaRaha(kasutajaKonto, summa.get());
                } catch (PolePiisavaltRahaErind ex) {
                    sõnum.setText(ex.getMessage());
                }
            });
        });

        // saadame raha
        saadaNupp.setOnAction(e -> {
            IntegerProperty saaja = new SimpleIntegerProperty(0);   // saaja kontonumber
            DoubleProperty summa = new SimpleDoubleProperty(0.0);   // saadetav summa

            // küsime saaja kontonumbrit
            SaajaKüsimiseAken saajaKüsimine = new SaajaKüsimiseAken("Kellele soovid raha saata?");
            saajaKüsimine.setOnHidden(event -> {
                // kuvame uue akna siis, kui saaja on sisestatud
                if (saaja.intValue() != 0) {
                    saaja.set(saajaKüsimine.getSisend());
                    // küsime summat
                    SummaKüsimiseAken summaKüsimine = new SummaKüsimiseAken("Palju raha soovid saata?");
                    summaKüsimine.setOnHidden(event2 -> {
                        summa.set(summaKüsimine.getSisend());
                        // kui tehingu tegemisel visatakse erind, väljastame selle sõnumi kastis
                        // !!
                        // siin võiks tegelikult neid veateateid näidata akende enda veateate kastides
                        // selleks peaks akende klasse veidi muutma, teha nt mingi PiiratudSummaKüsimiseAken vms
                        try {
                            pank.teeTehing(kasutajaKonto, saaja.intValue(), summa.doubleValue());
                        } catch (KontotEiEksisteeriErind ex) {
                            sõnum.setText(ex.getMessage());
                        }catch (PolePiisavaltRahaErind ex) {
                            sõnum.setText(ex.getMessage());
                        }
                    });
                }

            });
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
            sõnum.setText(pank.näitaTehinguid());
        });

        // salvestame kontod ja sulgeme programmi
        sulgeNupp.setOnAction(e -> {
            try {
                pank.salvestaKontod("kontod.txt");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            System.exit(0);
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

        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(grid, sõnum);


        Scene scene = new Scene(hBox, 300, 300);
        this.setScene(scene);
        this.setTitle("Vali tegevus");
        this.setResizable(true);
        this.show();
    }
}
