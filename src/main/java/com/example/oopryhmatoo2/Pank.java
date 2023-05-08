package com.example.oopryhmatoo2;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Pank {
    private List<Klient> kliendid;
    private List<Pangakonto> kontod;
    private List<Tehing> tehingud;

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

    public void teeTehing(Pangakonto saatja, int saaja, double summa) {
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
        // todo näidata vaid enda konto tehinguid
        // tagastame sõnena kõik tehingud
        String tulemus = "Kõik tehingud: \n";
        for (Tehing tehing : tehingud) {
            tulemus += tehing + "\n";
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
