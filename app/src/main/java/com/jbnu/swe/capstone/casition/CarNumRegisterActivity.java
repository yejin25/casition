package com.jbnu.swe.capstone.casition;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CarNumRegisterActivity extends AppCompatActivity{
    private ImageView menu, register;
    private Button logout, change_carNum, calc, select, accept, loc, calc_list;
    private EditText carNum;
    private TextView name, plateNum;

    private String server;

    private final int PICK_IMAGE = 1;
    private Bitmap saveImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inputnumber);

        SharedPreferences sf = getSharedPreferences("user",Context.MODE_PRIVATE);

        server = "http://114.70.193.152:10111/hipowebserver_war/android/user/"+sf.getString("id","") +"/registercar";

        carNum = (EditText) findViewById(R.id.user_car_number);

        menu = (ImageView) findViewById(R.id.menubar);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitializeLayout();
            }
        });

        register = (ImageView) findViewById(R.id.register);

        select = (Button) findViewById(R.id.btn_select);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        accept = (Button) findViewById(R.id.btn_accept);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String car_num = carNum.getText().toString();

                if(car_num.isEmpty()){
                    carNum.setError("차량번호를 입력하세요.");
                    carNum.requestFocus();
                }else if(saveImage == null){
                    Toast.makeText(CarNumRegisterActivity.this, "자동차 등록증을 추가하세요.", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(CarNumRegisterActivity.this, "승인 요청이 완료되었습니다.", Toast.LENGTH_LONG).show();
                    //서버로 데이터 전송
                    try {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);

                        OkHttpClient client = new OkHttpClient();

                        RequestBody requestBody = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("carNumber",car_num)
                                .addFormDataPart("file", sf.getString("id",""), RequestBody.create(MultipartBody.FORM, BitmapToByteArray(saveImage)))
                                .build();

                        Request request = new Request.Builder()
                                .url(server)
                                .post(requestBody)
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String data = response.body().string();

                                if (response.code() == 200) {
                                    Log.d("CarNumberRegister", "----- 1" + data);
//                                    sf.edit().putString("plateNum", "승인 대기").apply();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Log.d("CarNumberRegister", "----- 2" + data);
                                }
                            }
                        });

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE){
            if(resultCode == RESULT_OK && data != null){
                Uri uri = data.getData();
                assert uri != null;
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    if(bitmap != null){
                        saveImage = bitmap;
                        register.setImageBitmap(bitmap);
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public byte[] BitmapToByteArray(Bitmap bitmap){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

            return byteArrayOutputStream.toByteArray();
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
        plateNum.setText(sf.getString("plateNum", "등록필요"));

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
