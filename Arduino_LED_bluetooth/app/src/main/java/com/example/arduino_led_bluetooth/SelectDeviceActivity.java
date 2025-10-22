package com.example.arduino_led_bluetooth;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SelectDeviceActivity extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_device);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        getPairedDevicesList();
    }

    private void getPairedDevicesList() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        List<DeviceModel> deviceList = new ArrayList<>();
        if(pairedDevices.size() > 0){
            for(BluetoothDevice bt : pairedDevices){
                String deviceName = bt.getName();
                String deviceAddr = bt.getAddress();
                DeviceModel model = new DeviceModel(deviceName, deviceAddr);
                deviceList.add(model);
            }

            RecyclerView recyclerView = findViewById(R.id.rv);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            MyAdapter adapter = new MyAdapter(this, deviceList);
            recyclerView.setAdapter(adapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }
        else{
            View view = findViewById(R.id.rv);
            Snackbar snackbar = Snackbar.make(view, "Activate bluetooth or pair a bluetooth device", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            snackbar.show();
        }
    }
}