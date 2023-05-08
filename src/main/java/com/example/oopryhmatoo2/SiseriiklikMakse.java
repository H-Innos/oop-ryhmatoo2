package com.example.oopryhmatoo2;

public class SiseriiklikMakse extends Tehing {

    public SiseriiklikMakse(int tehinguNumber, Pangakonto saaja, Pangakonto saatja, double saadetudSumma) {
        super(tehinguNumber, saaja, saatja, saadetudSumma);
    }
    public void teostaMakse(){
        saaja.rahaJuurde(saadetudSumma);
        saatja.rahaVälja(saadetudSumma);
    }

    @Override
    public String maksetüüp() {
        return "Siseriiklik makse";
    }

}
