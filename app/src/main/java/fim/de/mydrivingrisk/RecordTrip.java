package fim.de.mydrivingrisk;

//Einbinden von anderen Klassen
import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.GnssStatus;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.Timer;




public class RecordTrip extends AppCompatActivity {

    //Erstellen von Objekten
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
    public double aktuellezentripetalkraft = 0.0;
    public int aktuelleanzahlsatelliten = 0;
    public boolean aufnahmelaeuft;
    public String timestring;
    public String aktuelletabelle;
    public TextView t1, t2, t3, t4, t5, t6, t7, t8;
    public ProgressBar p1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_trip);

        //TextView Objekte zuordnen
        t1 = (TextView) findViewById(R.id.textView3);
        t2 = (TextView) findViewById(R.id.textView4);
        t3 = (TextView) findViewById(R.id.textView5);
        t4 = (TextView) findViewById(R.id.textView6);
        t5 = (TextView) findViewById(R.id.textView7);
        t6 = (TextView) findViewById(R.id.textView8);
        t7 = (TextView) findViewById(R.id.textView9);
        t8 = (TextView) findViewById(R.id.textView29);

        p1 = (ProgressBar) findViewById(R.id.marker_progress);

        //Neue Instanz eines Datenbankhelfers, der die Datenbank Fahrdatenbank.db erstellt bzw. verwendet
        myDB = new DatabaseHelper(this, "Fahrtendatenbank.db");

        //GPS Hilfsobjekte erzeugen
        locationManager1 = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener1 = new LocationListener() {

            // Sobald sich die Position verändert oder mindestens jede Sekunde neues zuweisen der
            // aktuellen Position, Geschwindigkeit und Genauigkeit
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

        //Rechte überprüfen ob GPS an ist und ob zugegriffen werden kann(Im Moment nicht funktionsfähig)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //Start der GPS-Aktualisierungen
        locationManager1.requestLocationUpdates("gps", 1000, 0, locationListener1);
    }

    //Fahrt aufzeichnen Button
    public void recordButton(View view) {
        Button b1 = (Button)findViewById(R.id.button6);
        //Überprüfen ob der Button zum Starten oder zum Stoppen der Aufzeichnung gerade zuständig ist
        if (aufnahmelaeuft == false) {
            //Überprüfen ob GPS Signal einigermaßen genau ist (+/-10m)
            //Wenn ja: Beginn der Aufzeichnung einer neuen Fahrt
            //Wenn nein: Warnung ausgeben
            if ((aktuellegenauigkeit > 0.0)&&(aktuellegenauigkeit<=10)) {
                aufnahmelaeuft = true;
                addNewTrip();
                Toast.makeText(RecordTrip.this, "Neue Fahrt wird aufgezeichnet!", Toast.LENGTH_LONG).show();
                b1.setText("Aufzeichnung beenden");
            }
            else {
                Toast.makeText(RecordTrip.this, "GPS zu ungenau, bitte etwas warten und erneut versuchen!", Toast.LENGTH_LONG).show();
                t3.setText("Genauigkeit: ±"+aktuellegenauigkeit+" m (Beim letzten Versuch)");
            }
        }
        else if (aufnahmelaeuft == true) {
            aufnahmelaeuft = false;
            //stopRecord();
            b1.setText("Fahrt aufzeichnen");
            Toast.makeText(RecordTrip.this, "Aufnahme beendet!", Toast.LENGTH_LONG).show();
            toTripResult();
        }
    }

    //Neue Fahrt anlegen
    public void addNewTrip () {

        //Aktuelles Datum ermitteln, dieses in Format bringen
        Date aktuellesDatum = new Date();
        SimpleDateFormat MeinFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestring = MeinFormat.format(aktuellesDatum);
        aktuelletabelle = "trip_"+timestring;

        //Tabelle erstellen in der Fahrtendatenbank.db, mit der Aktuellen Zeit als Tabellenname
        myDB.createFahrtenTabelle(aktuelletabelle);

        //"Leeren" Startwert einfügen um einen Crash zu verhindern
        myDB.insertFahrtDaten(aktuelletabelle, aktuellerbreitengrad, aktuellerlaengengrad, 0.0, 0.0, 0.0, "Startwetter", 0.0);

        //Timer erstellen, um die Schleife nicht permanent zu wiederholen sondern nur jede Sekunde
        //Timer timer = new Timer();

        p1.setVisibility(View.VISIBLE);

        //Aufnahmeschleife aufrufen
        recordTrip();
    }


        //Aufnahmeschleife (Wird permanent wiederholt solange bis aufnahmeläuft auf false gesetzt wird
        //(Durch stoppen der Aufnahme) - Künstliche Verzögerung von 1s abgebaut da nur alle Sekunde
        //Daten angelegt werden sollen
        private Handler handler = new Handler();

        private Runnable runnable = new Runnable() {
            @Override
            public void run() {

                t1.setText("Breitengrad: "+aktuellerbreitengrad);
                t2.setText("Längengrad: "+aktuellerlaengengrad);
                t3.setText("Genauigkeit: ±"+(Math.round(100.0 * aktuellegenauigkeit)/100)+" m");
                t4.setText("Geschwindigkeit: "+(Math.round(100.0 * aktuellerspeed)/100)+" m/s | "+(Math.round(100.0 * aktuellerspeed * 3.6)/100)+" km/h");
                t5.setText("Aufnahmestatus: "+aufnahmelaeuft);
                t6.setText("Tabellenname: "+aktuelletabelle);

                aktuellebeschleunigung = myDB.berechneBeschleunigung(aktuelletabelle, (aktuellerspeed * 3.6));
                t7.setText("Beschleunigung: "+aktuellebeschleunigung+" m/s²");
                aktuellezentripetalkraft = myDB.berechneZentripetalkraft(aktuelletabelle, aktuellerbreitengrad, aktuellerlaengengrad, aktuellerspeed, aktuellerichtungsdifferenz);
                t8.setText("Zentripetalkraft: " + aktuellezentripetalkraft + " m/s²");

                myDB.insertFahrtDaten(aktuelletabelle, aktuellerbreitengrad, aktuellerlaengengrad, (aktuellerspeed * 3.6), aktuellebeschleunigung, aktuellezentripetalkraft, "Schönes Wetter", 0.0);

                if(aufnahmelaeuft) {
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

        Intent i = new Intent(getApplicationContext(), TripResult.class);
        i.putExtras(trip);
        startActivity(i);
    }

    public String getAktuelleTabelle(){
        return aktuelletabelle;
    }



}
