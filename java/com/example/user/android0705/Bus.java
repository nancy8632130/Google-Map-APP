package com.example.user.android0705;

import java.util.ArrayList;

//紀錄BUS資訊
public class Bus {
    private String RouteUID;
    private String SubRouteID;
    private String busname;
    private String route;
    private int BusRouteType;
    private String StartStop;
    private String EndStop;

	//公車
    public void Store(String routeID,String subRouteId,String routename,String Name,String Route,int busroutetype,String startStop,String endStop)
    {
        this.RouteUID=routeID;
        this.SubRouteID=subRouteId;
        this.RouteName=routename; //路線名稱
        this.busname=Name; //公車名稱
        this.route=Route;
        this.BusRouteType=busroutetype; //種類：BUS或客運
        this.StartStop=startStop; //起始站
        this.EndStop=endStop; //終點站
    }

    private ArrayList arrayListOperators = new ArrayList();
    private String RouteName;
	//客運
    public void StoreInterCity(String routeID, ArrayList arraylistoperators,String routename,String subRouteId,String Name,String headsign,int busroutetype,String startStop,String endStop)
    {
        this.RouteUID=routeID;
        this.arrayListOperators=arraylistoperators;  //比公車多出營業商列表，因為客運同一路線可能有多個營業商
        this. RouteName=routename;
        this.SubRouteID=subRouteId;
        this.busname=Name;
        this.route=headsign;
        this.BusRouteType=busroutetype;
        this.StartStop=startStop;//起始站
        this.EndStop=endStop;//終點站
    }

    public String getBusName() {
        return RouteName;
    }
    public String getSubRouteID() {
        return SubRouteID;
    }
}
