package com.codetroopers.trai.Receivers;

/**
 * Created by Srujan Jha on 7/7/2016.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;
import java.util.Date;

public abstract class PhonecallReceiver extends BroadcastReceiver
{
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        try
        {
            if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL"))
            {
                savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
            }
            else
            {
                String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
                String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
                // Check, whether this is a member of "Black listed" phone numbers stored in the database
                if( SP.getBoolean(number, false))
                {
                    System.out.println(number+"Rejected");
                    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    try {
                        Class c = Class.forName(tm.getClass().getName());
                        Method m = c.getDeclaredMethod("getITelephony");
                        m.setAccessible(true);
                        ITelephony telephonyService = (ITelephony) m.invoke(tm);
                        Bundle bundle = intent.getExtras();
                        String phoneNumber = bundle.getString("incoming_number");
                        Log.d("INCOMING", phoneNumber);
                        if ((phoneNumber != null)) {
                            telephonyService.endCall();
                            Log.d("HANG UP", phoneNumber);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }
                else System.out.println(number+"Accepted");
                int state = 0;
                if(stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE))
                {
                    state = TelephonyManager.CALL_STATE_IDLE;
                }
                else if(stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
                {
                    state = TelephonyManager.CALL_STATE_OFFHOOK;
                }
                else if(stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING))
                {
                    state = TelephonyManager.CALL_STATE_RINGING;
                }

                onCallStateChanged(context, state, number);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    //Derived classes should override these to respond to specific events of interest
    protected void onIncomingCallStarted(Context ctx, String number, Date start){}
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end){}

    public void onCallStateChanged(Context context, int state, String number)
    {
        if(lastState == state)
        {
            //No change, debounce extras
            return;
        }
        switch (state)
        {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;
                onIncomingCallStarted(context, number, callStartTime);
                break;

            case TelephonyManager.CALL_STATE_OFFHOOK:
                if (isIncoming)
                {
                    onIncomingCallEnded(context,savedNumber,callStartTime,new Date());
                }

            case TelephonyManager.CALL_STATE_IDLE:
                if(isIncoming)
                {
                    onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
                }
        }
        lastState = state;
    }
}