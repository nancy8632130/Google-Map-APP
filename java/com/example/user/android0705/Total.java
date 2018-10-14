package com.example.user.android0705;

import java.util.ArrayList;
//紀錄導航的大資訊(如總到站預計花費時間等...)

public class Total {

    private ArrayList <ArrayList<Step>>ListList;
    private String AllDistance,AllCostTime,EndAddress,StartAddress;
    public void Store(ArrayList <ArrayList<Step>> list,String allDistance,String allCostTime,String endAddress,String startAddress)
    {
        this.ListList=list;
        this.AllCostTime=allCostTime;
        this.AllDistance=allDistance;
        this.EndAddress=endAddress;
        this.StartAddress=startAddress;
    }

    public ArrayList <ArrayList<Step>> getListList() {return ListList;}
    public String  getAllDistance() {return AllDistance;}
    public String  getAllCostTime() {return AllCostTime;}
    public String  getEndAddress() {return EndAddress;}
    public String  getStartAddress() {return StartAddress;}


}
