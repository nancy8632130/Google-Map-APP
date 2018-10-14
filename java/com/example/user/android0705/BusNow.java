package com.example.user.android0705;
//記錄從GOOGLE api得到的目前可搭乘公車資訊

public class BusNow {
    private String BusEndStop; //需在哪一站下車
    private String BusStartStop ;//需在哪一站上車
    private String BusRoute; //公車路線名
    private String BusName; //公車名
    private String agencies; //單位：如~新竹市公車
    private double SLat,SLng,ELat,ELng;//起始站、終止站經緯度

    public void Store(String busEndStop,String busStartStop ,String busRoute,String busName,String a,double sLat,double sLng,double eLat,double eLng)
    {
        this.BusEndStop=busEndStop;
        this.BusStartStop =busStartStop;
        this.BusRoute=busRoute;
        this.BusName=busName;
        this.agencies=a;
        this.SLat=sLat;
        this.SLng=sLng;
        this.ELat=eLat;
        this.ELng=eLng;
    }

    public String getBusNowName() {
        return BusName;
    }
    public String getBusRoute() {
        return BusRoute;
    }
    public String getBusNowAgencies() {
        return agencies;
    }
    public String getBusStartStop() {
        return BusStartStop;
    }
    public String getBusEndStop() {
        return BusEndStop;
    }
    public double getSLat() {
        return SLat;
    }
    public double getSLng() {
        return SLng;
    }
    public double getELat() {
        return ELat;
    }
    public double getELng() {
        return ELng;
    }
}
