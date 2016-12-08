package fim.de.mydrivingrisk;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static fim.de.mydrivingrisk.R.id.textView;


public class TripResult extends AppCompatActivity {

    public TextView t1, t2, t3, t4, t5, t6, t7;
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
    public String fahrtName;
    public boolean aktuellerTripGespeichert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("myDrivingRisk - Fahrtergebnisse");

        myDB2 = new DatabaseHelper(this, "Fahrtendatenbank.db");
        setContentView(R.layout.activity_trip_result);
        t1 = (TextView) findViewById(R.id.textView23);
        t2 = (TextView) findViewById(R.id.textView24);
        t3 = (TextView) findViewById(R.id.textView25);
        t4 = (TextView) findViewById(R.id.textView26);
        t5 = (TextView) findViewById(R.id.textView27);
        t6 = (TextView) findViewById(R.id.textView28);
        t7 = (TextView) findViewById(R.id.textView35);

        Bundle zielkorb = getIntent().getExtras();
        aktuelletabelle = zielkorb.getString("datenpaket1");
        aktuellerbreitengrad = zielkorb.getDouble("breitengrad");
        aktuellerlaengengrad = zielkorb.getDouble("laengengrad");
        aktuellerspeed = zielkorb.getDouble("speed");
        aktuellerichtungsdifferenz = zielkorb.getDouble("richtungsdifferenz");

        aktuellerTripGespeichert = false;
    }

    public void onBackPressed() {
        cancelResultsDialog();
    }

    public void cancelResultsDialog() {
        if (aktuellerTripGespeichert == false) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Die Fahrt wurde noch nicht gespeichert! Wollen Sie sicher zum Hauptmenü zurückkehren?")
                    .setCancelable(false)
                    .setPositiveButton("Zum Hauptmenü", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            myDB2.deleteFahrtentabelle(aktuelletabelle);
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                        }
                    })
                    .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
        else {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }
    }







    //Fahrt berechnen Button
    public void calcButton(View view) {

        Button b1 = (Button) findViewById(R.id.button8);

        RatingBar r1 = (RatingBar) findViewById(R.id.ratingBar);
        b1.setVisibility(View.INVISIBLE);
        r1.setVisibility(View.INVISIBLE);

//       t1.setText("Ihr Score beträgt: ?");


        double average = myDB2.berechneDurschnittsgeschwindigkeit(aktuelletabelle);
        t2.setText(""+(Math.round(100.0*average)/100.0)+" km/h");

        accelerationscore = myDB2.berechneAccelarationScore(aktuelletabelle);
        t3.setText(""+Math.round(10.0*accelerationscore)/10.0);

        brakingscore = myDB2.berechneBrakingScore(aktuelletabelle);
        t4.setText(""+Math.round(10.0*brakingscore)/10.0);

        corneringscore = myDB2.berechneCorneringScore(aktuelletabelle, aktuellerbreitengrad, aktuellerlaengengrad, aktuellerspeed, aktuellerichtungsdifferenz);
        t5.setText(""+Math.round(10.0*corneringscore)/10.0);

        timescore = myDB2.berechneTimeScore(aktuelletabelle);
        t6.setText(""+Math.round(10.0*timescore)/10.0);

        speedingscore = myDB2.berechneSpeedingScore(aktuelletabelle);
        t7.setText(""+Math.round(10.0*speedingscore)/10.0);

        gesamtscore = berechneGesamtscore(brakingscore, accelerationscore, timescore, corneringscore, speedingscore);

        t1.setText("Ihr Score beträgt: "+(Math.round(10.0*gesamtscore)/10.0));

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Fahrt benennen");
        alert.setMessage("Geben Sie einen Fahrt-Namen ein!");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);

        input.setText("Unbenannte Fahrt");
        input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(25) });
        alert.setView(input);
        alert.setCancelable(false);
        alert.setPositiveButton("Speichern", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                fahrtName = input.getText().toString();
                Date start = new Date();
                Date ende = new Date();
                double selbstbewertung = 100.0;
                double gesamtScoreGerundet = (Math.round(10.0*gesamtscore)/10.0);

                Date aktuellesDatum = new Date();
                SimpleDateFormat MeinFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                String timestring = MeinFormat.format(aktuellesDatum);

                long begin = myDB2.getFahrtBeginn(aktuelletabelle);
                long end = myDB2.getFahrtEnde(aktuelletabelle);
                DateFormat df = DateFormat.getDateTimeInstance();

               // String beginS = df.format(new Date(begin));
               // String endS = df.format(new Date(end));
               // Toast.makeText(TripResult.this, "time1: "+beginS+" time2: "+endS+".", Toast.LENGTH_LONG).show();


                myDB2.addTripResult(begin, end, fahrtName, gesamtScoreGerundet, selbstbewertung);
                aktuellerTripGespeichert = true;
                Toast.makeText(TripResult.this, "Fahrt gespeichert unter:\n" + fahrtName, Toast.LENGTH_LONG).show();
            }
        });

        alert.setNegativeButton("Nicht speichern", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(TripResult.this, "Fahrt verworfen!", Toast.LENGTH_LONG).show();
            }
        });

        alert.show();






    }

    public double berechneGesamtscore(double brakingscore, double accelerationscore, double timescore, double corneringscore, double speedingscore) {
        gesamtscore = ((brakingscore * 0.3) + (accelerationscore * 0.2) + (timescore * 0.2) + (corneringscore * 0.2) + (speedingscore * 0.1));
        return gesamtscore;
    }

    public void mainMenuButton(View view) {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }


}
