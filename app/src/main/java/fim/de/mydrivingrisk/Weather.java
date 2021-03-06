/*
 *
 * @Weather.java 01.12.2016 (myDrivingRisk-Team)
 *
 * Copyright (c) 2016 FIM, Universität Augsburg
 *
 */

package fim.de.mydrivingrisk;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;

/**
 * Weather.java ist die Hilfsklasse die asynchron für die Wetterabfrage zuständig ist
 *
 * @author myDrivingRisk-Team
 */

public class Weather {

    //  URL und Key von OpenWeatherMap statisch festlegen
    private static final String OPEN_WEATHER_MAP_URL = "http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric&lang=de";
    private static final String OPEN_WEATHER_MAP_KEY = "92080114fee4af04b0fd05c803fba1fd";

    //  Schnittstelle für die Asynchrone Kommunikation
    public interface AsyncResponse {

        void processFinish(String output1, long output2, long output3);
    }


    public static class placeIdTask extends AsyncTask<String, Void, JSONObject> {

        public AsyncResponse delegate = null;

        public placeIdTask(AsyncResponse asyncResponse) {
            delegate = asyncResponse;
        }

        //  Methode läuft im Hintergrund ab
        @Override
        protected JSONObject doInBackground(String... params) {

            JSONObject jsonWeather = null;
            try {
                jsonWeather = getWeatherJSON(params[0], params[1]);
            } catch (Exception e) {
                Log.d("Error", "JSON-Ergebnisse können nicht verarbeitet werden", e);
            }

            return jsonWeather;
        }

        //  Wetterbeschreibung, Sonnenauf- und untergang aus JSONObjects holen
        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if (json != null) {
                    JSONObject details = json.getJSONArray("weather").getJSONObject(0);
                    DateFormat df = DateFormat.getDateTimeInstance();

                    String description = details.getString("description");
                    long sunrise = json.getJSONObject("sys").getLong("sunrise") * 1000;
                    long sunset = json.getJSONObject("sys").getLong("sunset") * 1000;

                    delegate.processFinish(description, sunrise, sunset);

                }
            } catch (JSONException e) {
                //  Log.e(LOG_TAG, "JSON-Ergebnisse können nicht verarbeitet werden", e);
            }
        }
    }


    //  Methode die mittels URL, KEY, Längen- und Breitengrad die Wetter JSONObjects abfrägt
    public static JSONObject getWeatherJSON(String lat, String lon) {
        try {
            URL url = new URL(String.format(OPEN_WEATHER_MAP_URL, lat, lon));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.addRequestProperty("x-api-key", OPEN_WEATHER_MAP_KEY);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp = "";
            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            //  Dieser Wert wird 404 sein, wenn die Anforderung nicht erfolgreich war
            if (data.getInt("cod") != 200) {
                return null;
            }

            return data;
        } catch (Exception e) {
            return null;
        }
    }

}
