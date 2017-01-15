/*
 *
 * @MyOSM.java 15.12.2016 (myDrivingRisk-Team)
 *
 * Copyright (c) 2016 FIM, Universität Augsburg
 *
 */

package fim.de.mydrivingrisk;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * MyOSM.java ist die Hilfsklasse die asynchron für die Abfrage des Tempolimits zuständig ist
 *Dieses wird bei OpenStreetMaps(OSM) abgefragt
 * @author myDrivingRisk-Team
 */

public class MyOSM {

    // Das Interface AsyncResponse übergibt die empfangenen und ausgewerteten OSM Daten an RecordTrip
    public interface AsyncResponse {

        void processFinish(double ergebnis1, String ergebnis2, String ergebnis3);
    }
    // Im Hintergrund ausführen
    public static class placeIdTask extends AsyncTask<String, Void, JSONObject> {

        public AsyncResponse delegate = null;

        public placeIdTask(AsyncResponse asyncResponse) {
            delegate = asyncResponse;
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            JSONObject jsonTempo = null;

            try {
                jsonTempo = holeOSMDaten(params[0], params[1]);
            } catch (Exception e) {
            }
            return jsonTempo;
        }

        // onPostExecute erhält die Daten von HoleOSMDaten und liest und filtert diese nach den benötigten Parametern
        @Override
        protected void onPostExecute(JSONObject osmdaten) {

            double hoechstgeschwindigkeit = 0.0;
            String strasse = "leer", strassentyp = "leer";
            try {
                if (osmdaten != null) {
                    JSONObject object = osmdaten;
                    //Hole alle Werte des Arrays "elements"
                    JSONArray objektliste1 = osmdaten.getJSONArray("elements");

                    for(int i = 0; i < objektliste1.length(); i++)
                    {
                    JSONObject jsonobjekt1 = objektliste1.getJSONObject(0);
                    String kennzeichnung = jsonobjekt1.getString("tags");
                    JSONObject jsonobjekt2 = new JSONObject(kennzeichnung);
                        // Aus dem Array wird die zulässige Höchstegeschwindigkeit erfasst
                    try {
                        hoechstgeschwindigkeit = jsonobjekt2.getDouble("maxspeed");

                    } catch (JSONException e) {
                        hoechstgeschwindigkeit = 0.0;
                    }
                        // Aus dem Array wird der Straßenname erfasst
                    try {
                        strasse = jsonobjekt2.getString("name");

                    } catch (JSONException e) {
                        try {
                            strasse = jsonobjekt2.getString("ref");
                        } catch (JSONException f) {

                        }
                    }
                        // Aus dem Array wird der Straßentyp erfasst
                    try {
                        strassentyp = jsonobjekt2.getString("highway");

                    } catch (JSONException e) {
                    }
                    }

                    // Falls keine Werte gefunden werden können, wird 0.0 für die Höchstgeschwindigkeit bzw. "nicht gefunden" für Name und Typ gesetzt
                } else {
                    hoechstgeschwindigkeit = 0.0;
                    strasse = "not found";
                    strassentyp = "not found";
                }
            } catch (JSONException e) {
                hoechstgeschwindigkeit = 0.0;
                strasse = "not found";
                strassentyp = "not found";
            }
            //Übergebe die fertigen Daten an AsyncResponse
            delegate.processFinish(hoechstgeschwindigkeit, strasse, strassentyp);
        }
    }

    //Die benötigten OSM Daten werden abgefragt
    public static JSONObject holeOSMDaten(String laengengrad, String breitengrad) {

        try {
            //OSM Url wird gebildet und übergeben
            String sURL3 = "http://overpass-api.de/api/interpreter?data=[out:json];way[\"highway\"~\"motorway|trunk|primary|secondary|tertiary|unclassified|residential|service|living_street\"][\"maxspeed\"](around:5.0," + laengengrad + "," + breitengrad + ");out;";
            URL url = new URL(sURL3);

            //Verbindung zum Server wird hergestellt
            HttpURLConnection verbindung = (HttpURLConnection) url.openConnection();
            verbindung.connect();

            // Die Daten werden gelesen und im JSONObject daten gespeichert. Anschließend an onPostExecute übergeben.
            BufferedReader leseosmdaten = new BufferedReader(new InputStreamReader(verbindung.getInputStream()));
            StringBuffer json = new StringBuffer(1024);
            String tmp = "";
            while ((tmp = leseosmdaten.readLine()) != null)
                json.append(tmp).append("\n");
            leseosmdaten.close();
            JSONObject daten = new JSONObject(json.toString());
            return daten;

        } catch (Exception e) {
            JSONObject daten = null;
            return daten;
        }
    }

}
