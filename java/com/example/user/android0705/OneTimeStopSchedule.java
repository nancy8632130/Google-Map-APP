package com.example.user.android0705;
//記錄一個公車發車的一個時間的資訊

public class OneTimeStopSchedule {

    public  String StopUID,ArrivalTime , DepartureTime,StopName,DepartureTimeHour,DepartureTimeMin;

    public void Store(String stopName,String stopUID,String arrivalTime ,String departureTime,String departureTimeHour,String departureTimeMin)
    {
        StopUID=stopUID;
        ArrivalTime=arrivalTime;
        DepartureTime=departureTime;
        StopName=stopName;
        DepartureTimeHour=departureTimeHour;
        DepartureTimeMin=departureTimeMin;

    }

    //為了變成(hour):(min)形式
    public void setDepartureTime(int h,int m)
    {
        //若min是個位數，在其前面加一個0
        if(m<10) DepartureTime=Integer.toString(h)+" : 0"+Integer.toString(m);
        else DepartureTime=Integer.toString(h)+" : "+Integer.toString(m);
    }

    public String getStopName(){return StopName;}
}
