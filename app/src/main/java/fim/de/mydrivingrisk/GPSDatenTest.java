package fim.de.mydrivingrisk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GPSDatenTest extends AppCompatActivity {


    private Button b1;
    private TextView t1;
    private TextView t2;
    private TextView t3;
    private TextView t4;
    private TextView t5;
    private TextView t6;
    private TextView t7;
    private TextView t8;
    private TextView t9;
    private LocationManager locationManager1;
    private LocationListener listener1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpsdaten_test);
        setTitle("My Driving Risk - GPS Daten Test");

        t1 = (TextView) findViewById(R.id.textView10);
        t2 = (TextView) findViewById(R.id.textView11);
        t3 = (TextView) findViewById(R.id.textView12);
        t4 = (TextView) findViewById(R.id.textView13);
        t5 = (TextView) findViewById(R.id.textView14);
        t6 = (TextView) findViewById(R.id.textView15);
        t7 = (TextView) findViewById(R.id.textView17);
        t8 = (TextView) findViewById(R.id.textView20);
        t9 = (TextView) findViewById(R.id.textView22);
        b1 = (Button) findViewById(R.id.button5);




        locationManager1 = (LocationManager) getSystemService(LOCATION_SERVICE);

        listener1 = new LocationListener() {

            //public double laengealt = 0.0;
            //public double breitealt = 0.0;
            public double laengeneu = 0.0;
            public double breiteneu = 0.0;
            public float[] results = {0, 0, 0};
            @Override
            public void onLocationChanged(Location location) {
                Calendar kalender = Calendar.getInstance();
                SimpleDateFormat zeitformat = new SimpleDateFormat("HH:mm:ss");

                breiteneu = location.getLatitude(); //Breitengrad
                laengeneu = location.getLongitude(); //Längengrad
                t2.setText("  " + breiteneu); //Breitengrad
                t4.setText("  " + laengeneu); //Längengrad

                t8.setText("  " + location.getSpeed());

                t6.setText("  " + zeitformat.format(kalender.getTime()));



                t7.setText("  " + (location.getAccuracy()*100.0) +"%");
                location.distanceBetween (breiteneu, laengeneu, 48.169108, 9.522374, results);
                t9.setText("  " + (results[0]/1000) + "km");
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        configure_button();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }

    void configure_button(){
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noinspection MissingPermission
                locationManager1.requestLocationUpdates("gps", 1000, 0, listener1);
            }
        });
    }



}
