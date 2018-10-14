package com.example.user.android0705;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;
//取得公車或客運列表

public class BusInformation { //取得公車/客運列表

    String id="XXX.XXX.XX.XXX"; //server IP:為了得到PTX資訊，我用Xampp & MySEQ 寫php檔來取得資訊
    ArrayList arrayList = new ArrayList();//建立一個ArrayList物件
    ArrayList arrayListInterCity = new ArrayList();//建立一個ArrayList物件

    public void initization()
    {
        CountyBusList();//公車
        InterCityList();//公路客運
    }

    //為取得客運列表
    public void InterCityList()  //客運
    {
        TransTask TT1 = new TransTask();
        try {
            //連結取得客運列表
            String InterCityRouteJson = TT1.execute( "http://"+id+"/NancyInterCityBusApi0811AllTaiwanInformation.php").get();
            parseJSONInterCity(InterCityRouteJson);
        }catch (InterruptedException e) { e.printStackTrace();
        } catch (ExecutionException e) { e.printStackTrace(); }
    }

    //為取得公車列表
    public void CountyBusList()
    {
        TransTask TT1 = new TransTask();
        try {
            //連結到server取得客運列表
            String BusRouteJson = TT1.execute( "http://"+id+"/NancyBusRouteInformationHsinchu0731.php").get();
            parseJSON(BusRouteJson);
        }catch (InterruptedException e) { e.printStackTrace();
        }catch (ExecutionException e) { e.printStackTrace(); }
    }

	//解析json(公車)
    private void parseJSON(String s) {
        JSONArray json;
        try {
            json = new JSONArray(s);
            for (int i = 0; i < json.length(); i++) {
                JSONArray SubRoutes=null;
                String RouteUID = null,StartStop=null,EndStop=null,RouteName=null;
                int BusRouteType=0;
                try { RouteUID = (String) ((JSONObject) json.get(i)).get("RouteUID"); }
                catch (Exception e) { RouteUID= "none"; }
                try {  BusRouteType= (int) ((JSONObject) json.get(i)).get("BusRouteType"); }
                catch (Exception e) { BusRouteType= 0; }
                try { RouteName = (String) ((JSONObject) json.get(i)).getJSONObject("RouteName").get("Zh_tw");}
                catch (Exception e) { RouteName= "none"; }
                try { StartStop= (String) ((JSONObject) json.get(i)).get("DepartureStopNameZh"); }
                catch (Exception e) { StartStop= "none"; }
                try { EndStop= (String) ((JSONObject) json.get(i)).get("DestinationStopNameZh"); }
                catch (Exception e) { EndStop= "none"; }
                try {
                    SubRoutes = ((JSONObject) json.get(i)).getJSONArray("SubRoutes");
                    for (int j = 0; j < SubRoutes.length(); j++) {
                        String SubRouteId = null, BusName = null, Headsign = null;

                        try {  SubRouteId = (String) ((JSONObject) SubRoutes.get(j)).get("SubRouteUID");}
                        catch (Exception e) { SubRouteId= "none"; }
                        try { BusName = (String) ((JSONObject) SubRoutes.get(j)).getJSONObject("SubRouteName").get("Zh_tw");}
                        catch (Exception e) { BusName= "none"; }
                        try { Headsign = (String) ((JSONObject) SubRoutes.get(j)).get("Headsign");}
                        catch (Exception e) { Headsign= "none"; }
                        //設置Bus物件存一種公車的資訊
                        Bus bus = new Bus();
                        bus.Store(RouteUID, SubRouteId, RouteName,BusName, Headsign, BusRouteType, StartStop, EndStop);
                        //存完再加道arrayList中
                        arrayList.add(bus);
                    }
                } catch (Exception e) { }
            }
        } catch (JSONException e) { e.printStackTrace(); }
    }

	//解析json(客運)
    private void parseJSONInterCity(String s) {
        JSONArray json;
        try {
            json = new JSONArray(s);
            for (int i = 0; i < json.length(); i++) {
                JSONArray Operators=null,SubRoutes=null;
                String RouteUID = null,StartStop=null,EndStop=null;
                String RouteName=null;
                int BusRouteType=0;
                try { RouteUID = (String) ((JSONObject) json.get(i)).get("RouteUID"); }
                catch (Exception e) { RouteUID= "none"; }

                ArrayList arrayListOperators = new ArrayList();
                try {
                    Operators = ((JSONObject) json.get(i)).getJSONArray("Operators");
                    for (int j = 0; j < Operators.length(); j++) {
                        String OperatorID = null, OperatorName = null;
                        try {  OperatorID = (String) ((JSONObject) Operators.get(j)).get("OperatorID");}
                        catch (Exception e) { OperatorID= "none"; }
                        try { OperatorName = (String) ((JSONObject) Operators.get(j)).getJSONObject("OperatorName").get("Zh_tw");}
                        catch (Exception e) { OperatorName= "none"; }
                        //客運才有：會有同一種名稱的班次但不同家客運都有
                        //設置Operator來記錄單一加客運商
                        Operator op=new Operator();
                        op.Store(OperatorID,OperatorName);
                        //存完加進列表
                        arrayListOperators.add(op);
                    }
                }catch (Exception e) {}

                try {  BusRouteType= (int) ((JSONObject) json.get(i)).get("BusRouteType"); }
                catch (Exception e) { BusRouteType= 0; }
                try { RouteName = (String) ((JSONObject) json.get(i)).getJSONObject("RouteName").get("Zh_tw");}
                catch (Exception e) { RouteName= "none"; }
                try { StartStop= (String) ((JSONObject) json.get(i)).get("DepartureStopNameZh"); }
                catch (Exception e) { StartStop= "none"; }
                try { EndStop= (String) ((JSONObject) json.get(i)).get("DestinationStopNameZh"); }
                catch (Exception e) { EndStop= "none"; }
                try {
                    SubRoutes = ((JSONObject) json.get(i)).getJSONArray("SubRoutes");
                    for (int j = 0; j < SubRoutes.length(); j++) {
                        String SubRouteId = null, BusName = null, Headsign= null;

                        try {  SubRouteId = (String) ((JSONObject) SubRoutes.get(j)).get("SubRouteUID");}
                        catch (Exception e) { SubRouteId= "none"; }
                        try { BusName = (String) ((JSONObject) SubRoutes.get(j)).getJSONObject("SubRouteName").get("Zh_tw");}
                        catch (Exception e) { BusName= "none"; }
                        try { Headsign = (String) ((JSONObject) SubRoutes.get(j)).get("Headsign");}
                        catch (Exception e) { Headsign= "none"; }

                        Bus bus = new Bus();
                        bus.StoreInterCity(RouteUID,arrayListOperators, RouteName,SubRouteId, BusName, Headsign, BusRouteType, StartStop, EndStop);
                        arrayListInterCity .add(bus);
                    }
                } catch (Exception e) { }
            }
        } catch (JSONException e) { e.printStackTrace(); }
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
