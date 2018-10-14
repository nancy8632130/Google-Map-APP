package com.example.user.android0705;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
//將經緯度轉成地址或地名(google map api)

public class FindAddress {

    public String Find(double lat, double lng)
    {
        TransTask TT3 = new TransTask();
        try {

            String U ="https://maps.googleapis.com/maps/api/geocode/json?latlng="+Double.toString(lat)+","+Double.toString(lng)+"&language=zh-tw&key=YOURKET";
            String json = TT3.execute(U).get();
            return parseJSON(json);
        } catch (InterruptedException e) { e.printStackTrace(); return "error";
        } catch (ExecutionException e) { e.printStackTrace(); return "error";}
    }


	//解析JSON
    private String parseJSON(String s)
    {
        JSONObject json;
        JSONObject jLegs = null,jSteps = null;
        String GO = "";
        JSONArray jResult;
        try {
            json = new JSONObject(s);
            jResult = json.getJSONArray("results");
            GO = (String)((JSONObject) jResult.get(0)).get("formatted_address");
            return GO;

        }catch (Exception e) {
            e.printStackTrace();
            return "none";
        }
    }

    class TransTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            StringBuilder sb = new StringBuilder();
            try {
                URL url = new URL(params[0]);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(url.openStream()));
                String line = in.readLine();
                while (line != null) {
                    sb.append(line);
                    line = in.readLine();
                }
            } catch (MalformedURLException e) { e.printStackTrace();
            } catch (IOException e) { e.printStackTrace(); }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}
