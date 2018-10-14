package com.example.user.android0705;
//記錄每一小(步)資訊，前提是每一小步需是大眾運輸工具

public class TransitDetails {

    private String BusEndStop,BusStartStop,BusRoute,Vechine,Agencies,BusName,TrainStartTime,TrainArriveTime,Headsign;
    private double ELat,ELng,SLat,SLng;

    public void Store(double eLat,double eLng,String busEndStop,double sLat,double sLng,String busStartStop,String busRoute,String vechine,String agencies,String busName,String trainStartTime,String trainArriveTime,String headsign)
    {
        this.ELat=eLat;
        this.ELng=eLng;
        this.BusEndStop=busEndStop;
        this.SLat=sLat;
        this.SLng=sLng;
        this.BusStartStop=busStartStop;
        this.BusRoute=busRoute;
        this.Vechine=vechine;
        this.Agencies=agencies;
        this.BusName=busName;
        this.TrainArriveTime=trainArriveTime;
        this.TrainStartTime=trainStartTime;
        this.Headsign=headsign;
    }


    public double getELat() { return ELat; }
    public double getELng() { return ELng; }
    public double getSLat() { return SLat; }
    public double getSLng() { return SLng; }

    public String getBusEndStop() { return BusEndStop; }
    public String getBusStartStop() { return BusStartStop; }
    public String getVechine() { return Vechine; }
    public String getAgencies() { return Agencies; }
    public String getBusName() { return BusName; }
    public String getBusRoute() { return BusRoute; }
    public String getTrainArriveTime() { return TrainArriveTime; }
    public String getTrainStartTime() { return TrainStartTime; }
    public String getHeadsign() { return Headsign; }
}
