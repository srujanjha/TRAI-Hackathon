package com.codetroopers.trai.Activities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codetroopers.trai.R;
import com.codetroopers.trai.Utils.GPSTracker;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class CallDrops extends MainActivity {

    String file_name = "CallDrops.csv";
    String file_name1 = "CallAll.csv";
    Button button2, button3,button4;
    PieChart pieChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_call_drops, frameLayout);
        getSupportActionBar().setTitle("CallDrops");
        pieChart = (PieChart) findViewById(R.id.chart);
        pieChart.setUsePercentValues(true);
        pieChart.setExtraOffsets(5, 5, 5, 5);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setCenterText(generateCenterSpannableText());
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(43f);
        pieChart.setRotationEnabled(false);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setDrawCenterText(true);
        button2 = (Button) findViewById(R.id.btnReset);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    FileWriter fw = new FileWriter(new File(getApplicationContext().getFilesDir(), file_name));
                    fw.close();fw = new FileWriter(new File(getApplicationContext().getFilesDir(), file_name1));
                    fw.close();
                    long[] datnet = {0,0};
                    makePieChart(datnet, pieChart);
                    pieChart.notifyDataSetChanged();
                    pieChart.invalidate();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        button3 = (Button) findViewById(R.id.btnRefresh);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long[] datnet = {0,0};
                datnet = makeDataNet();
                makePieChart(datnet, pieChart);
                pieChart.notifyDataSetChanged();
                pieChart.invalidate();
            }
        });
        button4 = (Button) findViewById(R.id.btnSend);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long[] datnet = {0,0};
                datnet = makeDataNet();
                MobileServiceClient mClient;
                try {
                    mClient = new MobileServiceClient("https://trai.azure-mobile.net/", "sKVAklhSawgSCrrQHfOfSDnYqVUiOR89", view.getContext());

                    final com.codetroopers.trai.Utils.CallDrops item = new com.codetroopers.trai.Utils.CallDrops();
                    String mPhoneNumber = "+918464813752";
                    try {
                        TelephonyManager tMgr = (TelephonyManager) view.getContext().getSystemService(Context.TELEPHONY_SERVICE);
                        mPhoneNumber = tMgr.getLine1Number();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (mPhoneNumber.equals("")) mPhoneNumber = "+918464813752";
                    item.Number = mPhoneNumber;
                    GPSTracker gps = new GPSTracker(view.getContext());
                    if(gps.canGetLocation())item.Area=gps.getLatitude()+","+gps.getLongitude();
                    else {gps.showSettingsAlert();return;}
                    item.C_cd=datnet[0];
                    item.C_ac=datnet[1];
                    TelephonyManager manager = (TelephonyManager) view.getContext().getSystemService(Context.TELEPHONY_SERVICE);
                    item.Provider= manager.getNetworkOperatorName();
                    mClient.getTable(com.codetroopers.trai.Utils.CallDrops.class).insert(item, new TableOperationCallback<com.codetroopers.trai.Utils.CallDrops>() {
                        public void onCompleted(com.codetroopers.trai.Utils.CallDrops entity, Exception exception, ServiceFilterResponse response) {
                            if (exception == null) {
                                try {
                                    FileWriter fw = new FileWriter(new File(getApplicationContext().getFilesDir(), file_name));
                                    fw.close();
                                    Toast.makeText(getApplicationContext(),"Report sent!",Toast.LENGTH_LONG).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                            }
                        }
                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });

        long[] datnet = {0,0};
        datnet = makeDataNet();
        makePieChart(datnet, pieChart);
    }
    private SpannableString generateCenterSpannableText() {
        SpannableString s = new SpannableString("Call-Drops\nAll-Calls");
        s.setSpan(new RelativeSizeSpan(1.5f), 0, 12, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 12, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 12, s.length(), 0);
        s.setSpan(new RelativeSizeSpan(.8f), 12, s.length(), 0);
        return s;
    }

    private void makePieChart(long[] datnet2, PieChart pieChart)
    {
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(datnet2[0], 0));
        entries.add(new Entry(datnet2[1], 1));

        System.out.println("CallDrops: " + datnet2[0]);
        System.out.println("AllCalls: " + datnet2[1]);

        PieDataSet dataset = new PieDataSet(entries, "# of Calls");
        dataset.setSliceSpace(3f);
        dataset.setSelectionShift(5f);

        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        dataset.setColors(colors);

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("Call-Drops");
        labels.add("All-Calls");
        PieData data = new PieData(labels, dataset);
        data.setValueTextSize(20f);
        pieChart.setData(data);
        pieChart.setDescription("Call-Drop Analysis");
    }

    long[] makeDataNet()
    {
        long[] datnet = new long[2];
        for(int i=0;i<2;i++)
            datnet[i] = 0;
        BufferedReader br = null;
        BufferedReader br1 = null;
        try {
            String line1 = "";
            br = new BufferedReader(new FileReader(new File(getApplicationContext().getFilesDir(), file_name)));
            br1 = new BufferedReader(new FileReader(new File(getApplicationContext().getFilesDir(), file_name1)));
            line1 = br.readLine();
            int ld=1;
            while (line1 != null) {
                System.out.println(line1);
                line1 = br.readLine();
                ld++;
            }
            int la=1;line1 = br1.readLine();
            while (line1 != null) {
                System.out.println(line1);
                line1 = br1.readLine();
                la++;
            }
            datnet[0] = ld;
            datnet[1]=la;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return datnet;
    }

}