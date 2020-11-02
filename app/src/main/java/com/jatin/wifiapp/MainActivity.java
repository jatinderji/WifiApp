package com.jatin.wifiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Switch switchWifi;
    private ImageView myImg;
    private TextView txtStatus;
    private WifiManager wifiManager;
    private Button btnDiscover;
    private ListView myListView;
    private ArrayList<String> list;
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = new ArrayList<>();
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);

        switchWifi = findViewById(R.id.switchWifi);
        myImg = findViewById(R.id.myImg);
        txtStatus = findViewById(R.id.txtStatus);
        btnDiscover = findViewById(R.id.btnDiscover);
        myListView = findViewById(R.id.myListView);

    wifiManager =  (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

     
        switchWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked)
                    wifiManager.setWifiEnabled(true);
                else
                    wifiManager.setWifiEnabled(false);
            }
        });

        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.clear();
                IntentFilter intentFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
                registerReceiver(myWifiDevicesReceiver,intentFilter);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter iFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(myWifiReceiver,iFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myWifiReceiver);
    }


    // Getting scanned devicec
    private BroadcastReceiver myWifiDevicesReceiver = new BroadcastReceiver() {
        int sno = 1;
        @Override
        public void onReceive(Context context, Intent intent) {
            final List<ScanResult> scanResults = wifiManager.getScanResults();
            unregisterReceiver(this);
            for(ScanResult result: scanResults){
                list.add(sno+": "+result.SSID+ " : "+ result.capabilities);
                sno++;
                adapter.notifyDataSetChanged();
            }
            myListView.setAdapter(adapter);
        }
    };


    // Telling Status of Wifi such as Enabled, Disabled, Enabling or Disabling, etc
    private BroadcastReceiver myWifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
            switch (wifiState){
                case WifiManager.WIFI_STATE_DISABLED:{
                    txtStatus.setText("Wifi is Disabled.");
                    switchWifi.setChecked(false);
                    myImg.setImageResource(R.drawable.icon_wifi_disabled);

                    break;
                }
                case WifiManager.WIFI_STATE_ENABLED:{
                    txtStatus.setText("Wifi is Enabled.");
                    switchWifi.setChecked(true);
                    myImg.setImageResource(R.drawable.icon_wifi_on);
                    break;
                }
                case WifiManager.WIFI_STATE_DISABLING:{
                    txtStatus.setText("Wifi is disabling...");
                    break;
                }
                case WifiManager.WIFI_STATE_ENABLING:{
                    txtStatus.setText("Wifi is Enabling...");
                    break;
                }
                case WifiManager.WIFI_STATE_UNKNOWN:{
                    txtStatus.setText("Wifi is UNKNOWN...");
                    break;
                }
            }
        }
    };

}