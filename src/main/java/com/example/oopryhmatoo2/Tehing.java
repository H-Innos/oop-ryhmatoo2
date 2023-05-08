package com.example.oopryhmatoo2;

public abstract class Tehing {
    protected final int tehinguNumber;
    protected final Pangakonto saaja;
    protected final Pangakonto saatja;
    protected final double saadetudSumma;

    public Tehing(int tehinguNumber, Pangakonto saaja, Pangakonto saatja, double saadetudSumma) {
        this.tehinguNumber = tehinguNumber;
        this.saaja = saaja;
        this.saatja = saatja;
        this.saadetudSumma = saadetudSumma;
    }

    public int getTehinguNumber() {
        return tehinguNumber;
    }

    public Pangakonto getSaaja() {
        return saaja;
    }

    public Pangakonto getSaatja() {
        return saatja;
    }

    public double getSaadetudSumma() {
        return saadetudSumma;
    }

    public abstract String maksetüüp();
    public abstract void teostaMakse();
    @Override
    public String toString() {
        return maksetüüp() + " - Tehing nr " + (tehinguNumber+1) + ": " + saatja.getKlient() +
                " saatis isikule " + saaja.getKlient() + " " + saadetudSumma + " eurot.";
    }
}
