package fim.de.mydrivingrisk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import static fim.de.mydrivingrisk.R.id.textView;


public class TripResult extends AppCompatActivity {

    public TextView t1, t2, t3, t4, t5;
    public DatabaseHelper myDB2;

    public Button b1, b2;
    public String aktuelletabelle;
    public double aktuellerbreitengrad, aktuellerlaengengrad, aktuellerspeed, aktuellerichtungsdifferenz;

    public double gesamtscore;
    public double brakingscore;
    public double accelerationscore;
    public double corneringscore;
    public double speedingscore;
    public double timescore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDB2 = new DatabaseHelper(this, "Fahrtendatenbank.db");
        setContentView(R.layout.activity_trip_result);
        t1 = (TextView) findViewById(R.id.textView23);
        t2 = (TextView) findViewById(R.id.textView24);
        t3 = (TextView) findViewById(R.id.textView25);
        t4 = (TextView) findViewById(R.id.textView26);
        t5 = (TextView) findViewById(R.id.textView27);

        Bundle zielkorb = getIntent().getExtras();
        aktuelletabelle = zielkorb.getString("datenpaket1");
        aktuellerbreitengrad = zielkorb.getDouble("breitengrad");
        aktuellerlaengengrad = zielkorb.getDouble("laengengrad");
        aktuellerspeed = zielkorb.getDouble("speed");
        aktuellerichtungsdifferenz = zielkorb.getDouble("richtungsdifferenz");

    }

    //Fahrt berechnen Button
    public void calcButton(View view) {
        Button b1 = (Button) findViewById(R.id.button8);

        RatingBar r1 = (RatingBar) findViewById(R.id.ratingBar);
        b1.setVisibility(View.GONE);
        r1.setVisibility(View.GONE);
        t1.setText("Ihr Score beträgt: ?");

        Toast.makeText(TripResult.this, "|" + aktuelletabelle, Toast.LENGTH_LONG).show();
        double average = myDB2.berechneDurschnittsgeschwindigkeit(aktuelletabelle);
        t2.setText("Durchschnittsgeschw.: " + average + " km/h");

        accelerationscore = myDB2.berechneAccelarationScore(aktuelletabelle);
        t3.setText("BeschleunigungsScore: " + accelerationscore);

        brakingscore = myDB2.berechneBrakingScore(aktuelletabelle);
        t4.setText("BremsenScore: " + brakingscore);

        corneringscore = myDB2.berechneCorneringScore(aktuelletabelle, aktuellerbreitengrad, aktuellerlaengengrad, aktuellerspeed, aktuellerichtungsdifferenz);
        t5.setText("KurvenScore: " + corneringscore);

    }

    public double berechneGesamtscore(double breakingscore, double accelerationscore, double timescore, double corneringscore, double speedingscore) {
        gesamtscore = ((breakingscore * 0.3) + (accelerationscore * 0.2) + (timescore * 0.2) + (corneringscore * 0.2) + (speedingscore * 0.1));
        return gesamtscore;
    }


    public void mainMenuButton(View view) {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }


}
