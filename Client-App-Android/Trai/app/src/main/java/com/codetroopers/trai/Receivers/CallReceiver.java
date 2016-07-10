package com.codetroopers.trai.Receivers;

/**
 * Created by Srujan Jha on 7/7/2016.
 */
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import android.os.Handler;
import java.util.Date;


public class CallReceiver extends PhonecallReceiver
{
    Context context;

    @Override
    protected void onIncomingCallStarted(final Context ctx, String number, Date start)
    {
        if(number.equals("+919010718698"))
            Toast.makeText(ctx,"Srujan Jha Incoming Call:"+ number,Toast.LENGTH_LONG).show();
        else Toast.makeText(ctx,"Unknown Incoming Call:"+ number,Toast.LENGTH_LONG).show();

        context =   ctx;

    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end)
    {
        if(number.equals("+919010718698"))
            Toast.makeText(ctx,"Srujan Jha Incoming Call Ended:"+ number,Toast.LENGTH_LONG).show();
        else Toast.makeText(ctx,"Unknown Incoming Call Ended:"+ number,Toast.LENGTH_LONG).show();
    }
}
