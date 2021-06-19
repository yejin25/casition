package com.jbnu.swe.capstone.casition;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AdministratorActivity extends Activity {
    private static final String TAG = "AdministratorActivity";
    public static final int ADMINISTRATOR_START_ACTIVITY = 90;

    public static ArrayList<String> userListStaticArrayList = new ArrayList<String>();
    public static HashMap<String, byte[]> userImageHashMap = new HashMap<String, byte[]>();

    private AdministratorUserAdapter administratorUserAdapter;

    private ListView userNameListView;

    private Button userNameListViewRenewButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator);

        userNameListView = (ListView)findViewById(R.id.admin_list_view);
        administratorUserAdapter = new AdministratorUserAdapter(AdministratorActivity.this);
        userNameListView.setAdapter(administratorUserAdapter);

        userNameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ShowUserLicenseActivity.class);
                intent.putExtra("userName", userListStaticArrayList.get(position));
                startActivityForResult(intent, ShowUserLicenseActivity.SHOWUSERLICENSE_START_ACTIVITY);
            }
        });

        userNameListViewRenewButton = (Button)findViewById(R.id.admin_list_renew_button);
        userNameListViewRenewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                renewalServerData();
                administratorUserAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ShowUserLicenseActivity.SHOWUSERLICENSE_START_ACTIVITY) {
            if (resultCode == ShowUserLicenseActivity.SHOWUSERLICENSE_SUCCESS_END) {
                Toast.makeText(AdministratorActivity.this, "완료 되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AdministratorActivity.this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void renewalServerData() {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            final String serverUrl = "http://114.70.193.152:10111/hipowebserver_war/android/admin/licenseimages";

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
                        userListStaticArrayList.clear();
                        userImageHashMap.clear();

                        JSONObject userObject = new JSONObject(response.body().string());

                        Iterator<String> keys = userObject.keys();

                        while (keys.hasNext()) {
                            String key = keys.next();

                            Log.d(TAG, "----- key : " + key);
                            if (!key.equals("") && !userListStaticArrayList.contains(key)) {
                                userListStaticArrayList.add(key);
                                userImageHashMap.put(key, userObject.get(key).toString().getBytes(Charset.forName("UTF-8")));
                            }
                        }

                    } catch (NullPointerException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


}
