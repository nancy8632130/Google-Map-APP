package com.example.user.android0705;
//記錄每一(小)步資訊

public class SubSteps {
    private String SubSubDistance,SubSubCostTime,Sub_html_instruction,traveMode;

    public void Store(String subSubDistance,String subSubCostTime,String sub_html_instruction,String travemode)
    {
        this.SubSubDistance=subSubDistance;
        this.SubSubCostTime=subSubCostTime;
        this.Sub_html_instruction=sub_html_instruction;
        this.traveMode=travemode;
    }

    public String getSubSubDistance() {return SubSubDistance; }
    public String getSubSubCostTime() {return SubSubCostTime; }
    public String getSub_html_instruction() {return Sub_html_instruction; }
    public String gettraveMode() {return traveMode; }

}
