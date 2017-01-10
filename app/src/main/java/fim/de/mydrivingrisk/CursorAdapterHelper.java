package fim.de.mydrivingrisk;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.DateFormat;

import static android.support.v4.content.ContextCompat.startActivity;



public class CursorAdapterHelper extends CursorAdapter {
    public Context context;


    public CursorAdapterHelper(Context context, Cursor cursor) {
        super(context, cursor, 0);
        this.context=context;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_type_1, parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView fahrtnameView = (TextView) view.findViewById(R.id.list_item_type1_text_view);
        TextView scoreView = (TextView) view.findViewById(R.id.list_item_type1_text_view_2);
        TextView beginView = (TextView) view.findViewById(R.id.list_item_type1_text_view_4);
        TextView endView = (TextView) view.findViewById(R.id.list_item_type1_text_view_5);
        TextView dauerView = (TextView) view.findViewById(R.id.list_item_type1_text_view_6);
        TextView idView = (TextView) view.findViewById(R.id.list_item_type1_text_view_3);

        Button delete_button = (Button) view.findViewById(R.id.list_item_type1_button_delete);



        // Extract properties from cursor
        String fahrtname = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
        System.out.println(fahrtname);
        Double score = cursor.getDouble(cursor.getColumnIndexOrThrow("Score"));
        long begin = cursor.getLong(cursor.getColumnIndexOrThrow("Beginn"));
        long end = cursor.getLong(cursor.getColumnIndexOrThrow("Ende"));
        String dauer = cursor.getString(cursor.getColumnIndexOrThrow("Fahrtdauer"));
        final Integer id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));

        DateFormat df = DateFormat.getDateTimeInstance();

        // Populate fields with extracted properties
        fahrtnameView.setText(""+fahrtname);
        scoreView.setText(""+score);
        beginView.setText("Beginn: "+df.format(begin));
        endView.setText("Ende: "+df.format(end));
        dauerView.setText("Fahrtdauer: "+dauer);
        idView.setText("ID: "+id);
        delete_button.setText(""+id);

        final DatabaseHelper MyDB4;
        //this.context=context;
        MyDB4 = new DatabaseHelper(context, "Fahrtendatenbank.db");


        delete_button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v)
                    {
                        MyDB4.deleteTripResult(id);
                        Intent intent = new Intent(context, TripHistory.class);
                        context.startActivity(intent);
                   }
        });

    }
}
