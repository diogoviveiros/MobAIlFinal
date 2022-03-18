package com.example.mobailfinal;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter adapter;
    private ListView lv;

    private int REQUEST_BLUETOOTH = 1;
    private int count = -1;

    private ArrayList<DeviceItem> deviceList = new ArrayList<DeviceItem>();
    private ArrayList<String> sortedDeviceNameList = new ArrayList<String>();
    private ArrayList<DeviceItem> sortedDeviceList = new ArrayList<DeviceItem>();
    private HashMap<String, Integer> h = new HashMap<String, Integer>();
    TreeMap<String,Integer> sorted_map = new TreeMap<String,Integer>();

    private String channel_id = "MobAIlFinal";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            CSVReader reader = new CSVReader(new FileReader("test.csv"));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {

                deviceList.add(new DeviceItem(nextLine[0], nextLine[1], Integer.parseInt(nextLine[2]), new Date(Date.parse(nextLine[3]))));
                String[] tokens = nextLine[0].split(" ");
                int num = Integer.parseInt(tokens[1]);

                if(count < num) {
                    count = num;
                }





            }
        } catch (IOException e) {

        }



        /*
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, channel_id)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Device Found Extremely Close to you")
                .setContentText("Device " + "found with signal strength of " + -48 + " dBm")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Device " + "found with signal strength of " + -48 + " dBm"))
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationChannel mChannel = new NotificationChannel(channel_id, "General Notifications", NotificationManager.IMPORTANCE_HIGH);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        manager.createNotificationChannel(mChannel);
        manager.notify(8909, builder.build());
    */

        adapter = BluetoothAdapter.getDefaultAdapter();
        lv = (ListView) findViewById(R.id.list_devices);
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

                    //Log.d("running...", "device");


                    sorted_map.putAll(h);

                    //  Get all entries using the entrySet() method
                    Set<Map.Entry<String, Integer> > entries
                            = sorted_map.entrySet();

                    // Way 1
                    // Using for loops
                    sortedDeviceNameList.clear();

                    for (Map.Entry<String, Integer> entry : entries) {


                        //sortedDeviceNameList.add(entry.getKey());
                        String name = "";
                        String RSSI = "-78";
                        String signalQuality = "Okay";


                        for(DeviceItem device: deviceList){
                            if(entry.getKey().equals(device.getHash())){
                                name = device.getDeviceName();
                                RSSI = device.getSignalStrength();
                                if (device.signalStrength() < -80){
                                    signalQuality = "Poor";
                                }else if(device.signalStrength() < -50 &&device.signalStrength() > -80){
                                    signalQuality = "Okay";
                                }else{
                                    signalQuality = "Great";
                                }
                            }
                        }

                        sortedDeviceNameList.add(name + " - Signal: " + RSSI +"dBm (" + signalQuality +")");
                        count++;

                    }

                    ArrayAdapter<String> ad = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, sortedDeviceNameList);

                    lv.setAdapter(ad);
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent myIntent = new Intent(view.getContext(), DeviceInfo.class);
                            myIntent.putExtra("name", sortedDeviceNameList.get(i));
                            Log.d("ArrayInfo", "HashMap is: " + entries.toString());
                            for (DeviceItem device: deviceList) {

                                Log.d("ArrayInfo", "Device hash is: " + device.getHash());

                                for(Map.Entry<String, Integer> entry : entries) {

                                    if (entry.getKey().equals(device.getHash())) {
                                        sortedDeviceList.add(device);
                                    }
                                }

                            }

                            Bundle args = new Bundle();
                            args.putSerializable("Arraylist", (Serializable) sortedDeviceList);
                            myIntent.putExtra("Bundle", args);
                            startActivity(myIntent);



                        }
                    });



                    ha.postDelayed(this, 5000);
                }
            }, 5000);



        }






    }


    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = "";
                String deviceHardwareAddress = device.getAddress(); // MAC address
                String hash = getHash(deviceHardwareAddress);
                int  rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);

                if(h.containsKey(hash)){

                    for(DeviceItem name : deviceList){

                        if(name.getHash().equals(hash)){
                            deviceName = name.getDeviceName();
                        }

                    }

                }else{
                    count = count + 1;
                    deviceName = "Device " + count;

                }

                DeviceItem newDevice= new DeviceItem(deviceName,hash,rssi, new Date(System.currentTimeMillis()));
                deviceList.add(newDevice);
                Log.d(newDevice.toString(), "device");
                try {


                    String entry = newDevice.getDeviceName() + ","
                            + newDevice.getHash() + ","
                            + newDevice.getSignalStrength() + "," +
                            newDevice.getTime() + "\n";
                    // adding header to csv
                    FileOutputStream out = openFileOutput("test.csv", Context.MODE_APPEND);

                    out.write(entry.getBytes());
                    Log.d("WRITING TO CSV FILE", "csv");
                    out.close();
                }
                catch (IOException e) {

                    e.printStackTrace();
                }

                if(rssi > -49){


                    NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, channel_id)
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setContentTitle("Device Found Extremely Close to you")
                            .setContentText(deviceName + " found with signal strength of " + rssi + " dBm")
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(deviceName + " found with signal strength of " + rssi + " dBm"))
                            .setPriority(NotificationCompat.PRIORITY_MAX);

                    NotificationChannel mChannel = new NotificationChannel(channel_id, "General Notifications", NotificationManager.IMPORTANCE_HIGH);

                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    manager.createNotificationChannel(mChannel);
                    manager.notify(8909, builder.build());


                }


                // NEW
                // check if deviceHardwareAddress in HashMap of deviceHardwareAddresses seen in the last 15 minutes
                //based on LocalDateTime.now -> ask if this is staggered or reset every 15 minutes.

                if (h.containsKey(hash)) {
                       h.put(hash, h.get(hash) + 1);
                }else{
                       h.put(hash, 1);
                }

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

    public static String getHash(String hash) {
        try {
            MessageDigest digest = null;
            try {
                digest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e1) {
                e1.printStackTrace();
            }
            digest.reset();
            return bin2hex(digest.digest(hash.getBytes()));
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String bin2hex(byte[] data) {
        StringBuilder hex = new StringBuilder(data.length * 2);
        for (byte b : data)
            hex.append(String.format("%02x", b & 0xFF));
        return hex.toString();
    }


}