package com.jbnu.swe.capstone.casition;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    private ImageView menu, park_area;
    private Button logout, change_carNum, calc, loc, calc_list;
    private TextView name, plateNum, loc_txt;

    public static Bitmap parkingImageBitmap;
    public static String parkingAreaString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hasGetPermissions(this);

        park_area = (ImageView) findViewById(R.id.loc_img);
        loc_txt = (TextView) findViewById(R.id.loc_txt);
        menu = (ImageView) findViewById(R.id.menubar);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitializeLayout();
            }
        });

        getParkingAreaImage();
    }

    public void getParkingAreaImage() {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            SharedPreferences sf = getSharedPreferences("user", Context.MODE_PRIVATE);
            String serverUrl = "http://114.70.193.152:10111/hipowebserver_war/android/user/parkingArea/" + sf.getString("id", "");

            OkHttpClient okHttpClient = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(serverUrl)
                    .build();

            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {

                        JSONObject userObject = new JSONObject(response.body().string());

                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String key = userObject.keys().next();
                                    parkingAreaString = key;

                                    byte[] userImageBytes = Base64.decode(userObject.get(key).toString(), Base64.DEFAULT);
                                    parkingImageBitmap = BitmapFactory.decodeByteArray(userImageBytes, 0, userImageBytes.length);

                                    park_area.setBackground(new BitmapDrawable(getResources(), parkingImageBitmap));
                                    loc_txt.setText(parkingAreaString);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 0);

                    } catch (NullPointerException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (NullPointerException e) {
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

    public boolean hasGetPermissions(Context context) {
        final int PERMISSION_REQUEST_CODE1 = 9990;
        final String[] permissions = {
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        if (context != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, permissions, PERMISSION_REQUEST_CODE1);
                    if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}