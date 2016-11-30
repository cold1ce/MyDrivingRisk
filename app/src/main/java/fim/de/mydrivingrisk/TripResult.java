package fim.de.mydrivingrisk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import static fim.de.mydrivingrisk.R.id.textView;


public class TripResult extends AppCompatActivity {

    public TextView t1, t2, t3;
    public DatabaseHelper myDB2;

    public Button b1, b2;
    public String aktuelletabelle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDB2 = new DatabaseHelper(this, "Fahrtendatenbank.db");
        setContentView(R.layout.activity_trip_result);
        t1 = (TextView) findViewById(R.id.textView23);
        t2 = (TextView) findViewById(R.id.textView24);
        t3 = (TextView) findViewById(R.id.textView25);

        Bundle zielkorb = getIntent().getExtras();
        aktuelletabelle = zielkorb.getString("datenpaket1");

    }

    //Fahrt berechnen Button
    public void calcButton(View view) {
        Button b1 = (Button)findViewById(R.id.button8);

        RatingBar r1 = (RatingBar)findViewById(R.id.ratingBar);
        b1.setVisibility(View.GONE);
        r1.setVisibility(View.GONE);
        t1.setText("Ihr Score beträgt: ?");

        Toast.makeText(TripResult.this,"|"+aktuelletabelle, Toast.LENGTH_LONG).show();
        double average = myDB2.berechneDurschnittsgeschwindigkeit(aktuelletabelle);
        t2.setText("Durchschnittsgeschw.: "+average+" km/h");

        double beschleunigungsScore = myDB2.AccelarationScore(aktuelletabelle);
        t3.setText("BeschleunigungsScore: " + beschleunigungsScore);
    }

    public void mainMenuButton(View view) {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }


}
