package com.example.user.android0705;

import java.util.ArrayList;
//紀錄商店資訊

public class StoreInformation {
    private String icon;
    private String name;
    private String open;
    private String placeID;
    private double rate,Lat,Lng;
    private ArrayList typelist;
    public void Store(String Icon,String Name,String Open,String PlaceID,double Rate,ArrayList TypeList,double lat,double lng)
    {
        this.icon=Icon;
        this.name=Name;
        this.open=Open;
        this.placeID=PlaceID;
        this.rate=Rate;
        this.typelist=TypeList;
        this.Lat=lat;
        this.Lng=lng;
    }

    public String getName() {
        return name;
    }

    public double getRate() {
        return rate;
    }
    public double getLat() {
        return Lat;
    }
    public double getLng() {
        return Lng;
    }





}
