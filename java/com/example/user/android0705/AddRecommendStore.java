package com.example.user.android0705;

import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
//利用google places api取得商店資訊

public class AddRecommendStore {

    private TextToSpeech tts;
    private boolean Voice;
    private Double LocationLat = 23.0, LocationLng = 120.0;

    public void Call(int number,boolean voice,TextToSpeech t,Double locationLat,Double locationLng)
    {
        if(number==0)  addRecommend_Function("restaurant");
        else if(number==1) addRecommend_Function("cafe");
        else if(number==2)  addRecommend_Function("bakery");
        else if(number==3)  addRecommend_Function("convenience_store");
        else if(number==4)  addRecommend_Function("parking");
        Voice=voice;  tts=t;  
        LocationLat=locationLat;  LocationLng=locationLng;//目前位置
    }

    //google places api取得商店資訊
    private void addRecommend_Function(String type)
    {
        String PlaceApiUrl=getPlaceApiUrl(LocationLat, LocationLng,type); //取得api的url
        TransTask TT4 = new TransTask();
        try{
            String p_json = TT4.execute(PlaceApiUrl).get();
            parseJSON_placeapi(p_json);
        }catch (InterruptedException e) { e.printStackTrace();
        }catch (ExecutionException e) { e.printStackTrace(); }
    }
	
	//得到url並回傳
    private String getPlaceApiUrl(double Lat, double Lng,String t) {
        String type="type=" + t;
        String str_location = "location=" + Lat + "," + Lng;  //value of location
        String radius = "radius=" + "500"; //value of search radius
        String language = "language=zh-TW";
        String key = "key=" + "YOURKEY" ;//此處請填入自己申請的KEY
        String param = str_location + "&" + radius +"&"+language+ "&" + type + "&" + key;  //build the full param
        String output = "json"; //output format
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/" + output + "?" + param;
        return url;
    }

    ArrayList arrayList = new ArrayList();//建立一個ArrayList物件
	//解析json
    private void parseJSON_placeapi(String s) {
        arrayList.clear();
        JSONObject json,NextpageToken;
        JSONArray jResult = null,jType = null;
        try {
            String NPT;
            json = new JSONObject(s);
            try {  NPT = (String) json.get("next_page_token");
            }catch (Exception e) {  NPT="none"; }
            jResult = json.getJSONArray("results");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < jResult.length(); i++) {
                String icon = "", name = "", placeID = "",open = "none";
                double rate = 0.0,Lat=0.0 ,Lng=0.0; JSONObject geometry;

                try {   //取得店家經緯度
                    geometry = ((JSONObject) jResult.get(i)).getJSONObject("geometry");
                    Lat=(Double)((JSONObject)geometry.getJSONObject("location")).get("lat");
                    Lng=(Double)((JSONObject)geometry.getJSONObject("location")).get("lng");
                } catch (Exception e) { Lat=0.0 ; Lng=0.0; }

                try { icon = (String) ((JSONObject) jResult.get(i)).get("icon"); }
                catch (Exception e) { icon="none"; }
                try { name = (String) ((JSONObject) jResult.get(i)).get("name"); }
                catch (Exception e) { name="none"; }
                try {
                    JSONObject openObject = ((JSONObject) jResult.get(i)).getJSONObject("opening_hours");
                    if((boolean) openObject.get("open_now")) open = "true";
                    else open = "false";
                }catch (Exception e) { open = "none"; }

                try { placeID = (String) ((JSONObject) jResult.get(i)).get("place_id"); }
                catch (Exception e) { placeID = "none"; }
                try { rate = (double) ((JSONObject) jResult.get(i)).get("rating"); }
                catch (Exception e) { rate = 0.0; }

                ArrayList TypeList = new ArrayList();
                try {
                    jType = ((JSONObject) jResult.get(i)).getJSONArray("types");
                    for (int j = 0; j < jType.length(); j++) {
                        String temp = "";
                        temp = (String) jType.get(j);
                        TypeList.add(temp);
                        sb.append(temp+" ");
                    }
                }catch (Exception e) { }
                StoreInformation store=new StoreInformation();
                store.Store(icon,name,open,placeID,rate,TypeList,Lat,Lng);
                arrayList.add(store);
            }
            StoreListSort();
            Recommend();
        } catch (Exception e) { e.printStackTrace(); }
    }

	//語音撥放控制
    private void Recommend()
    {
        StoreInformation store = (StoreInformation)arrayList.get(0);
        if(Voice)
        {
            tts.speak("推薦", TextToSpeech.QUEUE_ADD, null);
            tts.speak(store.getName(), TextToSpeech.QUEUE_ADD, null);
            tts.speak("評價",TextToSpeech.QUEUE_ADD, null);
            tts.speak(Double.toString(store.getRate()), TextToSpeech.QUEUE_ADD, null);
        }
    }
	
	//依照rate來排序
    private void StoreListSort()
    {
        for(int i=1 ; i<arrayList.size()-1;i++)
        {
            for(int j=0 ; j<arrayList.size()-1;j++)
            {
                StoreInformation store1 = (StoreInformation)arrayList.get(j);
                StoreInformation store2 = (StoreInformation)arrayList.get(j+1);
                if(store1.getRate() < store2.getRate()){
                    arrayList.set(j, store2); arrayList.set(j+1, store1);
                }
            }
        }
    }

    class TransTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            StringBuilder sb = new StringBuilder();
            try {
                URL url = new URL(params[0]);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
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
