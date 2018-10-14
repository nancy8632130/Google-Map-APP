package com.example.user.android0705;
//紀錄單一站牌的資訊

public class AllStopsOfRoute {

    private String StopUID,StopID,StopName,RouteUID;
    private int Direction,Num,sequence;
    private  double PositionLat,PositionLng;


    public void Store(String routeUID,String stopUID,String stopID,String stopName,int direction,int num,double positionLat,double positionLng,int seq)
    {
        this.StopUID=stopUID;   //站牌UID
        this.StopID=stopID;     //站牌ID
        this.StopName=stopName; //站牌名
        this.Direction=direction; //去程或回程
        this.Num=num;     //此公車路線站牌總數
        this.PositionLat=positionLat; //經緯度
        this.PositionLng=positionLng;
        this.RouteUID=routeUID; //所屬公車路線UID
        this.sequence=seq;  //編號
    }


    public String getStopUID() {return StopUID;}
    public String getStopName() {return StopName;}
    public String getRouteUID() {return RouteUID;}
    public int getNum() {return Num;}
    public double getPositionLat() { return PositionLat;}
    public double getPositionLng() { return PositionLng;}
    public int getSequence() { return sequence;}
}
