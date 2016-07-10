package com.codetroopers.trai.Activities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.codetroopers.trai.Receivers.NetworkType;
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

public class SpeedTest extends MainActivity {

    String file_name = "NetworkHistory.csv";
    Button button2, button3, button4;
    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_speedtest, frameLayout);
        getSupportActionBar().setTitle("SpeedTest");
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
                    Long tsLong = System.currentTimeMillis() / 1000;
                    String ts = tsLong.toString();
                    if (isNetworkConnected(getApplicationContext())) {
                        String ntype = new NetworkType().getNetworkType(getApplicationContext());
                        fw.write(ts + "," + ntype + "\n");
                    } else
                        fw.write(ts + "," + "NC" + "\n");
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Thread thread = new Thread();
                try {
                    thread.sleep(1001);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                long[] datnet = new long[4];
                datnet = makeDataNet();
                makePieChart(datnet, pieChart);
                pieChart.notifyDataSetChanged();
                pieChart.invalidate();
            }
        });

        button3 = (Button) findViewById(R.id.btnRefresh);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long[] datnet = new long[4];
                datnet = makeDataNet();
                System.out.println("NC: " + datnet[0]);
                System.out.println("2g: " + datnet[1]);
                System.out.println("3g: " + datnet[2]);
                System.out.println("4g: " + datnet[3]);
                makePieChart(datnet, pieChart);
                pieChart.notifyDataSetChanged();
                pieChart.invalidate();
            }
        });
        button4 = (Button) findViewById(R.id.btnSend);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long[] datnet = new long[4];
                datnet = makeDataNet();
                System.out.println("NC: " + datnet[0]);
                System.out.println("2g: " + datnet[1]);
                System.out.println("3g: " + datnet[2]);
                System.out.println("4g: " + datnet[3]);
                MobileServiceClient mClient;
                try {
                    mClient = new MobileServiceClient("https://trai.azure-mobile.net/", "sKVAklhSawgSCrrQHfOfSDnYqVUiOR89", view.getContext());

                final com.codetroopers.trai.Utils.SpeedTest item = new com.codetroopers.trai.Utils.SpeedTest();
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
                    TelephonyManager manager = (TelephonyManager) view.getContext().getSystemService(Context.TELEPHONY_SERVICE);
                    item.Provider= manager.getNetworkOperatorName();
                mClient.getTable(com.codetroopers.trai.Utils.SpeedTest.class).insert(item, new TableOperationCallback<com.codetroopers.trai.Utils.SpeedTest>() {
                    public void onCompleted(com.codetroopers.trai.Utils.SpeedTest entity, Exception exception, ServiceFilterResponse response) {
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

        long[] datnet = new long[4];
        datnet = makeDataNet();
        makePieChart(datnet, pieChart);
    }
    private SpannableString generateCenterSpannableText() {
        SpannableString s = new SpannableString("Network Type\nmobile data");
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
        entries.add(new Entry(datnet2[2], 2));
        entries.add(new Entry(datnet2[3], 3));

        System.out.println("NC: " + datnet2[0]);
        System.out.println("2g: " + datnet2[1]);
        System.out.println("3g: " + datnet2[2]);
        System.out.println("4g: " + datnet2[3]);

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
        labels.add("NC");
        labels.add("2g");
        labels.add("3g");
        labels.add("4g");
        PieData data = new PieData(labels, dataset);
        data.setValueTextSize(20f);
        pieChart.setData(data);
        pieChart.setDescription("Network Type Analysis");
//        pieChart.notify();
        //pieChart.notifyAll();
    }

    long[] makeDataNet()
    {
        long[] datnet = new long[4];
        for(int i=0;i<4;i++)
            datnet[i] = 0;
        BufferedReader br = null;
        String cvsSplitBy = ",";
        try {
            String line1 = "";
            String line2 = "";
            br = new BufferedReader(new FileReader(new File(getApplicationContext().getFilesDir(), file_name)));
            line1 = br.readLine();
            while (line1 != null && line2 != null) {
                line2 = br.readLine();
                if(line2 == null)
                    break;
                String[] tsdat1 = line1.split(cvsSplitBy);
                String[] tsdat2 = line2.split(cvsSplitBy);
                long l1 = Long.parseLong(tsdat1[0]);
                long l2 = Long.parseLong(tsdat2[0]);
                if(tsdat1[1].equals("NC"))
                    datnet[0] = datnet[0] + (l2 - l1);
                else if(tsdat1[1].equals("2g"))
                    datnet[1] = datnet[1] + (l2 - l1);
                else if(tsdat1[1].equals("3g"))
                    datnet[2] = datnet[2] + (l2 - l1);
                else
                    datnet[3] = datnet[3] + (l2 - l1);
                //System.out.println("l1: " + l1 + " l2: " + l2);
                long min = (l2 - l1)/60;
                long sec = (l2 - l1)%60;
                System.out.println("Time Active: " + min + " Minutes and " + sec + " Seconds" + " , Signal: " + tsdat1[1]);
                line1 = line2;
            }
            if(line2 == null)
            {
                String[] tsdat1 = line1.split(cvsSplitBy);
                long l1 = Long.parseLong(tsdat1[0]);
                long l2 = System.currentTimeMillis()/1000;
                if(tsdat1[1].equals("NC"))
                    datnet[0] = datnet[0] + (l2 - l1);
                else if(tsdat1[1].equals("2g"))
                    datnet[1] = datnet[1] + (l2 - l1);
                else if(tsdat1[1].equals("3g"))
                    datnet[2] = datnet[2] + (l2 - l1);
                else
                    datnet[3] = datnet[3] + (l2 - l1);
                long min = (l2 - l1)/60;
                long sec = (l2 - l1)%60;
                System.out.println("Time Active: " + min + " Minutes and " + sec + " Seconds" + " , Signal: " + tsdat1[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return datnet;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}