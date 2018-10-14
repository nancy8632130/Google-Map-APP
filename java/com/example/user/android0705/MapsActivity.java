package com.example.user.android0705;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;

import android.net.Uri;
import android.os.AsyncTask;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
//地圖介面

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private double Lat = -34, Lng = 151;
    public GoogleMap mMap;
    private TextView tv_show, tv1, tv2; //debug用
    public Double t1, t2, t3, t4; //起點終點經緯度
    public static String SlocationName, ElocationName;//起點終點名
    public String mode = "driving";//導航模式
    //GPS定位 //requestLocationUpdates 函數是設定 LOCATION_UPDATE_MIN_DISTANCE (ms) 的時間或移動
    // LOCATION_UPDATE_MIN_TIME 距離時觸發 mLocationListener 傾聽器。
    private LocationManager mLocationManager;
    private static final int LOCATION_UPDATE_MIN_DISTANCE = 3000;
    private static final int LOCATION_UPDATE_MIN_TIME = 100;
    private Double LocationLat = 23.0, LocationLng = 120.0;//紀錄當前位置經緯度
    private ArrayList choiceList = new ArrayList<>();//用來記錄store type

    GetBusSchedule getBusSchedule=new GetBusSchedule();
    GetInterCitySchedule getInterCitySchedule=new GetInterCitySchedule();
    AddRecommendStore addRecommendStore=new AddRecommendStore();//先new一個AddRecommendStore物件

    Transit tran=new Transit(); //先new一個Transit物件
    Walk walking=new Walk();//先new一個Walk物件
    Drive driving=new Drive();//先new一個Drive物件
    BusInformation busInformation=new BusInformation(); //存公車列表資訊

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //將輸入欄&按鈕設透明度
        View v = findViewById(R.id.relLayout1);//找到你要設透明背景的layout 的id
        v.getBackground().setAlpha(100);//0~255透明度值
        createLanguageTTS(); // 建立 TTS
        //找到TextView ；並設定choiceList的商店列表
        tv_show = (TextView) findViewById(R.id.gps); tv1 = (TextView) findViewById(R.id.route); tv2 = (TextView) findViewById(R.id.tv3);
        choiceList.add("餐廳");   choiceList.add("咖啡廳"); choiceList.add("麵包店");//restaurant
        choiceList.add("便利商店"); choiceList.add("停車場");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getCurrentLocationStart(); //GPS定位
        MarkLocation();
        //重新開始要清除之前取得的公車行程表
        for(int i=0;i<getBusSchedule.AllBus.size();i++)
        {
            BusSchedule busSchedule=(BusSchedule)getBusSchedule.AllBus.get(i);
            busSchedule.FriClear();  busSchedule.MonClear();
            busSchedule.ThuClear();  busSchedule.WedClear();
            busSchedule.ThurClear(); busSchedule.SatClear(); busSchedule.SunClear();
        }
        //取得公車客運今日的時刻表
        getBusSchedule.Call(tv_show,getDate());
        getInterCitySchedule.Call(tv_show,getDate());
        //init EditText
        etSLocationName = (EditText) findViewById(R.id.etSLocationName);
        etELocationName = (EditText) findViewById(R.id.etELocationName);
        ClickMapGetLatLng(); //控制點擊地圖位置當起點終點
		
		//先看之前有沒有導航紀錄資訊，有的話要再導航一次
        SharedPreferences SaveSlocationName=getSharedPreferences("start",MODE_WORLD_READABLE);
        String OldStart=SaveSlocationName.getString("SlocationName","no");
        if(!(OldStart.equals("no")))
        {
            etSLocationName.setText(OldStart);
            SlocationName=OldStart;
        }
        SharedPreferences SaveElocationName=getSharedPreferences("end",MODE_WORLD_READABLE);
        String OldEnd=SaveElocationName.getString("ElocationName","no");
        if(!(OldEnd.equals("no")))
        {
            etELocationName.setText(OldEnd);
            ElocationName=OldEnd;
        }

        SharedPreferences SaveLat=getSharedPreferences("lat",MODE_WORLD_READABLE);
        String Lat=SaveLat.getString("GetLat","no");
        SharedPreferences SaveLng=getSharedPreferences("lng",MODE_WORLD_READABLE);
        String Lng=SaveLng.getString("GetLng","no");
        if(!(Lat.equals("no")))
        {
            LocationLat=Double.parseDouble(Lat);
        }
        if(!(Lng.equals("no")))
        {
            LocationLng=Double.parseDouble(Lng);
        }

        SharedPreferences SaveMode=getSharedPreferences("mode",MODE_WORLD_READABLE);
        String M=SaveMode.getString("GetMode","no");
        if(!(M.equals("no")))
        {
            mode=M;
        }

        if(M.equals("transit"))
        {
            tran.initization(SlocationName,ElocationName,mMap,LocationLat,LocationLng,tts,tv1,MapsActivity.this,voice);
            tran.addLatLng();
            //判斷setting設定的偏好推薦商店有無打勾紀錄
            CallRecommand();
            CallRecommand();
        }
        else if(M.equals("walking"))
        {
            walking.initization(SlocationName,ElocationName,mMap,LocationLat,LocationLng,tts,voice);
            walking.addLatLng();;
            //判斷setting設定的偏好推薦商店有無打勾紀錄
            CallRecommand();
            CallRecommand();
        }
        else if(M.equals("driving"))
        {
            driving.initization(SlocationName,ElocationName,mMap,LocationLat,LocationLng,tts,voice);
            driving.addLatLng();
            //判斷setting設定的偏好推薦商店有無打勾紀錄
            CallRecommand();
            CallRecommand();
        }
    }

    public TextToSpeech tts;
    private boolean voice=true;
    //初始化語音系統
    private void createLanguageTTS() {
        //取得GlobalVariable的值，判斷聲音開不開
        GlobalVariable gv = (GlobalVariable)getApplicationContext();
        voice=gv.getOnOff();
        if (tts == null) {
            tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int arg0) {  // TTS 初始化成功
                    if (arg0 == TextToSpeech.SUCCESS) {
                        Locale l = Locale.TAIWAN;
                        //取得GlobalVariable的值，得到語速
                        GlobalVariable gv = (GlobalVariable)getApplicationContext();
                        tts.setSpeechRate(gv.getSpace());//語音速度 ，default 1.0
                        // 目前指定的【語系+國家】TTS, 已下載離線語音檔, 可以離線發音
                        if (tts.isLanguageAvailable(l) == TextToSpeech.LANG_COUNTRY_AVAILABLE) tts.setLanguage(l);
                    }
                }
            });
        }
    }

    //讀取使用者輸入的起點及終點
    EditText etSLocationName,etELocationName;
    private void UserInput()
    {
        SlocationName = etSLocationName.getText().toString().trim() ;
        ElocationName = etELocationName.getText().toString().trim() ;
        SharedPreferences SaveSlocationName=getSharedPreferences("start",MODE_WORLD_READABLE);
        SaveSlocationName.edit().putString("SlocationName",SlocationName).commit(); //記在SharedPreferences裡
        SharedPreferences SaveElocationName=getSharedPreferences("end",MODE_WORLD_READABLE);
        SaveElocationName.edit().putString("ElocationName",ElocationName).commit(); //記在SharedPreferences裡

        SharedPreferences SaveMode=getSharedPreferences("mode",MODE_WORLD_READABLE);
        SaveMode.edit().putString("GetMode",mode).commit(); //記在SharedPreferences裡

        SharedPreferences SaveLat=getSharedPreferences("lat",MODE_WORLD_READABLE);
        SaveMode.edit().putString("GetLat",Double.toString(LocationLat)).commit(); //記在SharedPreferences裡
        SharedPreferences SaveLng=getSharedPreferences("lng",MODE_WORLD_READABLE);
        SaveMode.edit().putString("GetLng",Double.toString(LocationLng)).commit(); //記在SharedPreferences裡

    }

    //判斷SettingActivity裡的偏好推薦商店有沒有被打勾
    private void CallRecommand()
    {
        for(int k = 0; k < 5; k++) {
            if(k==0)
            {
                SharedPreferences SaveCheckList=getSharedPreferences("CheckList1",MODE_WORLD_READABLE);
                if(SaveCheckList.getBoolean("0",false)) addRecommendStore.Call(k,voice,tts,LocationLat,LocationLng);
            }
            else if(k==1)
            {
                SharedPreferences SaveCheckList=getSharedPreferences("CheckList2",MODE_WORLD_READABLE);
                if(SaveCheckList.getBoolean("1",false)) addRecommendStore.Call(k,voice,tts,LocationLat,LocationLng);
            }
            else if(k==2)
            {
                SharedPreferences SaveCheckList=getSharedPreferences("CheckList3",MODE_WORLD_READABLE);
                if(SaveCheckList.getBoolean("2",false)) addRecommendStore.Call(k,voice,tts,LocationLat,LocationLng);
            }
            else if(k==3)
            {
                SharedPreferences SaveCheckList=getSharedPreferences("CheckList4",MODE_WORLD_READABLE);
                if(SaveCheckList.getBoolean("3",false)) addRecommendStore.Call(k,voice,tts,LocationLat,LocationLng);
            }
            else if(k==4)
            {
                SharedPreferences SaveCheckList=getSharedPreferences("CheckList5",MODE_WORLD_READABLE);
                if(SaveCheckList.getBoolean("4",false)) addRecommendStore.Call(k,voice,tts,LocationLat,LocationLng);
            }
        }
    }

    //點擊大眾運輸工具導航
    public void onClickTran(View view) {
        busInformation.initization(); //取得公車客運時刻表
        mode = "transit";
        UserInput(); //讀取使用者輸入的起點及終點
        tran.initization(SlocationName,ElocationName,mMap,LocationLat,LocationLng,tts,tv1,MapsActivity.this,voice);
        tran.addLatLng();
        //判斷setting設定的偏好推薦商店有無打勾紀錄
        CallRecommand();
        CallRecommand();

    }

    //點擊走路導航
    public void onClickWalk(View view) {
        mode = "walking";
        UserInput(); //讀取使用者輸入的起點及終點
        walking.initization(SlocationName,ElocationName,mMap,LocationLat,LocationLng,tts,voice);
        walking.addLatLng();
        //判斷setting設定的偏好推薦商店有無打勾紀錄
        CallRecommand();
        CallRecommand();
    }

    //點擊開車導航
    public void onClickDrive(View view) {
        mode = "driving";
        UserInput(); //讀取使用者輸入的起點及終點
        driving.initization(SlocationName,ElocationName,mMap,LocationLat,LocationLng,tts,voice);
        driving.addLatLng();
        //判斷setting設定的偏好推薦商店有無打勾紀錄
        CallRecommand();
        CallRecommand();
    }

    //取得定位並標記在地圖上
    public void onDirectClick(View view) { MarkLocation(); }

    class TransTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            StringBuilder sb = new StringBuilder();
            try {
                URL url = new URL(params[0]);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String line = in.readLine();
                while (line != null) {
                    sb.append(line);
                    line = in.readLine();
                }
            } catch (MalformedURLException e) { e.printStackTrace();
            } catch (IOException e) { e.printStackTrace(); }
            return sb.toString();
        }
        @Override
        protected void onPostExecute(String s) { super.onPostExecute(s); }
    }

    //點擊顯示推薦店家的按鍵，跳出alterdialog
    public void onShowShopClick(View view)
    {
        View mView=getLayoutInflater().inflate(R.layout.shopdialog,null);
        final TextView sh1=(TextView)mView.findViewById(R.id.shop1); TextView sh2=(TextView)mView.findViewById(R.id.shop2);
        TextView sh3=(TextView)mView.findViewById(R.id.shop3); TextView sh4=(TextView)mView.findViewById(R.id.shop4);
        TextView sh5=(TextView)mView.findViewById(R.id.shop5);
        Button b1=(Button)mView.findViewById(R.id.button1); Button b1_1=(Button)mView.findViewById(R.id.button1_1);
        Button b2=(Button)mView.findViewById(R.id.button2); Button b2_1=(Button)mView.findViewById(R.id.button2_1);
        Button b3=(Button)mView.findViewById(R.id.button3); Button b3_1=(Button)mView.findViewById(R.id.button3_1);
        Button b4=(Button)mView.findViewById(R.id.button4); Button b4_1=(Button)mView.findViewById(R.id.button4_1);
        Button b5=(Button)mView.findViewById(R.id.button5); Button b5_1=(Button)mView.findViewById(R.id.button5_1);
        int M=5;  //取前5個店家
        if(addRecommendStore.arrayList.size()<5) M=addRecommendStore.arrayList.size(); //不足5個
        for(int i=0;i<M;i++)//
        {
            StoreInformation store1 = (StoreInformation)addRecommendStore.arrayList.get(i);
            if(i==0)sh1.setText(store1.getName()+": rate="+Double.toString(store1.getRate()));
            else if(i==1)sh2.setText(store1.getName()+": rate="+Double.toString(store1.getRate()));
            else if(i==2)sh3.setText(store1.getName()+": rate="+Double.toString(store1.getRate()));
            else if(i==3)sh4.setText(store1.getName()+": rate="+Double.toString(store1.getRate()));
            else if(i==4)sh5.setText(store1.getName()+": rate="+Double.toString(store1.getRate()));
        }
        //點擊direct跳一個alterdialog詢問要如何導航到商店
        onShowShopClick_Function(b1_1,0);   onShowShopClick_Function(b2_1,1);
        onShowShopClick_Function(b3_1,2);   onShowShopClick_Function(b4_1,3);
        onShowShopClick_Function(b5_1,4);
        //點擊Info連結進入google map的商店介紹
        onShowShopClick_Function2(b1);  onShowShopClick_Function2(b2);
        onShowShopClick_Function2(b3);  onShowShopClick_Function2(b4);
        onShowShopClick_Function2(b5);
        AlertDialog.Builder mBusBuilder=new AlertDialog.Builder(MapsActivity.this);
        mBusBuilder.setView(mView);
        final AlertDialog showdialog=mBusBuilder.create();
        //設定close
        Button b_close=(Button) mView.findViewById(R.id.button_close);
        b_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showdialog.dismiss();
            }
        });
        showdialog.show();
    }

    //點擊Info連結進入google map的商店介紹
    private void onShowShopClick_Function2(Button b)
    {
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int M=5; if(addRecommendStore.arrayList.size()<5)M=addRecommendStore.arrayList.size();
                if(M>1)
                {
                    StoreInformation store1 = (StoreInformation)addRecommendStore.arrayList.get(1);
                    Uri uri = Uri.parse("geo:0,0?q="+store1.getName());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent); //
                }
            }
        });
    }

    //點擊direct跳一個alterdialog詢問要如何導航到商店
    private void onShowShopClick_Function(Button b, final int temp)
    {
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int M=5; if(addRecommendStore.arrayList.size()<5)M=addRecommendStore.arrayList.size();
                AlertDialog.Builder mBuilder=new AlertDialog.Builder(MapsActivity.this);
                if(mode.equals("walking") || mode.equals("driving"))
                {
                    View mView=getLayoutInflater().inflate(R.layout.checkifdirect,null);
                    Button b_replace=(Button) mView.findViewById(R.id.replace); Button b_extend=(Button) mView.findViewById(R.id.extend);
                    Button b_insert=(Button) mView.findViewById(R.id.insert);
                    mBuilder.setView(mView);
                    final AlertDialog dialog=mBuilder.create();
                    //direct_replace
                    final int finalM = M;
                    setFunctionButton(b_replace,finalM,temp,"replace");
                    setFunctionButton(b_extend,finalM,temp,"extend");
                    setFunctionButton(b_insert,finalM,temp,"insert");
                    //close
                    Button b_close=(Button) mView.findViewById(R.id.cancel);
                    b_close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) { dialog.dismiss(); }
                    });
                    dialog.show();
                }
                else //大眾運輸工具只有replace
                {
                    View mView=getLayoutInflater().inflate(R.layout.checkifdirect_transit,null);
                    Button b_replace=(Button) mView.findViewById(R.id.replace);
                    mBuilder.setView(mView);
                    final AlertDialog dialog=mBuilder.create();
                    final int finalM = M;
                    setFunctionButton(b_replace,finalM,temp,"replace");
                    //close
                    Button b_close=(Button) mView.findViewById(R.id.cancel);
                    b_close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) { dialog.dismiss(); }
                    });
                    dialog.show();
                }
            }
        });
    }

    private void setFunctionButton(Button b,final int finalM,final int temp,final String how)
    {
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {setShopButton1(finalM,temp,how); }
        });
    }

    private void setShopButton1(int M,int i,String how)
    {
        if(M>i+1)
        {
            StoreInformation store1 = (StoreInformation)addRecommendStore.arrayList.get(i);
            if(mode.equals("driving"))
            {
                driving.ReNewToShop(LocationLat,LocationLng,store1.getLat(),store1.getLng(),store1.getName(),how,tv_show);
                driving.addLatLng();
            }
            else if(mode.equals("walking"))
            {
                walking.ReNewToShop(LocationLat,LocationLng,store1.getLat(),store1.getLng(),store1.getName(),how);
                walking.addLatLng();
            }
            else if(mode.equals("transit"))
            {
                tran.ReNewToShop(LocationLat,LocationLng,store1.getLat(),store1.getLng(),store1.getName(),how);
                tran.addLatLng();
            }
        }
    }

    //取得今天日期
    private int getDate() { Calendar c = Calendar.getInstance(); return c.get(Calendar.DAY_OF_WEEK);//得到星期幾，編號由Sunday(1)~Saturday(7)
    }

    //處理點擊公車查看估計到站時間
    private void onShowTransitClick_sub(Button b1,final int temp)
    {
        b1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(tran.arrayTransitList.size()>temp)//檢查按鈕確實有對應到公車
                        {
                            BusNow busnow= (BusNow) tran.ReturnarrayTransitList().get(temp);
                            CheckBus checkbus=new CheckBus();
                            //去check是否有對應到ptx資料庫的公車列表，回傳ArrayList記錄各種找到公車的資訊
                            ArrayList checked=checkbus.HsinchuBus1((BusNow)tran.arrayTransitList.get(temp),busInformation);
                            ArrayList bus1= (ArrayList) checked;
                            //BusDialogOne用來寫顯示資訊
                            View v1=BusDialogOne(bus1,busnow.getBusStartStop(),busnow.getBusEndStop(),busnow.getSLat(),busnow.getSLng(),busnow.getELat(),busnow.getELng(),busnow.getBusRoute());
                            AlertDialog.Builder mBusBuilder=new AlertDialog.Builder(MapsActivity.this);
                            mBusBuilder.setView(v1);
                            final AlertDialog bus1dialog=mBusBuilder.create();
                            bus1dialog.show();
                            //close
                            Button b_close=(Button) v1.findViewById(R.id.button_close);
                            b_close.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    bus1dialog.dismiss();
                                }
                            });
                        }
                    }
                });
    }

    //點擊查看公車列表按鍵(跳出AlterDialog)
    public void onShowTransitClick(View view)
    {
        AlertDialog.Builder mBuilder=new AlertDialog.Builder(MapsActivity.this);
        View mView=getLayoutInflater().inflate(R.layout.dialog_test,null);
        Button b1=(Button)mView.findViewById(R.id.button1); Button b2=(Button)mView.findViewById(R.id.button2);
        Button b3=(Button)mView.findViewById(R.id.button3); Button b4=(Button)mView.findViewById(R.id.button4);
        Button b5=(Button)mView.findViewById(R.id.button5);
        //設置按鈕(公車)
        for(int i=0;i<tran.arrayTransitList.size();i++)
        {
            //BusNow用來記錄google map api取得的可搭成公車列表的資訊
            BusNow busnow= (BusNow) tran.ReturnarrayTransitList().get(i);
            //最多5個
            if(i==0) b1.setText(busnow.getBusNowName());
            else if(i==1) b2.setText(busnow.getBusNowName());
            else if(i==2) b3.setText(busnow.getBusNowName());
            else if(i==3) b4.setText(busnow.getBusNowName());
            else if(i==4) b5.setText(busnow.getBusNowName());
        }
        //點擊公車查看估計到站時間
        onShowTransitClick_sub(b1,0); onShowTransitClick_sub(b2,1);
        onShowTransitClick_sub(b3,2); onShowTransitClick_sub(b4,3);
        onShowTransitClick_sub(b5,4);

        Button b2_colse=(Button) mView.findViewById(R.id.button6_close);
        mBuilder.setView(mView);
        final AlertDialog dialog=mBuilder.create();
        dialog.show();
        b2_colse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    //為了用gps定位判斷google map站牌與ptx站牌是否為同一站，需要取得2點距離
    private double GetDistance(double lat1,double lng1,double lat2,double lng2)
    {
        double Lat=Math.abs(lat1-lat2);  double Lng=Math.abs(lng1-lng2);
        double SqrtLat=Math.pow(Lat,2);  double SqrtLng=Math.pow(Lng,2);
        double Sub=Math.abs(SqrtLat-SqrtLng);  double V=Math.sqrt(Sub);
        return V;
    }

    //為了找到起點終點站代號
    private ArrayList FindStartEndStop(ArrayList Stops,int Num,double Slat,double Slng,double Elat,double Elng,int tempS,int tempE,String routeID)
    {
            ArrayList<Integer> tempSList = new ArrayList<Integer>();  ArrayList<Integer> tempEList = new ArrayList<Integer>();
            for(int i=0;i<Stops.size();i++)
            {
                AllStopsOfRoute stop=(AllStopsOfRoute) Stops.get(i);
                Num=stop.getNum();
                //先比較距離，把可能的都存入arrayList
                double V1=GetDistance(stop.getPositionLat(),stop.getPositionLng(),Slat,Slng);
                double V2=GetDistance(stop.getPositionLat(),stop.getPositionLng(),Elat,Elng);
                if(V1<7e-4) tempSList.add(i);
                if(V2<7e-4)  tempEList.add(i);
            }
            //因為終點站編號一定大於起點站，以此來進行比較
            for(int t=0;t<tempSList.size();t++)
            {
                for(int r=0;r<tempEList.size();r++)
                {
                    AllStopsOfRoute stop1=(AllStopsOfRoute) Stops.get(tempSList.get(t));
                    AllStopsOfRoute stop2=(AllStopsOfRoute) Stops.get(tempEList.get(r));
                    if(stop1.getStopUID().equals("HSZTempStop"))
                    {
                        tempS=-1;
                        AllStopsOfRoute stop=(AllStopsOfRoute) Stops.get(tempSList.get(t));
                        routeID=stop.getRouteUID();
                        break;
                    }
                    else if(stop2.getStopUID().equals("HSZTempStop"))
                    {
                        if(tempEList.get(r)>tempSList.get(t) && tempSList.get(t)<=Num)
                        {
                            tempS=tempSList.get(t);
                            tempE=tempEList.get(r);
                            AllStopsOfRoute stop=(AllStopsOfRoute) Stops.get(tempSList.get(t));
                            routeID=stop.getRouteUID();
                        }
                    }
                    else
                    {
                        if(tempEList.get(r)>tempSList.get(t))
                        {
                            tempS=tempSList.get(t);
                            tempE=tempEList.get(r);
                            AllStopsOfRoute stop=(AllStopsOfRoute) Stops.get(tempSList.get(t));
                            routeID=stop.getRouteUID();
                        }
                    }
                }
            }
            ArrayList a=new ArrayList();
            a.add(tempS); a.add(tempE); a.add(routeID); a.add(Num);
            return a;
    }

    //顯示公車資訊
    private View BusDialogOne(ArrayList bus,String busStopStart,String busStopEnd,double Slat,double Slng,double Elat,double Elng,String BusRoute)
    {
        View mView=getLayoutInflater().inflate(R.layout.bus_dialog_test,null);
        TextView title=(TextView) mView.findViewById(R.id.title);
        TextView s1=(TextView) mView.findViewById(R.id.stop1); TextView s2=(TextView) mView.findViewById(R.id.stop2);
        TextView s3=(TextView) mView.findViewById(R.id.stop3); TextView s4=(TextView) mView.findViewById(R.id.stop4);
        TextView s5=(TextView) mView.findViewById(R.id.stop5);
        TextView t1=(TextView) mView.findViewById(R.id.time1); TextView t2=(TextView) mView.findViewById(R.id.time2);
        TextView t3=(TextView) mView.findViewById(R.id.time3); TextView t4=(TextView) mView.findViewById(R.id.time4);
        TextView t5=(TextView) mView.findViewById(R.id.time5); TextView send=(TextView) mView.findViewById(R.id.stopend);
        TextView bustext1=(TextView) mView.findViewById(R.id.bus1); TextView bustext2=(TextView) mView.findViewById(R.id.bus2);
        TextView bustext3=(TextView) mView.findViewById(R.id.bus3); TextView bustext4=(TextView) mView.findViewById(R.id.bus4);
        TextView bustext5=(TextView) mView.findViewById(R.id.bus5);
        //設置標題+路線
        title.setText((String)bus.get(0)+"\n"+BusRoute);
        //設置起點站、終點站
        s3.setText(busStopStart); send.setText(busStopEnd);
        ArrayList SubarrayList,Stops ;
        SubarrayList=(ArrayList)bus.get(3); //預估時間取得
        Stops=(ArrayList)bus.get(4); //所以stops取得

        int tempS=-2,tempE=-1,Num=-1;//Num為去程stops數；tempS、tempE是為了得到起點終點站代號
        String routeID="";

        if(Stops!=null) //找到起點站&終點站的編號(0~SIZE-1)
        {
            ArrayList a=FindStartEndStop(Stops,Num,Slat, Slng, Elat,Elng,tempS,tempE,routeID);
            tempS=(int)a.get(0);   tempE=(int)a.get(1); routeID=(String)a.get(2); Num=(int)a.get(3);
            if(tempS!=-2)
            {
                if(tempS!=-1)
                {
                    int last2=tempS-2 ,last1=tempS-1;
                    int next2=tempS+2 ,next1=tempS+1;
                    AllStopsOfRoute stop=(AllStopsOfRoute) Stops.get(tempS);
                    s3.setText(stop.getStopName());
                    if(last2>=0)
                    {
                        AllStopsOfRoute laststop2=(AllStopsOfRoute) Stops.get(last2);
                        s1.setText(laststop2.getStopName());
                    }
                    if(last1>=0)
                    {
                        AllStopsOfRoute laststop1=(AllStopsOfRoute) Stops.get(last1);
                        s2.setText(laststop1.getStopName());
                    }
                    if(last2<0)
                    {
                        s1.setText("無");
                        t1.setText("無");
                    }
                    if(last1<0)
                    {
                        s2.setText("無");
                        t2.setText("無");
                    }
                    if(next2<Stops.size())
                    {
                        AllStopsOfRoute nextstop2=(AllStopsOfRoute) Stops.get(next2);
                        s5.setText(nextstop2.getStopName());
                    }
                    if(next1<Stops.size())
                    {
                        AllStopsOfRoute nextstop1=(AllStopsOfRoute) Stops.get(next1);
                        s4.setText(nextstop1.getStopName());
                    }
                    if(next2>=Stops.size())
                    {
                        s5.setText("無");
                        t5.setText("無");
                    }
                    if(next1>=Stops.size())
                    {
                        s4.setText("無");
                        t4.setText("無");
                    }
                }
                else
                {
                    s1.setText("無");  t1.setText("無");
                    s2.setText("無");  t2.setText("無");
                    AllStopsOfRoute nextstop1=(AllStopsOfRoute) Stops.get(Num);
                    s4.setText(nextstop1.getStopName());
                    AllStopsOfRoute nextstop2=(AllStopsOfRoute) Stops.get(Num+1);
                    s5.setText(nextstop2.getStopName());
                }
            }
        }

        if(SubarrayList!=null) //找到站牌的估計時間
        {
            if(tempS!=-2)
            {
                if(tempS!=-1)
                {
                    int last2=tempS-2 ,last1=tempS-1;  int next2=tempS+2 ,next1=tempS+1;
                    for(int n=0;n<SubarrayList.size();n++)
                    {
                        StopEstimatedTimeOfArrival S,S1;
                        S=(StopEstimatedTimeOfArrival)SubarrayList.get(n);
                        if(routeID.equals(S.getRouteUID()))
                        {
                            if( (S.getStopSequence()-1)+S.getDirection()*Num == last2)
                            {
                                int time=S.getEstimateTime();
                                if(time!=-1)
                                {
                                    int min=time/60;  int sec=time-min*60;
                                    t1.setText(Integer.toString(min)+"分"+Integer.toString(sec)+"秒");
                                    bustext1.setText(S.getPlateNumb());
                                }
                                else
                                {
                                    t1.setText("暫無");
                                    bustext1.setText("");
                                }
                            }
                            else if( (S.getStopSequence()-1)+S.getDirection()*Num == last1 )
                            {
                                int time=S.getEstimateTime();
                                if(time!=-1)
                                {
                                    int min=time/60;
                                    int sec=time-min*60;
                                    t2.setText(Integer.toString(min)+"分"+Integer.toString(sec)+"秒");
                                    bustext2.setText(S.getPlateNumb());
                                }
                                else
                                {
                                    t2.setText("暫無");
                                    bustext2.setText("");
                                }
                            }
                            else if ( (S.getStopSequence()-1) +S.getDirection()*Num == tempS )
                            {
                                int time=S.getEstimateTime();
                                if(time!=-1)
                                {
                                    int min=time/60;
                                    int sec=time-min*60;
                                    t3.setText(Integer.toString(min)+"分"+Integer.toString(sec)+"秒");
                                    bustext3.setText(S.getPlateNumb());
                                }
                                else
                                {
                                    t3.setText("暫無");
                                    bustext3.setText("");
                                }
                            }
                            else if ( (S.getStopSequence()-1) +S.getDirection()*Num == next1 )
                            {
                                int time=S.getEstimateTime();
                                if(time!=-1)
                                {
                                    int min=time/60;
                                    int sec=time-min*60;
                                    t4.setText(Integer.toString(min)+"分"+Integer.toString(sec)+"秒");
                                    bustext4.setText(S.getPlateNumb());
                                }
                                else  {
                                    t4.setText("暫無");
                                    bustext4.setText("");
                                }
                            }
                            else if ( (S.getStopSequence()-1) +S.getDirection()*Num == next2 )
                            {
                                int time=S.getEstimateTime();
                                if(time!=-1)
                                {
                                    int min=time/60;  int sec=time-min*60;
                                    t5.setText(Integer.toString(min)+"分"+Integer.toString(sec)+"秒");
                                    bustext5.setText(S.getPlateNumb());
                                }
                                else {
                                    t5.setText("暫無");
                                    bustext5.setText("");
                                }
                            }
                        }
                    }
                }
                else
                {
                    for(int n=0;n<SubarrayList.size();n++) {
                        StopEstimatedTimeOfArrival S, S1;
                        S = (StopEstimatedTimeOfArrival) SubarrayList.get(n);
                        if(routeID.equals(S.getRouteUID())) {
                            if ((S.getStopSequence() - 1) + S.getDirection() * Num == Num) {
                                int time=S.getEstimateTime();
                                if(time!=-1)
                                {
                                    int min=time/60;
                                    int sec=time-min*60;
                                    t4.setText(Integer.toString(min)+"分"+Integer.toString(sec)+"秒");
                                    bustext4.setText(S.getPlateNumb());
                                }
                                else  {
                                    t4.setText("暫無");
                                    bustext4.setText("");
                                }
                            }
                            else  if ((S.getStopSequence() - 1) + S.getDirection() * Num == Num+1) {
                                int time=S.getEstimateTime();
                                if(time!=-1)
                                {
                                    int min=time/60;
                                    int sec=time-min*60;
                                    t5.setText(Integer.toString(min)+"分"+Integer.toString(sec)+"秒");
                                    bustext5.setText(S.getPlateNumb());
                                }
                                else  {
                                    t5.setText("暫無");
                                    bustext5.setText("");
                                }
                            }
                        }
                    }
                }
            }
        }
        //時刻表
        Button b_timetable=(Button)mView.findViewById(R.id.button_timetable);
        final String finalRouteID = routeID;
        b_timetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View vt=getLayoutInflater().inflate(R.layout.timetable,null);
                TextView t=(TextView)vt.findViewById(R.id.textView2) ;  TextView t1=(TextView)vt.findViewById(R.id.textView7) ;
                TextView t2=(TextView)vt.findViewById(R.id.textView9) ;  TextView t3=(TextView)vt.findViewById(R.id.textView10) ;
                TextView title=(TextView)vt.findViewById(R.id.timetabletitle);
                ArrayList AllBus=getBusSchedule.AllBus;
                for(int i=0;i<AllBus.size();i++)
                {
                    BusSchedule busSchedule=(BusSchedule)AllBus.get(i);
                    if(busSchedule.getSubRouteUID().equals(finalRouteID))
                    {
                        String sb="",sb1="",sb2="",sb3="";
                        ArrayList<OneTimeStopSchedule> oneTimeStopSchedules=busSchedule.getSaturday();
                        int date=getDate();
                        if(date==1)
                        {
                            oneTimeStopSchedules=busSchedule.getSunday();
                            title.setText("  發車時刻表 : "+"星期日");
                        }
                        else if(date==2)
                        {
                            oneTimeStopSchedules=busSchedule.getMonday();
                            title.setText("  發車時刻表 : "+"星期一");
                        }
                        else if(date==3)
                        {
                            oneTimeStopSchedules=busSchedule.getThueday();
                            title.setText("  發車時刻表 : "+"星期二");
                        }
                        else if(date==4)
                        {
                            oneTimeStopSchedules=busSchedule.getWednesday();
                            title.setText("  發車時刻表 : "+"星期三");
                        }
                        else if(date==5)
                        {
                            oneTimeStopSchedules=busSchedule.getThursday();
                            title.setText("  發車時刻表 : "+"星期四");
                        }
                        else if(date==6)
                        {
                            oneTimeStopSchedules=busSchedule.getFriday();
                            title.setText("  發車時刻表 : "+"星期五");
                        }
                        else if(date==7)
                        {
                            oneTimeStopSchedules=busSchedule.getSaturday();
                            title.setText("  發車時刻表 : "+"星期六");
                        }

                        OneTimeStopSchedule one_temp=oneTimeStopSchedules.get(0);
                        sb+=busSchedule.getRouteName()+"\n"+one_temp.getStopName()+"\n";
                        String S="";
                        for(int j=0;j<oneTimeStopSchedules.size();j++)
                        {
                            if(j<19)
                            {
                                OneTimeStopSchedule one=oneTimeStopSchedules.get(j);
                                sb+=one.DepartureTime+"\n";
                            }
                            else if(j<41)
                            {
                                OneTimeStopSchedule one=oneTimeStopSchedules.get(j);
                                sb1+=one.DepartureTime+"\n";
                            }
                            else if(j<63)
                            {
                                OneTimeStopSchedule one=oneTimeStopSchedules.get(j);
                                sb2+=one.DepartureTime+"\n";
                            }
                            else
                            {
                                OneTimeStopSchedule one=oneTimeStopSchedules.get(j);
                                sb3+=one.DepartureTime+"\n";
                            }
                        }
                        t.setText(sb);
                        t1.setText(sb1);
                        t2.setText(sb2);
                        t3.setText(sb3);
                        break;
                    }
                }

                ArrayList AllBus2=getInterCitySchedule.AllBus;
                for(int i=0;i<AllBus2.size();i++)
                {
                    BusSchedule busSchedule=(BusSchedule)AllBus2.get(i);
                    if(busSchedule.getSubRouteUID().equals(finalRouteID))
                    {
                        String sb="",sb1="",sb2="",sb3="";
                        int date=getDate();
                        ArrayList<OneTimeStopSchedule> oneTimeStopSchedules=busSchedule.getMonday();
                        if(date==1)
                        {
                            oneTimeStopSchedules=busSchedule.getSunday();
                            title.setText("  發車時刻表 : "+"星期日");
                        }
                        else if(date==2)
                        {
                            oneTimeStopSchedules=busSchedule.getMonday();
                            title.setText("  發車時刻表 : "+"星期一");
                        }
                        else if(date==3)
                        {
                            oneTimeStopSchedules=busSchedule.getThueday();
                            title.setText("  發車時刻表 : "+"星期二");
                        }
                        else if(date==4)
                        {
                            oneTimeStopSchedules=busSchedule.getWednesday();
                            title.setText("  發車時刻表 : "+"星期三");
                        }
                        else if(date==5)
                        {
                            oneTimeStopSchedules=busSchedule.getThursday();
                            title.setText("  發車時刻表 : "+"星期四");
                        }
                        else if(date==6)
                        {
                            oneTimeStopSchedules=busSchedule.getFriday();
                            title.setText("  發車時刻表 : "+"星期五");
                        }
                        else if(date==7)
                        {
                            oneTimeStopSchedules=busSchedule.getSaturday();
                            title.setText("  發車時刻表 : "+"星期六");
                        }

                        OneTimeStopSchedule one_temp=oneTimeStopSchedules.get(0);
                        sb+=busSchedule.getRouteName()+"\n"+one_temp.getStopName()+"\n";

                        for(int j=0;j<oneTimeStopSchedules.size();j++)
                        {
                            if(j<19)
                            {
                                OneTimeStopSchedule one=oneTimeStopSchedules.get(j);
                                sb+=one.DepartureTime+"\n";
                            }
                            else if(j<41)
                            {
                                OneTimeStopSchedule one=oneTimeStopSchedules.get(j);
                                sb1+=one.DepartureTime+"\n";
                            }
                            else if(j<63)
                            {
                                OneTimeStopSchedule one=oneTimeStopSchedules.get(j);
                                sb2+=one.DepartureTime+"\n";
                            }
                            else
                            {
                                OneTimeStopSchedule one=oneTimeStopSchedules.get(j);
                                sb3+=one.DepartureTime+"\n";
                            }
                        }
                        t.setText(sb);
                        t1.setText(sb1);
                        t2.setText(sb2);
                        t3.setText(sb3);
                        break;
                    }
                }

                AlertDialog.Builder mBusBuilder4=new AlertDialog.Builder(MapsActivity.this);
                mBusBuilder4.setView(vt);
                final AlertDialog bustimetabledialog=mBusBuilder4.create();
                bustimetabledialog.show();
                Button B_close=(Button) vt.findViewById(R.id.button7_close);
                B_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bustimetabledialog.dismiss();
                    }
                });
            }
        });
        return  mView;
    }

    private String ShowRoute(String htmlroute) {
        htmlroute = htmlroute.replace("<b>", "");
        htmlroute = htmlroute.replace("</b>", "");
        htmlroute = htmlroute.replace("</div>", "");
        String h = "";
        int temp = 0;
        for (int i = 0; i < htmlroute.length(); i++) {
            if (htmlroute.charAt(i) == '<') temp = 1;
            if (temp == 0) {
                h += htmlroute.charAt(i);
            }
            if (htmlroute.charAt(i) == '>') {
                temp = 0;
                h += " ";
            }
        }
        return h;
    }

    //點擊詳細導航資訊
    private void Details_Transit(ArrayList<StepList> top_arrayList)
    {
        String sb="";
        AlertDialog.Builder mBuilder=new AlertDialog.Builder(MapsActivity.this);
        View mView=getLayoutInflater().inflate(R.layout.details,null);
        TextView t=(TextView) mView.findViewById(R.id.text);
        for(int i=0;i<top_arrayList.size();i++)
        {
            StepList stepList=top_arrayList.get(i);
            sb+="第"+Integer.toString(i+1)+"種方案"+"\n"+"大約花"+stepList.getAllCostTime()+",距離"+stepList.getAllDistance()+"\n";
            ArrayList<Step> step_List=stepList.getListList();
            if(i==0)sb+="詳細導航:"+"\n";
            for(int j=0;j<step_List.size();j++)
            {
                Step step=step_List.get(j);
                sb+=Integer.toString(j+1)+":"+ShowRoute(step.getHtml_instruction())+",大約花"+step.getSubCostTime()+",距離"+step.getSubDistance()+"\n";
                ArrayList subStepList=step.getSubStepsList();
                if(subStepList==null)
                {
                    TransitDetails transitDetails=(TransitDetails)step.getTransitDetails();
                    if(transitDetails.getVechine().equals("火車"))
                    {
                        sb+="*火車資訊:"+transitDetails.getBusName()+",開"+transitDetails.getHeadsign()+","+transitDetails.getTrainStartTime()+"發車";
                        sb+=",從"+transitDetails.getBusStartStop()+"坐到"+transitDetails.getBusEndStop()+"\n";
                    }
                    else if(transitDetails.getVechine().equals("公車"))
                    {
                        sb+="*公車資訊:"+transitDetails.getBusName()+","+transitDetails.getBusRoute();
                        sb+=",從"+transitDetails.getBusStartStop()+"坐到"+transitDetails.getBusEndStop()+"\n";
                    }
                }
                else
                {
                    for(int k=0;k<subStepList.size();k++)
                    {
                        SubSteps subSteps=(SubSteps)subStepList.get(k);
                        sb+=" ("+Integer.toString(k+1)+")"+ShowRoute(subSteps.getSub_html_instruction())+"大約花"+subSteps.getSubSubCostTime()+",距離"+subSteps.getSubSubDistance()+"\n";
                    }
                }
            }
            sb+="--------------------"+"\n";
        }
        if(sb.equals("")) t.setText("尚未開始導航~");
        else t.setText(sb);
        mBuilder.setView(mView);
        AlertDialog dialog=mBuilder.create();
        dialog.show();
    }
    //點擊詳細導航資訊
    private void Details_WalkDrive(ArrayList<StepList> top_arrayList)
    {
        String sb="";
        AlertDialog.Builder mBuilder=new AlertDialog.Builder(MapsActivity.this);
        View mView=getLayoutInflater().inflate(R.layout.details,null);
        TextView t=(TextView) mView.findViewById(R.id.text);
        for(int i=0;i<top_arrayList.size();i++)
        {
            StepList stepList=top_arrayList.get(i);
            sb+="第"+Integer.toString(i+1)+"種方案"+"\n"+"大約花"+stepList.getAllCostTime()+",距離"+stepList.getAllDistance()+"\n";

            ArrayList<Step> step_List=stepList.getListList();
            if(i==0)sb+="詳細導航:"+"\n";
            for(int j=0;j<step_List.size();j++)
            {
                Step step=step_List.get(j);
                sb+=Integer.toString(j+1)+":"+ShowRoute(step.getHtml_instruction())+",大約花"+step.getSubCostTime()+",距離"+step.getSubDistance()+"\n";
                ArrayList subStepList=step.getSubStepsList();
            }
            sb+="--------------------"+"\n";
        }
        if(sb.equals("")) t.setText("尚未開始導航~");
        else t.setText(sb);
        mBuilder.setView(mView);
        AlertDialog dialog=mBuilder.create();
        dialog.show();
    }
    //點擊詳細導航資訊
    public void onDetailClick(View view) {
        AlertDialog.Builder mBuilder=new AlertDialog.Builder(MapsActivity.this);
        View mView=getLayoutInflater().inflate(R.layout.details,null);
        TextView t=(TextView) mView.findViewById(R.id.text);
        String sb="";
        ArrayList<StepList> top_arrayList=null;
        if(mode.equals("transit"))Details_Transit(tran.List);
        else if(mode.equals("walking")) Details_WalkDrive(walking.List);
        else if(mode.equals("driving")) Details_WalkDrive(driving.List);
    }

    //控制點到地圖
    private void ClickMapGetLatLng()
    {
        etELocationName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            Marker melbourne = mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title("終點"));
                            melbourne.showInfoWindow();
                            FindAddress findAddress=new FindAddress();
                            String address=findAddress.Find(latLng.latitude,latLng.longitude);
                            etELocationName.setText(address);

                        }
                    });
                } else {
                }
            }
        });

        etSLocationName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            Marker melbourne = mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title("起點"));
                            melbourne.showInfoWindow();
                            FindAddress findAddress=new FindAddress();
                            String address=findAddress.Find(latLng.latitude,latLng.longitude);
                            etSLocationName.setText(address);
                        }
                    });
                } else {
                }
            }
        });
    }

    private void MarkLocation()
    {
        LatLng latLng1 = new LatLng(LocationLat, LocationLng);
        MarkerOptions m=new MarkerOptions().position(latLng1).title("現在位置");
        Marker markerName =mMap.addMarker(m);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 15));
    }

    /*private void getCurrentLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        String provider = mLocationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            //            // here to request the missing permissions, and then overriding
            //            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //            //                                          int[] grantResults)
            //            // to handle the case where the user grants the permission. See the documentation
            //            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            },1);
        }
        mLocationManager.requestLocationUpdates(provider, 3000,10, mLocationListener);
        Location location = mLocationManager.getLastKnownLocation(provider);
        if (location != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("您現在的位置\n"); sb.append("經度：" + location.getLongitude() + "\n");
            sb.append("緯度：" + location.getLatitude() + "\n"); sb.append("高度：" + location.getAltitude() + "\n");
            sb.append("速度：" + location.getSpeed() + "\n"); sb.append("方向：" + location.getBearing() + "\n");
            sb.append("定位精度：" + location.getAccuracy() + "\n");
            LocationLat = location.getLatitude();  LocationLat = location.getLatitude();
        }
    }*/

    private void getCurrentLocationStart() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        String provider = mLocationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            },1);
        }
        mLocationManager.requestLocationUpdates(provider, 7000, 100, mLocationListener2);
        Location location = mLocationManager.getLastKnownLocation(provider);
        if (location != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("您現在的位置\n");  sb.append("經度：" + location.getLongitude() + "\n");
            sb.append("緯度：" + location.getLatitude() + "\n"); sb.append("高度：" + location.getAltitude() + "\n");
            sb.append("速度：" + location.getSpeed() + "\n"); sb.append("方向：" + location.getBearing() + "\n");
            sb.append("定位精度：" + location.getAccuracy() + "\n");
            LocationLat = location.getLatitude();
            LocationLng = location.getLongitude();
        }
    }

    //private static final int REQUEST_FINE_LOCATION_PERMISSION = 102;

    //LocationListener中 onLocationChanged 主要是當 GPS 位置改變時，會觸發該函數，
    //在此透過Toast顯示當下最新之GPS經緯度。
   /* private LocationListener mLocationListener = new LocationListener() {
        @SuppressLint("WrongConstant")
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                getCurrentLocation();
                driving.ReNew(LocationLat,LocationLng);
                driving.addLatLng();
                String msg = "1:"+String.format("%f, %f", location.getLatitude(), location.getLongitude());
                Toast.makeText(getApplicationContext(), msg, 100).show();
            } else { Toast.makeText(getApplicationContext(), "Location is null", Toast.LENGTH_SHORT).show(); }
        }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) { }
        @Override
        public void onProviderEnabled(String s) { }
        @Override
        public void onProviderDisabled(String s) { }
    };*/

    private LocationListener mLocationListener2 = new LocationListener() {
        @SuppressLint("WrongConstant")
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                // getCurrentLocationStart();
                MarkLocation();
                String msg = String.format("%f, %f", location.getLatitude(), location.getLongitude());
                Toast.makeText(getApplicationContext(), msg, 100).show();
            } else { Toast.makeText(getApplicationContext(), "Location is null", Toast.LENGTH_SHORT).show(); }
        }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) { }
        @Override
        public void onProviderEnabled(String s) { }
        @Override
        public void onProviderDisabled(String s) { }
    };
}

