/*
 *
 * @MainActivity.java 14.11.2016 (myDrivingRisk-Team)
 *
 * Copyright (c) 2016 FIM, Universität Augsburg
 *
 */

package fim.de.mydrivingrisk;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

/**
 * MainActivity.java als Hauptmenü der App, hier erreicht man alle Funktionen und
 * man findet sich auch beim Start der App hier wieder.
 *
 * @author myDrivingRisk-Team
 */

public class MainActivity extends AppCompatActivity {

    public int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
    public int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION;

    @Override   //  Aktivieren des 3-Punkte-Optionsmenüs
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //  Menügestaltung, Buttons deaktivieren/aktivieren
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);


        //  Überprüfen ob die App die Rechte hat um auf den Standort zuzugreifen, dies geschieht vor Android 6 über die Manifest-Datei und schon
        //  bei der Installation. Allerdings ab Android 6 einzeln während des Betriebs. Daher diese Abfrage.
        LocationManager locationManager5 = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        }

        //  Überprüfen ob GPS angeschaltet ist. Wenn nicht, dem Benutzer anbieten GPS einzuschalten.
        if (!locationManager5.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Standort-Funktion deaktiviert oder auf niedriger Genauigkeit. Bitte aktivieren Sie die Standort-Funktion um eine Fahrt aufzeichnen zu können!")
                    .setCancelable(false)
                    .setPositiveButton("Standort-Einstellungen", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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
    }

    //  Wird bei Klick auf den Fahrt-Aufzeichnen-Button ausgeführt und öffnet
    //  die Aufzeichne-Oberfläche
    public void toRecordTrip(View view) {
        Intent i = new Intent(getApplicationContext(), RecordTrip.class);
        startActivity(i);
    }

    //  Wird bei Klick auf den Fahrt-Historie-Button ausgeführt und öffnet
    //  die Oberfläche mit den aufgezeichneten Fahrten
    public void toTripHistory(View view) {
        Intent i = new Intent(getApplicationContext(), TripHistory.class);
        startActivity(i);
    }

    //  Wird bei Klick auf den Über die App-Button ausgeführt und öffnet
    //  die Oberfläche mit Erklärungen zu der App
    public void toAboutTheApp(View view) {
        Intent i = new Intent(getApplicationContext(), AboutTheApp.class);
        startActivity(i);
    }


    //  Wird bei Klick auf den Impressum Button im 3-Punkte-Menü ausgeführt und öffnet
    //  die Oberfläche für das Impressum
    public boolean toImpressum(MenuItem item) {
        Intent i = new Intent(getApplicationContext(), Impressum.class);
        startActivity(i);
        return true;
    }


    //  Fals der Zurück-Knopf am Handy gedrückt wird, fragen ob App beendet werden soll.
    public void onBackPressed() {
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        //  Vibrate for 500 milliseconds
        v.vibrate(100);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("App vollständig beenden?")
                .setCancelable(false)
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
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

}
