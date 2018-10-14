package com.example.user.android0705;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
//比較ptx的資訊跟google map api的資訊(有時2者的公車名會有些許差異)

public class CheckBus {

    //目前只完成新竹的公車
    public ArrayList HsinchuBus1(BusNow busnow, BusInformation busInformation)
    {
             ArrayList SubarrayList = new ArrayList();//建立一個ArrayList物件
            if(busnow.getBusNowName().equals("藍1區")||busnow.getBusNowName().equals("藍1區間車"))
            {
                ArrayList test=GetEstimatedTimeOfArrival("Blue01",busnow.getBusNowAgencies(),"Bus");
                SubarrayList.add(busnow.getBusNowName()); //0
                SubarrayList.add(busnow.getBusNowAgencies()); //1
                SubarrayList.add("bus"); //2
                //取得公車路線所有停靠站
                ArrayList Stops=GetAllStopsOfRoute("Blue01",busnow.getBusNowAgencies(),"Bus");
                if(test!=null) SubarrayList.add(test); //3
                else SubarrayList.add(null);

                if(Stops!=null) SubarrayList.add(Stops); //4
                else SubarrayList.add(null);
            }
            else if(busnow.getBusNowName().equals("2支"))
            {
                ArrayList test=GetEstimatedTimeOfArrival("2",busnow.getBusNowAgencies(),"Bus");
                SubarrayList.add(busnow.getBusNowName()); //0
                SubarrayList.add(busnow.getBusNowAgencies()); //1
                SubarrayList.add("bus"); //2
                ArrayList Stops=GetAllStopsOfRoute("2Sub",busnow.getBusNowAgencies(),"Bus");
                if(test!=null) SubarrayList.add(test); //3
                else SubarrayList.add(null);

                if(Stops!=null) SubarrayList.add(Stops); //4
                else SubarrayList.add(null);
            }
            else if(busnow.getBusNowName().equals("11甲"))
            {
                ArrayList test=GetEstimatedTimeOfArrival("11",busnow.getBusNowAgencies(),"Bus");
                SubarrayList.add(busnow.getBusNowName()); //0
                SubarrayList.add(busnow.getBusNowAgencies()); //1
                SubarrayList.add("bus"); //2
                ArrayList Stops=GetAllStopsOfRoute("11_01",busnow.getBusNowAgencies(),"Bus");
                if(test!=null) SubarrayList.add(test); //3
                else SubarrayList.add(null);

                if(Stops!=null) SubarrayList.add(Stops); //4
                else SubarrayList.add(null);
            }
            else if(busnow.getBusNowName().equals("11支"))
            {
                ArrayList test=GetEstimatedTimeOfArrival("11",busnow.getBusNowAgencies(),"Bus");
                SubarrayList.add(busnow.getBusNowName()); //0
                SubarrayList.add(busnow.getBusNowAgencies()); //1
                SubarrayList.add("bus"); //2
                ArrayList Stops=GetAllStopsOfRoute("11Sub",busnow.getBusNowAgencies(),"Bus");
                if(test!=null) SubarrayList.add(test); //3
                else SubarrayList.add(null);

                if(Stops!=null) SubarrayList.add(Stops); //4
                else SubarrayList.add(null);
            }
            else if(busnow.getBusNowName().equals("20支"))
            {
                ArrayList test=GetEstimatedTimeOfArrival("20",busnow.getBusNowAgencies(),"Bus");
                SubarrayList.add(busnow.getBusNowName()); //0
                SubarrayList.add(busnow.getBusNowAgencies()); //1
                SubarrayList.add("bus"); //2
                ArrayList Stops=GetAllStopsOfRoute("20Sub",busnow.getBusNowAgencies(),"Bus");
                if(test!=null) SubarrayList.add(test); //3
                else SubarrayList.add(null);

                if(Stops!=null) SubarrayList.add(Stops); //4
                else SubarrayList.add(null);
            }
            else if(busnow.getBusNowName().equals("23支"))
            {
                ArrayList test=GetEstimatedTimeOfArrival("23",busnow.getBusNowAgencies(),"Bus");
                SubarrayList.add(busnow.getBusNowName()); //0
                SubarrayList.add(busnow.getBusNowAgencies()); //1
                SubarrayList.add("bus"); //2
                ArrayList Stops=GetAllStopsOfRoute("23Sub",busnow.getBusNowAgencies(),"Bus");
                if(test!=null) SubarrayList.add(test); //3
                else SubarrayList.add(null);

                if(Stops!=null) SubarrayList.add(Stops); //4
                else SubarrayList.add(null);
            }
            else if(busnow.getBusNowName().equals("52支"))
            {
                ArrayList test=GetEstimatedTimeOfArrival("52",busnow.getBusNowAgencies(),"Bus");
                SubarrayList.add(busnow.getBusNowName()); //0
                SubarrayList.add(busnow.getBusNowAgencies()); //1
                SubarrayList.add("bus"); //2
                ArrayList Stops=GetAllStopsOfRoute("52Sub",busnow.getBusNowAgencies(),"Bus");
                if(test!=null) SubarrayList.add(test); //3
                else SubarrayList.add(null);

                if(Stops!=null) SubarrayList.add(Stops); //4
                else SubarrayList.add(null);
            }
            else if(busnow.getBusNowName().equals("世博3"))
            {
                ArrayList test=GetEstimatedTimeOfArrival("Expo_03",busnow.getBusNowAgencies(),"Bus");
                SubarrayList.add(busnow.getBusNowName()); //0
                SubarrayList.add(busnow.getBusNowAgencies()); //1
                SubarrayList.add("bus"); //2
                ArrayList Stops=GetAllStopsOfRoute("Expo_03",busnow.getBusNowAgencies(),"Bus");
                if(test!=null) SubarrayList.add(test); //3
                else SubarrayList.add(null);

                if(Stops!=null) SubarrayList.add(Stops); //4
                else SubarrayList.add(null);
            }
            else if(busnow.getBusNowName().equals("世博5"))
            {
                ArrayList test=GetEstimatedTimeOfArrival("Expo_05",busnow.getBusNowAgencies(),"Bus");
                SubarrayList.add(busnow.getBusNowName()); //0
                SubarrayList.add(busnow.getBusNowAgencies()); //1
                SubarrayList.add("bus"); //2
                ArrayList Stops=GetAllStopsOfRoute("Expo_05",busnow.getBusNowAgencies(),"Bus");
                if(test!=null) SubarrayList.add(test); //3
                else SubarrayList.add(null);

                if(Stops!=null) SubarrayList.add(Stops); //4
                else SubarrayList.add(null);
            }
            else if(busnow.getBusNowName().equals("73區"))
            {
                ArrayList test=GetEstimatedTimeOfArrival("73",busnow.getBusNowAgencies(),"Bus");
                SubarrayList.add(busnow.getBusNowName()); //0
                SubarrayList.add(busnow.getBusNowAgencies()); //1
                SubarrayList.add("bus"); //2
                ArrayList Stops=GetAllStopsOfRoute("73_02",busnow.getBusNowAgencies(),"Bus");
                if(test!=null) SubarrayList.add(test); //3
                else SubarrayList.add(null);

                if(Stops!=null) SubarrayList.add(Stops); //4
                else SubarrayList.add(null);
            }
            else if(busnow.getBusNowName().equals("藍線"))
            {
                ArrayList test=GetEstimatedTimeOfArrival("Blue",busnow.getBusNowAgencies(),"Bus");
                SubarrayList.add(busnow.getBusNowName()); //0
                SubarrayList.add(busnow.getBusNowAgencies()); //1
                SubarrayList.add("bus"); //2
                ArrayList Stops=GetAllStopsOfRoute("Blue",busnow.getBusNowAgencies(),"Bus");
                if(test!=null) SubarrayList.add(test); //3
                else SubarrayList.add(null);

                if(Stops!=null) SubarrayList.add(Stops); //4
                else SubarrayList.add(null);
            }
            else if(busnow.getBusNowName().equals("綠線"))
            {
                ArrayList test=GetEstimatedTimeOfArrival("Green",busnow.getBusNowAgencies(),"Bus");
                SubarrayList.add(busnow.getBusNowName()); //0
                SubarrayList.add(busnow.getBusNowAgencies()); //1
                SubarrayList.add("bus"); //2
                ArrayList Stops=GetAllStopsOfRoute("Green",busnow.getBusNowAgencies(),"Bus");
                if(test!=null) SubarrayList.add(test); //3
                else SubarrayList.add(null);

                if(Stops!=null) SubarrayList.add(Stops); //4
                else SubarrayList.add(null);
            }
            else if(busnow.getBusNowName().equals("藍15區")||busnow.getBusNowName().equals("藍15區間車"))
            {
                if(busnow.getBusRoute().equals("火車站-旅遊服務中心假日"))
                {
                    ArrayList test=GetEstimatedTimeOfArrival("Blue15",busnow.getBusNowAgencies(),"Bus");
                    SubarrayList.add(busnow.getBusNowName()); //0
                    SubarrayList.add(busnow.getBusNowAgencies()); //1
                    SubarrayList.add("bus"); //2
                    ArrayList Stops=GetAllStopsOfRoute("Blue15_01",busnow.getBusNowAgencies(),"Bus");
                    if(test!=null) SubarrayList.add(test); //3
                    else SubarrayList.add(null);

                    if(Stops!=null) SubarrayList.add(Stops); //4
                    else SubarrayList.add(null);
                }
                else{
                    ArrayList test=GetEstimatedTimeOfArrival("Blue15",busnow.getBusNowAgencies(),"Bus");
                    SubarrayList.add(busnow.getBusNowName()); //0
                    SubarrayList.add(busnow.getBusNowAgencies()); //1
                    SubarrayList.add("bus"); //2
                    ArrayList Stops=GetAllStopsOfRoute("Blue15",busnow.getBusNowAgencies(),"Bus");
                    if(test!=null) SubarrayList.add(test); //3
                    else SubarrayList.add(null);

                    if(Stops!=null) SubarrayList.add(Stops); //4
                    else SubarrayList.add(null);
                }
            }
            else if(busnow.getBusNowName().equals("81"))
            {
                if(busnow.getBusRoute().equals("古奇峰-新莊車站先經關東路"))
                {
                    ArrayList test=GetEstimatedTimeOfArrival("81",busnow.getBusNowAgencies(),"Bus");
                    SubarrayList.add(busnow.getBusNowName()); //0
                    SubarrayList.add(busnow.getBusNowAgencies()); //1
                    SubarrayList.add("bus"); //2
                    ArrayList Stops=GetAllStopsOfRoute("81",busnow.getBusNowAgencies(),"Bus");
                    if(test!=null) SubarrayList.add(test); //3
                    else SubarrayList.add(null);

                    if(Stops!=null) SubarrayList.add(Stops); //4
                    else SubarrayList.add(null);
                }
                else{
                    ArrayList test=GetEstimatedTimeOfArrival("81",busnow.getBusNowAgencies(),"Bus");
                    SubarrayList.add(busnow.getBusNowName()); //0
                    SubarrayList.add(busnow.getBusNowAgencies()); //1
                    SubarrayList.add("bus"); //2
                    ArrayList Stops=GetAllStopsOfRoute("81_01",busnow.getBusNowAgencies(),"Bus");
                    if(test!=null) SubarrayList.add(test); //3
                    else SubarrayList.add(null);

                    if(Stops!=null) SubarrayList.add(Stops); //4
                    else SubarrayList.add(null);
                }
            }
            else if(busnow.getBusNowName().equals("73"))
            {
                if(busnow.getBusRoute().equals("大潤發2站-→花園新城"))
                {
                    ArrayList test=GetEstimatedTimeOfArrival("73",busnow.getBusNowAgencies(),"Bus");
                    SubarrayList.add(busnow.getBusNowName()); //0
                    SubarrayList.add(busnow.getBusNowAgencies()); //1
                    SubarrayList.add("bus"); //2
                    ArrayList Stops=GetAllStopsOfRoute("73",busnow.getBusNowAgencies(),"Bus");
                    if(test!=null) SubarrayList.add(test); //3
                    else SubarrayList.add(null);

                    if(Stops!=null) SubarrayList.add(Stops); //4
                    else SubarrayList.add(null);
                }
                else{
                    ArrayList test=GetEstimatedTimeOfArrival("73",busnow.getBusNowAgencies(),"Bus");
                    SubarrayList.add(busnow.getBusNowName()); //0
                    SubarrayList.add(busnow.getBusNowAgencies()); //1
                    SubarrayList.add("bus"); //2
                    ArrayList Stops=GetAllStopsOfRoute("73_01",busnow.getBusNowAgencies(),"Bus");
                    if(test!=null) SubarrayList.add(test); //3
                    else SubarrayList.add(null);

                    if(Stops!=null) SubarrayList.add(Stops); //4
                    else SubarrayList.add(null);
                }
            }
            else
            {
                for(int j=0;j<busInformation.arrayList.size();j++)
                {
                    Bus bus=(Bus)busInformation.arrayList.get(j);
                    if(busnow.getBusNowName().equals(bus.getBusName()))
                    {
                        SubarrayList.add(busnow.getBusNowName());
                        SubarrayList.add(busnow.getBusNowAgencies());
                        SubarrayList.add("bus");
                        ArrayList test=GetEstimatedTimeOfArrival(busnow.getBusNowName(),busnow.getBusNowAgencies(),"Bus");
                        ArrayList Stops=GetAllStopsOfRoute(busnow.getBusNowName(),busnow.getBusNowAgencies(),"Bus");
                        if(test!=null) SubarrayList.add(test); //3
                        else SubarrayList.add(null);

                        if(Stops!=null) SubarrayList.add(Stops); //4
                        else SubarrayList.add(null);
                    }
                }
                for(int j=0;j<busInformation.arrayListInterCity.size();j++) //客運
                {
                    Bus bus=(Bus)busInformation.arrayListInterCity.get(j);
                    if(busnow.getBusNowName().equals(bus.getBusName()))
                    {
                        SubarrayList.add(busnow.getBusNowName());
                        SubarrayList.add(busnow.getBusNowAgencies());
                        SubarrayList.add("intercity");
                        ArrayList test=GetEstimatedTimeOfArrival(busnow.getBusNowName(),busnow.getBusNowAgencies(),"InterCity");
                        ArrayList Stops=GetAllStopsOfRoute(busnow.getBusNowName(),busnow.getBusNowAgencies(),"InterCity");
                        if(test!=null) SubarrayList.add(test); //3
                        else SubarrayList.add(null);
                        if(Stops!=null) SubarrayList.add(Stops); //4
                        else SubarrayList.add(null);
                    }
                }
            }
        return SubarrayList;
    }

    //取得公車路線所有停靠站
    private ArrayList GetAllStopsOfRoute(String BusName,String AgencyName,String Type)
    {
        if(Type.equals("Bus"))
        {
            GetAllStopsOfRoute getAllStopsOfRoute=new GetAllStopsOfRoute();
            ArrayList test=getAllStopsOfRoute.Call(BusName,AgencyName,Type);
            return test;
        }
        else
        {
            GetAllStopsOfRoute getAllStopsOfRoute=new GetAllStopsOfRoute();
            ArrayList test=getAllStopsOfRoute.CallInterCity(BusName,AgencyName,Type);
            return test;
        }
    }
	
	//取得公車路線預計到站時間
    private ArrayList  GetEstimatedTimeOfArrival(String BusName,String AgencyName,String Type)
    {
        if(Type.equals("Bus"))
        {
            EstimatedTimeOfArrival estimatedTimeOfArrival=new EstimatedTimeOfArrival();
            ArrayList test=estimatedTimeOfArrival.Call(BusName,AgencyName,Type);
            return  test;
        }
        else
        {
            EstimatedTimeOfArrival estimatedTimeOfArrival=new EstimatedTimeOfArrival();
            ArrayList test=estimatedTimeOfArrival.CallInterCity(BusName,AgencyName,Type);
            return  test;
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
