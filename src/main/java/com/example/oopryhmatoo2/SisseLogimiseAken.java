package com.example.oopryhmatoo2;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SisseLogimiseAken extends Stage {
    // võib olla kõik need isendiväljad pole vajalikud, pole kindel
    private Label juhised; // siia tulevad juhised, mida sisestada
    private TextField tekstiriba; // siia sisestatakse teksti
    private Button kinnita; // kinnitamise nupp
    private Label veateade; // siia tulevad veateated
    private VBox vbox;

    private Pangakonto pangakonto; // pangakonto, millesse sisse logitakse
    private Pank pank;
    private String kliendiNimi;
    private String kliendiRiik;
    double rahasumma;
    public SisseLogimiseAken(Pank pank) {
        this.pank = pank;

        // alati küsime nime
        juhised = new Label("Sisesta oma ees- ja perekonnanimi: ");
        tekstiriba = new TextField();
        tekstiriba.requestFocus();
        kinnita = new Button("Enter");
        veateade = new Label("");

        vbox = new VBox();
        vbox.getChildren().addAll(juhised, tekstiriba, kinnita, veateade);

        // enter või nupuvajutus
        kinnita.setOnAction(event -> otsiKlienti());
        tekstiriba.setOnKeyPressed( event -> {
            if (event.getCode() == KeyCode.ENTER) {
                otsiKlienti();
            }
        });

        Scene scene = new Scene(vbox, 300, 250);
        this.setScene(scene);
        this.setTitle("Logi sisse/ava konto");
        this.setResizable(true);
        this.show();
    }

    private void otsiKlienti() {
        kliendiNimi = tekstiriba.getText();

        kliendiNimi = töötleNimi(kliendiNimi); // teisendame nime õigesse formaati
        tekstiriba.clear();
        // kui nimega konto on olemas, tagastame selle
        for (Pangakonto pangakonto : pank.getKontod()) {
            if (pangakonto.getKlient().getNimi().equals(kliendiNimi)){
                this.pangakonto = pangakonto;
                this.hide();
            }
        }
        // kui nimega kontot ei ole, loome uue
        if (this.pangakonto == null) {
            uueKontoLoomine();
        }
    }

    public void uueKontoLoomine() {
        // küsime koduriiki
        juhised.setText("Sisesta oma koduriik: ");
        tekstiriba.requestFocus();

        kinnita.setOnAction(event -> loeRiik());
        tekstiriba.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                loeRiik();
            }
        });
    }

    private void loeRiik() {
        kliendiRiik = tekstiriba.getText();
        kliendiRiik = töötleNimi(kliendiRiik); // teisendame riigi nime sobivasse formaati
        tekstiriba.clear();
        // küsime algset rahasummat
        juhised.setText("Palju raha soovid kontole panna?");
        tekstiriba.requestFocus();

        kinnita.setOnAction(e -> loeRaha());
        tekstiriba.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                loeRaha();
        });
    }

    private void loeRaha() {
        String sisestatudSumma = tekstiriba.getText();
        // laseme sisestada ainult numbri
        try {
            rahasumma = Double.parseDouble(sisestatudSumma);
            this.pangakonto = pank.avaKonto(kliendiNimi, kliendiRiik, rahasumma);
            this.hide();
        } catch (NumberFormatException ex) {
            veateade.setText("Palun sisestage number");
            tekstiriba.clear();
            tekstiriba.requestFocus();
        }
    }

    public Pangakonto getPangakonto() {
        return pangakonto;
    }

    private String töötleNimi(String töötlemataNimi) {
        // teisendab nime algustähed suurteks
        String[] nimeOsad  = töötlemataNimi.split("\\s+");
        StringBuilder töödeldud = new StringBuilder();
        for (String nimi : nimeOsad) {
            töödeldud.append(nimi.substring(0, 1).toUpperCase())
                    .append(nimi.substring(1).toLowerCase())
                    .append(" ");
        }
        String töödeldudNimi = töödeldud.toString().trim();
        return töödeldudNimi;
    }
}
