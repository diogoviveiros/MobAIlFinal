package com.example.mobailfinal;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter adapter;

    private int REQUEST_BLUETOOTH = 1;
    private ArrayList<DeviceItem> deviceList = new ArrayList<DeviceItem>();
    //FileOutputStream fos = openFileOutput("devices.data", Context.MODE_PRIVATE);
    //ObjectOutputStream os = new ObjectOutputStream(fos);

    public MainActivity() throws IOException {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        adapter = BluetoothAdapter.getDefaultAdapter();
        // Phone does not support Bluetooth so let the user know and exit.
        if (adapter == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        if (!adapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_BLUETOOTH);
        }else{


            if(this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION") != PackageManager.PERMISSION_GRANTED || this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION") != PackageManager.PERMISSION_GRANTED){

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1000);

            }

            adapter.enable();

            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(receiver, filter);
            adapter.startDiscovery();

            final Handler ha=new Handler();
            ha.postDelayed(new Runnable() {

                @Override
                public void run() {

                    Log.d("running...", "device");


                    /*
                    if (adapter.isDiscovering()) {
                        adapter.disable();
                        unregisterReceiver(receiver);
                        adapter.cancelDiscovery();
                    }

                    adapter.enable();
                    registerReceiver(receiver, filter);
                    adapter.startDiscovery();
                    */



                    if(deviceList.size() > 0){
                        for(DeviceItem device: deviceList){


                           Log.d(device.toString(), "device");


                        }
                    }

                    ha.postDelayed(this, 5000);
                }
            }, 5000);



        }






    }


    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onReceive(Context context, Intent intent) {
            // public void onReceive(Context context, Intent intent, ArrayList deviceHardwareAddresses) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                int  rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                DeviceItem newDevice= new DeviceItem(deviceName,deviceHardwareAddress,"false", rssi, LocalDateTime.now());
                deviceList.add(newDevice);
                Log.d(newDevice.toString(), "device1");

                // NEW
                // check if deviceHardwareAddress in HashMap of deviceHardwareAddresses seen in the last 15 minutes
                // based on LocalDateTime.now -> ask if this is staggered or reset every 15 minutes.
                // if deviceHardwareAddresses.containsKey(deviceHardwareAddress) {
                //       deviceHardwareAddresses[deviceHardwareAddress] += 1;
                // }
                // // print list of keys with value > 10 // this is an arbitrary choice rn
                // END NEW
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
        adapter.cancelDiscovery();

    }


}