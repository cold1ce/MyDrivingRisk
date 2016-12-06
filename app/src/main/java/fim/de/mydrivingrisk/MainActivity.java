package fim.de.mydrivingrisk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public boolean ALLOWED_TO_ACCESS_FINE_LOCATION = false;
    public boolean ALLOWED_TO_ACCESS_COARSE_LOCATION = false;
    public int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION ;
    public int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        }


    }

    public void toRecordTrip(View view) {
        Intent i = new Intent(getApplicationContext(), RecordTrip.class);
        startActivity(i);
    }

    public void toTripHistory(View view) {
        Intent i = new Intent(getApplicationContext(), TripHistory.class);
        startActivity(i);
    }
}
