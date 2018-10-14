package com.example.user.android0705;

import android.os.AsyncTask;
import android.widget.TextView;

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
//242行

public class GetInterCitySchedule {
    String id="XXX.XXX.XX.XXX"; //server IP:為了得到PTX資訊，我用Xampp & MySEQ 寫php檔來取得資訊
    int W;

    public void Call(TextView textView, int week)
    {
        W=week;
        String url="http://"+id+"/InterCitySchedule_Hsinchu_5608_Nancy.php";
        String url2="http://"+id+"/InterCitySchedule_Hsinchu_5801_Nancy.php";
        String url3="http://"+id+"/InterCitySchedule_Hsinchu_5804_Nancy.php";

        TransTask TT1 = new TransTask();
        TransTask TT2 = new TransTask();
        TransTask TT3= new TransTask();
        try {
            String ET = TT1.execute(url).get();
            parseJson(ET);
            String ET2 = TT2.execute(url2).get();
            parseJson(ET2);
            String ET3 = TT3.execute(url3).get();
            parseJson(ET3);
        }catch (InterruptedException e) { e.printStackTrace();
        } catch (ExecutionException e) { e.printStackTrace(); }
        //return null;
    }

    public ArrayList AllBus=new ArrayList();

    private void parseJson(String s)
    {
        JSONArray json;
        int num;
        try {
            json = new JSONArray(s);
            for (int i = 0; i < json.length(); i++) {
                //進一台新的公車就new一個
                BusSchedule busSchedule=new BusSchedule();

                String SubRouteUID="",SubRouteName="",RouteName="";
                int Direction=-1;
                //BUS路線代碼(SUB)
                try { SubRouteUID=(String) ((JSONObject)json.get(i)).get("SubRouteUID");
                } catch (Exception e) { SubRouteUID= "none"; }
                //BUS Name
                try { SubRouteName= (String) ((JSONObject) json.get(i)).getJSONObject("SubRouteName").get("Zh_tw");
                }  catch (Exception e) { SubRouteName= "none"; }
                try { RouteName= (String) ((JSONObject) json.get(i)).getJSONObject("RouteName").get("Zh_tw");
                }  catch (Exception e) { RouteName= "none"; }
                //方向
                try { Direction = (int) ((JSONObject) json.get(i)).get("Direction"); //0:去程  1:返程
                } catch (Exception e) { Direction= -1; } //沒有預設為-1

                busSchedule.Store(SubRouteUID,SubRouteName,Direction,RouteName);
                //時間表
                JSONArray TimeTable=null;
                try {
                    TimeTable = ((JSONObject) json.get(i)).getJSONArray("Timetables");
                    for (int j = 0; j < TimeTable.length(); j++) {
                        int Mon,Tue,Wed,Thu,Fri,Sat,Sun;
						//判斷此車號星期己有無開；有=1 無=0
                        try{
                            Sun=(int)((JSONObject) TimeTable.get(j)).getJSONObject("ServiceDay").get("Sunday");
                            Mon=(int)((JSONObject) TimeTable.get(j)).getJSONObject("ServiceDay").get("Monday");
                            Tue=(int)((JSONObject) TimeTable.get(j)).getJSONObject("ServiceDay").get("Tuesday");
                            Wed=(int)((JSONObject) TimeTable.get(j)).getJSONObject("ServiceDay").get("Wednesday");
                            Thu=(int)((JSONObject) TimeTable.get(j)).getJSONObject("ServiceDay").get("Thursday");
                            Fri=(int)((JSONObject) TimeTable.get(j)).getJSONObject("ServiceDay").get("Friday");
                            Sat=(int)((JSONObject) TimeTable.get(j)).getJSONObject("ServiceDay").get("Saturday");
                        }catch (Exception e) { Sun=-1; Mon=-1; Tue=-1; Wed=-1; Thu=-1; Fri=-1; Sat=-1; }

                        JSONArray StopTimes=null;
                        try {
                            StopTimes= ((JSONObject) TimeTable.get(j)).getJSONArray("StopTimes");
                            //發車時間
                            for (int k= 0; k < StopTimes.length(); k++) {
                                String StopUID = "",StopName = "",ArrivalTime="",DepartureTime="",DepartureTimeHour="",DepartureTimeMin="";
                                try {StopUID = (String) ((JSONObject) StopTimes.get(k)).get("StopUID");
                                }  catch (Exception e) { StopUID= "none"; }

                                try { StopName= (String) ((JSONObject) StopTimes.get(k)).getJSONObject("StopName").get("Zh_tw");
                                }  catch (Exception e) { StopName= "none"; }

                                try {ArrivalTime = (String) ((JSONObject) StopTimes.get(k)).get("ArrivalTime");
                                }  catch (Exception e) { ArrivalTime= "none"; }

                                try {
                                    DepartureTime = (String) ((JSONObject) StopTimes.get(k)).get("DepartureTime");
                                    String[] split_line = DepartureTime.split(":");
                                    DepartureTimeHour = split_line[0];
                                    DepartureTimeMin = split_line[1];
                                }  catch (Exception e) { DepartureTime= "none"; }

								//單一站單一時段資訊
                                OneTimeStopSchedule oneTimeStopSchedule=new OneTimeStopSchedule();
                                oneTimeStopSchedule.Store(StopName,StopUID, ArrivalTime , DepartureTime,DepartureTimeHour,DepartureTimeMin);
								//存入BusSchedule對應的日期的arrayList裡
                                if(Sun==1) busSchedule.SunStoreTimeTable(oneTimeStopSchedule);
                                if(Sat==1)  busSchedule.SatStoreTimeTable(oneTimeStopSchedule);
                                if(Mon==1)  busSchedule.MonStoreTimeTable(oneTimeStopSchedule);
                                if(Tue==1)  busSchedule.TueStoreTimeTable(oneTimeStopSchedule);
                                if(Wed==1)  busSchedule.WedStoreTimeTable(oneTimeStopSchedule);
                                if(Thu==1)  busSchedule.ThuStoreTimeTable(oneTimeStopSchedule);
                                if(Fri==1)  busSchedule.FriStoreTimeTable(oneTimeStopSchedule);

                            }
                        }catch (Exception e) {}
                    }
                }  catch (Exception e) {}
                Sorting(busSchedule);//將時段sorting
                AllBus.add(busSchedule);//AllBus負責整合所有公車，做好一種公車所有的時刻資訊後加到裡面
            }
        } catch (JSONException e) { e.printStackTrace(); }
    }

	//將時刻表排序
    private void Sorting(BusSchedule busSchedule)
    {
        if(W==1)
        {
            ArrayList<OneTimeStopSchedule> sun=busSchedule.getSunday();//取得目前的list
            ArrayList<OneTimeStopSchedule> r_sun=SubSorting(sun);//丟進SubSorting排序
            sun=r_sun;//傳回來的ArrayList再放回原本的
        }
        else if(W==2)
        {
            ArrayList<OneTimeStopSchedule> mon=busSchedule.getMonday();
            ArrayList<OneTimeStopSchedule> r_mon=SubSorting(mon);
            mon=r_mon;
        }
        else if(W==3)
        {
            ArrayList<OneTimeStopSchedule> tue=busSchedule.getThueday();
            ArrayList<OneTimeStopSchedule> r_tue=SubSorting(tue);
            tue=r_tue;
        }
        else if(W==4)
        {
            ArrayList<OneTimeStopSchedule> wed=busSchedule.getWednesday();
            ArrayList<OneTimeStopSchedule> r_wed=SubSorting(wed);
            wed=r_wed;
        }
        else if(W==5)
        {
            ArrayList<OneTimeStopSchedule> thu=busSchedule.getThursday();
            ArrayList<OneTimeStopSchedule> r_thu=SubSorting(thu);
            thu=r_thu;
        }
        else if(W==6)
        {
            ArrayList<OneTimeStopSchedule> fri=busSchedule.getFriday();
            ArrayList<OneTimeStopSchedule> r_fri=SubSorting(fri);
            fri=r_fri;
        }
        else if(W==7)
        {
            ArrayList<OneTimeStopSchedule> sat=busSchedule.getThueday();
            ArrayList<OneTimeStopSchedule> r_sat=SubSorting(sat);
            sat=r_sat;
        }
    }


	//排序
    private ArrayList<OneTimeStopSchedule> SubSorting(ArrayList<OneTimeStopSchedule> list)
    {
        ArrayList<Integer> hour = new ArrayList<Integer>();
        ArrayList<Integer> min = new ArrayList<Integer>();
		//先把hour及min資訊分別存進arratList
        for(int i=0;i<list.size();i++)
        {
            OneTimeStopSchedule one=list.get(i);
            int h=Integer.valueOf(one.DepartureTimeHour);
            int m=Integer.valueOf(one.DepartureTimeMin);
            hour.add(h); min.add(m);
        }

		//進行sorting；由小排到大
        for(int i=0;i<hour.size();i++)
        {
            for(int j=i+1;j<hour.size();j++)
            {
                if(hour.get(i)>hour.get(j))
                {
                    int temph=hour.get(i);  int tempm=min.get(i);
                    hour.set(i,hour.get(j)); min.set(i,min.get(j));
                    hour.set(j,temph);       min.set(j,tempm);
                }
                else if(hour.get(i) == hour.get(j))
                {
                    if(min.get(i)>min.get(j))
                    {
                        int temph=hour.get(i);  int tempm=min.get(i);
                        hour.set(i,hour.get(j)); min.set(i,min.get(j));
                        hour.set(j,temph);       min.set(j,tempm);
                    }
                }
            }
        }

        for(int i=0;i<list.size();i++)
        {
            OneTimeStopSchedule one=list.get(i);
			//將其變成(hour):(min)的形式
            one.setDepartureTime(hour.get(i),min.get(i));
        }
        return list;
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

