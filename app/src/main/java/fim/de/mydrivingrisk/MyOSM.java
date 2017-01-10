package fim.de.mydrivingrisk;

import android.os.AsyncTask;



import org.json.*;


import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;



public class MyOSM {


    public interface AsyncResponse {

        void processFinish(double output1, String output2, String output3);
    }


    public static class placeIdTask extends AsyncTask<String, Void, JSONObject> {

        public AsyncResponse delegate = null;

        public placeIdTask(AsyncResponse asyncResponse) {
            delegate = asyncResponse;
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            JSONObject jsonTempo = null;
            System.out.println("doinBackground gestartet");
            try {
                jsonTempo = getTempoJSON(params[0], params[1]);
            } catch (Exception e) {
                System.out.println("doinBackground EXCEPTION");
                Log.d("Error", "Cannot process JSON results", e);
            }

            System.out.println("JsonTempo: "+jsonTempo);
            return jsonTempo;
        }

        @Override
        protected void onPostExecute(JSONObject jsoninput) {
            System.out.println("onPostExecuteStarted");
            double maxspeed = 0.0;
            String strasse ="leer", strassentyp="leer";
            try {
                if (jsoninput != null) {
                    System.out.println("JSON Paket ist nicht leer :-)");
                    JSONObject object = jsoninput;
                    System.out.println("JSON Object 1: "+object);

                    JSONArray array1 = jsoninput.getJSONArray("elements");
                    System.out.println("Elements Array: "+array1);

                    //for(int i = 0; i < array1.length(); i++)
                    //{
                        JSONObject object3 = array1.getJSONObject(0);
                        System.out.println("Object3 : "+object3);

                        //JSONArray array2 = object3.getJSONArray("tags");
                        //System.out.println("Array2 : "+array2);

                        String zwo = object3.getString("tags");
                        System.out.println("String zwo/tags : "+zwo);

                        JSONObject object4 = new JSONObject(zwo);
                        System.out.println("Object 4 : "+object4);





                        try {
                            maxspeed = object4.getDouble("maxspeed");
                            System.out.println("MAXSPEED: " + maxspeed);
                        }
                        catch (JSONException e){
                            System.out.println("kein maxspeed-wert erhalten");
                            maxspeed = 0.0;
                        }

                        try {
                            strasse = object4.getString("name");
                            System.out.println("Straße: "+strasse);
                        }
                        catch (JSONException e){
                            System.out.println("kein strasse-wert erhalten");
                            try {
                                strasse = object4.getString("ref");
                                System.out.println("Straße: "+strasse);
                            }
                            catch (JSONException f){
                                System.out.println("kein ref-wert erhalten");
                            }
                        }

                        try {
                            strassentyp = object4.getString("highway");
                            System.out.println("Straßentyp: "+strassentyp);
                        }
                        catch (JSONException e){
                            System.out.println("kein straßentyp-wert erhalten");
                        }






                    //}


                    //JSONArray object2 = new JSONArray(one);
                    //System.out.println("JSON Object 2: "+object2);

                    //String two = object2.getString(0);
                    //System.out.println("Two String: "+two);

                    //JSONArray object3 = new JSONArray(two);
                    //System.out.println("JSON Object 3: "+object3);



                    //System.out.println("Tags String: "+one);

                    //JSONArray jArray1 = object.getJSONArray("tags");
                    //String abc = object2.getString("maxspeed");

                    //System.out.println("Det: "+abc);
                    //System.out.println("JSON ist nicht Null. top.");
                    //JSONObject details = jsoninput.getJSONArray("elements").getJSONObject(0);
                    //JSONObject details2 = details.getJSONArray("tags");

                    //System.out.println("Details: "+details);
                    //System.out.println("Details: "+details2);



                    //String description = details.getString("id");
                    //long sunrise = json.getJSONObject("sys").getLong("sunrise") * 1000;
                    //long sunset = json.getJSONObject("sys").getLong("sunset") * 1000;
                    //String description = details.getString("id");
                    //String description2 = "desc2";
                    //String description2 = details2.getString("maxspeed");
                    System.out.println("Ausgabe: "+maxspeed+" | "+strasse+" | "+strassentyp);


                }
                else {
                    System.out.println("JSON Paket ist leer :-(");
                    maxspeed = 0.0;
                    strasse = "not found";
                    strassentyp = "not found";
                }
            } catch (JSONException e) {
                    maxspeed = 0.0;
                    strasse = "not found";
                    strassentyp = "not found";
                    Log.e("FEHLER", "Cannot process JSON results", e);
                    System.out.println("OSM-Abfrage-Exception");

            }
            delegate.processFinish(maxspeed, strasse, strassentyp);
        }
    }


    public static JSONObject getTempoJSON(String lat, String lon) {
        System.out.println("getTempoJSON gestartet");
        try {
            //String sURL3 = "http://freegeoip.net/json/"; //just a string
            //String sURL3 = "http://overpass-api.de/api/interpreter?data=[out:json];node(around:10,"+lat+","+lon+");way(bn)[highway];out;";
            //String sURL3 = "http://overpass-api.de/api/interpreter?data=[out:json][timeout:25];(node[\"source:maxspeed\"](around:5,"+lat+","+lon+");way[\"source:maxspeed\"](around:5,"+lat+","+lon+");relation[\"source:maxspeed\"](around:5,"+lat+","+lon+"););out body;>;out skel qt;";
            //String sURL3 = "http://overpass-api.de/api/interpreter?data=[out:json];node(around:50,48.167006,%209.511681);way(bn)[highway];out;";
            //String sURL3 = "http://overpass-api.de/api/way[\"highway\"](around:5.0,48.165553,9.519322);out;"
            //String sURL3 = ("http://overpass-api.de/api/interpreter?data=[out:json];way[highway](around:5.0,"+lat+","+lon+");out;");
            String sURL3 = "http://overpass-api.de/api/interpreter?data=[out:json];way[\"highway\"~\"motorway|trunk|primary|secondary|tertiary|unclassified|residential|service|living_street\"][\"maxspeed\"](around:7.0,"+lat+","+lon+");out;";
            String version = "nix";
            String zipcode = "nix";

            URL url = new URL(sURL3);
            System.out.println("URL: "+url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.connect();
            System.out.println("Connection connect ausgeführt");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            System.out.println("Buffered Reader ausgeführt");

            StringBuffer json = new StringBuffer(1024);
            String tmp = "";
            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();

            //System.out.println("StringBuffer json: "+json);

            JSONObject data = new JSONObject(json.toString());

            //  This value will be 404 if the request was not
            //  successful
            //if (data.getInt("cod") != 200) {
             //   return null;
            //}
            System.out.println("Data: "+data);
            return data;


        } catch (Exception e) {
            System.out.println("Downloaden des OMS JSON Pakets nicht möglich!");
            JSONObject data = null;
            //maxspeed = 0.0;
            //return null;
            return data;
        }
    }

}
