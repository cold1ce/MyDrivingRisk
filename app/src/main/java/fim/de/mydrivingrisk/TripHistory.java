/*
 *
 * @TripHistory.java 05.12.2016 (myDrivingRisk-Team)
 *
 * Copyright (c) 2016 FIM, Universität Augsburg
 *
 */

package fim.de.mydrivingrisk;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * In der TripHistory.java ist es möglich vergangene Fahrten anzuschauen, sowie die Anzahl und den Schnitt
 * aller aufgezeichneten Fahrten
 *
 * @author myDrivingRisk-Team
 */

public class TripHistory extends AppCompatActivity {

    private DatabaseHelper myDB3;
    private TextView t1, t2;
    private double scoreschnitt;
    private int anzahlfahrten;

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


        t1 = (TextView) findViewById(R.id.textView81);
        t2 = (TextView) findViewById(R.id.textView83);

        scoreschnitt = myDB3.getDurchschnittScoreAllerFahrten();
        scoreschnitt = (Math.round(10.0 * scoreschnitt) / 10.0);

        //  Den Listview füllen, sobald mehr als oder genau 1 Fahrt vorhanden ist
        Cursor todoCursor = myDB3.getListContents();
        if (todoCursor.getCount() == 0) {
            Toast.makeText(this, "Keine aufgezeichneten Fahrten vorhanden!", Toast.LENGTH_SHORT).show();
            anzahlfahrten = 0;
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        } else {
            anzahlfahrten = todoCursor.getCount();
            ListView lvItems = (ListView) findViewById(R.id.listview_3);
            CursorAdapterHelper todoAdapter = new CursorAdapterHelper(this, todoCursor);
            lvItems.setAdapter(todoAdapter);
        }

        t1.setText("" + anzahlfahrten);
        t2.setText("" + scoreschnitt);

    }

    //  Methode um alle Fahrten zu löschen
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

