package com.example.user.android0705;
//紀錄單一站牌預估到站時間

public class StopEstimatedTimeOfArrival {
    private String PlateNumb;
    private  String StopUID;
    private  String StopID,RouteUID;
    private  String StopName;
    private int Direction;
    private int EstimateTime;
    private  int StopCountDown;
    private int StopStatus;
    private int StopSequence;

    public void Store(String routeUID,String plateNumb,String stopUID,String stopID,String stopName,int direction,int estimateTime,int stopCountDown,int stopStatus,int stopSequence)
    {
        this.PlateNumb=plateNumb;
        this.StopUID=stopUID;
        this.StopID=stopID;
        this.StopName=stopName;
        this.Direction=direction;
        this.EstimateTime=estimateTime;
        this.StopCountDown=stopCountDown;
        this.StopStatus=stopStatus;
        this.StopSequence=stopSequence;
        this.RouteUID=routeUID;
    }

    public String getStopName(){ return StopName; }
    public String getRouteUID(){ return RouteUID; }
    public int getStopSequence(){ return StopSequence; }
    public int getEstimateTime(){ return EstimateTime; }
    public String getPlateNumb(){ return PlateNumb; }
    public int getDirection(){ return Direction; }


}
