package com.example.user.android0705;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
//主介面

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }

    //按下Map按鍵，轉換到MapsActivity
    public void onMapClick(View view)
    {
        //*轉換頁面*//
        Intent intent = new Intent();
        //從某Activity轉到某Activity
        intent.setClass(Main2Activity.this, MapsActivity.class);
        startActivity(intent);
    }

    //按下"說明"按鍵，跳出AlertDialog說明
    public void onIntroduceClick(View view)
    {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder mBusBuilder=new AlertDialog.Builder(Main2Activity.this);
        View mView=getLayoutInflater().inflate(R.layout.introduction,null);
        mBusBuilder.setView(mView);
        TextView text=(TextView)mView.findViewById(R.id.text);
        TextView text_map=(TextView)mView.findViewById(R.id.text_map);
        TextView textView_set=(TextView) mView.findViewById(R.id.text_set);
        Button b_colse=(Button) mView.findViewById(R.id.button_close);
        text.setText("此APP提供語音導航及導覽的功能，使用者可先在Setting處設定偏好，再於Map處使用導航或導覽的功能。"+"\n"+"以下介紹個按鍵的功能 : ");
        text_map.setText("點擊Map鍵 : 進入地圖，使用者則可輸入目的地開始導航/導覽。"+"\n"+"以下是Map介面裡的按鍵介紹 : ");
        textView_set.setText("點擊Setting鍵 : 進入設定介面，使用者可在此設定偏好。"+"\n"+"例如 : 聲音開關、設定語速、推薦商店偏好");
        final AlertDialog dialog=mBusBuilder.create();
        dialog.show();
        //按下關閉鍵，AlertDialog關閉。
        b_colse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    //按下Set按鍵，轉換到SettingActivityAudio
    public void onSetClick(View view)
    {
        //轉換頁面
        Intent intent = new Intent();
        intent.setClass(Main2Activity.this, SettingActivityAudio.class);
        startActivity(intent);
    }
}
