package fim.de.mydrivingrisk;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;


public class TripHistory extends AppCompatActivity {

    public DatabaseHelper myDB3;
    public TextView t1, t2, t3;
    public double scoreschnitt;
    public int anzahlfahrten;



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_trip_history, menu);

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_history);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setTitle("Aufgezeichnete Fahrten");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);


        myDB3 = new DatabaseHelper(this, "Fahrtendatenbank.db");
        myDB3.createtripResultsTabelle2();


        t2 = (TextView) findViewById(R.id.textView81);
        t3 = (TextView) findViewById(R.id.textView83);

        scoreschnitt = myDB3.getDurchschnittScoreAllerFahrten();
        scoreschnitt = (Math.round(10.0 * scoreschnitt) / 10.0);







        Cursor todoCursor = myDB3.getListContents();
        if (todoCursor.getCount() == 0) {
            Toast.makeText(this, "Keine aufgezeichneten Fahrten vorhanden!", Toast.LENGTH_SHORT).show();
            anzahlfahrten = 0;
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        } else {
            anzahlfahrten = todoCursor.getCount();
            //Toast.makeText(this, "Anzahl aufgezeichneter Fahrten: " + todoCursor.getCount(), Toast.LENGTH_SHORT).show();
            // Find ListView to populate
            ListView lvItems = (ListView) findViewById(R.id.listview_3);
            // Setup cursor adapter using cursor from last step
            CursorAdapterHelper todoAdapter = new CursorAdapterHelper(this, todoCursor);
            // Attach cursor adapter to the ListView
            lvItems.setAdapter(todoAdapter);
        }

        t2.setText(""+anzahlfahrten);
        t3.setText(""+scoreschnitt);

    }

    public boolean deleteAllButton(MenuItem item) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Wollen Sie wirklich alle Fahrten löschen? Dies kann nicht rückgängig gemacht werden!")
                .setCancelable(false)
                .setPositiveButton("Alle löschen", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        myDB3.deleteAllTripResults();
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

        return true;
    }


}

