package com.example.user.android0705;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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
//點擊大眾運輸導航處理
public class Transit {
    int i = 0;
    public Double t3,t4,t1,t2;//紀錄起點終點經緯度
    public static String SlocationName,ElocationName,ShopName;//起點終點名字、商店名字
    public GoogleMap mMap;
    private Double LocationLat = 23.0,LocationLng = 120.0,ShopLat=0.0,ShopLng=0.0;//當下位置經緯度、商店位置經緯度
    public TextToSpeech tts;
    Context Activity;
    TextView tv1;
    String How="init";
    ArrayList arrayTransitList = new ArrayList();//建立一個ArrayList物件
    boolean toShop=false;
    boolean V;

	//初始化資訊
    public void initization(String SName, String EName, GoogleMap Map, Double Lat, Double Lng, TextToSpeech ts, TextView text,Context MapsActivity,boolean voice)
    {
        SlocationName=SName; ElocationName=EName; mMap=Map; LocationLat=Lat; LocationLng=Lng;
        tts=ts; tv1=text; Activity=MapsActivity;
        arrayTransitList.clear(); //重新清除，不然會一直紀錄上一次的資料
        mMap.clear();
        How="init";
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
    Circle mapCircle; //地圖上放圓形圖案
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
            if(How.equals("replace")) //大眾運輸工具沒有insert跟extend，
            {
                TransTask TT3 = new TransTask();
                try {
                    MarkShop(LocationLat,LocationLng,"現在位置");
                    MarkShop(ShopLat,ShopLng,ShopName);//標記商店
                    String U = getRequestUrl(LocationLat, LocationLng, ShopLat, ShopLng);//開始從現在位置導航到商店
                    String json = TT3.execute(U).get();
                    parseJSON_route(json);
                } catch (InterruptedException e) { e.printStackTrace();
                } catch (ExecutionException e) { e.printStackTrace(); }
            }
            else
            {
                String URL_E = "https://maps.googleapis.com/maps/api/geocode/json?address=" + ElocationName+ ",台灣" + "&key=YOURKEY";
                TransTask TT2 = new TransTask(); TransTask TT3 = new TransTask();
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
            if(How.equals("replace")) //大眾運輸工具沒有insert跟extend，
            {
                TransTask TT3 = new TransTask();
                try {
                    MarkShop(t1,t2,SlocationName);
                    MarkShop(ShopLat,ShopLng,ShopName);//標記商店
                    String U = getRequestUrl(t1, t2, ShopLat, ShopLng);//開始從現在位置導航到商店
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

    //解析地址轉經緯度的json
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

    //解析地址轉經緯度的json
    private void parseJSON_end(String s) {
        JSONObject json; JSONObject jLegs = null;
        JSONArray jRoutes = null;
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

    private String getRequestUrl(double fromLat, double fromLng, double toLat, double toLng) {
        String str_org = "origin=" + fromLat + "," + fromLng;  //value of origin
        String str_dest = "destination=" + toLat + "," + toLng; //value of destination
        String m = "mode=transit";  //mode for find direction
        String language = "language=zh-TW";
        String alter="alternatives=true";
        String key="key=YOURKEY";
        String param = str_org + "&" + str_dest + "&" + m + "&" + language+"&"+alter + "&" + key;  //build the full param
        String output = "json"; //output format
        //create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        return url;
    }

    public ArrayList ReturnarrayTransitList() {  return arrayTransitList; }

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

    //解析導航路徑json
    public  ArrayList <StepList>List = new ArrayList<StepList>();
    private void parseJSON_route(String s) {
        JSONObject json;
        JSONArray jRoutes = null,jLegs = null,jSteps = null;
        String SpeakString = "";
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
                    String  BusRoute="",BusName="",agencies="",TrainStartTime="",TrainArriveTime="",headsign="";
                    double SLat=0.0,SLng=0.0;  double ELat=0.0,ELng=0.0;

                    for (int k = 0; k < jSteps.length(); k++) {
                        BusRoute="none";BusEndStop="none";BusStartStop="none";BusName="none";
                        TrainStartTime="none"; TrainArriveTime="none";headsign="none";
                        SLat=0.0; SLng=0.0;  ELat=0.0; ELng=0.0;
                        String Vechine="";
                        try { SubDistance = (String) ((JSONObject) jSteps.get(k)).getJSONObject("distance").get("text");
                        } catch (Exception e) { SubDistance = "none"; }

                        try { SubCostTime = (String) ((JSONObject) jSteps.get(k)).getJSONObject("duration").get("text");
                        } catch (Exception e) { SubCostTime = "none"; }

                        try { html_instruction = (String) ((JSONObject) jSteps.get(k)).get("html_instructions");
                        } catch (Exception e) { html_instruction = "none"; }

                        //語音
                        if(V)
                        {
                            SpeakString = ShowRoute(html_instruction);
                            SpeakString += "走" + SubDistance;
                            SpeakString += "大約" + SubCostTime;
                            if (i == 0&& k==0) tts.speak(SpeakString, TextToSpeech.QUEUE_ADD, null);
                        }

                        try {  //畫路線在地圖上
                            Polyline = (String) ((JSONObject) jSteps.get(k)).getJSONObject("polyline").get("points");
                            if(i==0)  DrawPolyLine(Polyline,"#CC0000");//polyline第一個
                            else if(i==1) DrawPolyLine(Polyline,"#227700"); //第二個
                            else if(i==2) DrawPolyLine(Polyline,"#00BBFF"); //第三個
                            else if(i==3) DrawPolyLine(Polyline,"#0000CC"); //第四個
                            else if(i==4) DrawPolyLine(Polyline,"#7700BB"); //第五個
                        } catch (Exception e) { Polyline = "none"; }

                        //not transit
                        String SubSubDistance="",SubSubCostTime="",Sub_html_instruction="",traveMode="";
                        JSONArray jSubSteps = null;
                        try {
                            ArrayList <SubSteps>SubStepsList = new ArrayList<SubSteps>();
                            jSubSteps = ((JSONObject) jSteps.get(k)).getJSONArray("steps");
                            for(int x=0;x<jSubSteps.length();x++)
                            {
                                try {
                                    SubSubDistance = (String) ((JSONObject) jSubSteps.get(x)).getJSONObject("distance").get("text");
                                    SubSubCostTime = (String) ((JSONObject) jSubSteps.get(x)).getJSONObject("duration").get("text");
                                }catch (Exception e) { SubSubDistance = "none"; SubSubCostTime="none"; }

                                try { Sub_html_instruction = (String) ((JSONObject) jSubSteps.get(x)).get("html_instructions");
                                } catch (Exception e) { Sub_html_instruction = "none"; }

                                //語音
                                if(V)
                                {
                                    SpeakString = ShowRoute(Sub_html_instruction);
                                    SpeakString += "走" + SubSubDistance;
                                    SpeakString += "大約" + SubSubCostTime;
                                    if (i == 0&& x==0&&k==0) tts.speak(SpeakString, TextToSpeech.QUEUE_ADD, null);
                                }

                                try { traveMode = (String) ((JSONObject) jSubSteps.get(x)).get("travel_mode");
                                } catch (Exception e) { traveMode = "none"; }

                                SubSteps substeps=new SubSteps();
                                substeps.Store(SubSubDistance,SubSubCostTime,Sub_html_instruction,traveMode);
                                SubStepsList.add(substeps);
                            }
                            Step step=new Step();
                            step.Store(k,SubDistance,SubCostTime,html_instruction,Polyline,SubStepsList);
                            array_StepList.add(step);
                        }catch (Exception e) { }

                        //transit
                        JSONObject transit_details_json;
                        try { transit_details_json =  ((JSONObject) jSteps.get(k)).getJSONObject("transit_details");
                            try { //終止站名+經緯度
                                try {
                                    JSONObject Etransit_details=((JSONObject)transit_details_json).getJSONObject("arrival_stop");
                                    try { ELat=(double) Etransit_details.getJSONObject("location").get("lat");
                                    }catch (Exception e) { ELat=0.0; }
                                    try { ELng=(double) Etransit_details.getJSONObject("location").get("lng");
                                    }catch (Exception e) { ELng=0.0; }
                                    try { BusEndStop = (String) Etransit_details.get("name");
                                    }catch (Exception e) { BusEndStop="none"; }
                                }catch (Exception e) { }
                            } catch (Exception e) { BusEndStop = "none"; }

                            try {  //起始站名+經緯度
                                try {
                                    JSONObject Etransit_details=((JSONObject)transit_details_json).getJSONObject("departure_stop");
                                    try { SLat = (double) Etransit_details.getJSONObject("location").get("lat");
                                    }catch (Exception e) { SLat=0.0;}
                                    try { SLng=(double) Etransit_details.getJSONObject("location").get("lng");
                                    }catch (Exception e) { SLng=0.0;}
                                    try { BusStartStop = (String) Etransit_details.get("name");
                                    }catch (Exception e) { BusStartStop="none"; }
                                }catch (Exception e) { }
                            } catch (Exception e) { BusStartStop = "none"; }

                            try {//起始火車時間
                                JSONObject Etransit_details=((JSONObject)transit_details_json).getJSONObject("departure_time");
                                try { TrainStartTime = (String) Etransit_details.get("text");
                                }catch (Exception e) { TrainStartTime="none"; }
                            }catch (Exception e) { }

                            try {//到站火車時間
                                JSONObject Stransit_details=((JSONObject)transit_details_json).getJSONObject("arrival_time");
                                try { TrainArriveTime = (String) Stransit_details.get("text");
                                }catch (Exception e) { TrainArriveTime="none"; }
                            }catch (Exception e) { }

                            //路線名稱，例如:火車站-竹中
                            try{ BusRoute=(String) ((JSONObject) transit_details_json).getJSONObject("line").get("name");
                            }catch (Exception e) { BusRoute = "none"; }

                            JSONArray jAgencies = null;
                            try{
                                JSONObject AgenciesObject=((JSONObject) transit_details_json).getJSONObject("line");
                                jAgencies  = ((JSONObject) AgenciesObject).getJSONArray("agencies");
                                Vechine=(String )((JSONObject) AgenciesObject).getJSONObject("vehicle").get("name");
                                for (int n = 0; n < jAgencies.length(); n++) {
                                    try { agencies = (String) ((JSONObject) jAgencies.get(n)).get("name");
                                    } catch (Exception e) { agencies = "none"; }
                                }
                            }catch (Exception e) { agencies = "none"; }

                            try { BusName = (String) ((JSONObject) transit_details_json).getJSONObject("line").get("short_name");
                            }catch (Exception e) { BusName = "none"; }

                            try { headsign = (String) ((JSONObject) transit_details_json).get("headsign");
                            }catch (Exception e) { headsign = "none"; }

                            TransitDetails transitDetails=new TransitDetails();
                            transitDetails.Store(ELat,ELng,BusEndStop,SLat,SLng,BusStartStop,BusRoute,Vechine,agencies,BusName,TrainStartTime,TrainArriveTime,headsign);
                            Step step=new Step();
                            step.Store2(k,SubDistance,SubCostTime,html_instruction,Polyline,transitDetails);
                            array_StepList.add(step);
                        } catch (Exception e) { BusEndStop = "none"; BusStartStop = "none";  BusRoute = "none"; BusName = "none"; Vechine="none"; agencies="none"; }

                        if(BusEndStop.equals("none") || BusStartStop.equals("none") || BusRoute.equals("none") || BusName.equals("none"))
                        { }
                        else {
                            if(Vechine.equals("巴士"))
                            {
                                BusNow busnow=new BusNow();
                                busnow.Store(BusEndStop,BusStartStop,BusRoute,BusName,agencies,SLat,SLng,ELat,ELng);
                                arrayTransitList.add(busnow);
                            }
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
            shift = 0; result = 0;
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



