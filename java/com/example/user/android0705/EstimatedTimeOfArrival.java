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
//取得預估到站資訊(ptx)

public class EstimatedTimeOfArrival {
    String id="XXX.XXX.XX.XXX";//server IP:為了得到PTX資訊，我用Xampp & MySEQ 寫php檔來取得資訊

	//公車
    public ArrayList Call(String BusName,String AgencyName,String Type)
    {
        String url="";
        TransTask TT1 = new TransTask();
        try {
            url="http://"+id+"/HsinchuBus" + BusName+"EstimatedTimeOfArrival.php";
            String ET = TT1.execute(url).get();
            return parseJson(ET);
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
            url="http://"+id+"/HsinchuInterCity_"+BusName+"_EstimatedTimeOfArrival_Nancy.php";
            String ET = TT1.execute(url).get();
            return parseJson(ET);
        }catch (InterruptedException e) { e.printStackTrace();
        } catch (ExecutionException e) { e.printStackTrace(); }
        return null;
    }
	
	//解析JSON
    private ArrayList parseJson(String s)
    {
        JSONArray json;
        ArrayList StopInformation=new ArrayList();
        try {
            json = new JSONArray(s);
            for (int i = 0; i < json.length(); i++) {
                String PlateNumb="",StopName="",StopUID="",StopID="",RouteUID="";
                int Direction,EstimateTime,StopCountDown,StopStatus,StopSequence;
                try { RouteUID = (String) ((JSONObject) json.get(i)).get("SubRouteUID");
                }  catch (Exception e) { RouteUID= "none"; }
                try { PlateNumb = (String) ((JSONObject) json.get(i)).get("PlateNumb");
                }  catch (Exception e) { PlateNumb= "none"; }
                try { StopUID = (String) ((JSONObject) json.get(i)).get("StopUID");
                }  catch (Exception e) { StopUID= "none"; }
                try { StopSequence = (int) ((JSONObject) json.get(i)).get("StopSequence");
                }  catch (Exception e) { StopSequence=-1; }
                try { StopID = (String) ((JSONObject) json.get(i)).get("StopID");
                }  catch (Exception e) { StopID= "none"; }
                try { StopName= (String) ((JSONObject) json.get(i)).getJSONObject("StopName").get("Zh_tw");
                }  catch (Exception e) { StopName= "none"; }
                try {  Direction= (int) ((JSONObject) json.get(i)).get("Direction");
                }  catch (Exception e) { Direction=-1; }
                try {  EstimateTime= (int) ((JSONObject) json.get(i)).get("EstimateTime");
                }  catch (Exception e) { EstimateTime=-1; }
                try {  StopCountDown= (int) ((JSONObject) json.get(i)).get("StopCountDown");
                }  catch (Exception e) { StopCountDown=-1; }
                try {  StopStatus= (int) ((JSONObject) json.get(i)).get("StopCountDown");
                }  catch (Exception e) { StopStatus=-1; }
                //StopStatus (string, optional): 車輛狀態備註 = ['1: 尚未發車', '2: 交管不停靠', '3: 末班車已過', '4: 今日未營運'],
                StopEstimatedTimeOfArrival S=new StopEstimatedTimeOfArrival();
                //將每一站的估計時間存在StopEstimatedTimeOfArrival
                S.Store(RouteUID,PlateNumb,StopUID,StopID,StopName,Direction,EstimateTime,StopCountDown,StopStatus,StopSequence);
                //再整合進arrayList
                StopInformation.add(S);
            }
        } catch (JSONException e) { e.printStackTrace(); }
        return StopInformation;
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
