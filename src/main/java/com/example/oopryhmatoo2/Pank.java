package com.example.oopryhmatoo2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Pank {
    private List<Klient> kliendid;
    private List<Pangakonto> kontod;
    private List<Tehing> tehingud;

    public Pank() throws IOException {
        this.kliendid = new ArrayList<>();
        this.kontod = new ArrayList<>();
        this.tehingud = new ArrayList<>();
        loeKontod("kontod.txt");

    }

    public List<Klient> getKliendid() {
        return kliendid;
    }

    public List<Pangakonto> getKontod() {
        return kontod;
    }

    public List<Tehing> getTehingud() {
        return tehingud;
    }

    public void loeKontod(String failinimi) throws IOException {
        File fail = new File(failinimi);
        Scanner scanner = new Scanner(fail, StandardCharsets.UTF_8);
        while (scanner.hasNextLine()){
            String rida = scanner.nextLine();
            String[] osad = rida.split("; ");

            if (osad.length == 7) {
                Klient klient = new Klient(osad[0], osad[1]);

                if (!kliendid.contains(klient)) kliendid.add(klient);

                kontod.add(new Pangakonto(klient, Integer.parseInt(osad[2]),
                        Double.parseDouble(osad[3]), Double.parseDouble(osad[4]),
                        Double.parseDouble(osad[5]), Integer.parseInt(osad[6])));

            } else if (osad.length == 4) {
                Pangakonto saaja = null;
                Pangakonto saatja = null;

                for (Pangakonto konto : kontod) {
                    if (konto.getKontoNumber() == Integer.parseInt(osad[1])){
                        saaja = konto;
                    }
                    if (konto.getKontoNumber() == Integer.parseInt(osad[2])){
                        saatja = konto;
                    }
                }
                if (saatja.getKlient().getRiik().equals(saaja.getKlient().getRiik())){
                    tehingud.add(new SiseriiklikMakse(Integer.parseInt(osad[0]), saaja, saatja, Double.parseDouble(osad[3])));
                } else {
                    tehingud.add(new Välismakse(Integer.parseInt(osad[0]), saaja, saatja, Double.parseDouble(osad[3])));
                }
            }
        }
    }

    public void salvestaKontod(String failinimi) throws IOException {
        // kirjutab faili
        FileWriter kirjutaja = new FileWriter(failinimi);
        for (Pangakonto konto: kontod) {
            kirjutaja.write(
                    konto.getKlient().getNimi() +"; " + konto.getKlient().getRiik() + "; " + konto.getKontoNumber() + "; " +
                            konto.getKontoJääk() + "; " + konto.getVäljaminekud() + "; " +
                            konto.getSissetulekud() + "; " + konto.getTehinguteArv() + "\n");
        }
        for (Tehing tehing : tehingud) {
            kirjutaja.write(
                    tehing.getTehinguNumber() + "; " + tehing.getSaaja().getKontoNumber() + "; " +
                            tehing.getSaatja().getKontoNumber() + "; " + tehing.getSaadetudSumma() + "\n");
        }
        kirjutaja.close();
        System.out.println("Pank sulges!");
    }

    public Pangakonto avaKonto(String nimi, String riik, double algneSumma) {
        // loome kasutaja
        Klient kasutaja = new Klient(nimi, riik);

        // suvaline arv kontonumbriks, kui tehtud number juba eksisteerib, siis teeb uue ja kontrollib uuesti
        Random random = new Random();
        int uusKontoNumber = random.nextInt(1000000000, Integer.MAX_VALUE);
        for (int i = 0; i < kontod.size(); i++) {
            if (kontod.get(i).getKontoNumber() == uusKontoNumber) {
                uusKontoNumber = random.nextInt(1000000000, Integer.MAX_VALUE);
                i = -1;
            }
        }
        // loob uue konto
        Pangakonto kasutajaKonto = new Pangakonto(kasutaja, uusKontoNumber, algneSumma);
        kontod.add(kasutajaKonto);
        return kasutajaKonto;
    }

    public void teeTehing(Pangakonto saatja, int saaja, double summa) throws KontotEiEksisteeriErind, PolePiisavaltRahaErind {
        for (Pangakonto konto : kontod) {
            // leiame kontonumbri
            if (konto.getKontoNumber() == saaja){
                if (saatja.getKontoJääk() < summa) {
                    throw new PolePiisavaltRahaErind("Kontol pole piisavalt raha.");
                }
                // loob vastava tehingu ja salvestab selle
                Tehing tehing;
                if (saatja.getKlient().getRiik().equals(konto.getKlient().getRiik())){
                    tehing = new SiseriiklikMakse(tehingud.size(), konto, saatja, summa);
                } else {
                    tehing = new Välismakse(tehingud.size(), konto, saatja, summa);
                }
                tehing.teostaMakse();
                tehingud.add(tehing);

                System.out.println("Tehing edukalt sooritatud!");
                return;
            }
        }
        throw new KontotEiEksisteeriErind("Sellise numbriga kontot ei eksisteeri.");
    }

    public void näitaKontod(){
        // väljastab selle vaid alguses, enne kliendi tegevusi, et oleks kergem aru saada panga klientidest
        System.out.println("Klientide andmed:");
        for (Pangakonto konto : kontod) {
            System.out.println(konto);
        }
    }
    public void kontoJääk(Pangakonto konto){
        System.out.println("Sinu konto jääk:");
        System.out.println(konto.getKontoJääk());
    }

    public String näitaTehinguid() {
        // tagastame sõnena kõik tehingud
        String tulemus = "Kõik tehingud: \n";
        for (Tehing tehing : tehingud) {
            tulemus+= tehing + "\n";
        }
        return tulemus;
    }

    public void lisaRaha(Pangakonto konto, double summa) {
        konto.rahaJuurde(summa);
        System.out.println("Raha lisatud!");
    }

    public void väljastaRaha(Pangakonto konto, double summa) {
        // kui raha pole piisavalt, viskame erindi
        if (summa > konto.getKontoJääk())
            throw new PolePiisavaltRahaErind("Kontol pole piisavalt raha.");
        konto.rahaVälja(summa);
        System.out.println("Raha väljastatud!");
    }
}
