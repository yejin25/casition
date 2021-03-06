package com.jbnu.swe.capstone.casition;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CalcListActivity extends AppCompatActivity {
    private ImageView menu;
    private Button logout, change_carNum, calc, loc, calc_list;
    private TextView name, plateNum;
    private TextView area, area_data, time, time_data, out, out_data;
    private String server;
    private Adapter adapter;
    private ListView listview;
    public static ArrayList<Data> dataArrayList = new ArrayList<Data>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calclist);

        SharedPreferences sf = getSharedPreferences("user",Context.MODE_PRIVATE);
        listview = (ListView)findViewById(R.id.list);
        area = (TextView) findViewById(R.id.text_area);
        area_data = (TextView) findViewById(R.id.text_area_data);
        time = (TextView) findViewById(R.id.text_time);
        time_data = (TextView) findViewById(R.id.text_time_data);
        out = (TextView) findViewById(R.id.text_out);
        out_data = (TextView) findViewById(R.id.text_out_data);

        server = "http://114.70.193.152:10111/hipowebserver_war/android/user/myinfo/all/"+sf.getString("id","");

        adapter = new Adapter(this);
        listview.setAdapter(adapter);

        getList();

        menu = (ImageView) findViewById(R.id.menubar);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitializeLayout();
            }
        });

    }

    public void getList(){
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            OkHttpClient client = new OkHttpClient();
            HttpUrl.Builder urlBuilder = HttpUrl.parse(server).newBuilder();
            String url = urlBuilder.build().toString();

            Request request = new Request.Builder().url(url).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.d("CalcListActivity", "----- Read Func " + e + " -----");
                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try {
                        String data = response.body().string();
                        if (data != null) {
                            dataArrayList.clear();
                            JSONArray jsonArray = new JSONArray(data);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i); // obj -> jsonObj
                                Data calc_data = new Data();
                                calc_data.area = object.getString("parkingArea");
                                calc_data.time = object.getString("time");
                                calc_data.out_time = object.getString("outTime");
                                dataArrayList.add(calc_data);
                            }
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void InitializeLayout()
    {
        DrawerLayout drawLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);

        SharedPreferences sf = getSharedPreferences("user", Context.MODE_PRIVATE);

        logout = (Button) findViewById(R.id.btn_logout);
        change_carNum = (Button) findViewById(R.id.btn_modify);
        calc = (Button) findViewById(R.id.btn_calc);
        name = (TextView) findViewById(R.id.currentname);
        plateNum = (TextView) findViewById(R.id.carnum);
        loc = (Button) findViewById(R.id.btn_loc);
        calc_list = (Button) findViewById(R.id.btn_calc_list);

        name.setText(sf.getString("name","NoName"));
        plateNum.setText(sf.getString("plateNum", "?????? ??????"));

        View header = navigationView.getHeaderView(0);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        change_carNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CarNumRegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CalcActivity.class);
                startActivity(intent);
                finish();
            }
        });

        calc_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CalcListActivity.class);
                startActivity(intent);
                finish();
            }
        });


        drawLayout.openDrawer(Gravity.LEFT);
    }
}
