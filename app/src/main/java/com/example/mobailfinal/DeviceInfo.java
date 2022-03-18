package com.example.mobailfinal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class DeviceInfo extends AppCompatActivity {

    GraphView graphView;
    private ArrayList<DeviceItem> deviceList;
    private ArrayList<DeviceItem> currentDeviceList = new ArrayList<DeviceItem>();
    private String deviceName;
    private TextView text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ArrayInfo", "test");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        Bundle b = getIntent().getExtras();
        text = findViewById(R.id.textView3);


        graphView = findViewById(R.id.idGraphView);


        if(null != b){
            Bundle args = getIntent().getBundleExtra("Bundle");
            deviceList = (ArrayList<DeviceItem>)args.getSerializable("Arraylist");
            deviceName = b.getString("name");
        }

        String[] tokens = deviceName.split("-");

        tokens = tokens[0].split(" ");

        deviceName = tokens[0] + " " + tokens[1];

        text.setText(deviceName);




        try {
            CSVReader reader = new CSVReader(new FileReader("test.csv"));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {

                if(nextLine[0].equals(deviceName)){
                    currentDeviceList.add(new DeviceItem(deviceName, nextLine[1], Integer.parseInt(nextLine[2]), new Date(Date.parse(nextLine[2]))));
                }

            }
        } catch (IOException e) {

        }


        DataPoint[] data = new DataPoint[currentDeviceList.size()];
        Collections.sort(deviceList, new Comparator<DeviceItem>() {
            @Override
            public int compare(DeviceItem deviceItem, DeviceItem t1) {
                return deviceItem.getTime().compareTo(t1.getTime());
            }
        });

        for(int i = 0; i < currentDeviceList.size(); i++){
            data[i] = new DataPoint(currentDeviceList.get(i).getTime(), (double) currentDeviceList.get(i).signalStrength()) ;
        }




        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(data);

        graphView.setTitle(deviceName);

        graphView.setTitleColor(R.color.teal_200);

        graphView.setTitleTextSize(20);

        graphView.addSeries(series);


        GridLabelRenderer gridLabel = graphView.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Time");
        gridLabel.setVerticalAxisTitle("Signal Strength (dBm)");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}