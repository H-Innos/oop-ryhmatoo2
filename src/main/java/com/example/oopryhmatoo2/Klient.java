package com.example.oopryhmatoo2;

public class Klient {
    private final String nimi;
    private final String riik;

    public Klient(String nimi, String riik) {
        this.nimi = nimi;
        this.riik = riik;
    }

    public String getNimi(){
        return nimi;
    }
    public String getRiik() {
        return riik;
    }

    @Override
    public String toString() {
        return nimi;
    }
}
