package fim.de.mydrivingrisk;

//Einbinden von anderen Klassen

import android.Manifest;
import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class RecordTrip extends AppCompatActivity {

    //  Erstellen der benötigten Objekte
    public DatabaseHelper myDB;
    private LocationManager locationManager1;
    private LocationListener locationListener1;
    public double aktuellerbreitengrad = 0.0;
    public double aktuellerlaengengrad = 0.0;
    public double aktuellerspeed = 0.0;
    public double aktuellerspeedkmh = aktuellerspeed * 3.6;
    public double aktuellegenauigkeit = 0.0;
    public double aktuellebeschleunigung = 0.0;
    public double aktuellerichtungsdifferenz = 0.0;
    public double aktuellelateralebeschleunigung = 0.0;
    public double aktuellemaxbeschleunigung = 0.0;
    public int aktuelleanzahlsatelliten = 0;
    public boolean aufnahmelaeuft;
    public String timestring;
    public String aktuelletabelle;
    public TextView t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17;
    public ProgressBar p1;
    public String wetter, wetterkategorie, stadt, temperatur;
    public long sonnenaufgang, sonnenuntergang, aktuellezeit, aktuelleRechenzeit;



    //?Stefan: Bitte erklären?
    public RecordTrip() throws JSONException {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                //Toast.makeText(RecordTrip.this, "asd", Toast.LENGTH_LONG).show();
                cancelRecordDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }





    @Override
    public void onBackPressed() {
        cancelRecordDialog();
    }

    public void cancelRecordDialog() {
        if (aufnahmelaeuft == true) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Aufnahme läuft! Wollen Sie die Aufnahme beenden ohne zu speichern?")
                    .setCancelable(false)
                    .setPositiveButton("Beenden", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            stop();
                            myDB.deleteFahrtentabelle(aktuelletabelle);
                            aufnahmelaeuft = false;
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                        }
                    })
                    .setNegativeButton("Fortsetzen", new DialogInterface.OnClickListener() {
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_trip);
        this.setTitle("Fahrt aufzeichnen");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setAc
        //  Oberflächen-Objekte zuordnen
        t1 = (TextView) findViewById(R.id.textView3);
        t2 = (TextView) findViewById(R.id.textView4);
        t3 = (TextView) findViewById(R.id.textView5);
        t4 = (TextView) findViewById(R.id.textView6);
        t5 = (TextView) findViewById(R.id.textView7);
        t6 = (TextView) findViewById(R.id.textView8);
        t7 = (TextView) findViewById(R.id.textView9);
        t8 = (TextView) findViewById(R.id.textView29);
        t9 = (TextView) findViewById(R.id.textView30);
        t10 = (TextView) findViewById(R.id.textView31);
        t11 = (TextView) findViewById(R.id.textView32);
        t12 = (TextView) findViewById(R.id.textView33);
        t13 = (TextView) findViewById(R.id.textView34);
        t14 = (TextView) findViewById(R.id.textView35);
        t15 = (TextView) findViewById(R.id.textView2);
        t16 = (TextView) findViewById(R.id.textView10);
        t17 = (TextView) findViewById(R.id.textView13);
        p1 = (ProgressBar) findViewById(R.id.marker_progress);

        //  Neue Instanz eines Datenbankhelfers, der die Datenbank Fahrdatenbank.db erstellt bzw. verwendet
        myDB = new DatabaseHelper(this, "Fahrtendatenbank.db");

        //  GPS Hilfsobjekte erzeugen
        locationManager1 = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener1 = new LocationListener() {

            //  Sobald sich die Position verändert(oder mindestens jede Sekunde) werden die in der
            //  Schleife befindlichen Variablen verändert.
            @Override
            public void onLocationChanged(Location location) {
                aktuellerbreitengrad = location.getLatitude();
                aktuellerlaengengrad = location.getLongitude();
                aktuellerspeed = location.getSpeed();
                aktuellegenauigkeit = location.getAccuracy();
                aktuellerichtungsdifferenz = location.getBearing();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }

        };

        // Here, thisActivity is the current activity


        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            Toast.makeText(RecordTrip.this, "Bitte erlauben Sie der App Zugriff auf den aktuellen Standort!", Toast.LENGTH_LONG).show();
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }
        else if (!locationManager1.isProviderEnabled( LocationManager.GPS_PROVIDER )) {
            //final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
            Toast.makeText(RecordTrip.this, "Bitte aktivieren sie die Standort-Funktion auf Ihrem Gerät!", Toast.LENGTH_LONG).show();
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }
        else {
                locationManager1.requestLocationUpdates("gps", 1000, 0, locationListener1);
            }

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String s = "<b>Bolded text</b>, <i>italic text</i>, even <u>underlined</u>!";
        builder.setMessage(R.string.caution_message)
                .setCancelable(false)
                .setPositiveButton("Verstanden und weiter", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    //  Fahrt aufzeichnen Button
    public void recordButton(View view) {
        Button b1 = (Button) findViewById(R.id.button6);
        //  Überprüfen ob der Button zum Starten oder zum Stoppen der Aufzeichnung gerade zuständig ist
        if (aufnahmelaeuft == false) {
            //  Überprüfen ob GPS Signal einigermaßen genau ist (+/-10m)
            //  Wenn ja: Beginn der Aufzeichnung einer neuen Fahrt
            //  Wenn nein: Warnung ausgeben
            if ((aktuellegenauigkeit > 0.0) && (aktuellegenauigkeit <= 10)) {
                aufnahmelaeuft = true;
                addNewTrip();
                Toast.makeText(RecordTrip.this, "Neue Fahrt wird aufgezeichnet!", Toast.LENGTH_LONG).show();
                b1.setText("Aufzeichnung beenden");
                //b1.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_media_rew, 0, 0, 0);
                b1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_media_stop, 0, 0, 0);



            } else {
                Toast.makeText(RecordTrip.this, "GPS zu ungenau, bitte etwas warten und erneut versuchen!", Toast.LENGTH_LONG).show();
                t3.setText("Genauigkeit: ±" + aktuellegenauigkeit + " m (Beim letzten Versuch)");
            }
        } else if (aufnahmelaeuft == true) {
            aufnahmelaeuft = false;
            //  stopRecord();
            Toast.makeText(RecordTrip.this, "Aufnahme beendet!", Toast.LENGTH_LONG).show();
            b1.setText("Aufzeichnung starten");
            b1.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_edit, 0, 0, 0);
            toTripResult();
        }
    }


    //  Neue Fahrt anlegen
    public void addNewTrip() {

        //  Aktuelles Datum ermitteln, dieses in Format bringen
        Date aktuellesDatum = new Date();
        SimpleDateFormat MeinFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestring = MeinFormat.format(aktuellesDatum);
        aktuelletabelle = "trip_" + timestring;

        //  Tabelle erstellen in der Fahrtendatenbank.db, mit der Aktuellen Zeit als Tabellenname
        myDB.createFahrtenTabelle(aktuelletabelle);

        //  "Leeren" Startwert einfügen um einen Crash zu verhindern
        //  myDB.insertFahrtDaten(aktuelletabelle, aktuellerbreitengrad, aktuellerlaengengrad, 0.0, 0.0, 0.0, stadt, wetter, temperatur, sonnenaufgang, sonnenuntergang, 0.0);
        DateFormat df = DateFormat.getDateTimeInstance();
        aktuellezeit = new Date().getTime();


        myDB.insertFahrtDaten(aktuellezeit, 0, aktuelletabelle, aktuellerbreitengrad, aktuellerlaengengrad, 0.0, 0.0, 0.0, 0.0, wetter, wetterkategorie, 0, 0, 0.0);

        //  Timer erstellen, um die Schleife nicht permanent zu wiederholen sondern nur jede Sekunde
        //  Timer timer = new Timer();

        p1.setVisibility(View.VISIBLE);

        //  Aufnahmeschleife aufrufen
        recordTrip();
    }


    //  Aufnahmeschleife (Wird permanent wiederholt solange bis aufnahmeläuft auf false gesetzt wird
    //  (Durch stoppen der Aufnahme) - Künstliche Verzögerung von 1s abgebaut da nur alle Sekunde
    //  Daten angelegt werden sollen
    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            DateFormat df = DateFormat.getDateTimeInstance();
            aktuellezeit = new Date().getTime();
            Date aktuelleZeitDate = new Date(aktuellezeit);
            Date letzteZeitDate = new Date(myDB.getLetzteZeit(aktuelletabelle));
            aktuelleRechenzeit = Math.abs(aktuelleZeitDate.getTime() - letzteZeitDate.getTime());
            t17.setText(""+aktuelleRechenzeit+"ms");
            t16.setText(""+aktuellezeit+" | "+df.format(new Date(aktuellezeit)));
            t1.setText(""+aktuellerbreitengrad);
            t2.setText(""+aktuellerlaengengrad);
            t3.setText("±"+Math.round(aktuellegenauigkeit)+" m");
            aktuellerspeedkmh=aktuellerspeed*3.6;
            t4.setText(""+(Math.round(10.0 * aktuellerspeed) / 10.0) + " m/s | " + (Math.round(aktuellerspeedkmh)) + " km/h");
            t5.setText(""+aufnahmelaeuft);
            t6.setText(""+aktuelletabelle);

            aktuellebeschleunigung = myDB.berechneBeschleunigung(aktuelletabelle, (aktuellerspeed * 3.6), aktuelleRechenzeit);
            t7.setText(""+(Math.round(100.0 * aktuellebeschleunigung) / 100.0)+" m/s²");

            aktuellelateralebeschleunigung = myDB.berechneLateraleBeschleunigung(aktuelletabelle, aktuellerbreitengrad, aktuellerlaengengrad, aktuellerspeed, aktuellerichtungsdifferenz);
            t8.setText(""+(Math.round(100.0 * aktuellelateralebeschleunigung) / 100.0)+" m/s²");

            aktuellemaxbeschleunigung = myDB.berechneMaximalBeschleunigung(aktuellelateralebeschleunigung);
            t9.setText(""+(Math.round(100.0 * aktuellemaxbeschleunigung) / 100.0)+" m/s²");


            Wetter(String.valueOf(aktuellerbreitengrad), String.valueOf(aktuellerlaengengrad));
            wetterkategorie = myDB.wetterkategorie(aktuelletabelle);

            t10.setText(""+wetter);
            t11.setText(""+wetterkategorie);
            t12.setText(""+df.format(new Date(sonnenaufgang)));
            t13.setText(""+df.format(new Date(sonnenuntergang)));

            /*
            t9.setText("Stadt: " + stadt);
            t10.setText("Wetter: " + wetter);
            t11.setText("Temperatur: " + temperatur);
            t12.setText("Sonnenaufgang: " + sonnenaufgang);
            t13.setText("Sonnenuntergang: " + sonnenuntergang);
            */

            //  myDB.insertFahrtDaten(aktuelletabelle, aktuellerbreitengrad, aktuellerlaengengrad, (aktuellerspeed * 3.6), aktuellebeschleunigung, aktuellelateralebeschleunigung, stadt, wetter, temperatur, sonnenaufgang, sonnenuntergang, 0.0);
            myDB.insertFahrtDaten(aktuellezeit, aktuelleRechenzeit, aktuelletabelle, aktuellerbreitengrad, aktuellerlaengengrad, (aktuellerspeed * 3.6), aktuellebeschleunigung, aktuellelateralebeschleunigung, aktuellemaxbeschleunigung, wetter, wetterkategorie, sonnenaufgang, sonnenuntergang, 0.0);

            if (aufnahmelaeuft) {
                recordTrip();
            }
        }
    };

    public void stop() {
        aufnahmelaeuft = false;
        handler.removeCallbacks(runnable);
    }

    public void recordTrip() {
        aufnahmelaeuft = true;
        handler.postDelayed(runnable, 1000);
    }


    public void toTripResult() {
        Bundle trip = new Bundle();
        trip.putString("datenpaket1", aktuelletabelle);
        trip.putDouble("breitengrad", aktuellerbreitengrad);
        trip.putDouble("laengengrad", aktuellerlaengengrad);
        trip.putDouble("speed", aktuellerspeed);
        trip.putDouble("richtungsdifferenz", aktuellerichtungsdifferenz);

        Intent i = new Intent(getApplicationContext(), TripResult.class);
        i.putExtras(trip);
        startActivity(i);
    }

    public String getAktuelleTabelle() {
        return aktuelletabelle;
    }


    public void Wetter(String latitude, String longitude) {
        Weather.placeIdTask asyncTask = new Weather.placeIdTask(new Weather.AsyncResponse() {
            @Override
            //  public void processFinish(String output1, String output2, String output3, String output4, String output5) {
            public void processFinish(String output1, long output2, long output3) {

                wetter = output1;
                sonnenaufgang = output2;
                sonnenuntergang = output3;

                /*
                stadt = output1;
                wetter = output2;
                temperatur = output3;
                sonnenaufgang = output4;
                sonnenuntergang = output5;
                 */

            }
        });
        asyncTask.execute(latitude, longitude);
    }


}
