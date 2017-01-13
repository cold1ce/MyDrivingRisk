/*
 *
 * @RecordTrip.java 08.01.2017 (myDrivingRisk-Team)
 *
 * Copyright (c) 2017 FIM, Universität Augsburg
 *
 */

package fim.de.mydrivingrisk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
        t1 = (TextView) findViewById(R.id.textView90);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_the_app);

        String string1 = getString(R.string.aboutstring);
        System.out.println("asd" + string1);

    }
}
