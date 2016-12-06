package fim.de.mydrivingrisk;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class TripHistory extends AppCompatActivity {

    public DatabaseHelper myDB3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_history);


        ListView listView = (ListView) findViewById(R.id.listView2);
        myDB3 = new DatabaseHelper(this, "Fahrtendatenbank.db");
        myDB3.createTripResultsTabelle();

        //populate an ArrayList<String> from the database and then view it
        ArrayList<String> theList = new ArrayList<>();
        Cursor data = myDB3.getListContents();
        if (data.getCount() == 0) {
            Toast.makeText(this, "Noch keine aufgezeichneten Fahrten vorhanden!", Toast.LENGTH_LONG).show();
        } else {
            while (data.moveToNext()) {
                theList.add("------->FAHRT ID: " + data.getString(0));
                theList.add("Startzeit: " + data.getString(1));
                theList.add("Endzeit: " + data.getString(2));
                theList.add("Fahrtbezeichnung: " + data.getString(3));
                theList.add("Score: " + data.getString(4));
                theList.add("Fahrtdauer: " + data.getString(5));
                theList.add("Selbsteinschätzung: " + data.getString(6));
            }
        }

                ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, theList);

                listView.setAdapter(listAdapter);


    }
}