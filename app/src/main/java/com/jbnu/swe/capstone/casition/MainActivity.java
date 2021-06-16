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
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity {

    private ImageView menu;
    private Button logout, change_carNum, calc, loc;
    private TextView name, plateNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hasGetPermissions(this);

        menu = (ImageView) findViewById(R.id.menubar);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitializeLayout();
            }
        });
    }

    public void InitializeLayout()
    {
        DrawerLayout drawLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);

        SharedPreferences sf = getSharedPreferences("user",Context.MODE_PRIVATE);

        logout = (Button) findViewById(R.id.btn_logout);
        change_carNum = (Button) findViewById(R.id.btn_modify);
        calc = (Button) findViewById(R.id.btn_calc);
        name = (TextView) findViewById(R.id.currentname);
        plateNum = (TextView) findViewById(R.id.carnum);
        loc = (Button) findViewById(R.id.btn_loc);

        name.setText(sf.getString("name","NoName"));
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