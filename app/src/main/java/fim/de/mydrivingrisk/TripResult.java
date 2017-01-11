package fim.de.mydrivingrisk;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DateFormat;

public class TripResult extends AppCompatActivity {

    public TextView t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19;
    public DatabaseHelper myDB2;

    public String aktuelletabelle;
    public double aktuellerbreitengrad, aktuellerlaengengrad, aktuellerspeed, aktuellerichtungsdifferenz, averagespeed, maxspeed, selbstbewertung;

    public double gesamtscore;
    public double brakingscore;
    public double accelerationscore;
    public double corneringscore;
    public double speedingscore;
    public double timescore;
    public String fahrtName;
    public boolean aktuellerTripGespeichert;
    public String aktuelleRisikoKlasse = "Risiko 123";
    public String fahrtDauerString;
    public long fahrtBeginn, fahrtEnde;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Fahrtergebnis");
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_trip_result);

        myDB2 = new DatabaseHelper(this, "Fahrtendatenbank.db");

        Bundle zielkorb = getIntent().getExtras();
        aktuelletabelle = zielkorb.getString("datenpaket1");
        aktuellerbreitengrad = zielkorb.getDouble("breitengrad");
        aktuellerlaengengrad = zielkorb.getDouble("laengengrad");
        aktuellerspeed = zielkorb.getDouble("speed");
        aktuellerichtungsdifferenz = zielkorb.getDouble("richtungsdifferenz");



        t1 = (TextView) findViewById(R.id.textView23);

        t2 = (TextView) findViewById(R.id.textView24);
        t3 = (TextView) findViewById(R.id.textView25);
        t4 = (TextView) findViewById(R.id.textView26);
        t5 = (TextView) findViewById(R.id.textView27);
        t6 = (TextView) findViewById(R.id.textView28);  //  Uhrzeitrisiko
        t7 = (TextView) findViewById(R.id.textView35);

        t8 = (TextView) findViewById(R.id.textView16);

        t9 = (TextView) findViewById(R.id.textView59);
        t10 = (TextView) findViewById(R.id.textView49);
        t11 = (TextView) findViewById(R.id.textView62);
        t12 = (TextView) findViewById(R.id.textView55);
        t13 = (TextView) findViewById(R.id.textView53);
        t14 = (TextView) findViewById(R.id.textView52);
        t15 = (TextView) findViewById(R.id.textView51);
        t16 = (TextView) findViewById(R.id.textView54);

        t17 = (TextView) findViewById(R.id.textView60);
        t18 = (TextView) findViewById(R.id.textView61);

        t1.setVisibility(View.INVISIBLE);
        t9.setVisibility(View.INVISIBLE);
        t10.setVisibility(View.INVISIBLE);
        t11.setVisibility(View.INVISIBLE);
        t12.setVisibility(View.INVISIBLE);
        t13.setVisibility(View.INVISIBLE);
        t14.setVisibility(View.INVISIBLE);
        t15.setVisibility(View.INVISIBLE);
        t16.setVisibility(View.INVISIBLE);

        t17.setVisibility(View.INVISIBLE);
        t2.setVisibility(View.INVISIBLE);
        t18.setVisibility(View.INVISIBLE);
        t7.setVisibility(View.INVISIBLE);
        t5.setVisibility(View.INVISIBLE);
        t4.setVisibility(View.INVISIBLE);
        t3.setVisibility(View.INVISIBLE);
        t6.setVisibility(View.INVISIBLE);

        t19 = (TextView) findViewById(R.id.textView50);

        Button b2 = (Button) findViewById(R.id.button9);
        Button b3 = (Button) findViewById(R.id.button_save);
        b2.setVisibility(View.INVISIBLE);
        b3.setVisibility(View.INVISIBLE);

        final RatingBar mBar = (RatingBar) findViewById(R.id.ratingBar);
        mBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                final float stars = mBar.getRating();

                if (stars == 1) {
                    t19.setText("Sie bewerten Ihre Fahrt als sehr risikoreich.");
                }
                else if (stars == 2) {
                    t19.setText("Sie bewerten Ihre Fahrt als risikoreich.");
                }
                else if (stars == 3) {
                    t19.setText("Sie bewerten Ihre Fahrt als neutral.");
                }
                else if (stars == 4) {
                    t19.setText("Sie bewerten Ihre Fahrt als sicher.");
                }
                else if (stars == 5) {
                    t19.setText("Sie bewerten Ihre Fahrt als sehr sicher.");
                }
                else {
                    t19.setText("\"???\"");
                }
            }
        });

        aktuellerTripGespeichert = false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                if (aktuellerTripGespeichert == false) {
                    cancelResultsDialog();
                } else {
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public void onBackPressed() {
        if (aktuellerTripGespeichert == false) {
            cancelResultsDialog();
        } else {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }
    }

    public void cancelResultsDialog() {
        if (aktuellerTripGespeichert == false) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Die Fahrt wurde noch nicht gespeichert! Wollen Sie wirklich zum Hauptmenü zurückkehren?")
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


    //  Fahrt berechnen Button
    public void calcButton(View view) {
        Button b2 = (Button) findViewById(R.id.button9);
        Button b3 = (Button) findViewById(R.id.button_save);
        Button b1 = (Button) findViewById(R.id.button8);
        b2.setVisibility(View.VISIBLE);
        b3.setVisibility(View.VISIBLE);
        RatingBar r1 = (RatingBar) findViewById(R.id.ratingBar);

        b1.setVisibility(View.INVISIBLE);
        r1.setVisibility(View.INVISIBLE);

        t1.setVisibility(View.VISIBLE);

        t9.setVisibility(View.VISIBLE);
        t10.setVisibility(View.VISIBLE);
        t11.setVisibility(View.VISIBLE);
        t12.setVisibility(View.VISIBLE);
        t13.setVisibility(View.VISIBLE);
        t14.setVisibility(View.VISIBLE);
        t15.setVisibility(View.VISIBLE);
        t16.setVisibility(View.VISIBLE);

        t17.setVisibility(View.VISIBLE);
        t2.setVisibility(View.VISIBLE);
        t18.setVisibility(View.VISIBLE);
        t7.setVisibility(View.VISIBLE);
        t5.setVisibility(View.VISIBLE);
        t4.setVisibility(View.VISIBLE);
        t3.setVisibility(View.VISIBLE);
        t6.setVisibility(View.VISIBLE);

        //  Gets linearlayout
        LinearLayout layout = (LinearLayout)findViewById(R.id.textll);
        //  Gets the layout params that will allow you to resize the layout
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        //  Changes the height and width to the specified *pixels*
        params.height = 225;
        //  params.width = 100;
        layout.setLayoutParams(params);

        averagespeed = myDB2.berechneDurschnittsgeschwindigkeit(aktuelletabelle);
        maxspeed = myDB2.berechneHöchstgeschwindigkeit(aktuelletabelle);
        accelerationscore = myDB2.berechneAccelarationScore(aktuelletabelle);
        brakingscore = myDB2.berechneBrakingScore(aktuelletabelle);
        corneringscore = myDB2.berechneCorneringScore(aktuelletabelle);
        timescore = myDB2.berechneTimeScore(aktuelletabelle);
        speedingscore = myDB2.berechneSpeedingScore(aktuelletabelle);
        fahrtBeginn = myDB2.getFahrtBeginn(aktuelletabelle);
        fahrtEnde = myDB2.getFahrtEnde(aktuelletabelle);
        fahrtDauerString = myDB2.getFahrtdauerAsString(fahrtBeginn, fahrtEnde);
        selbstbewertung = 100.0;
        gesamtscore = berechneGesamtscore(brakingscore, accelerationscore, timescore, corneringscore, speedingscore);

        aktuelleRisikoKlasse = getRisikoklasse(gesamtscore);

        t8.setText(getString(R.string.selbstbewertung_2)+""+aktuelleRisikoKlasse+"!");


        t1.setText("Ihr Score beträgt: "+(Math.round(10.0*gesamtscore)/10.0));
        t2.setText(""+(Math.round(10.0*averagespeed)/10.0)+" km/h");
        t3.setText(""+Math.round(10.0*accelerationscore)/10.0);
        t4.setText(""+Math.round(10.0*brakingscore)/10.0);
        t5.setText(""+Math.round(10.0*corneringscore)/10.0);
        t6.setText(""+Math.round(10.0*timescore)/10.0);
        t7.setText(""+Math.round(10.0*speedingscore)/10.0);
        t18.setText(""+(Math.round(maxspeed)+" km/h"));
        t17.setText(""+fahrtDauerString);
    }

    public void saveButton(View view) {

        saveTrip();
    }

    public double berechneGesamtscore(double brakingscore, double accelerationscore, double timescore, double corneringscore, double speedingscore) {
        gesamtscore = ((brakingscore * 0.33) + (accelerationscore * 0.23) + (timescore * 0.08) + (corneringscore * 0.23) + (speedingscore * 0.13));
        return gesamtscore;
    }

    public void mainMenuButton(View view) {
        if (aktuellerTripGespeichert == false) {
            cancelResultsDialog();
        } else {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }
    }

    public String getRisikoklasse(double gesamtscore) {
        if (gesamtscore >= 0 && gesamtscore <= 10.0) {
            aktuelleRisikoKlasse = "sehr sicher";
        }
        else if (gesamtscore > 10.0 && gesamtscore <= 20.0) {
            aktuelleRisikoKlasse = "sicher";
        }
        else if (gesamtscore > 20.0 && gesamtscore <= 30.0) {
            aktuelleRisikoKlasse = "neutral";
        }
        else if (gesamtscore > 30.0 && gesamtscore <= 40.0) {
            aktuelleRisikoKlasse = "risikoreich";
        }
        else if (gesamtscore > 40.0 && gesamtscore <= 140.0) {
            aktuelleRisikoKlasse = "extrem risikoreich";
        }
        else {
            aktuelleRisikoKlasse = "Fahrt mit unbekanntem Risiko";
        }

        return aktuelleRisikoKlasse;
    }

    public void saveTrip() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Fahrt benennen");
        alert.setMessage("Geben Sie einen Fahrt-Namen ein!");

        //  Set an EditText view to get user input
        final EditText input = new EditText(this);

        input.setText("Unbenannte Fahrt");
        input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(25) });
        alert.setView(input);
        alert.setCancelable(false);
        alert.setPositiveButton("Speichern", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                DateFormat df = DateFormat.getDateTimeInstance();
                fahrtName = input.getText().toString();

                double gesamtScoreGerundet = (Math.round(10.0*gesamtscore)/10.0);

                myDB2.addTripResult(fahrtBeginn, fahrtEnde, fahrtName, gesamtScoreGerundet, fahrtDauerString, selbstbewertung);
                aktuellerTripGespeichert = true;
                Button b2 = (Button) findViewById(R.id.button9);
                Button b3 = (Button) findViewById(R.id.button_save);
                Button b1 = (Button) findViewById(R.id.button8);

                b3.setVisibility(View.INVISIBLE);
                Toast.makeText(TripResult.this, "Fahrt gespeichert unter:\n" + fahrtName, Toast.LENGTH_SHORT).show();
            }
        });

        alert.setNegativeButton("Nicht speichern", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                Toast.makeText(TripResult.this, "Nicht gespeichert!", Toast.LENGTH_SHORT).show();
            }
        });

        alert.show();
    }

}
