package fim.de.mydrivingrisk;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
    //public boolean insertFahrtDaten(String aktuelletabelle, double breitengrad, double laengengrad, double geschwindigkeit, double beschleunigung, double lateralebeschleunigung, String stadt, String wetter, String temperatur, String update, long sonnenaufgang, long sonnenuntergang, double tempolimit/*, double zeit*/) {
    public boolean insertFahrtDaten(String aktuelletabelle, double breitengrad, double laengengrad, double geschwindigkeit, double beschleunigung, double lateralebeschleunigung, String stadt, String wetter, String temperatur, String sonnenaufgang, String sonnenuntergang, double tempolimit/*, double zeit*/) {
        SQLiteDatabase db = this.getWritableDatabase(); // Überprüfen?
        ContentValues contentValues = new ContentValues();

        contentValues.put("Breitengrad", breitengrad);
        contentValues.put("Laengengrad", laengengrad);
        contentValues.put("Geschwindigkeit", geschwindigkeit);
        contentValues.put("Beschleunigung", beschleunigung);
        contentValues.put("LateraleBeschleunigung", lateralebeschleunigung);
        contentValues.put("Stadt", stadt);
        contentValues.put("Wetter", wetter);
        contentValues.put("Temperatur", temperatur);
        contentValues.put("Sonnenaufgang", sonnenaufgang);
        contentValues.put("Sonnenuntergang", sonnenaufgang);
        contentValues.put("Tempolimit", tempolimit);
//      contentValues.put("Zeit", zeit);
        long result = db.insert(aktuelletabelle, null, contentValues);

        if (result == -1)
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
        Cursor cursor = db.rawQuery("SELECT Geschwindigkeit FROM " + aktuelletabelle + " ORDER BY ID DESC LIMIT 1", null); //Letzte Geschwindigkeit auslesen

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
        //Spalten 2 Sekunden abstand generiert werden(Verzögerung durch Rechendauer)
        beschleunigung = ((aktuellegeschwindigkeit - letzte) / 1) / 3.6;
        return beschleunigung;

    }

    //Neue Tabelle für die aufzuzeichnende Fahrt anlegen
    public void createFahrtenTabelle(String timestring) {
        //Tabelle für eine Fahrt in der Fahrtendatenbank.db erstellen
        SQLiteDatabase db = this.getWritableDatabase();
        //       db.execSQL("create table " + timestring + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, sqltime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, Breitengrad REAL, Laengengrad REAL, Geschwindigkeit REAL, Beschleunigung REAL, LateraleBeschleunigung REAL, Stadt TEXT, Wetter TEXT, Temperatur TEXT, Update TEXT, Sonnenaufgang INTEGER, Sonnenuntergang INTEGER, Tempolimit REAL)");
        db.execSQL("create table " + timestring + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, sqltime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, Breitengrad REAL, Laengengrad REAL, Geschwindigkeit REAL, Beschleunigung REAL, LateraleBeschleunigung REAL, Stadt TEXT, Wetter TEXT, Temperatur TEXT, Sonnenaufgang TEXT, Sonnenuntergang TEXT, Tempolimit REAL)");

    }

    public double berechneDurschnittsgeschwindigkeit(String aktuelletabelle) {
        SQLiteDatabase db = this.getWritableDatabase();

        //Durchschnitt der Geschwindigkeiten auslesen
        Cursor cursor = db.rawQuery("SELECT avg(Geschwindigkeit) FROM " + aktuelletabelle + "", null);


        double avg = -1.0;

        //in Variable speichern
        cursor.moveToLast();
        avg = cursor.getDouble(0);

        return avg;

    }

    public double berechneAccelarationScore(String aktuelletabelle) {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor1 = db.rawQuery("SELECT count(*) FROM " + aktuelletabelle + " WHERE Beschleunigung > 0.55", null);
        cursor1.moveToFirst();
        double n = cursor1.getDouble(0);

        Cursor cursor2 = db.rawQuery("SELECT count(*) FROM " + aktuelletabelle + " WHERE Beschleunigung > 2.5", null);
        cursor2.moveToFirst();
        double s = cursor2.getDouble(0);

        //  cursor1.close();
        //  cursor2.close();
        //  falls n = 0, Division durch 0 abfangen
        if (n < 1) {
            n = n + 1;
        }

        return (s / n * 100);
    }

    public double berechneLateraleBeschleunigung(String aktuelletabelle, double latitude1, double longitude1, double aktuellerspeed, double aktuellerichtungsdifferenz) {
        double R = 6371000;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor1 = db.rawQuery("SELECT Breitengrad FROM " + aktuelletabelle + " ORDER BY ID DESC LIMIT 1", null);
        Cursor cursor2 = db.rawQuery("SELECT Laengengrad FROM " + aktuelletabelle + " ORDER BY ID DESC LIMIT 1", null);
        double latitude2 = 0.0;
        double longitude2 = 0.0;
        double lateralebeschleunigung = 0.0;

        cursor1.moveToLast();
        latitude2 = cursor1.getDouble(0);

        cursor2.moveToLast();
        longitude2 = cursor2.getDouble(0);

        double a = Math.pow(Math.sin((latitude2 - latitude1) / 2), 2) + Math.cos(latitude1) * Math.cos(latitude2) * Math.pow(Math.sin(longitude2 - longitude1), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        double r = d / (2 * Math.sin(aktuellerichtungsdifferenz / 2));

        lateralebeschleunigung = Math.pow(aktuellerspeed, 2) / r;

        //unzulässige Werte abfangen
        if (lateralebeschleunigung > 50 || lateralebeschleunigung < -50) {
            lateralebeschleunigung = 0;
        }

        return lateralebeschleunigung;
        //return Math.abs(lateralebeschleunigung);
    }

}

