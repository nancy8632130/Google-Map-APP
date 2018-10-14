package com.example.user.android0705;

import java.util.ArrayList;
//取得公車時刻表

public class BusSchedule {
    private  String SubRouteUID,SubRouteName,RouteName;
    private int Direction;
    //用來記錄
    private ArrayList<OneTimeStopSchedule> Monday=new ArrayList<OneTimeStopSchedule>();
    private ArrayList<OneTimeStopSchedule> Thueday=new ArrayList<OneTimeStopSchedule>();
    private ArrayList<OneTimeStopSchedule> Wednesday=new ArrayList<OneTimeStopSchedule>();
    private ArrayList<OneTimeStopSchedule> Thursday=new ArrayList<OneTimeStopSchedule>();
    private ArrayList<OneTimeStopSchedule> Friday=new ArrayList<OneTimeStopSchedule>();
    private ArrayList<OneTimeStopSchedule> Saturday=new ArrayList<OneTimeStopSchedule>();
    private ArrayList<OneTimeStopSchedule> Sunday=new ArrayList<OneTimeStopSchedule>();

    public String getSubRouteUID(){return SubRouteUID;}
    public String getSubRouteName(){return SubRouteName;}
    public String getRouteName(){return RouteName;}
    public ArrayList<OneTimeStopSchedule> getMonday(){return Monday;}
    public ArrayList<OneTimeStopSchedule> getThueday(){return Thueday;}
    public ArrayList<OneTimeStopSchedule> getWednesday(){return Wednesday;}
    public ArrayList<OneTimeStopSchedule> getThursday(){return Thursday;}
    public ArrayList<OneTimeStopSchedule> getFriday(){return Friday;}
    public ArrayList<OneTimeStopSchedule> getSaturday(){return Saturday;}
    public ArrayList<OneTimeStopSchedule> getSunday(){return Sunday;}

    public void Store(String subRouteUID,String subRouteName,int direction,String routeName)
    {
        SubRouteUID=subRouteUID;
        SubRouteName=subRouteName;
        Direction=direction;
        RouteName=routeName;
    }

    //清除之前資料
    public void SatClear()
    {
        Saturday.clear();
    }
    public void SunClear()
    {
        Sunday.clear();
    }
    public void MonClear()
    {
        Monday.clear();
    }
    public void ThuClear()
    {
        Thueday.clear();
    }
    public void WedClear()
    {
        Wednesday.clear();
    }
    public void ThurClear()
    {
        Thursday.clear();
    }
    public void FriClear()
    {
        Friday.clear();
    }

    public void SunStoreTimeTable(OneTimeStopSchedule oneTimeStopSchedule_temp)
    {
        Sunday.add(oneTimeStopSchedule_temp);
    }

    public void SatStoreTimeTable(OneTimeStopSchedule oneTimeStopSchedule_temp)
    {
        Saturday.add(oneTimeStopSchedule_temp);
    }

    public void MonStoreTimeTable(OneTimeStopSchedule oneTimeStopSchedule_temp)
    {
        Monday.add(oneTimeStopSchedule_temp);
    }

    public void TueStoreTimeTable(OneTimeStopSchedule oneTimeStopSchedule_temp)
    {
        Thueday.add(oneTimeStopSchedule_temp);
    }

    public void WedStoreTimeTable(OneTimeStopSchedule oneTimeStopSchedule_temp)
    {
        Wednesday.add(oneTimeStopSchedule_temp);
    }

    public void ThuStoreTimeTable(OneTimeStopSchedule oneTimeStopSchedule_temp)
    {
        Thursday.add(oneTimeStopSchedule_temp);
    }

    public void FriStoreTimeTable(OneTimeStopSchedule oneTimeStopSchedule_temp)
    {
        Friday.add(oneTimeStopSchedule_temp);
    }
}


