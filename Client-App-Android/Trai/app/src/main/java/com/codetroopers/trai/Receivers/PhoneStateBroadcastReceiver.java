package com.codetroopers.trai.Receivers;

/**
 * Created by Srujan Jha on 7/8/2016.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class PhoneStateBroadcastReceiver extends BroadcastReceiver {

    String file_name = "CallDrops.csv";
    String file_name1 = "CallAll.csv";
    private static final String TAG = "PhoneState:";
    Context mContext;
    String incoming_nr;
    private int prev_state;

    @Override
    public void onReceive(Context context, Intent intent) {
        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE); //TelephonyManager object
        CustomPhoneStateListener customPhoneListener = new CustomPhoneStateListener();
        telephony.listen(customPhoneListener, PhoneStateListener.LISTEN_CALL_STATE); //Register our listener with TelephonyManager

        Bundle bundle = intent.getExtras();
        String phoneNr= bundle.getString("incoming_number");
        Log.v(TAG, "phoneNr: "+phoneNr);
        mContext=context;
    }

    /* Custom PhoneStateListener */
    public class CustomPhoneStateListener  extends PhoneStateListener {

        private static final String TAG = "CustomPhone:";

        @Override
        public void onCallStateChanged(int state, String incomingNumber){

            if(incomingNumber!=null&&incomingNumber.length()>0) incoming_nr=incomingNumber;

            switch(state){
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.d(TAG, "CALL_STATE_RINGING");
                    prev_state=state;
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.d(TAG, "CALL_STATE_OFFHOOK");
                    prev_state=state;
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.d(TAG, "CALL_STATE_IDLE==>"+incoming_nr);
                    if((prev_state==TelephonyManager.CALL_STATE_OFFHOOK)){
                        prev_state=state;
                        Random rnd=new Random();
                        if(rnd.nextBoolean())
                        try {
                            Long tsLong = System.currentTimeMillis()/1000;
                            String ts = tsLong.toString();
                            FileWriter fw = new FileWriter(new File(mContext.getFilesDir(), file_name),true);
                            fw.write(ts+"\n");
                            fw.close();
                            System.out.println("Call Drop Recorded!");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            Long tsLong = System.currentTimeMillis()/1000;
                            String ts = tsLong.toString();
                            FileWriter fw = new FileWriter(new File(mContext.getFilesDir(), file_name1),true);
                            fw.write(ts+"\n");
                            fw.close();
                            System.out.println("All-Calls Recorded!");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if((prev_state==TelephonyManager.CALL_STATE_RINGING)){
                        prev_state=state;
                        try {
                            Long tsLong = System.currentTimeMillis()/1000;
                            String ts = tsLong.toString();
                            FileWriter fw = new FileWriter(new File(mContext.getFilesDir(), file_name1),true);
                            fw.write(ts+"\n");
                            fw.close();
                            System.out.println("All-Calls Recorded!");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

            }
        }
    }
}
