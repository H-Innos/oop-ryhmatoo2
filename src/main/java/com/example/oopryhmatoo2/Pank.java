package com.example.oopryhmatoo2;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Pank {
    private final List<Klient> kliendid;
    private final List<Pangakonto> kontod;
    private final List<Tehing> tehingud;

    public Pank() {
        this.kliendid = new ArrayList<>();
        this.kontod = new ArrayList<>();
        this.tehingud = new ArrayList<>();
        try {
            loeKontod();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

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

    // loeb kontod failist listi
    public void loeKontod() throws IOException {
        // Kontode andmed
        try (DataInputStream dis = new DataInputStream(new FileInputStream("kontod.dat"))) {
            int arv = dis.readInt();
            for (int i = 0; i < arv; i++) {
                String nimi = dis.readUTF();
                String riik = dis.readUTF();
                int kontoNumber = dis.readInt();
                double kontoJääk = dis.readDouble();
                double väljaminekud = dis.readDouble();
                double sissetulekud = dis.readDouble();
                int tehinguteArv = dis.readInt();

                Klient klient = new Klient(nimi, riik);
                if (!kliendid.contains(klient)) kliendid.add(klient);
                kontod.add(new Pangakonto(klient, kontoNumber, kontoJääk, väljaminekud, sissetulekud, tehinguteArv));
            }
        }

        // Tehingute andmed
        try (DataInputStream dis = new DataInputStream(new FileInputStream("tehingud.dat"))) {
            int arv = dis.readInt();
            for (int i = 0; i < arv; i++) {
                int tehinguNumber = dis.readInt();
                int saajaKonto = dis.readInt();
                int saatjaKonto = dis.readInt();
                double saadetudSumma = dis.readDouble();

                Pangakonto saaja = null;
                Pangakonto saatja = null;

                for (Pangakonto konto : kontod) {
                    if (konto.getKontoNumber() == saajaKonto){
                        saaja = konto;
                    }
                    if (konto.getKontoNumber() == saatjaKonto){
                        saatja = konto;
                    }
                }

                // võrreldes saatja ja saaja riike, loob kas siseriikliku või välismakse
                if (saatja.getKlient().getRiik().equals(saaja.getKlient().getRiik())){
                    tehingud.add(new SiseriiklikMakse(tehinguNumber, saaja, saatja, saadetudSumma));
                } else {
                    tehingud.add(new Välismakse(tehinguNumber, saaja, saatja, saadetudSumma));
                }
            }
        }
    }

    public void salvestaKontod() throws IOException {
        // Kirjutab faili binaarkoodina kontod
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream("kontod.dat"))) {
            dos.writeInt(kontod.size());
            for (Pangakonto konto : kontod) {
                dos.writeUTF(konto.getKlient().getNimi());
                dos.writeUTF(konto.getKlient().getRiik());
                dos.writeInt(konto.getKontoNumber());
                dos.writeDouble(konto.getKontoJääk());
                dos.writeDouble(konto.getVäljaminekud());
                dos.writeDouble(konto.getSissetulekud());
                dos.writeInt(konto.getTehinguteArv());
            }
        }
        // Tehingud
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream("tehingud.dat"))) {
            dos.writeInt(tehingud.size());
            for (Tehing tehing : tehingud) {
                dos.writeInt(tehing.getTehinguNumber());
                dos.writeInt(tehing.getSaaja().getKontoNumber());
                dos.writeInt(tehing.getSaatja().getKontoNumber());
                dos.writeDouble(tehing.getSaadetudSumma());
            }
        }
        System.out.println("Pank sulges!");
    }

    // loob panga kontode nimekirja uue konto ja tagastab selle
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

    // kontrollib kas numbriga konto eksisteerib
    public Pangakonto kasKontoEksisteerib(int kontoNumber) {
        for (Pangakonto konto : kontod) {
            if (konto.getKontoNumber() == kontoNumber)
                return konto;
        }
        throw new KontotEiEksisteeriErind("Sellise numbriga kontot ei eksisteeri.");
    }

    // loob uue tehingu, salvestab selle ja teostab raha ülekande
    public void teeTehing(Pangakonto saatja, Pangakonto saaja, double summa) {
        if (saatja.getKontoJääk() < summa) {
            throw new PolePiisavaltRahaErind("Kontol pole piisavalt raha.");
        }
        // loob vastava tehingu ja salvestab selle
        Tehing tehing;
        if (saatja.getKlient().getRiik().equals(saaja.getKlient().getRiik())){
            tehing = new SiseriiklikMakse(tehingud.size(), saaja, saatja, summa);
        } else {
            tehing = new Välismakse(tehingud.size(), saaja, saatja, summa);
        }
        tehing.teostaMakse();
        tehingud.add(tehing);

        //System.out.println("Tehing edukalt sooritatud!");
    }

    public void näitaKontod(){
        // väljastab selle vaid alguses, enne kliendi tegevusi, et oleks kergem aru saada panga klientidest
        System.out.println("Pangakontod süsteemis:");
        for (Pangakonto konto : kontod) {
            System.out.println(konto);
        }
    }
    public void kontoJääk(Pangakonto konto){
        System.out.println("Sinu konto jääk:");
        System.out.println(konto.getKontoJääk());
    }

    // tagastab kõigi selle kontoga tehtud tehingute info
    public String näitaTehinguid(Pangakonto konto) {
        // tagastame sõnena kõik tehingud
        String tulemus = "Kõik tehingud sinu kontoga: \n";
        int kontoNumber = konto.getKontoNumber();
        for (Tehing tehing : tehingud) {
            if (tehing.saatja.getKontoNumber() == kontoNumber || tehing.saaja.getKontoNumber() == kontoNumber)
                tulemus += tehing + "\n";
        }
        return tulemus;
    }

    // lisab kontole rahasumma
    public void lisaRaha(Pangakonto konto, double summa) {
        konto.rahaJuurde(summa);
        //System.out.println("Raha lisatud!");
    }

    // väljastab kontolt rahasumma
    public void väljastaRaha(Pangakonto konto, double summa) {
        // kui raha pole piisavalt, viskame erindi
        if (summa > konto.getKontoJääk())
            throw new PolePiisavaltRahaErind("Kontol pole piisavalt raha.");
        konto.rahaVälja(summa);
        //System.out.println("Raha väljastatud!");
    }
}
