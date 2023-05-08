package com.example.oopryhmatoo2;

public class Pangakonto {
    private final Klient klient;
    private final int kontoNumber;
    private double kontoJääk;
    private double väljaminekud;
    private double sissetulekud;
    private int tehinguteArv;

    public Pangakonto(Klient klient, int kontoNumber, double kontoJääk) {
        this.klient = klient;
        this.kontoNumber = kontoNumber;
        this.kontoJääk = kontoJääk;
        this.väljaminekud = 0;
        this.sissetulekud = 0;
        this.tehinguteArv = 0;
    }

    public Pangakonto(Klient klient, int kontoNumber, double kontoJääk, double väljaminekud, double sissetulekud, int tehinguteArv) {
        this.klient = klient;
        this.kontoNumber = kontoNumber;
        this.kontoJääk = kontoJääk;
        this.väljaminekud = väljaminekud;
        this.sissetulekud = sissetulekud;
        this.tehinguteArv = tehinguteArv;
    }

    public Klient getKlient() {
        return klient;
    }

    public int getKontoNumber() {
        return kontoNumber;
    }

    public double getKontoJääk() {
        return kontoJääk;
    }

    public double getVäljaminekud() {
        return väljaminekud;
    }

    public double getSissetulekud() {
        return sissetulekud;
    }

    public int getTehinguteArv() {
        return tehinguteArv;
    }

    @Override
    public String toString() {
        return "Konto " + kontoNumber + " kuulub isikule " + klient.toString() +
                ", kontol on praegu raha " + Math.round(kontoJääk*100)/100.0 + " eurot. Sellega on tehtud " + tehinguteArv + " tehingut.";
    }

    public String bilanss(){
        String tekst;

        if (väljaminekud > sissetulekud) {
            tekst = ", kontol on väljaminekud suuremad " +
                    (väljaminekud-sissetulekud) + " võrra.";
        } else {
            tekst = ", kontol on sissetulekud suuremad " +
                    (sissetulekud-väljaminekud) + " võrra.";
        }
        return "Konto väljaminekud: " + väljaminekud +
                ", sissetulekud: " + sissetulekud + tekst;
    }

    public void rahaJuurde(double summa) {
        kontoJääk += summa;
        sissetulekud += summa;
        tehinguteArv++;
    }

    public void rahaVälja(double summa){
        kontoJääk -= summa;
        väljaminekud += summa;
        tehinguteArv++;
    }
}
