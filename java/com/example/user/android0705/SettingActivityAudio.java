package com.example.user.android0705;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
//設定介面

public class SettingActivityAudio extends AppCompatActivity {

    private SeekBar sb ;
    private TextView tw_voice_speed ;
    private Switch sw;
    public static  boolean OnOff = false;
    private ArrayList choiceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_audio);
        Button b=(Button)findViewById(R.id.button_choose);
        String S=""; //存要印出來的string
        choiceList.add("Restaurant");   choiceList.add("Cafe"); choiceList.add("Bakery");//restaurant
        choiceList.add("Convenience store"); choiceList.add("Parking lot");
        setVoiceOn();
        setVoiceSpeed();

        //找之前prefer stores 紀錄，並存入 checkedStatusList
        boolean bool_1=FindOldStore("CheckList1","0");
        boolean bool_2=FindOldStore("CheckList2","1");
        boolean bool_3=FindOldStore("CheckList3","2");
        boolean bool_4=FindOldStore("CheckList4","3");
        boolean bool_5=FindOldStore("CheckList5","4");
        if(bool_1) S+=choiceList.get(0)+"\n";
        if(bool_2) S+=choiceList.get(1)+"\n";
        if(bool_3) S+=choiceList.get(2)+"\n";
        if(bool_4) S+=choiceList.get(3)+"\n";
        if(bool_5) S+=choiceList.get(4);
        //什麼都沒勾選的情形，印"CHOOSE"
        if(S.equals("")) b.setText("CHOOSE");
        else b.setText(S);
    }

    //去認有沒有之前存的boolean值，default為false
    private boolean FindOldStore(String name,String key)
    {
        SharedPreferences SaveCheckList=getSharedPreferences(name,MODE_WORLD_READABLE);
        boolean bool=SaveCheckList.getBoolean(key,false);
        return  bool;
    }

    //設置list
    private void setList(boolean[] isChecked,int number)
    {
        isChecked[number]=true;
        checkedStatusList_temp.set(number,true);
    }

    //存檔
    private void SaveCheckList(int i,SharedPreferences SaveCheckList1,SharedPreferences SaveCheckList2,SharedPreferences SaveCheckList3,SharedPreferences SaveCheckList4,SharedPreferences SaveCheckList5,boolean bool)
    {
        GlobalVariable gv = (GlobalVariable)getApplicationContext();
        gv = (GlobalVariable)getApplicationContext(); //也記在GlobalVariable裡，讓別的Activity可以用
        if(i==0)
        {
            SaveCheckList1.edit().putBoolean("0",bool).commit();
            gv.setList1(SaveCheckList1);
        }
        else if(i==1)
        {
            SaveCheckList2.edit().putBoolean("1",bool).commit();
            gv.setList2(bool);
        }
        else if(i==2)
        {
            SaveCheckList3.edit().putBoolean("2",bool).commit();
            gv.setList3(bool);
        }
        else if(i==3)
        {
            SaveCheckList4.edit().putBoolean("3",bool).commit();
            gv.setList4(bool);
        }
        else if(i==4)
        {
            SaveCheckList5.edit().putBoolean("4",bool).commit();
            gv.setList5(bool);
        }
    }

    //onSetClick function：對應button_choose　功能為選擇推薦prefer stores type
    List<Boolean> checkedStatusList = new ArrayList<>();
    List<Boolean> checkedStatusList_temp = new ArrayList<>(); //暫時紀錄打勾的store type

    //按下prefer store type的按紐觸發
    public void onSetClick(View view) {
        boolean[] isChecked = new boolean[choiceList.size()];
        checkedStatusList_temp.clear();//將暫存的list先清空
        for(int i=0;i<5;i++){ checkedStatusList_temp.add(false); } //並預設成false
        //找之前prefer stores 紀錄，並存入 checkedStatusList_temp 及 isChecked
        if(FindOldStore("CheckList1","0"))  setList(isChecked,0);
        if(FindOldStore("CheckList2","1"))  setList(isChecked,1);
        if(FindOldStore("CheckList3","2"))  setList(isChecked,2);
        if(FindOldStore("CheckList4","3"))  setList(isChecked,3);
        if(FindOldStore("CheckList5","4"))  setList(isChecked,4);
        //跳出AlertDialog選單給使用者選
        new AlertDialog.Builder(SettingActivityAudio.this).setMultiChoiceItems((CharSequence[]) choiceList.toArray(new String[choiceList.size()]),isChecked ,new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which,boolean isChecked) {
                if(isChecked) Toast.makeText(SettingActivityAudio.this, "你勾選了" + choiceList.get(which), Toast.LENGTH_SHORT).show();
                else Toast.makeText(SettingActivityAudio.this, "你取消勾選了" + choiceList.get(which), Toast.LENGTH_SHORT).show();
                checkedStatusList_temp.set(which, isChecked);
            }
        }).setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkedStatusList=checkedStatusList_temp; //最後確定時，將checkedStatusList_temp存入checkedStatusList
                //用 SharedPreferences紀錄
                SharedPreferences SaveCheckList1=getSharedPreferences("CheckList1",MODE_WORLD_READABLE);
                SharedPreferences SaveCheckList2=getSharedPreferences("CheckList2",MODE_WORLD_READABLE);
                SharedPreferences SaveCheckList3=getSharedPreferences("CheckList3",MODE_WORLD_READABLE);
                SharedPreferences SaveCheckList4=getSharedPreferences("CheckList4",MODE_WORLD_READABLE);
                SharedPreferences SaveCheckList5=getSharedPreferences("CheckList5",MODE_WORLD_READABLE);
                String S=""; //暫時紀錄打勾的部分
                for(int i = 0; i < checkedStatusList.size(); i++){
                    if(checkedStatusList.get(i)) {
                        if(i==4) S+=choiceList.get(i);
                        else S+=choiceList.get(i)+"\n";
                        SaveCheckList(i,SaveCheckList1,SaveCheckList2,SaveCheckList3,SaveCheckList4,SaveCheckList5,true);
                    }
                    else SaveCheckList(i,SaveCheckList1,SaveCheckList2,SaveCheckList3,SaveCheckList4,SaveCheckList5,false);
                }
                Toast.makeText(SettingActivityAudio.this, "你選擇的是"+S, Toast.LENGTH_SHORT).show();
                Button b=(Button)findViewById(R.id.button_choose);
                b.setText(S);
            }
        }).show();
    }

    //設置聲音開關
    private void setVoiceOn()
    {
        SharedPreferences SaveAudioOpen=getSharedPreferences("audio",MODE_WORLD_READABLE);
        sw = (Switch) findViewById(R.id.switch1);
        sw.setChecked(false);
        GlobalVariable gv = (GlobalVariable)getApplicationContext();
        //先去讀上一次的紀錄，看是否是on，若是 --> sw設成on
        if(SaveAudioOpen.getBoolean("OpenAudio",false))
        {
            sw.setChecked(true);
            gv = (GlobalVariable)getApplicationContext(); //也記在GlobalVariable裡，讓別的Activity可以用
            gv.setOnOff(true);
        } else
        {
            sw.setChecked(false);
            gv = (GlobalVariable)getApplicationContext(); //也記在GlobalVariable裡，讓別的Activity可以用
            gv.setOnOff(false);
        }
        //監聽使用者改變sw
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                SharedPreferences SaveAudioOpen=getSharedPreferences("audio",MODE_WORLD_READABLE);
                GlobalVariable gv = (GlobalVariable)getApplicationContext();
                if (buttonView.isChecked()) {
                    Toast.makeText(SettingActivityAudio.this,"voice on",Toast.LENGTH_SHORT).show();
                    gv.setOnOff(true);  OnOff=true; //也記在GlobalVariable裡，讓別的Activity可以用
                } else {
                    Toast.makeText(SettingActivityAudio.this,"voice off",Toast.LENGTH_SHORT).show();
                    //GlobalVariable gv = (GlobalVariable)getApplicationContext();
                    gv.setOnOff(false);  OnOff=false; //也記在GlobalVariable裡，讓別的Activity可以用
                }
                SaveAudioOpen.edit().putBoolean("OpenAudio",OnOff).commit(); //記在SharedPreferences裡
            }
        });
    }

    //設置聲音速度
    private void setVoiceSpeed()
    {
        sb = (SeekBar) findViewById(R.id.sb);
        tw_voice_speed = (TextView) findViewById(R.id.tw_voice_speed);
        //取得紀錄的speed值
        SharedPreferences SaveAudioSpace=getSharedPreferences("space",MODE_WORLD_READABLE);
        int sp=SaveAudioSpace.getInt("Space",10);
        sb.setProgress(sp);
        tw_voice_speed.setText(Float.toString((float)sp/10));
        //監聽使用者改變sb
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar sb2, int progress, boolean fromUser) {
                SharedPreferences SaveAudioSpace=getSharedPreferences("space",MODE_WORLD_READABLE);
                tw_voice_speed.setText(String.valueOf((float)progress/10));
                GlobalVariable gv = (GlobalVariable)getApplicationContext();
                gv.setSpace((float)progress/10); //也記在GlobalVariable裡，讓別的Activity可以用
                SaveAudioSpace.edit().putInt("Space",progress).commit(); //記在SharedPreferences裡
            }
            @Override
            public void onStartTrackingTouch(SeekBar sb2) {
                Log.e("------------", "開始滑?");
            }
            @Override
            public void onStopTrackingTouch(SeekBar sb2) {
                Log.e("------------", "停止滑?");
            }
        });
    }
}
