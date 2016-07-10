package com.codetroopers.trai.Activities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.codetroopers.trai.Adapters.CustomListAdapter;
import com.codetroopers.trai.R;
import com.codetroopers.trai.Receivers.CallUser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TrueCaller extends MainActivity {
    private CustomListAdapter adapter;
    private RecyclerView mRecyclerView;
    String file_name = "CallDrops.csv";
    String file_name1 = "CallAll.csv";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.activity_true_caller, frameLayout);
        getSupportActionBar().setTitle("Call History");
        mRecyclerView = (RecyclerView) findViewById(R.id.rvCategoriesCard);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.card_spacing);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        adapter = new CustomListAdapter(getCallDetails(), TrueCaller.this);
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        try {
            FileWriter fw = new FileWriter(new File(getFilesDir(), file_name),true);
            fw.close();
            fw = new FileWriter(new File(getFilesDir(), file_name1),true);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private List<CallUser> getCallDetails() {
        try {
            StringBuffer sb = new StringBuffer();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                System.out.println("No Permission");
            }
            Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                    null, null, null);
            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            sb.append("Call Details :");
            List<CallUser> users=new ArrayList<>();
            int i=0;
            while (managedCursor.moveToNext()) {
                CallUser usr=new CallUser();
                usr.phoneNumber = managedCursor.getString(number);
                usr.callType = managedCursor.getString(type);
                usr.callDate = managedCursor.getString(date);
                Date callDayTime = new Date(Long.valueOf(usr.callDate));
                usr.callDate=callDayTime.toString();
                usr.callDuration = managedCursor.getString(duration);
                usr.userName=getContactDisplayNameByNumber(usr.phoneNumber);
                users.add(usr);i++;
                if(i>=10)break;
            }
            managedCursor.close();
            return users;}
        catch (Exception e){}
        return null;

    }
    public String getContactDisplayNameByNumber(String number) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String name = "?";

        ContentResolver contentResolver = getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, new String[] {BaseColumns._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                //String contactId = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        return name;
    }

}
