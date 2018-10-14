package com.example.user.android0705;

import java.util.ArrayList;
//紀錄導航每一(大)步中小步的list

public class StepList {
    private ArrayList <Step>ListList;
    private String AllDistance,AllCostTime,EndAddress,StartAddress;
    public void Store(ArrayList <Step> list,String allDistance,String allCostTime,String endAddress,String startAddress)
    {
        this.ListList=list;
        this.AllCostTime=allCostTime;
        this.AllDistance=allDistance;
        this.EndAddress=endAddress;
        this.StartAddress=startAddress;
    }

    public ArrayList <Step> getListList() {return ListList;}
    public String  getAllDistance() {return AllDistance;}
    public String  getAllCostTime() {return AllCostTime;}
    public String  getEndAddress() {return EndAddress;}
    public String  getStartAddress() {return StartAddress;}
}
