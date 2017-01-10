package fim.de.mydrivingrisk;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.util.ULocale;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class DatabaseHelper extends SQLiteOpenHelper {

    //  Abspeichern, sowie kategorisieren aller möglichen Wetterabfragen in die 3 Kategorien die für die Score-Berechnung gebraucht werden
    //  einige Wetterbeschreibungen sind auf Englisch, werden von OWM nur so zur Verfügung gestellt
    private String[] dry = {"Dunst", "ein paar Wolken", "einige Gewitter", "Frische Brise", "Gewitter", "Heftiges Gewitter", "heiß",
            "Hochwind, annähender Sturm", "kalt", "klarer Himmel", "Leichte Brise", "leichte Gewitter", "Mäßige Brise", "Milde Brise",
            "Nebel", "Rauch", "Sand", "sand", "Sand / Staubsturm", "schwere Gewitter", "Schwerer Sturm", "Starke Brise", "Staub", "dust", "Sturm",
            "Tornado", "tornado", "trüb", "überwiegend bewölkt", "Vulkanasche", "volcanic ash", "Windböen", "squalls", "windig", "Windstille",
            "wolkenbedeckt"};
    private String[] wet = {"ragged shower rain", "einige Regenschauer", "Gewitter mit leichtem Nieselregen", "Gewitter mit leichtem Regen",
            "Gewitter mit Nieselregen", "Gewitter mit Regen", "Gewitter mit starkem Nieselregen", "leichter Nieselregen", "leichter Regen",
            "leichtes Nieseln", "mäßiger Regen", "mäßiger Schnee", "Nieseln", "Nieselregen", "Nieselschauer", "Regen", "Regenschauer",
            "shower rain and drizzle", "Regenschauer und Nieseln", "starker Nieselregen", "starkes Nieseln"};
    private String[] extreme = {"Eisregen", "Gewitter mit starkem Regen", "Graupel", "Hagel", "heftige Regenschauer", "heftiger Schneefall",
            "Hurrikan", "leichte Regenschauer", "light rain and snow", "leichter Regen und Schnee", "leichter Schneeschauer", "light shower snow",
            "Orkan", "rain and snow", "Regen und Schnee", "Schnee", "shower sleet", "Schneeschauer", "sehr starker Regen",
            "heavy shower rain and drizzle", "starker Regenschauer und Nieseln", "starker Schneeschauer", "heavy shower snow", "Starkregen",
            "Tropensturm"};


    //  Abspeichern des Erdradius in einer Konstanten R. Dieser wird für spätere Berechnungen des Kurvenverhaltens benötigt.
    private final double R = 6371000;


    //  Diesen Konstruktor aufrufen um Datenbank zu erzeugen
    public DatabaseHelper(Context context, String name/*, SQLiteDatabase.CursorFactory factory, int version*/) {
        super(context, name, null, 1);

    }

    //  Sobald ein DataBaseHelper gestartet wird, wird automatisch eine Tabelle für die Ergebnisse der Fahrten angelegt.
    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL("create table tripResultsTabelle2 (ID INTEGER PRIMARY KEY AUTOINCREMENT, Beginn TEXT, Ende TEXT, Name TEXT, Score REAL, Fahrtdauer REAL, Selbstbewertung REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    //  Fahrtdaten einfügen, sammelt alle Daten die in die Tabelle sollen und fügt sie ein.
    public boolean insertFahrtDaten(long meineZeit, long rechenZeit, String aktuelletabelle, double breitengrad, double laengengrad, double geschwindigkeit, double beschleunigung, double lateralebeschleunigung, double maxbeschleunigung, String wetter, String wetterkategorie, long sonnenaufgang, long sonnenuntergang, double tempolimit/*, double zeit*/, String strasse, String strassentyp) {
        SQLiteDatabase db = this.getWritableDatabase(); // Überprüfen?

        ContentValues contentValues = new ContentValues();

        contentValues.put("Zeit", meineZeit);
        contentValues.put("Rechenzeit", rechenZeit);
        contentValues.put("Breitengrad", breitengrad);
        contentValues.put("Laengengrad", laengengrad);
        contentValues.put("Geschwindigkeit", geschwindigkeit);
        contentValues.put("Beschleunigung", beschleunigung);
        contentValues.put("LateraleBeschleunigung", lateralebeschleunigung);
        contentValues.put("MaxBeschleunigung", maxbeschleunigung);
        contentValues.put("Wetter", wetter);
        contentValues.put("Wetterkategorie", wetterkategorie);
        contentValues.put("Sonnenaufgang", sonnenaufgang);
        contentValues.put("Sonnenuntergang", sonnenuntergang);
        contentValues.put("Tempolimit", tempolimit);
        contentValues.put("AktuelleStrasse", strasse);
        contentValues.put("AktuellerStrassentyp", strassentyp);
        //  contentValues.put("Zeit", zeit);
        long result = db.insert(aktuelletabelle, null, contentValues);

        if (result == -1)
            return false;
        else
            return true;
    }

    /*
        public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase(); // Überprüfen?
        Cursor res = db.rawQuery("select * from "+TABLE_NAME, null);
        return res;
        }
    */
    public void createtripResultsTabelle2() {
        SQLiteDatabase db = this.getWritableDatabase(); // Überprüfen?
        db.execSQL("CREATE TABLE IF NOT EXISTS TripResultsTabelle2 (_id INTEGER PRIMARY KEY AUTOINCREMENT, Beginn DATETIME, Ende DATETIME, Name TEXT, Score REAL, Fahrtdauer REAL, Selbstbewertung REAL)");
    }

    public void deleteFahrtentabelle(String aktuelletabelle) {
        SQLiteDatabase db = this.getWritableDatabase(); // Überprüfen?
        db.execSQL("DROP TABLE IF EXISTS " + aktuelletabelle);
    }

    public void deleteTripResult(int id) {
        SQLiteDatabase db = this.getWritableDatabase(); // Überprüfen?
        db.execSQL("DELETE FROM TripResultsTabelle2 WHERE _id = " + id);
    }

    public void deleteAllTripResults() {
        SQLiteDatabase db = this.getWritableDatabase(); // Überprüfen?
        db.execSQL("DELETE FROM TripResultsTabelle2");
    }

    public void deleteStartwerte(String aktuelletabelle) {
        SQLiteDatabase db = this.getWritableDatabase(); // Überprüfen?
        db.execSQL("DELETE FROM " + aktuelletabelle + " WHERE id in (SELECT id FROM " + aktuelletabelle + " LIMIT 2 OFFSET 0)");
    }

    public String getFahrtdauerAsString(long fahrtBeginn, long fahrtEnde) {
        DateFormat df = DateFormat.getDateTimeInstance();

        //long tripStart = tripStartDate; //muss von Date gecastet werden
        //long tripEnde = tripEndeDate; //muss von Date gecastet werden
        String fahrtDauer = "-";

        Date startDate = new Date(fahrtBeginn);
        Date endDate = new Date(fahrtEnde);

        //long duration  = endDate.getTime() - startDate.getTime();

        // long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        // long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        // long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);

        String diff = "";
        long timeDiff = Math.abs(endDate.getTime() - startDate.getTime());
        if (timeDiff >= 0 && timeDiff < 60000) {
            diff = TimeUnit.MILLISECONDS.toSeconds(timeDiff) + "s";
        } else if (timeDiff >= 60000 && timeDiff < 3600000) {
            diff = TimeUnit.MILLISECONDS.toMinutes(timeDiff) + "min";
        } else {
            diff = String.format("%dh %dmin", TimeUnit.MILLISECONDS.toHours(timeDiff),
                    TimeUnit.MILLISECONDS.toMinutes(timeDiff) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeDiff)));
        }


        // fahrtDauer = (diffInHours+"h "+diffInMinutes+"min "+diffInSeconds+"sec ");
        return diff;
    }

    public boolean addTripResult(long tripStartDate, long tripEndeDate, String tripName, double score, String fahrtDauer, double selbstBewertung) {

        SQLiteDatabase db = this.getWritableDatabase(); // Überprüfen?
        createtripResultsTabelle2();
        ContentValues contentValues = new ContentValues();

        DateFormat df = DateFormat.getDateTimeInstance();

        long tripStart = tripStartDate; //muss von Date gecastet werden
        long tripEnde = tripEndeDate; //muss von Date gecastet werden


        contentValues.put("Beginn", tripStart);
        contentValues.put("Ende", tripEnde);
        contentValues.put("Name", tripName);
        contentValues.put("Score", score);
        contentValues.put("Fahrtdauer", fahrtDauer);
        contentValues.put("Selbstbewertung", selbstBewertung);

        long result = db.insert("tripResultsTabelle2", null, contentValues);

        if (result == -1)
            return false;
        else
            return true;
    }


    //  Beschleunigung berechnen, im Konstruktor wird die aktuelle Geschwindigkeit sowie die aktuelle
    //  Fahrtentabelle übergeben.
    public double berechneBeschleunigung(String aktuelletabelle, double aktuellegeschwindigkeit, long rechenZeit) {
        SQLiteDatabase db = this.getWritableDatabase();

        //  Letzte abgespeicherte Geschwindigkeit aus der Tabelle abfragen
        Cursor cursor = db.rawQuery("SELECT Geschwindigkeit FROM " + aktuelletabelle + " ORDER BY ID DESC LIMIT 1", null); //Letzte Geschwindigkeit auslesen

        double letzte = 0.0;
        //  double vorletzte = 0.0;
        double beschleunigung = 0.0;
        //System.out.println("rechenzeit bei berechnebeschl:"+rechenZeit);
        double zeitPeriode = rechenZeit / 1000.0;

        //  Zuvor abgefragte letzte Geschwindigkeit in Variable speichern
        cursor.moveToLast();
        letzte = cursor.getDouble(0);

        //  Vorletzte Geschwindigkeit schreiben
        //  cursor2.moveToLast();
        //  vorletzte = cursor2.getDouble(0);

        //  Berechnen der Beschleunigung (Geteilt durch 1 für eine Sekunde, sollte man evtl. noch nachbessern da manchmal zwischen zwei
        //  Spalten 2 Sekunden abstand generiert werden(Verzögerung durch Rechendauer) Edit: durch zeitPeriode versucht zu beheben
        beschleunigung = ((aktuellegeschwindigkeit - letzte) / zeitPeriode) / 3.6;
        //System.out.println("BERECHNUNG BESCHLEUNIGUNG: (("+aktuellegeschwindigkeit+" - "+letzte+") / "+zeitPeriode+") / "+3.6+")");
        //System.out.println("BESCHLEUNIGUNG: "+beschleunigung);
        cursor.close();
        return beschleunigung;

    }

    //  Neue Tabelle für die aufzuzeichnende Fahrt anlegen
    public void createFahrtenTabelle(String timestring) {
        //  Tabelle für eine Fahrt in der Fahrtendatenbank.db erstellen
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("CREATE TABLE " + timestring + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, sqltime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, Zeit INTEGER, Rechenzeit REAL, Breitengrad REAL, Laengengrad REAL, Geschwindigkeit REAL, Beschleunigung REAL, LateraleBeschleunigung REAL, MaxBeschleunigung REAL, Wetter TEXT, Wetterkategorie TEXT, Sonnenaufgang INTEGER, Sonnenuntergang INTEGER, Tempolimit REAL, AktuelleStrasse TEXT, AktuellerStrassentyp TEXT)");


    }

    public long getFahrtBeginn(String aktuelletab) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT Zeit FROM " + aktuelletab + " ORDER BY ID ASC LIMIT 1 OFFSET 2;", null);
        cursor.moveToLast();
        long ret = cursor.getLong(0);
        cursor.close();
        return ret;
    }

    public long getFahrtEnde(String aktuelletab) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT Zeit FROM " + aktuelletab + " ORDER BY ID DESC LIMIT 1", null);
        cursor.moveToLast();
        long ret = cursor.getLong(0);
        cursor.close();
        return ret;
    }

    //Eigentlich gleiche Funktion wie getFahrtEnde :-D
    public long getLetzteZeit(String aktuelletab) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT Zeit FROM " + aktuelletab + " ORDER BY ID DESC LIMIT 1", null);
        cursor.moveToLast();
        long ret = cursor.getLong(0);
        cursor.close();
        return ret;
    }

    public double berechneDurschnittsgeschwindigkeit(String aktuelletabelle) {
        SQLiteDatabase db = this.getWritableDatabase();

        //  Durchschnitt der Geschwindigkeiten auslesen
        Cursor cursor = db.rawQuery("SELECT AVG(Geschwindigkeit) FROM " + aktuelletabelle + "", null);


        double avg = -1.0;

        //  in Variable speichern
        cursor.moveToLast();
        avg = cursor.getDouble(0);
        cursor.close();
        return avg;

    }

    public double berechneHöchstgeschwindigkeit(String aktuelletabelle) {
        SQLiteDatabase db = this.getWritableDatabase();

        //  Durchschnitt der Geschwindigkeiten auslesen

        Cursor cursor = db.rawQuery("SELECT MAX(Geschwindigkeit) FROM " + aktuelletabelle + "", null);

        double max = -1.0;

        //  in Variable speichern
        cursor.moveToLast();
        max = cursor.getDouble(0);
        cursor.close();
        return max;

    }

    public double berechneAccelarationScore(String aktuelletabelle) {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor1 = db.rawQuery("SELECT count(*) FROM " + aktuelletabelle + " WHERE Beschleunigung > 0.55 ", null);
        cursor1.moveToLast();
        double n = cursor1.getDouble(0);

        Cursor cursor2 = db.rawQuery("SELECT count(*) FROM " + aktuelletabelle + " WHERE Beschleunigung > 2.5 ", null);
        cursor2.moveToLast();
        double s = cursor2.getDouble(0);

        //  falls n = 0, Division durch 0 abfangen
        if (n < 1) {
            n = n + 1;
        }
        cursor1.close();
        cursor2.close();
        return (s / n * 100);
    }

    public double berechneSpeedingScore(String aktuelletabelle) {
        double n = 0;
        double s = 0;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor1 = db.rawQuery("SELECT Geschwindigkeit FROM " + aktuelletabelle + "", null);
        Cursor cursor2 = db.rawQuery("SELECT Tempolimit FROM " + aktuelletabelle + "", null);

        while (cursor1.moveToNext() && cursor2.moveToNext()) {
            double speed = cursor1.getDouble(0);
            double speedlimit = cursor2.getDouble(0);
            if ((speed != 0) || (speedlimit > 0 && speedlimit <= 130)) {
                n = n + 1;
            }

            if ((speedlimit > 0) && (speedlimit <= 130)) {
                if ((speed - speedlimit) > 15) {
                    s = s + 2;
                } else if (((speed - speedlimit) > 0) && ((speed - speedlimit) <= 15)) {
                    s = s + 1;
                }
            }
        }
        //  falls n = 0, Division durch 0 abfangen
        if (n < 1) {
            n = n + 1;
        }
        cursor1.close();
        cursor2.close();
        return (s / n * 100);
    }

    public double berechneTimeScore(String aktuelletabelle) {

        double n = 0;
        double s = 0;
        //long currentTime = new Date().getTime();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor1 = db.rawQuery("SELECT Sonnenaufgang FROM " + aktuelletabelle + "", null);
        Cursor cursor2 = db.rawQuery("SELECT Sonnenuntergang FROM " + aktuelletabelle + "", null);
        Cursor cursor3 = db.rawQuery("SELECT Zeit FROM " + aktuelletabelle + "", null);
        long sunrise;
        long sunset;
        long currentTime;

        while (cursor1.moveToNext() && cursor2.moveToNext() && cursor3.moveToNext()) {
            sunrise = cursor1.getLong(0);
            sunset = cursor2.getLong(0);
            currentTime = cursor3.getLong(0);
            //Wenn Zeit nach Sonnenuntergang des Vortages UND vor Sonnenaufgang des aktuellen Tages(Aktuell z.B. 0-8 Uhr)
            //ODER
            //Wenn Zeit nach Sonnenuntergang des aktuellen Tages UND vor Sonnenaufgang des nächsten Tages (Aktuell z.B. 16-0 Uhr)
            if (sunrise != 0 || sunset != 0) {
                n = n + 1;
                if (((currentTime > sunset - 86400000) && (currentTime <= sunrise)) ||
                        ((currentTime > sunset) && (currentTime <= sunrise + 86400000))) {
                    s = s + 1;
                }
            }
        }
        cursor1.close();
        cursor2.close();
        cursor3.close();
        return (s / n * 100);
    }

    public double berechneBrakingScore(String aktuelletabelle) {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor1 = db.rawQuery("SELECT count(*) FROM " + aktuelletabelle + " WHERE Beschleunigung < -0.55", null);
        cursor1.moveToLast();
        double n = cursor1.getDouble(0);

        Cursor cursor2 = db.rawQuery("SELECT count(*) FROM " + aktuelletabelle + " WHERE Beschleunigung < -2.5 AND Wetterkategorie = 'dry' ", null);
        cursor2.moveToLast();
        double a = cursor2.getDouble(0);

        Cursor cursor3 = db.rawQuery("SELECT count(*) FROM " + aktuelletabelle + " WHERE Beschleunigung < -2.5 AND Wetterkategorie = 'wet' ", null);
        cursor3.moveToLast();
        double b = cursor3.getDouble(0);

        Cursor cursor4 = db.rawQuery("SELECT count(*) FROM " + aktuelletabelle + " WHERE Beschleunigung < -2.5 AND Wetterkategorie = 'extreme' ", null);
        cursor4.moveToLast();
        double c = cursor4.getDouble(0);

        double s = a + b * 1.5 + c * 2;

        //  falls n = 0, Division durch 0 abfangen
        if (n < 1) {
            n = n + 1;
        }
        cursor1.close();
        cursor2.close();
        cursor3.close();
        cursor4.close();
        return (s / n * 100);
    }

    public double berechneCorneringScore(String aktuelletabelle) {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor1 = db.rawQuery("SELECT count(*) FROM " + aktuelletabelle + " WHERE LateraleBeschleunigung > 0.15 ", null);
        cursor1.moveToLast();
        double n = cursor1.getDouble(0);

        Cursor cursor2 = db.rawQuery("SELECT count(*) FROM " + aktuelletabelle + " WHERE ABS(LateraleBeschleunigung) > 1 AND Wetterkategorie = 'extreme' ", null);
        cursor2.moveToLast();
        double a = cursor2.getDouble(0);

        Cursor cursor3 = db.rawQuery("SELECT count(*) FROM " + aktuelletabelle + " WHERE LateraleBeschleunigung > 2.5 ", null);
        cursor3.moveToLast();
        double b = cursor3.getDouble(0);

        Cursor cursor4 = db.rawQuery("SELECT count(*) FROM " + aktuelletabelle + " WHERE ABS(Beschleunigung) > MaxBeschleunigung AND LateraleBeschleunigung > 0.15 ", null);
        cursor4.moveToLast();
        double c = cursor4.getDouble(0);

        double s = a + b + c;

        //  falls n = 0, Division durch 0 abfangen
        if (n < 1) {
            n = n + 1;
        }
        cursor1.close();
        cursor2.close();
        cursor3.close();
        cursor4.close();
        return (s / n * 100);
    }

    public double berechneMaximalBeschleunigung(double lateralebeschleunigung) {
        double maximalbeschleunigung = 0.509 * Math.pow(lateralebeschleunigung, 2) - 2.351 * lateralebeschleunigung + 2.841;
        return maximalbeschleunigung;
    }

    public double berechneLateraleBeschleunigung(String aktuelletabelle, double latitude1, double longitude1, double aktuellerspeed, double aktuellerichtungsdifferenz) {

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

        //  unzulässige Werte abfangen
        if (lateralebeschleunigung > 50 || lateralebeschleunigung < -50) {
            lateralebeschleunigung = 0;
        }
        cursor1.close();
        cursor2.close();
        return lateralebeschleunigung;
    }

    public String wetterkategorie(String aktuelletabelle) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT Wetter FROM " + aktuelletabelle + " ORDER BY ID DESC LIMIT 1", null);
        cursor.moveToLast();
        String wetter = cursor.getString(0);
        for (int i = 0; i < dry.length; ++i) {
            if (dry[i].equals(wetter)) {
                return "dry";
            }
        }
        for (int i = 0; i < wet.length; ++i) {
            if (wet[i].equals(wetter)) {
                return "wet";
            }
        }
        for (int i = 0; i < extreme.length; ++i) {
            if (extreme[i].equals(wetter)) {
                return "extreme";
            }
        }
        cursor.close();
        return "keine Kategorie";
    }

    public Cursor getListContents() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM tripResultsTabelle2 ORDER BY _id DESC", null);
        return data;
    }

}

