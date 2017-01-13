/*
 *
 * @RecordTrip.java 08.01.2017 (myDrivingRisk-Team)
 *
 * Copyright (c) 2017 FIM, Universität Augsburg
 *
 */

package fim.de.mydrivingrisk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * AboutTheApp.java zeigt Informationen, Erklärungen und Benutzerhinweise zur App
 *
 * @author myDrivingRisk-Team
 */

public class AboutTheApp extends AppCompatActivity {
    public TextView t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_the_app);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.setTitle("Über die App");
        t1 = (TextView) findViewById(R.id.textView90);

        //  Text aus strings.xml holen und im textView der Activity anzeigen
        String text = getResources().getString(R.string.abouttheappstring);
        t1.setText(text);

    }

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

}