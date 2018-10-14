package com.example.user.android0705;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
//取得公車路線的所有站牌資訊

public class GetAllStopsOfRoute {
     String id="XXX.XXX.XX.XXX"; //server IP:為了得到PTX資訊，我用Xampp & MySEQ 寫php檔來取得資訊

	//公車
    public ArrayList Call(String BusName,String AgencyName,String Type)
    {
        String url="";
        TransTask TT1 = new TransTask();
        try {
            if(BusName.equals("11")) //11號公車較特殊單獨處理
            {
                url="http://"+id+"/HsinchuBus" + BusName+"StopOfRoute.php";
                String ET = TT1.execute(url).get();
                return parseJson_bus11(ET,BusName);
            }
            else
            {
                url="http://"+id+"/HsinchuBus" + BusName+"StopOfRoute.php";
                String ET = TT1.execute(url).get();
                return parseJson(ET,BusName);
            }
        }catch (InterruptedException e) { e.printStackTrace();
        } catch (ExecutionException e) { e.printStackTrace(); }
        return null;
    }

	//客運
    public ArrayList CallInterCity(String BusName,String AgencyName,String Type)
    {
        String url="";
        TransTask TT1 = new TransTask();
        try {
                url="http://"+id+"/HsinchuInterCity_"+BusName+"_StopOfRoute_Nancy.php";
                String ET = TT1.execute(url).get();
                return parseJson(ET,BusName);

        }catch (InterruptedException e) { e.printStackTrace();
        } catch (ExecutionException e) { e.printStackTrace(); }
        return null;
    }

	//解析json
    private ArrayList parseJson(String s,String BusName)
    {
        JSONArray json;
        ArrayList AllStops=new ArrayList();
        int num,sequence=0;
        try {
            json = new JSONArray(s);
            for (int i = 0; i < json.length(); i++) {
                JSONArray Stops=null;
                String RouteUID="";
                JSONArray N=((JSONObject) json.get(0)).getJSONArray("Stops");//得到去程站數量
                num=N.length();//得到去程站數量
                int Direction;
                //路線代碼
                try {  RouteUID=(String) ((JSONObject)json.get(i)).get("SubRouteUID");
                } catch (Exception e) { RouteUID= "none"; }
                try {
                    Stops = ((JSONObject) json.get(i)).getJSONArray("Stops");
                    try { Direction = (int) ((JSONObject) json.get(i)).get("Direction"); //0:去程  1:返程
                    } catch (Exception e) { Direction= -1; } //沒有預設為-1

                    for (int j = 0; j < Stops.length(); j++) {
                        String StopUID = "", StopID = "",StopName = "";
                        double PositionLat,PositionLng;

                        try {StopUID = (String) ((JSONObject) Stops.get(j)).get("StopUID");
                        }  catch (Exception e) { StopUID= "none"; }
                        try { StopID = (String) ((JSONObject) Stops.get(j)).get("StopID");
                        } catch (Exception e) { StopID= "none"; }
                        try { StopName= (String) ((JSONObject) Stops.get(j)).getJSONObject("StopName").get("Zh_tw");
                        }  catch (Exception e) { StopName= "none"; }
                        try { PositionLat= (double) ((JSONObject) Stops.get(j)).getJSONObject("StopPosition").get("PositionLat");
                        }  catch (Exception e) { PositionLat= 0.0; }
                        try { PositionLng= (double) ((JSONObject) Stops.get(j)).getJSONObject("StopPosition").get("PositionLon");
                        }  catch (Exception e) { PositionLng= 0.0; }
                        //AllStopsOfRoute：記每個站牌資訊
                        AllStopsOfRoute allStopsOfRoute=new AllStopsOfRoute();
                        allStopsOfRoute.Store(RouteUID,StopUID,StopID,StopName,Direction,num,PositionLat,PositionLng,sequence);
                        sequence++;
                       AllStops.add(allStopsOfRoute);
                    }
                }  catch (Exception e) {}
            }
        } catch (JSONException e) { e.printStackTrace(); }
        return  AllStops;
    }

    //11路公車較特別，另外處理
    private ArrayList parseJson_bus11(String s,String BusName)
    {
        JSONArray json;
        ArrayList AllStops=new ArrayList();
        int num,sequence=0;
        try {
            json = new JSONArray(s);
            for (int i = 0; i < json.length(); i++) {
                JSONArray Stops=null;
                String RouteUID="";
                JSONArray N=((JSONObject) json.get(0)).getJSONArray("Stops");//得到去程站數量
                num=N.length();//得到去程站數量
                int Direction;

                //路線代碼
                try {  RouteUID=(String) ((JSONObject)json.get(i)).get("SubRouteUID");
                } catch (Exception e) { RouteUID= "none"; }

                try {
                    Stops = ((JSONObject) json.get(i)).getJSONArray("Stops");
                    try { Direction = (int) ((JSONObject) json.get(i)).get("Direction"); //0:去程  1:返程
                    } catch (Exception e) { Direction= -1; } //沒有預設為-1

                    for (int j = 0; j < Stops.length(); j++) {
                        String StopUID = "", StopID = "",StopName = "";
                        double PositionLat,PositionLng;

                        try {StopUID = (String) ((JSONObject) Stops.get(j)).get("StopUID");
                        }  catch (Exception e) { StopUID= "none"; }
                        try { StopID = (String) ((JSONObject) Stops.get(j)).get("StopID");
                        } catch (Exception e) { StopID= "none"; }
                        try { StopName= (String) ((JSONObject) Stops.get(j)).getJSONObject("StopName").get("Zh_tw");
                        }  catch (Exception e) { StopName= "none"; }
                        try { PositionLat= (double) ((JSONObject) Stops.get(j)).getJSONObject("StopPosition").get("PositionLat");
                        }  catch (Exception e) { PositionLat= 0.0; }
                        try { PositionLng= (double) ((JSONObject) Stops.get(j)).getJSONObject("StopPosition").get("PositionLon");
                        }  catch (Exception e) { PositionLng= 0.0; }

                        AllStopsOfRoute allStopsOfRoute=new AllStopsOfRoute();
                        allStopsOfRoute.Store(RouteUID,StopUID,StopID,StopName,Direction,num,PositionLat,PositionLng,sequence);
                        sequence++;
                        AllStops.add(allStopsOfRoute);
                    }
                }  catch (Exception e) {}
                if(i==1)
                {
                    AllStopsOfRoute allStopsOfRoute2=new AllStopsOfRoute();
                    allStopsOfRoute2.Store(RouteUID,"HSZTempStop","TempStop","順天宮",1,num,24.818542,120.927703,-1);
                    AllStops.add(allStopsOfRoute2);
                }
            }
        } catch (JSONException e) { e.printStackTrace(); }
        return  AllStops;
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
