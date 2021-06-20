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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CalcActivity extends AppCompatActivity {
    private ImageView menu;
    private Button logout, change_carNum, calc, loc, calc_list;
    private TextView name, plateNum, time;
    private String server = "http://114.70.193.152:10111/hipowebserver_war/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);


        time = (TextView) findViewById(R.id.entrance_time);
        getTime();

        menu = (ImageView) findViewById(R.id.menubar);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitializeLayout();
            }
        });

    }

    public void getTime(){
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            SharedPreferences sf = getSharedPreferences("user",Context.MODE_PRIVATE);
            OkHttpClient client = new OkHttpClient();
            HttpUrl.Builder urlBuilder = HttpUrl.parse(server).newBuilder();
            String url = urlBuilder.build().toString();
            Request request = new Request.Builder().url(url).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.d("CalcActivity", "----- Read Func " + e + " -----");
                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try {
                        String data = response.body().string();
                        if (data != null) {
                            JSONArray jsonArray = new JSONArray(data);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                String name = object.toString();
                                if(name.equals(sf.getString("name", ""))){
                                    JSONArray array = (JSONArray) object.get("presentCarList");
                                    JSONObject jsonObject =  array.getJSONObject(0);
                                    time.setText(jsonObject.getString("parkingTime"));
                                    break;
                                }
                            }
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void InitializeLayout() {
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

        name.setText(sf.getString("name", "NoName"));
        plateNum.setText(sf.getString("plateNum", "등록 필요"));

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