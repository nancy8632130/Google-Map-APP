package com.example.user.android0705;

import android.graphics.Color;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
//點擊走路導航處理

public class Walk {
    public Double t3,t4,t1,t2;//紀錄起點終點經緯度
    public static String SlocationName,ElocationName,ShopName;//起點終點名字、商店名字
    public GoogleMap mMap;
    private Double LocationLat = 23.0,LocationLng = 120.0,ShopLat=0.0,ShopLng=0.0;//當下位置經緯度、商店位置經緯度
    public TextToSpeech tts;
    private String How="init";
    private boolean V;

	//初始化資訊
    public void initization(String SName,String EName,GoogleMap Map,Double Lat,Double Lng,TextToSpeech ts,boolean voice)
    {
        SlocationName=SName;  ElocationName=EName;
        mMap=Map;
        LocationLat=Lat;  LocationLng=Lng;
        tts=ts;
        How="init";
        mMap.clear();
        V=voice;
    }
	
	//若要導航至商店，更新資訊
    public void ReNewToShop(Double Lat,Double Lng,Double shopLat,Double shopLng,String shopName,String how)
    {
        LocationLat=Lat;  LocationLng=Lng;
        ShopLat=shopLat;  ShopLng=shopLng;  ShopName=shopName;
        How=how;
        mMap.clear();
    }

    //暫時不用
    Circle mapCircle;
    private void addCircle() {
        LatLng center = new LatLng(LocationLat, LocationLng);
        mapCircle = mMap.addCircle(
                new CircleOptions() //建立CircleOptions物件
                        .center(center) //設定圓心，無預設值
                        .radius(10)  //半徑長(m)
                        .strokeWidth(5) //外框線粗細(像素)，預設為10
                        .strokeColor(Color.TRANSPARENT) //設定TRANSPARENT代表全透明
                        .fillColor(Color.CYAN) //設定填充顏色(ARGB)預設為黑
        );
    }

    //在地圖上mark
    private void MarkShop(double lat,double lng ,String name)
    {
        LatLng latLng2 = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(latLng2).title(name));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng2, 15));
    }

    //開始取得導航資訊
    public void addLatLng() {
        if(SlocationName.equals(""))  //目前位置(使用者未設置起點)
        {
            if(How.equals("replace"))  //選擇店家為取代目的地
            {
                TransTask TT3 = new TransTask();
                try {
                    //標記起點及終點(現在為店家)
                    MarkShop(ShopLat,ShopLng,ShopName);  MarkShop(LocationLat,LocationLng,"現在位置");
                    //重新用google direction api找路徑
                    String U = getRequestUrl(LocationLat, LocationLng, ShopLat, ShopLng);
                    String json = TT3.execute(U).get();
                    parseJSON_route(json);
                } catch (InterruptedException e) { e.printStackTrace();
                } catch (ExecutionException e) { e.printStackTrace(); }
            }
            else if(How.equals("extend")) //選擇店家為延伸目的地
            {
                TransTask TT3 = new TransTask();
                try {
                    MarkShop(LocationLat,LocationLng,"現在位置");
                    MarkShop(ShopLat,ShopLng,ShopName);
                    MarkShop(t3,t4,ElocationName);
                    String U = getRequestUrl_waypoint(LocationLat, LocationLng,ShopLat, ShopLng,t3, t4);
                    String json = TT3.execute(U).get();
                    parseJSON_route(json);
                } catch (InterruptedException e) { e.printStackTrace();
                } catch (ExecutionException e) { e.printStackTrace(); }
            }
            else if(How.equals("insert")) //選擇店家為插入目的地之間
            {
                TransTask TT3 = new TransTask();
                try {
                    MarkShop(LocationLat,LocationLng,"現在位置");
                    MarkShop(ShopLat,ShopLng,ShopName);
                    MarkShop(t3,t4,ElocationName);
                    String U = getRequestUrl_waypoint(LocationLat, LocationLng,t3, t4,ShopLat, ShopLng);
                    String json = TT3.execute(U).get();
                    parseJSON_route(json);
                } catch (InterruptedException e) { e.printStackTrace();
                } catch (ExecutionException e) { e.printStackTrace(); }
            }
            else
            {
                String URL_E = "https://maps.googleapis.com/maps/api/geocode/json?address=" + ElocationName + ",台灣"+ "&key=YOURKEY";
                TransTask TT2 = new TransTask();
                TransTask TT3 = new TransTask();
                try {
                    String ue = TT2.execute(URL_E).get();  parseJSON_end(ue); //取得終點經緯度(t3,t4)
                    MarkShop(t3,t4,ElocationName);
                    String U = getRequestUrl(LocationLat, LocationLng, t3, t4);
                    String json = TT3.execute(U).get();
                    parseJSON_route(json);
                } catch (InterruptedException e) { e.printStackTrace();
                } catch (ExecutionException e) { e.printStackTrace(); }
            }
        }
        else //使用者有設置起點終點
        {
            if(How.equals("replace"))  //選擇店家為取代目的地
            {
                TransTask TT3 = new TransTask();
                try {
                    //標記起點及終點(現在為店家)
                    MarkShop(ShopLat,ShopLng,ShopName);  MarkShop(t1,t2,SlocationName);
                    //重新用google direction api找路徑
                    String U = getRequestUrl(t1, t2, ShopLat, ShopLng);
                    String json = TT3.execute(U).get();
                    parseJSON_route(json);
                } catch (InterruptedException e) { e.printStackTrace();
                } catch (ExecutionException e) { e.printStackTrace(); }
            }
            else if(How.equals("extend")) //選擇店家為延伸目的地
            {
                TransTask TT3 = new TransTask();
                try {
                    MarkShop(t1,t2,SlocationName);
                    MarkShop(ShopLat,ShopLng,ShopName);
                    MarkShop(t3,t4,ElocationName);
                    String U = getRequestUrl_waypoint(t1, t2,ShopLat, ShopLng,t3, t4);
                    String json = TT3.execute(U).get();
                    parseJSON_route(json);
                } catch (InterruptedException e) { e.printStackTrace();
                } catch (ExecutionException e) { e.printStackTrace(); }
            }
            else if(How.equals("insert")) //選擇店家為插入目的地之間
            {
                TransTask TT3 = new TransTask();
                try {
                    MarkShop(t1,t2,SlocationName);
                    MarkShop(ShopLat,ShopLng,ShopName);
                    MarkShop(t3,t4,ElocationName);
                    String U = getRequestUrl_waypoint(t1,t2,t3, t4,ShopLat, ShopLng);
                    String json = TT3.execute(U).get();
                    parseJSON_route(json);
                } catch (InterruptedException e) { e.printStackTrace();
                } catch (ExecutionException e) { e.printStackTrace(); }
            }
            else
            {
                String URL_S = "https://maps.googleapis.com/maps/api/geocode/json?address=" + SlocationName + ",台灣"+ "&key=YOURKEY";
                String URL_E = "https://maps.googleapis.com/maps/api/geocode/json?address=" + ElocationName + ",台灣"+ "&key=YOURKEY";
                TransTask TT1 = new TransTask(); TransTask TT2 = new TransTask(); TransTask TT3 = new TransTask();
                try {
                    String us = TT1.execute(URL_S).get(); String ue = TT2.execute(URL_E).get();
                    parseJSON_start(us); parseJSON_end(ue);
                    MarkShop(t1,t2,SlocationName);  MarkShop(t3,t4,ElocationName);
                    String U = getRequestUrl(t1, t2, t3, t4);
                    String json = TT3.execute(U).get();
                    parseJSON_route(json);
                } catch (InterruptedException e) {  e.printStackTrace();
                } catch (ExecutionException e) { e.printStackTrace(); }
            }
        }
    }

    private void parseJSON_start(String s) { //地名轉經緯度
        JSONObject json;
        JSONArray jRoutes = null;
        JSONObject jLegs = null;
        try {
            json = new JSONObject(s);
            jRoutes = json.getJSONArray("results");
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONObject("geometry");
                t1 = (Double) ((jLegs).getJSONObject("location").get("lat"));
                t2 = (Double) ((jLegs).getJSONObject("location").get("lng"));
            }
        } catch (JSONException e) { e.printStackTrace(); }
    }

    private void parseJSON_end(String s) {
        JSONObject json;
        JSONArray jRoutes = null;
        JSONObject jLegs = null,jSteps = null;
        try {
            json = new JSONObject(s);
            jRoutes = json.getJSONArray("results");
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONObject("geometry");
                t3 = (Double) ((jLegs).getJSONObject("location").get("lat"));
                t4 = (Double) ((jLegs).getJSONObject("location").get("lng"));
            }
        } catch (JSONException e) { e.printStackTrace(); }
    }

	//取代、延伸、插入 URL取得
    private String getRequestUrl_waypoint(double fromLat, double fromLng, double toLat, double toLng,double ShopLat,double ShopLng) {
        String str_org = "origin=" + fromLat + "," + fromLng;  //value of origin
        String str_dest = "destination=" + toLat + "," + toLng; //value of destination
        String waypoint = "waypoints=" + ShopLat + "," + ShopLng;
        String m = "mode=walking";  //mode for find direction
        String language = "language=zh-TW";
        String alter="alternatives=true";
        String key="key=YOURKEY";
        String param = str_org + "&" + str_dest + "&" + waypoint + "&" + m + "&" + language+"&"+alter + "&" + key;  //build the full param
        String output = "json"; //output format
        //create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        return url;
    }

	//google map direction api取得url
    private String getRequestUrl(double fromLat, double fromLng, double toLat, double toLng) {
        String str_org = "origin=" + fromLat + "," + fromLng;  //value of origin
        String str_dest = "destination=" + toLat + "," + toLng; //value of destination
        String sensor = "sensor=false"; //set value enable the sensor
        String m = "mode=walking";  //mode for find direction
        String language = "language=zh-TW";
        String key="key=YOURKEY";
        String alter="alternatives=true";
        String param = str_org + "&" + str_dest + "&" + sensor + "&" + m + "&" + language + "&"+alter+"&" + key;  //build the full param
        String output = "json"; //output format
        //create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        return url;
    }
	//在地圖上畫線
    private void DrawPolyLine(String Polyline,String color)
    {
        List<LatLng> list = decodePoly(Polyline);
        Polyline polyline1 = mMap.addPolyline(new PolylineOptions()  /** Traversing all points */
                .addAll(list)
                .width(12)
                .color(Color.parseColor(color))
                .geodesic(true)
        );
    }


    public  ArrayList <StepList>List = new ArrayList<StepList>();
    private void parseJSON_route(String s) {
        JSONObject json;
        JSONArray jRoutes = null,jLegs = null,jSteps = null;
        String GO = "";
        try {
            json = new JSONObject(s);
            jRoutes = json.getJSONArray("routes");
            for (int i = 0; i < 4; i++) {  //取前4筆資料
                ArrayList <Step>array_StepList = new ArrayList<Step>();
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                String AllDistance = "",AllCostTime = "",EndAddress = "",StartAddress = "";
                for (int j = 0; j < jLegs.length(); j++) {
                    AllDistance = (String) ((JSONObject) jLegs.get(j)).getJSONObject("distance").get("text");
                    AllCostTime = (String) ((JSONObject) jLegs.get(j)).getJSONObject("duration").get("text");
                    EndAddress = (String) ((JSONObject) jLegs.get(j)).get("end_address");
                    StartAddress = (String) ((JSONObject) jLegs.get(j)).get("start_address");
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                    String SubDistance = "",SubCostTime = "",html_instruction = "",Polyline = "",BusEndStop="",BusStartStop="";

                    String  BusRoute="",BusName="",agencies=""; double SLat=0.0,SLng=0.0;  double ELat=0.0,ELng=0.0;
                    ArrayList <SubSteps>SubStepsList = new ArrayList<SubSteps>();

                    for (int k = 0; k < jSteps.length(); k++) {
                        BusRoute="none";BusEndStop="none";BusStartStop="none";BusName="none";
                        SLat=0.0; SLng=0.0;  ELat=0.0; ELng=0.0;
                        String Vechine="";
                        try { SubDistance = (String) ((JSONObject) jSteps.get(k)).getJSONObject("distance").get("text");
                        } catch (Exception e) { SubDistance = "none"; }
                        try { SubCostTime = (String) ((JSONObject) jSteps.get(k)).getJSONObject("duration").get("text");
                        } catch (Exception e) { SubCostTime = "none"; }
                        try { html_instruction = (String) ((JSONObject) jSteps.get(k)).get("html_instructions");
                        } catch (Exception e) { html_instruction = "none"; }

                        try {  //畫路線在地圖上
                            Polyline = (String) ((JSONObject) jSteps.get(k)).getJSONObject("polyline").get("points");
                            if(i==0)  DrawPolyLine(Polyline,"#CC0000");//polyline第一個
                            else if(i==1) DrawPolyLine(Polyline,"#227700"); //第二個
                            else if(i==2) DrawPolyLine(Polyline,"#00BBFF"); //第三個
                            else if(i==3) DrawPolyLine(Polyline,"#0000CC"); //第四個
                            else if(i==4) DrawPolyLine(Polyline,"#7700BB"); //第五個
                        } catch (Exception e) { Polyline = "none"; }

                        Step step=new Step();
                        step.Store_walk_drive(SubDistance,SubCostTime, html_instruction);
                        array_StepList.add(step);
                        //語音
                        if(V)
                        {
                            GO = ShowRoute(html_instruction);
                            GO += "走" + SubDistance;
                            GO += "大約" + SubCostTime;
                            if (i == 1&& k==0) tts.speak(GO, TextToSpeech.QUEUE_ADD, null);
                        }
                    }
                }
                StepList stepList=new StepList();
                stepList.Store(array_StepList,AllDistance,AllCostTime,EndAddress,StartAddress);
                List.add(stepList);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private String ShowRoute(String htmlroute) {
        htmlroute = htmlroute.replace("<b>", "");
        htmlroute = htmlroute.replace("</b>", "");
        htmlroute = htmlroute.replace("</div>", "");
        String h = "";
        int temp = 0;
        for (int i = 0; i < htmlroute.length(); i++) {
            if (htmlroute.charAt(i) == '<') temp = 1;
            if (temp == 0) {
                h += htmlroute.charAt(i);
            }
            if (htmlroute.charAt(i) == '>') {
                temp = 0;
                h += " ";
            }
        }
        return h;
    }

    //解碼折線點的方法
    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
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
