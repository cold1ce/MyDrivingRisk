package fim.de.mydrivingrisk;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Julian on 21.11.2016.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyDrivingRisk.db"; //Datenbank Name
    public static final String TABLE_NAME = "fahrten_tabelle"; //Tabellen Name

    public static final String Spalte_1 = "ID";
    public static final String Spalte_2 = "BREITENGRAD";
    public static final String Spalte_3 = "LAENGENGRAD";
    public static final String Spalte_4 = "GESCHWINDIGKEIT";



    //Diesen Konstruktor aufrufen um Datenbank zu erzeugen
    public DatabaseHelper(Context context, String name/*, SQLiteDatabase.CursorFactory factory, int version*/) {
        super(context, name, null, 1);


    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Immer eine Tabelle generieren wenn DatabaseHelper erzeugt wird
        db.execSQL("create table " + TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT, BREITENGRAD REAL, LAENGENGRAD REAL, GESCHWINDIGKEIT REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(double breitengrad, double laengengrad, double geschwindigkeit) {
        SQLiteDatabase db = this.getWritableDatabase(); // Überprüfen?
        ContentValues contentValues = new ContentValues();

        contentValues.put("BREITENGRAD", breitengrad);
        contentValues.put("LAENGENGRAD", laengengrad);
        contentValues.put("GESCHWINDIGKEIT", geschwindigkeit);
        long result = db.insert(TABLE_NAME, null, contentValues);

        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase(); // Überprüfen?
        Cursor res = db.rawQuery("select * from "+TABLE_NAME, null);
        return res;
    }

}
