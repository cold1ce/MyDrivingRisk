package fim.de.mydrivingrisk;

//Einbinden von anderen Klassen

import android.Manifest;
import android.content.Context;
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
    public int aktuelleanzahlsatelliten = 0;
    public boolean aufnahmelaeuft;
    public String timestring;
    public String aktuelletabelle;

    public TextView t1, t2, t3, t4, t5, t6, t7, t8;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_trip);
        t1 = (TextView) findViewById(R.id.textView3);
        t2 = (TextView) findViewById(R.id.textView4);
        t3 = (TextView) findViewById(R.id.textView5);
        t4 = (TextView) findViewById(R.id.textView6);
        t5 = (TextView) findViewById(R.id.textView7);
        t6 = (TextView) findViewById(R.id.textView8);
        t7 = (TextView) findViewById(R.id.textView9);

        myDB = new DatabaseHelper(this, "Fahrtendatenbank.db");

        locationManager1 = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener1 = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                aktuellerbreitengrad = location.getLatitude();
                aktuellerlaengengrad = location.getLongitude();
                aktuellerspeed = location.getSpeed();
                aktuellegenauigkeit = location.getAccuracy();
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
        locationManager1.requestLocationUpdates("gps", 1000, 0, locationListener1);
    }


    public void recordButton(View view) {
        Button b1 = (Button)findViewById(R.id.button6);
        if (aufnahmelaeuft == false) {
            aufnahmelaeuft = true;
            addNewTrip();
            b1.setText("Aufzeichnung beenden");
        }
        else if (aufnahmelaeuft == true) {
            aufnahmelaeuft = false;
            //stopRecord();
            b1.setText("Fahrt aufzeichnen");
        }
    }

    public void addNewTrip () {
        //Aktuelles Datum ermitteln, dieses in Format bringen
        Date aktuellesDatum = new Date();
        SimpleDateFormat MeinFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestring = MeinFormat.format(aktuellesDatum);
        aktuelletabelle = "trip_"+timestring;

        //Tabelle erstellen in der Fahrtendatenbank.db, mit der Aktuellen Zeit als Tabellenname
        myDB.createFahrtenTabelle(aktuelletabelle);

        //2 Startwerte einfügen
        myDB.insertFahrtDaten(aktuelletabelle, aktuellerbreitengrad, aktuellerlaengengrad, 36.0, 0.0, "Startwetter", 0.0);
        //Timer erstellen, um die Schleife nicht permanent zu wiederholen sondern nur jede Sekunde
        Timer timer = new Timer();

        recordTrip();
    }



        private Handler handler = new Handler();

        private Runnable runnable = new Runnable() {
            @Override
            public void run() {

                t1.setText("Breitengrad: "+aktuellerbreitengrad);
                t2.setText("Längengrad:: "+aktuellerlaengengrad);
                t3.setText("Genauigkeit: ±"+aktuellegenauigkeit+" m");
                t4.setText("Geschwindigkeit: "+(Math.round(100.0 * aktuellerspeed)/100)+" m/s | "+(Math.round(100.0 * aktuellerspeed * 3.6)/100)+" km/h");
                t5.setText("Aufnahmestatus: "+aufnahmelaeuft);
                t6.setText("Tabellenname: "+aktuelletabelle);

                aktuellebeschleunigung = myDB.berechneBeschleunigung(aktuelletabelle, (aktuellerspeed * 3.6));
                t7.setText("Beschleunigung: "+aktuellebeschleunigung);




                myDB.insertFahrtDaten(aktuelletabelle, aktuellerbreitengrad, aktuellerlaengengrad, (aktuellerspeed * 3.6), aktuellebeschleunigung, "Schönes Wetter", 0.0);



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







}
