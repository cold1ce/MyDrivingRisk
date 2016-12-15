package fim.de.mydrivingrisk;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;



public class RecordTrip extends AppCompatActivity {

    //  Erstellen der benötigten Objekte
    public DatabaseHelper myDB;
    public LocationManager locationManager1;
    public LocationListener locationListener1;
    public double aktuellerbreitengrad = 0.0;
    public double aktuellerlaengengrad = 0.0;
    public double aktuellerspeed = 0.0;
    public double aktuellerspeedkmh = aktuellerspeed * 3.6;
    public double aktuellegenauigkeit = 0.0;
    public double aktuellebeschleunigung = 0.0;
    public double aktuellerichtungsdifferenz = 0.0;
    public double aktuellelateralebeschleunigung = 0.0;
    public double aktuellemaxbeschleunigung = 0.0;
    public boolean aufnahmelaeuft;
    public boolean test = true;
    public String aktuelletabelle;
    public TextView t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20;
    public ProgressBar p1;
    public String wetter, wetterkategorie, aktuellestrasse, aktuellerstrassentyp;
    public double aktuellestempolimit;
    public long sonnenaufgang, sonnenuntergang, aktuellezeit, aktuelleRechenzeit;

    // "altezeit" dient als Referenz zur aktuellenzeit, um OSM Abfragen alle x Minuten auszuführen
    public long altezeitOSM = (new Date().getTime()) - 1;
    // "altezeit" dient als Referenz zur aktuellenzeit, um Wetterabfragen alle x Minuten auszuführen
    public long altezeit = (new Date().getTime()) - 1;

    //Kann man evtl. löschen?
    public RecordTrip() throws JSONException {
    }

    @Override //Falls in der ActionBar der Zurück-Pfeil angeklickt wird,
    //ausführen des cancelRecordDialogs. Dieser überprüft ob eine Aufnahme
    //läuft und warnt davor eine Aufnahme ohne Speichern zu beenden.
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                cancelRecordDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override //Falls er Zurück-Knopf  angeklickt wird,
    //ausführen des cancelRecordDialogs. Dieser überprüft ob eine Aufnahme
    //läuft und warnt davor eine Aufnahme ohne Speichern zu beenden.
    public void onBackPressed() {
        cancelRecordDialog();
    }

    //Überprüft ob eine Aufnahme läuft und warnt davor eine Aufnahme ohne Speichern zu beenden.
    public void cancelRecordDialog() {
        try {
            locationManager1.removeUpdates(locationListener1);
        }
        catch(SecurityException e){
            System.out.println("LocatiionListener konnte nicht beendet werden!");
        }

        if (aufnahmelaeuft == true) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Aufnahme läuft! Wollen Sie die Aufnahme beenden ohne zu speichern?")
                    .setCancelable(false)
                    .setPositiveButton("Beenden", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            stop();
                            myDB.deleteFahrtentabelle(aktuelletabelle);
                            aufnahmelaeuft = false;
                            try {
                                locationManager1.removeUpdates(locationListener1);
                            }
                            catch(SecurityException e){
                                System.out.println("LocatiionListener konnte nicht beendet werden!");
                            }

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
        } else {
            try {
                locationManager1.removeUpdates(locationListener1);
            }
            catch(SecurityException e){
                System.out.println("LocatiionListener konnte nicht beendet werden!");
            }
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_trip);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        this.setTitle("Fahrt aufzeichnen");



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        t18 = (TextView) findViewById(R.id.textView11);
        t19 = (TextView) findViewById(R.id.textView15);
        t20 = (TextView) findViewById(R.id.textView18);
        p1 = (ProgressBar) findViewById(R.id.marker_progress);

        if (aktuellegenauigkeit > 10.0 || aktuellegenauigkeit <= 0.0) {
            Button b1 = (Button) findViewById(R.id.button6);
            b1.setEnabled(false);
            b1.setText("Suche GPS-Signal...");
            b1.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            p1.setVisibility(View.VISIBLE);
        }

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
                t3.setText("±" + Math.round(aktuellegenauigkeit) + " m");
                if (aktuellegenauigkeit <= 10.0) {
                    if (aufnahmelaeuft == false) {
                        Button b1 = (Button) findViewById(R.id.button6);
                        b1.setEnabled(true);
                        b1.setText("Aufzeichnung starten");
                        p1.setVisibility(View.INVISIBLE);
                        b1.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_edit, 0, 0, 0);
                    }
                }
                else {
                    if (aufnahmelaeuft == false) {
                        Button b1 = (Button) findViewById(R.id.button6);
                        b1.setEnabled(false);
                        b1.setText("Suche GPS-Signal...");
                        p1.setVisibility(View.VISIBLE);
                        b1.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    }
                }
                aktuellerichtungsdifferenz = location.getBearing();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                if (aktuellegenauigkeit != 0.0 || aktuellegenauigkeit >=30.0) {
                    Toast.makeText(RecordTrip.this, "GPS-Signal gefunden!", Toast.LENGTH_SHORT).show();
                }
                else if (aktuellegenauigkeit == 0.0 || aktuellegenauigkeit <=30.0) {
                    Toast.makeText(RecordTrip.this, "GPS-Signal verloren!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onProviderEnabled(String s) {
                Toast.makeText(RecordTrip.this, "Standortfunktion aktiviert!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String s) {
                if (aufnahmelaeuft == true) {
                    Toast.makeText(RecordTrip.this, "Sie haben Ihre Standortfunktion deaktiviert, Aufnahme wurde beendet!", Toast.LENGTH_LONG).show();
                    aufnahmelaeuft = false;
                    //  stopRecord();
                    Button b1 = (Button) findViewById(R.id.button6);
                    b1.setText("Aufzeichnung starten");
                    b1.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_edit, 0, 0, 0);
                    toTripResult();
                }
                else {
                    Toast.makeText(RecordTrip.this, "Bitte erlauben Sie der App Zugriff auf den aktuellen Standort!", Toast.LENGTH_LONG).show();
                    try {
                        locationManager1.removeUpdates(locationListener1);
                    }
                    catch(SecurityException e){
                        System.out.println("LocatiionListener konnte nicht beendet werden!");
                    }
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                }
            }

        };

        //Überprüfen ob Zugriff auf Standort erlaubt ist und ob GPS eingeschaltet ist, wenn nicht jeweils entsprechend abfangen und zurück ins Hauptmenü
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            Toast.makeText(RecordTrip.this, "Bitte erlauben Sie der App Zugriff auf den aktuellen Standort!", Toast.LENGTH_LONG).show();
            locationManager1.removeUpdates(locationListener1);
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        } else if (!locationManager1.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
            Toast.makeText(RecordTrip.this, "Bitte aktivieren sie die Standort-Funktion auf Ihrem Gerät!", Toast.LENGTH_LONG).show();
            locationManager1.removeUpdates(locationListener1);
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        } else {
            locationManager1.requestLocationUpdates("gps", 1000, 0, locationListener1);
        }

        //Sicherheitshinweis vor der Fahrt einblenden
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
        //  Überprüfen ob der Button gerade zum Starten oder zum Stoppen der Aufzeichnung gerade zuständig ist
        // Wenn keine Aufnahme läuft, versuchen eine Aufnahme zu starten
        if (aufnahmelaeuft == false) {
            //  Überprüfen ob GPS Signal einigermaßen genau ist (+/-10m)
            //  Wenn ja: Beginn der Aufzeichnung einer neuen Fahrt
            //  Wenn nein: Warnung ausgeben
            if ((aktuellegenauigkeit > 0.0) && (aktuellegenauigkeit <= 10)) {
                aufnahmelaeuft = true;
                addNewTrip();
                Toast.makeText(RecordTrip.this, "Neue Fahrt wird aufgezeichnet!", Toast.LENGTH_SHORT).show();
                b1.setText("Aufzeichnung beenden");
                //b1.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_media_rew, 0, 0, 0);
                b1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_media_stop, 0, 0, 0);
            } else {
                Toast.makeText(RecordTrip.this, "GPS zu ungenau, bitte etwas warten und erneut versuchen!", Toast.LENGTH_SHORT).show();
                //t3.setText("Genauigkeit: ±" + aktuellegenauigkeit + " m (Beim letzten Versuch)");
            }
        // Wenn eine Aufnahme läuft, diese beenden und auf die Ergebnisseite weiterleiten.
        // Zudem den Aufnahme Button wieder zurücksetzen
        } else if (aufnahmelaeuft == true) {
            aufnahmelaeuft = false;
            //  stopRecord();
            Toast.makeText(RecordTrip.this, "Aufnahme beendet!", Toast.LENGTH_SHORT).show();
            b1.setText("Aufzeichnung starten");
            b1.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_edit, 0, 0, 0);
            myDB.deleteStartwerte(aktuelletabelle);
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


        DateFormat df = DateFormat.getDateTimeInstance();
        aktuellezeit = new Date().getTime();

        //  "Leere" Startwerte einfügen um einen Crash zu verhindern
        //myDB.insertFahrtDaten(aktuellezeit, 0, aktuelletabelle, aktuellerbreitengrad, aktuellerlaengengrad, 0.0, 0.0, 0.0, 0.0, wetter, wetterkategorie, 0, 0, 0.0, "startwert", "startwert");
        //myDB.insertFahrtDaten(aktuellezeit, 0, aktuelletabelle, aktuellerbreitengrad, aktuellerlaengengrad, 0.0, 0.0, 0.0, 0.0, wetter, wetterkategorie, 0, 0, 0.0, "startwert", "startwert");
        myDB.insertFahrtDaten(aktuellezeit, 0, aktuelletabelle, aktuellerbreitengrad, aktuellerlaengengrad, 0.0, 0.0, 0.0, 0.0, wetter, wetterkategorie, 0, 0, 0.0, "startwert", "startwert");
        myDB.insertFahrtDaten(aktuellezeit, 0, aktuelletabelle, aktuellerbreitengrad, aktuellerlaengengrad, 0.0, 0.0, 0.0, 0.0, wetter, wetterkategorie, 0, 0, 0.0, "startwert", "startwert");

        //Drehender Kreis sichtbar machen um "Aufnahme" zu signalisieren
        p1.setVisibility(View.VISIBLE);

        //Voerst mal dafür sorgen dass das Display nicht ausgeht
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //  Aufnahmeschleife starten
        recordTrip();
    }


    //  Aufnahmeschleife (Wird permanent wiederholt solange bis "aufnahmeläuft" auf false gesetzt wird
    //  (Durch stoppen der Aufnahme) - Künstliche Verzögerung von 1s eingebaut da nur alle Sekunde
    //  Daten angelegt werden sollen
    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (aktuellegenauigkeit <=10.0) {
                //Abrufen der aktuellen Uhrzeit für weitere Berechnungen
                DateFormat df = DateFormat.getDateTimeInstance();
                aktuellezeit = new Date().getTime();

                //Rechenzeit berechnen, da die Eintragung nicht genau alle 1000ms stattfindet und somit
                //für das Berechnen eines genauen Scores die genaue Zeit zwischen zwei Messpunkten benötigt wird
                Date aktuelleZeitDate = new Date(aktuellezeit);
                Date letzteZeitDate = new Date(myDB.getLetzteZeit(aktuelletabelle));
                aktuelleRechenzeit = Math.abs(aktuelleZeitDate.getTime() - letzteZeitDate.getTime());

                //Anzeigen und Ausrechnen der nun sekündlich ermittelten Werte
                t17.setText("" + aktuelleRechenzeit + "ms");
                t16.setText("" + df.format(new Date(aktuellezeit)));
                t2.setText("" + aktuellerbreitengrad);
                t1.setText("" + aktuellerlaengengrad);
                //t3.setText("±" + Math.round(aktuellegenauigkeit) + " m"); Wird jetzt im Location Listener aktualisiert
                aktuellerspeedkmh = aktuellerspeed * 3.6;
                t4.setText("" + (Math.round(10.0 * aktuellerspeed) / 10.0) + " m/s | " + (Math.round(aktuellerspeedkmh)) + " km/h");
                t5.setText("" + aufnahmelaeuft);
                t6.setText("" + aktuelletabelle);

                aktuellebeschleunigung = myDB.berechneBeschleunigung(aktuelletabelle, (aktuellerspeed * 3.6), aktuelleRechenzeit);
                t7.setText("" + (Math.round(100.0 * aktuellebeschleunigung) / 100.0) + " m/s²");

                aktuellelateralebeschleunigung = myDB.berechneLateraleBeschleunigung(aktuelletabelle, aktuellerbreitengrad, aktuellerlaengengrad, aktuellerspeed, aktuellerichtungsdifferenz);
                t8.setText("" + (Math.round(100.0 * aktuellelateralebeschleunigung) / 100.0) + " m/s²");

                aktuellemaxbeschleunigung = myDB.berechneMaximalBeschleunigung(aktuellelateralebeschleunigung);
                t9.setText("" + (Math.round(100.0 * aktuellemaxbeschleunigung) / 100.0) + " m/s²");

                //  Wetterkategorie wird als letztes gesetzt, daher wird das überprüft
                //  wenn Wetterkategorie einen Wert hat, nicht mehr nach dem Wetter fragen
                if (wetterkategorie == "keine Kategorie" || wetterkategorie == null) {
                    Wetter(String.valueOf(aktuellerbreitengrad), String.valueOf(aktuellerlaengengrad));
                    wetterkategorie = myDB.wetterkategorie(aktuelletabelle);
                }

                //  altezeit wird oben mit (new Date().getTime())-1 initialiesiert, beide Werte sind in ms
                //  wenn Differenz > als 1000*60*10 ms = 10 min, dann wieder Wetter abfragen
                if ((aktuellezeit - altezeit) > (1000 * 60 * 10)) {
                    Wetter(String.valueOf(aktuellerbreitengrad), String.valueOf(aktuellerlaengengrad));
                    wetterkategorie = myDB.wetterkategorie(aktuelletabelle);
                    altezeit = (new Date().getTime()) - 1;
                }

                //Analog wie Wetterabfrage: OSM-Abfrage des Tempolimits, aktuell 5000ms, das heißt alle 5 Sekunden
                if ((aktuellezeit - altezeitOSM) > (3000)) {
                    //aktuellehoechstgeschwindigkeit = osm1.matchOSM(aktuellerbreitengrad, aktuellerlaengengrad);
                    if (aktuellegenauigkeit <= 5.0) {
                        Tempolimit(String.valueOf(aktuellerbreitengrad), String.valueOf(aktuellerlaengengrad));
                    } else {
                        aktuellestempolimit = 0.0;
                        aktuellestrasse = "GPS zu ungenau!";
                        aktuellerstrassentyp = "GPS zu ungenau!";
                    }
                    altezeitOSM = (new Date().getTime()) - 1;
                }

                t18.setText("" + aktuellestempolimit);
                t19.setText("" + aktuellestrasse);
                t20.setText("" + aktuellerstrassentyp);
                t10.setText("" + wetter);
                t11.setText("" + wetterkategorie);
                t12.setText("" + df.format(new Date(sonnenaufgang)));
                t13.setText("" + df.format(new Date(sonnenuntergang)));

                //Alle ermittelten Daten des aktuellen Datenpunktes in die Datenbank schreiben
                myDB.insertFahrtDaten(aktuellezeit, aktuelleRechenzeit, aktuelletabelle, aktuellerbreitengrad, aktuellerlaengengrad, (aktuellerspeed * 3.6), aktuellebeschleunigung, aktuellelateralebeschleunigung, aktuellemaxbeschleunigung, wetter, wetterkategorie, sonnenaufgang, sonnenuntergang, aktuellestempolimit, aktuellestrasse, aktuellerstrassentyp);
            }
            else {
                Toast.makeText(RecordTrip.this, "GPS zu ungenau, aktueller Erfassungspunkt wird nicht gespeichert!", Toast.LENGTH_SHORT).show();
                DateFormat df = DateFormat.getDateTimeInstance();
                t17.setText("" + aktuelleRechenzeit + "ms");
                t16.setText("" + df.format(new Date(aktuellezeit)));
                t2.setText("~" + aktuellerbreitengrad);
                t1.setText("~" + aktuellerlaengengrad);
                //t3.setText("±" + Math.round(aktuellegenauigkeit) + " m"); Wird jetzt oben im Location Listener gemacht
                t4.setText("?");
                t5.setText("" + aufnahmelaeuft);
                t6.setText("" + aktuelletabelle);
                t7.setText("?");
                t8.setText("?");
                t9.setText("?");
                t18.setText("?");
                t19.setText("?");
                t20.setText("?");
                t10.setText("" + wetter);
                t11.setText("" + wetterkategorie);
                t12.setText("" + df.format(new Date(sonnenaufgang)));
                t13.setText("" + df.format(new Date(sonnenuntergang)));
            }
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


    //Wenn die Aufnahme beendet wird, zur Ergebnisseite weiterleiten,
    //hierbei müssen einige Werte dieser Activitiy als Bundle weitergegeben werden
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

    //Gibt den Namen der aktuellen Tabelle zurück die verwendet wird
    public String getAktuelleTabelle() {
        return aktuelletabelle;
    }

    //Wetter-Abfrage als paralleler Task, um das Speichern der anderen Daten nicht zu behindern oder zu
    //verzögern
    public void Wetter(String latitude, String longitude) {
        Weather.placeIdTask asyncTask = new Weather.placeIdTask(new Weather.AsyncResponse() {
            @Override
            public void processFinish(String output1, long output2, long output3) {

                wetter = output1;
                sonnenaufgang = output2;
                sonnenuntergang = output3;

            }
        });
        asyncTask.execute(latitude, longitude);
    }

    //Tempolimit-Abfrage als paralleler Task, um das Speichern der anderen Daten nicht zu behindern oder zu
    //verzögern
    public void Tempolimit(String latitude, String longitude) {
        MyOSM.placeIdTask asyncTask = new MyOSM.placeIdTask(new MyOSM.AsyncResponse() {
            @Override
            public void processFinish(double output1, String output2, String output3) {

                aktuellestempolimit = output1;
                aktuellestrasse = output2;
                aktuellerstrassentyp = output3;

            }
        });
        asyncTask.execute(latitude, longitude);
    }


}
