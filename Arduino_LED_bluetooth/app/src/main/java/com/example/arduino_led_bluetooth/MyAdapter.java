package com.example.arduino_led_bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {

    Context context;
    List<DeviceModel> list;

    public MyAdapter(Context context, List<DeviceModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyHolder holder, int position) {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {

        }
        holder.tvName.setText(list.get(position).getDeviceName());
        holder.tvAddress.setText(list.get(position).getDeviceAddress());

        //when a device is selected
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("name", list.get(position).getDeviceName());
                intent.putExtra("addr", list.get(position).getDeviceAddress());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(list == null){
            return 0;
        }
        return list.size();
    }

    public void setList(ArrayList<DeviceModel> devices){
        list = devices;
        notifyDataSetChanged();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        private TextView tvName, tvAddress;
        private LinearLayout layout;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvDeviceName);
            tvAddress = itemView.findViewById(R.id.tvDeviceAddress);
            layout = itemView.findViewById(R.id.llDeviceInfo);

        }
    }
}

