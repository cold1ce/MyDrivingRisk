package fim.de.mydrivingrisk;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TripHistory extends ListActivity {

    private static final int LIST_ITEM_TYPE_1 = 0;
    private static final int LIST_ITEM_TYPE_2 = 1;
    private static final int LIST_ITEM_TYPE_COUNT = 2;

    //   private static final int LIST_ITEM_COUNT = 100;
    //  The first five list items will be list item type 1
    //  and the last five will be list item type 2
    //  private static final int LIST_ITEM_TYPE_1_COUNT = 5;

    public MyCustomAdapter mAdapter;
    public DatabaseHelper myDB3;
    public Button delbutton;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        delbutton = (Button) findViewById(R.id.list_item_type1_button_delete);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_history);


        this.setTitle("myDrivingRisk - Aufgezeichnete Fahrten");

        myDB3 = new DatabaseHelper(this, "Fahrtendatenbank.db");
        myDB3.createTripResultsTabelle();

        mAdapter = new MyCustomAdapter();

        /*for (int i = 0; i < LIST_ITEM_COUNT; i++) {
            if (i < LIST_ITEM_TYPE_1_COUNT)
                mAdapter.addItem("item type 1");
            else
                mAdapter.addItem("item type 2");
        }*/


        // ListView listView = (ListView) findViewById(@id\);


        //  populate an ArrayList<String> from the database and then view it
        ArrayList<String> theList = new ArrayList<>();
        Cursor data = myDB3.getListContents();
        if (data.getCount() == 0) {
            Toast.makeText(this, "Noch keine aufgezeichneten Fahrten vorhanden!", Toast.LENGTH_LONG).show();
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        } else {
            Toast.makeText(this, "Anzahl aufgezeichneter Fahrten: " + data.getCount(), Toast.LENGTH_LONG).show();
            while (data.moveToNext()) {

                DateFormat df = DateFormat.getDateTimeInstance();

                //  String beginS = df.format(new Date(begin));
                //  String endS = df.format(new Date(end));

                mAdapter.addItem("ID: " + data.getString(0), "Beginn: " + df.format(data.getLong(1)), "Ende: " + df.format(data.getLong(2)), data.getString(3), data.getDouble(4), "Fahrtdauer: " + data.getString(5), "Selbstbewertung: " + data.getString(6)/*, data.getString(7)*/);
                //  mAdapter.addItem("Startzeit: " + data.getString(1));
                //  mAdapter.addItem("Endzeit: " + data.getString(2));
                //  mAdapter.addItem("Fahrtbezeichnung: " + data.getString(3));
                //  mAdapter.addItem("Score: " + data.getString(4));
                //  mAdapter.addItem("Fahrtdauer: " + data.getString(5));
                //  mAdapter.addItem("Selbsteinschätzung: " + data.getString(6));
            }
        }
        setListAdapter(mAdapter);

        ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, theList);


    }

    //  @Override
    //  public boolean onCreateOptionsMenu(Menu menu) {
    //  Inflate the menu items for use in the action bar
    //  MenuInflater inflater = getMenuInflater();
    //  inflater.inflate(R.menu.menu_trip_history, menu);
    //  return super.onCreateOptionsMenu(menu);
    //  }


    private class MyCustomAdapter extends BaseAdapter {

        private ArrayList<String> mData = new ArrayList<String>();
        private ArrayList<String> mData2 = new ArrayList<String>();
        private ArrayList<String> mData3 = new ArrayList<String>();
        private ArrayList<String> mData4 = new ArrayList<String>();
        private ArrayList<Double> mData5 = new ArrayList<Double>();
        private ArrayList<String> mData6 = new ArrayList<String>();
        //  private ArrayList<String> mData7 = new ArrayList<String>();
        private LayoutInflater mInflater;

        public MyCustomAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addItem(final String item, final String item2, final String item3, final String item4, final Double item5, final String item6, final String item7) {
            mData.add(item);
            mData2.add(item2);
            mData3.add(item3);
            mData4.add(item4);
            mData5.add(item5);
            mData6.add(item6);
            //  mData7.add(item7);
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            //  if(position < LIST_ITEM_TYPE_1_COUNT)
            return LIST_ITEM_TYPE_1;
            //  else
            //  return LIST_ITEM_TYPE_1; // war davoer 2
        }

        @Override
        public int getViewTypeCount() {
            return LIST_ITEM_TYPE_COUNT;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public String getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View convertView2 = convertView;
            View convertView3 = convertView;
            View convertView4 = convertView;
            View convertView5 = convertView;
            View convertView6 = convertView;
            //  View convertView7 = convertView;
            //View convertView8 = convertView;

            ViewHolder holder = null;
            ViewHolder holder2 = null;
            ViewHolder holder3 = null;
            ViewHolder holder4 = null;
            ViewHolder holder5 = null;
            ViewHolder holder6 = null;
            //  ViewHolder holder7 = null;
            //ViewHolder holder8 = null;

            int type = getItemViewType(position);
            int type2 = getItemViewType(position);
            int type3 = getItemViewType(position);
            int type4 = getItemViewType(position);
            int type5 = getItemViewType(position);
            int type6 = getItemViewType(position);
            //  int type7 = getItemViewType(position);
            //int type8 = getItemViewType(position);

            if (convertView == null) {
                holder = new ViewHolder();
                holder2 = new ViewHolder();
                holder3 = new ViewHolder();
                holder4 = new ViewHolder();
                holder5 = new ViewHolder();
                holder6 = new ViewHolder();
                //holder7 = new ViewHolder();
                //holder8 = new ViewHolder();

                switch (type) {
                    case LIST_ITEM_TYPE_1:
                        convertView = mInflater.inflate(R.layout.list_item_type_1, null);
                        convertView2 = convertView;
                        convertView2 = convertView;
                        convertView3 = convertView;
                        convertView4 = convertView;
                        convertView5 = convertView;
                        convertView6 = convertView;
                        //convertView7 = convertView;
                        //convertView8 = convertView;

                        holder.textView = (TextView) convertView.findViewById(R.id.list_item_type1_text_view_3);
                        holder2.textView = (TextView) convertView2.findViewById(R.id.list_item_type1_text_view_4);
                        holder3.textView = (TextView) convertView2.findViewById(R.id.list_item_type1_text_view_5);
                        holder4.textView = (TextView) convertView2.findViewById(R.id.list_item_type1_text_view);
                        holder5.textView = (TextView) convertView2.findViewById(R.id.list_item_type1_text_view_2);
                        holder6.textView = (TextView) convertView2.findViewById(R.id.list_item_type1_text_view_6);
                        // holder7.textView = (TextView)convertView2.findViewById(R.id.list_item_type1_text_view_7); Selbstbewertung
                        //delbutton = (Button) convertView8.findViewById(R.id.list_item_type1_button_delete) ;

                        break;
                    case LIST_ITEM_TYPE_2:
                        // convertView = mInflater.inflate(R.layout.list_item_type_2, null);
                        // holder.textView = (TextView)convertView.findViewById(R.id.list_item_type);
                        // break;
                }
                convertView.setTag(holder);
                convertView.setTag(holder2);
                convertView.setTag(holder3);
                convertView.setTag(holder4);
                convertView.setTag(holder5);
                convertView.setTag(holder6);
                //  convertView.setTag(holder7);
                //convertView.setTag(holder8);

            } else {
                holder = (ViewHolder) convertView.getTag();
                holder2 = (ViewHolder) convertView2.getTag();
                holder3 = (ViewHolder) convertView3.getTag();
                holder4 = (ViewHolder) convertView4.getTag();
                holder5 = (ViewHolder) convertView5.getTag();
                holder6 = (ViewHolder) convertView6.getTag();
                //  holder7 = (ViewHolder)convertView7.getTag();
                //holder8 = (ViewHolder)convertView8.getTag();
            }
            holder.textView.setText(mData.get(position));
            holder2.textView.setText(mData2.get(position));
            holder3.textView.setText(mData3.get(position));
            holder4.textView.setText(mData4.get(position));
            holder5.textView.setText("" + mData5.get(position));
            holder6.textView.setText(mData6.get(position));
            //  holder7.textView.setText(mData7.get(position));

            //delbutton = (Button) convertView8.findViewById(R.id.list_item_type1_button_delete) ;
            //delbutton.setText("asd");

            return convertView;
        }

    }

    public static class ViewHolder {
        public TextView textView;
    }


    //public void deleteTripResultFunction(int id) {
    //   myDB3.deleteTripResult(id);
    //  }

}