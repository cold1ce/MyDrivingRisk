//change
package fim.de.mydrivingrisk;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    //public boolean ALLOWED_TO_ACCESS_FINE_LOCATION = false;
    //public boolean ALLOWED_TO_ACCESS_COARSE_LOCATION = false;
    public int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
    public int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION;
    private LocationManager locationManager5;
    private LocationManager locationListener5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Überprüfen ob die App die Rechte hat um auf den Standort zuzugreifen, dies geschieht vor Android 6 über die Manifest Datei und schon
        //bei der Installation. Allerdings ab Android 6 einzeln während des Betriebs. Daher diese Abfrage.
        locationManager5 = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        }

        //Überprüfen ob GPS angeschaltet ist. Wenn nicht, anbieten GPS einzuschalten.
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

    //Wird bei Klick auf den Fahrt-Aufzeichnen-Button ausgeführt und öffnet
    //die Aufzeichne-Oberfläche
    public void toRecordTrip(View view) {
        Intent i = new Intent(getApplicationContext(), RecordTrip.class);
        startActivity(i);
    }

    //Wird bei Klick auf den Fahrt-Historie-Button ausgeführt und öffnet
    //die Oberfläche mit den aufgezeichneten Fahrten
    public void toTripHistory(View view) {
        Intent i = new Intent(getApplicationContext(), TripHistory.class);
        startActivity(i);
    }

    //Wird bei Klick auf den Über-die-App-Button ausgeführt und öffnet
    //eine Bedienungsanleitung und Infos über die Funktionsweise
    public void toAboutTheApp(View view) {
        Intent i = new Intent(getApplicationContext(), About.class);
        startActivity(i);
    }

    public void onBackPressed() {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("App vollständig beenden?")
                    .setCancelable(false)
                    .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            finish(); System.exit(0);
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
