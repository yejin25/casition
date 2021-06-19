package com.jbnu.swe.capstone.casition;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;

public class ShowUserLicenseActivity extends Activity {
    public static final int SHOWUSERLICENSE_START_ACTIVITY = 80;
    public static final int SHOWUSERLICENSE_SUCCESS_END = 81;
    public static final int SHOWUSERLICENSE_FAIL_END = 82;

    private ImageView licenseImageView;
    private Bitmap userImage;

    private Button acceptButton, rejectButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.activity_showuserlicense);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if (getIntent().getExtras().getString("userName") != null && !getIntent().getExtras().getString("userName").equals("")) {
            byte[] userBytes = Base64.decode(AdministratorActivity.userImageHashMap.get(getIntent().getExtras().getString("userName")), Base64.DEFAULT);
            userImage = BitmapFactory.decodeByteArray(userBytes, 0, userBytes.length);
        }

        licenseImageView = findViewById(R.id.license_image_view);
        licenseImageView.setBackground(new BitmapDrawable(getResources(), userImage));

        acceptButton = (Button)findViewById(R.id.license_accept_btn);
        rejectButton = (Button)findViewById(R.id.license_reject_btn);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptedUserLicense();
            }
        });

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(SHOWUSERLICENSE_FAIL_END);
                finish();
            }
        });

    }

    private void acceptedUserLicense() {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            final String serverUrl = "http://114.70.193.152:10111/hipowebserver_war/android/admin/accepted/" + getIntent().getExtras().getString("userName");

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
                        if (response.code() == 200) {
                            setResult(SHOWUSERLICENSE_SUCCESS_END);
                        } else {
                            setResult(SHOWUSERLICENSE_FAIL_END);
                        }
                        finish();

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

}
