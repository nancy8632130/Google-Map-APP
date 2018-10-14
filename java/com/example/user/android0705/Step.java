package com.example.user.android0705;

import java.util.ArrayList;
//紀錄導航每一(大)步資訊

public class Step {

    private String SubDistance,SubCostTime,Html_instruction,Polyline;
    private ArrayList SubStepsList;
    private int K;
    private TransitDetails TransitDetails;


    public void Store_walk_drive(String subDistance,String subCostTime,String html_instruction)
    {
        Html_instruction=html_instruction;
        SubDistance=subDistance;
        SubCostTime=subCostTime;
    }


    public void Store(int k,String subDistance,String subCostTime,String html_instruction,String polyline,ArrayList<SubSteps> subStepsList)
    {
        this.SubCostTime=subCostTime;
        this.SubDistance=subDistance;
        this.Html_instruction=html_instruction;
        this.Polyline=polyline;
        this.SubStepsList=subStepsList;
        this.K=k;
        this.TransitDetails=null;
    }

    public void Store2(int k,String subDistance,String subCostTime,String html_instruction,String polyline,TransitDetails transitDetails)
    {
        this.SubCostTime=subCostTime;
        this.SubDistance=subDistance;
        this.Html_instruction=html_instruction;
        this.Polyline=polyline;
        this.TransitDetails=transitDetails;
        this.K=k;
        this.SubStepsList=null;
    }

    public String getHtml_instruction() { return Html_instruction; }
    public String getSubCostTime() { return SubCostTime; }
    public String getSubDistance() { return SubDistance; }
    public String getPolyline() { return Polyline; }
    public ArrayList getSubStepsList() { return SubStepsList; }
    public int getK() {return K;}
    public TransitDetails getTransitDetails() {return TransitDetails;}

}
