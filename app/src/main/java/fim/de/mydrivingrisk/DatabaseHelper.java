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

    //public static final String DATABASE_NAME = "MyDrivingRisk.db"; //Datenbank Name
    //public static final String TABLE_NAME = "fahrten_tabelle"; //Tabellen Name

    //Diesen Konstruktor aufrufen um Datenbank zu erzeugen
    public DatabaseHelper(Context context, String name/*, SQLiteDatabase.CursorFactory factory, int version*/) {
        super(context, name, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Immer eine Tabelle generieren wenn DatabaseHelper erzeugt wird
        //db.execSQL("create table " + TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT, BREITENGRAD REAL, LAENGENGRAD REAL, GESCHWINDIGKEIT REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        //onCreate(db);
    }


    //Fahrtdaten einfügen, sammelt alle Daten die in die Tabelle sollen und fügt sie ein.
    public boolean insertFahrtDaten(String aktuelletabelle, double breitengrad, double laengengrad, double geschwindigkeit, double beschleunigung, String wetter, double tempolimit/*, double zeit*/) {
        SQLiteDatabase db = this.getWritableDatabase(); // Überprüfen?
        ContentValues contentValues = new ContentValues();

        contentValues.put("Breitengrad", breitengrad);
        contentValues.put("Laengengrad", laengengrad);
        contentValues.put("Geschwindigkeit", geschwindigkeit);
        contentValues.put("Beschleunigung", beschleunigung);
        contentValues.put("Wetter", wetter);
        contentValues.put("Tempolimit", tempolimit);
        //contentValues.put("Zeit", zeit);
        long result = db.insert(aktuelletabelle, null, contentValues);

        if(result == -1)
            return false;
        else
            return true;
    }

    //public Cursor getAllData() {
        //SQLiteDatabase db = this.getWritableDatabase(); // Überprüfen?
        //Cursor res = db.rawQuery("select * from "+TABLE_NAME, null);
        //return res;
    //}


    //Beschleunigung berechnen, im Konstruktor wird die aktuelle Geschwindigkeit sowie die aktuelle
    //Fahrtentabelle übergeben.
    public double berechneBeschleunigung(String aktuelletabelle, double aktuellegeschwindigkeit) {
        SQLiteDatabase db = this.getWritableDatabase();

        //Letzte abgespeicherte Geschwindigkeit aus der Tabelle abfragen
        Cursor cursor = db.rawQuery("SELECT Geschwindigkeit FROM "+aktuelletabelle+" ORDER BY ID DESC LIMIT 1", null); //Letzte Geschwindigkeit auslesen

        //Cursor cursor2 = db.rawQuery("SELECT Geschwindigkeit FROM "+aktuelletabelle+" ORDER BY ID DESC LIMIT 1,1;", null); //Vorletzte Geschwindigkeit auslesen

        double letzte = 0.0;
        //double vorletzte = 0.0;
        double beschleunigung = 0.0;

        //Zuvor abgefragte letzte Geschwindigkeit in Variable speichern
        cursor.moveToLast();
        letzte = cursor.getDouble(0);

        //Vorletzte Geschwindigkeit schreiben
        //cursor2.moveToLast();
        //vorletzte = cursor2.getDouble(0);

        //Berechnen der Beschleunigung (Geteilt durch 1 für eine Sekunde, sollte man evtl. noch nachbessern da manchmal zwischen zwei
        //Spalten 2 Sekunden abstand generiert werden(Verzögerung durch Rechendauer
        beschleunigung = ((aktuellegeschwindigkeit - letzte)/1)/3.6;
        return beschleunigung;

    }

    //Neue Tabelle für die aufzuzeichnende Fahrt anlegen
    public void createFahrtenTabelle(String timestring){
        //Tabelle für eine Fahrt in der Fahrtendatenbank.db erstellen
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("create table "+timestring+"(ID INTEGER PRIMARY KEY AUTOINCREMENT, sqltime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, Breitengrad REAL, Laengengrad REAL, Geschwindigkeit REAL, Beschleunigung REAL, Wetter TEXT, Tempolimit REAL)");


    }


}
