package com.example.arduino_led_bluetooth;


import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Button btnShow;
    private ImageView imgGreenOn, imgGreenOff, imgRedOn, imgRedOff;
    private ProgressBar progressBar;
    private SeekBar seekBar;
    private String deviceName = null;
    private String deviceAddress;
    private Intent bluetootlEnableIntent;
    private int requestBluetoothEnabled = 1;
    byte komanda = 0;
    boolean isConnected = false;

    public static Handler handler;
    public static BluetoothSocket btSocket;
    public static CommunicationThread communicationThread;
    public static ConnectThread connectThread;

    private final static int CONNECTING_STATUS = 1; // used in bluetooth handler to identify message status
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        bluetootlEnableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

        //If a bluetooth device has been selected from SelectDeviceActivity
        deviceName = getIntent().getStringExtra("name");
        if (deviceName != null) {
            deviceAddress = getIntent().getStringExtra("addr");
            progressBar.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Connecting to " + deviceName + "...", Toast.LENGTH_SHORT).show();

            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            connectThread = new ConnectThread(bluetoothAdapter, deviceAddress);
            connectThread.start();
        }

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case CONNECTING_STATUS:
                        switch (msg.arg1) {
                            case 1:
                                Toast.makeText(MainActivity.this, "Connected to " + deviceName, Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                                isConnected = true;
                                break;
                            case -1:
                                Toast.makeText(MainActivity.this, "Connection failed", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                                break;
                        }
                        break;
                    case MESSAGE_READ:
                        byte progress = (byte) msg.obj;
                        seekBar.setProgress(progress);
                        break;
                }
            }
        };

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Move to activity with list of devices
                Intent intent = new Intent(MainActivity.this, SelectDeviceActivity.class);
                startActivity(intent);
            }
        });

        imgGreenOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected) {
                    communicationThread.write(komanda);
                    communicationThread.write("green on\n");
                }
            }
        });

        imgGreenOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected) {
                    communicationThread.write(komanda);
                    communicationThread.write("green off\n");
                }
            }
        });

        imgRedOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected) {
                    communicationThread.write(komanda);
                    communicationThread.write("red on\n");
                }
            }
        });

        imgRedOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected) {
                    communicationThread.write(komanda);
                    communicationThread.write("red off\n");
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (isConnected) {
                    byte x = (byte) progress;
                    communicationThread.write(x);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


    private void initViews() {
        btnShow = findViewById(R.id.btnList);
        imgGreenOff = findViewById(R.id.iv3);
        imgGreenOn = findViewById(R.id.iv1);
        imgRedOn = findViewById(R.id.iv2);
        imgRedOff = findViewById(R.id.iv4);
        seekBar = findViewById(R.id.bar);
        progressBar = findViewById(R.id.pb);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == requestBluetoothEnabled && resultCode == RESULT_OK) {
            Toast.makeText(this, "Bluetooth is enabled!", Toast.LENGTH_SHORT).show();
            //showDevicesList();
        } else {
            Toast.makeText(this, "You have to enable bluetooth!", Toast.LENGTH_SHORT).show();
        }
    }


    /****************** Thread to create Bluetooth connection *******************************/
    public class ConnectThread extends Thread {

        public ConnectThread(BluetoothAdapter bluetoothAdapter, String address) {
            /*
            Use a temporary object that is later assigned to mmSocket
            because mmSocket is final.
             */
            BluetoothSocket tmp = null;
            BluetoothDevice btDevice = bluetoothAdapter.getRemoteDevice(address);

            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

            }
            UUID uuid = btDevice.getUuids()[0].getUuid();

            try{
                tmp = btDevice.createInsecureRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                Log.d("Tag", "Socket's create() method failed", e);
            }

            btSocket = tmp;
        }

        public void run(){
            // Cancel discovery because it otherwise slows down the connection.
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

            }
            bluetoothAdapter.cancelDiscovery();

            try {
                btSocket.connect();
                Log.d("Status", "Device connected!");
                handler.obtainMessage(CONNECTING_STATUS, 1, -1).sendToTarget();
            }
            catch (IOException e){
                // Unable to connect. Close the socket and return
                try {
                    btSocket.close();
                    Log.d("Status", "Can't connect to device...");
                    handler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
                }catch (IOException closeException){
                    Log.d("Status", "Could not close client socket", closeException);
                }

                return;
            }

            // The connection attempt succeeded. Perform work associated with the connection in a separate thread
            communicationThread = new CommunicationThread(btSocket);
            communicationThread.start();
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel(){
            try{
                btSocket.close();
            }catch (IOException e){
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }


    /************************ Thread for data transfer ***********************************/
    public static class CommunicationThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public CommunicationThread(BluetoothSocket socket){
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try{
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            }catch (IOException e){

            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        @Override
        public void run() {
            byte received = 0;

            while(true){
                try {
                    //Read from the InputStream from Arduino. Then send the message to GUI Handler.
                    received = (byte) mmInStream.read();
                    handler.obtainMessage(MESSAGE_READ, received).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        //Send data to remote device
        public void write(char input){
            try{
                mmOutStream.write(input);
            }catch(IOException e){
                Log.e("Send Error","Unable to send message",e);
            }
        }

        //Send data to remote device
        public void write(byte input){
            try {
                mmOutStream.write(input);
            }catch (IOException e){
                Log.d("Send Error", "Unable to send message", e);
            }
        }

        public void write(String input){
            byte[] bytes = input.getBytes(); //convert entered string into bytes
            try{
                mmOutStream.write(bytes);
            }
            catch (IOException e){
                Log.d(TAG, "Send Error", e);
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }


    @Override
    public void onBackPressed() {
        // Terminate Bluetooth Connection and close app
        super.onBackPressed();
        if(connectThread != null){
            connectThread.cancel();
        }
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}