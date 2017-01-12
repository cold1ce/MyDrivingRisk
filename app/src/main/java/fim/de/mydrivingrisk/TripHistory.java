package fim.de.mydrivingrisk;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.ui.SeriesRenderer;
import com.androidplot.ui.widget.TextLabelWidget;
import com.androidplot.util.PixelUtils;
import com.androidplot.util.SeriesUtils;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
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

    public XYPlot plot;

    public int anzahlfahrten2;
    public int[] anzahlfahrtenarray;
    public Number[] scores;
    public XYSeries series1;



    public MyBarFormatter formatter1;
    public MyBarFormatter formatter2;
    public MyBarFormatter selectionFormatter;
    public TextLabelWidget selectionWidget;
    public Pair<Integer, XYSeries> selection;
    private enum SeriesSize {
        TEN,
        TWENTY,
        SIXTY
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

        //////DIAGRAMM BEREICH

        plot = (XYPlot) findViewById(R.id.plotXX);

        anzahlfahrten = myDB3.getAnzahlFahrten();
        anzahlfahrtenarray = new int[anzahlfahrten];
        scores = new Number[anzahlfahrten];
        System.out.println("Anzahl der Fahrten: "+anzahlfahrten+"Länge des Arrays: "+anzahlfahrtenarray.length);

        for (int i=1; i<=anzahlfahrten; i++) {
            anzahlfahrtenarray[i-1] = i;
            System.out.println("i: "+i+" : "+anzahlfahrtenarray[i-1]);
        }

        for (int j=0; j<anzahlfahrten; j++) {
            scores[j] = myDB3.getScoreOfTrip(j);
            System.out.println("j: "+j+" : "+scores[j]);
        }

        formatter1 = new MyBarFormatter(Color.rgb(100, 150, 100), Color.LTGRAY);
        formatter1.setMarginLeft(PixelUtils.dpToPix(1));
        formatter1.setMarginRight(PixelUtils.dpToPix(1));
        formatter2 = new MyBarFormatter(Color.rgb(100, 100, 150), Color.LTGRAY);
        formatter2.setMarginLeft(PixelUtils.dpToPix(1));
        formatter2.setMarginRight(PixelUtils.dpToPix(1));
        selectionFormatter = new MyBarFormatter(Color.YELLOW, Color.WHITE);


        updatePlot();






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

    //////////////////////////////DIAGRAMM BEREICH

    private void updatePlot() {
        updatePlot(null);
    }

    private void updatePlot(SeriesSize seriesSize) {

        // Remove all current series from each plot
        plot.clear();

        // Setup our Series with the selected number of elements
        series1 = new SimpleXYSeries(Arrays.asList(scores),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "");
       /* series2 = new SimpleXYSeries(Arrays.asList(series2Numbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Them");*/

        plot.setDomainBoundaries(0, Math.round((series1.size()) * 10.0) / 10.0, BoundaryMode.FIXED);
        plot.setDomainLowerBoundary(0,BoundaryMode.FIXED);

        plot.setRangeUpperBoundary(SeriesUtils.minMax(series1).getMaxY().intValue() + 5, BoundaryMode.FIXED);

        plot.setRangeLowerBoundary(0, BoundaryMode.FIXED);
        int eins = 1;
        int fuenf = 5;
        plot.setDomainStep(StepMode.INCREMENT_BY_VAL, eins);
        plot.setRangeStep(StepMode.INCREMENT_BY_VAL, fuenf);
        plot.setDomainStepValue(eins);
        plot.setRangeStepValue(fuenf);

        plot.setLinesPerRangeLabel(eins);


        plot.setLinesPerDomainLabel(eins);









        // add a new series' to the xyplot:
        // if (series1CheckBox.isChecked()) plot.addSeries(series1, formatter1);
        plot.addSeries(series1, formatter1);

        // Setup the BarRenderer with our selected options
        MyBarRenderer renderer = plot.getRenderer(MyBarRenderer.class);
        //renderer.setBarOrientation((BarRenderer.BarOrientation) spRenderStyle.getSelectedItem());
        renderer.setBarOrientation(BarRenderer.BarOrientation.SIDE_BY_SIDE);

        //renderer.setBarGroupWidth(barGroupWidthMode, barGroupWidthMode == BarRenderer.BarGroupWidthMode.FIXED_WIDTH ? sbFixedWidth.getProgress() : sbVariableWidth.getProgress());
        renderer.setBarGroupWidth(BarRenderer.BarGroupWidthMode.FIXED_WIDTH, 50);







        plot.redraw();

    }



    class MyBarFormatter extends BarFormatter {

        public MyBarFormatter(int fillColor, int borderColor) {
            super(fillColor, borderColor);
        }

        @Override
        public Class<? extends SeriesRenderer> getRendererClass() {
            return MyBarRenderer.class;
        }

        @Override
        public SeriesRenderer doGetRendererInstance(XYPlot plot) {
            return new MyBarRenderer(plot);
        }
    }

    class MyBarRenderer extends BarRenderer<MyBarFormatter> {

        public MyBarRenderer(XYPlot plot) {
            super(plot);
        }


        @Override
        public MyBarFormatter getFormatter(int index, XYSeries series) {
            if (selection != null &&
                    selection.second == series &&
                    selection.first == index) {
                return selectionFormatter;
            } else {
                return getFormatter(series);
            }
        }
    }



}

