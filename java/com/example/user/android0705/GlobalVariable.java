package com.example.user.android0705;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
//處理全域變數，使其能在不同activity使用

public class GlobalVariable extends Application {

    private boolean OnOff;
    private float voice;
    private float space;
    private Context context;
    private SharedPreferences list;
    private boolean list1_on ,list2_on ,list3_on,list4_on,list5_on;

    //修改 變數値
    public void setOnOff(boolean onoff){
        this.OnOff = onoff;
    }
    public void setVoice(float voice){
        this.voice = voice;
    }
    public void setSpace(float space){
        this.space = space;
    }
    public void setContext(Context context) {this.context=context;}
    public void setList1(SharedPreferences SaveCheckList1) { this.list=SaveCheckList1; }
    public void setList2(boolean on) { this.list2_on=on; }
    public void setList3(boolean on) { this.list3_on=on; }
    public void setList4(boolean on) { this.list4_on=on; }
    public void setList5(boolean on) { this.list5_on=on; }

    //取得 變數值
    public boolean getOnOff() {
        return OnOff;
    }
    public float getVoice(){
        return voice;
    }
    public float getSpace(){
        return space;
    }
    public SharedPreferences getList1() { return list; }
    public boolean getList2() { return list2_on; }
    public boolean getList3() { return list3_on; }
    public boolean getList4() { return list4_on; }
    public boolean getList5() { return list5_on; }

    public Context getContext() { return context; }
}
