package com.codetroopers.trai.Adapters;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.trai.R;
import com.codetroopers.trai.Receivers.CallUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Srujan Jha on 7/7/2016.
 */
public class CustomListAdapter extends RecyclerView.Adapter<CustomListAdapter.CardViewHolder> {
    public static class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView Name, Number, Type, Duration, Date;
        ImageView thumbnail;

        CardViewHolder(View convertView) {
            super(convertView);
            thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
            Name = (TextView) convertView.findViewById(R.id.name);
            Number = (TextView) convertView.findViewById(R.id.number);
            Duration = (TextView) convertView.findViewById(R.id.duration);
            Date = (TextView) convertView.findViewById(R.id.date);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        /* Interface for handling clicks - both normal and long ones. */
        public interface ClickListener {

            /**
             * Called when the view is clicked.
             *
             * @param v view that is clicked
             * @param position of the clicked item
             * @param isLongClick true if long click, false otherwise
             */
            public void onClick(View v, int position, boolean isLongClick);

        }

        private ClickListener clickListener;

        /* Setter for listener. */
        public void setClickListener(ClickListener clickListener) {
            this.clickListener = clickListener;
        }

        @Override
        public void onClick(View v) {

            // If not long clicked, pass last variable as false.
            clickListener.onClick(v, getPosition(), false);
        }

        @Override
        public boolean onLongClick(View v) {

            // If long clicked, passed last variable as true.
            clickListener.onClick(v, getPosition(), true);
            return true;
        }
    }

    List<CallUser> searchItems = new ArrayList<CallUser>();
    private static Context mContext;

    public CustomListAdapter(List<CallUser> searchItems, Context context) {
        this.searchItems.clear();
        this.searchItems = searchItems;
        this.mContext = context;
    }

    @Override
    public int getItemCount() {
        return searchItems.size();
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.call_row, viewGroup, false);
        CardViewHolder pvh = new CardViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final CardViewHolder cardViewHolder, final int i) {
        // getting movie data for the row
        final CallUser m = searchItems.get(i);
        //System.out.println(m.phoneNumber);
        // thumbnail image
        int dircode = Integer.parseInt(m.callType);
        switch (dircode) {
            case CallLog.Calls.OUTGOING_TYPE:
                cardViewHolder.thumbnail.setImageResource(android.R.drawable.sym_call_outgoing);
                break;

            case CallLog.Calls.INCOMING_TYPE:
                cardViewHolder.thumbnail.setImageResource(android.R.drawable.sym_call_incoming);
                break;

            case CallLog.Calls.MISSED_TYPE:
                cardViewHolder.thumbnail.setImageResource(android.R.drawable.sym_call_missed);
                break;
            default:cardViewHolder.thumbnail.setImageResource(android.R.drawable.sym_call_missed);
                break;
        }

        cardViewHolder.Name.setText(m.userName);
        cardViewHolder.Number.setText(m.phoneNumber);
        cardViewHolder.Duration.setText(m.callDuration + " s");
        cardViewHolder.Date.setText(m.callDate);
        cardViewHolder.setClickListener(new CardViewHolder.ClickListener() {
            @Override
            public void onClick(final View v, final int pos, boolean isLongClick) {
                if (isLongClick) {
                    SharedPreferences SP1 = PreferenceManager.getDefaultSharedPreferences(mContext);
                    int menu_id=R.menu.menu_call;
                    if(SP1.getBoolean(m.phoneNumber, false)){menu_id=R.menu.menu_call_d;}

                    //Creating the instance of PopupMenu
                    PopupMenu popup = new PopupMenu(mContext, v);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(menu_id, popup.getMenu());
                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.menu_dnd) {
                                SharedPreferences SP1 = PreferenceManager.getDefaultSharedPreferences(mContext);
                                if(SP1.getBoolean(m.phoneNumber, true)){
                                SharedPreferences.Editor editor1 = SP1.edit();
                                editor1.putBoolean(m.phoneNumber,false);
                                editor1.apply();
                                    System.out.println(m.phoneNumber+"UnBlocked");
                                    //Toast.makeText(mContext, "The number has been blocked." + v.getTag().toString(), Toast.LENGTH_LONG).show();
                                }
                                else {
                                    SharedPreferences.Editor editor1 = SP1.edit();
                                    editor1.putBoolean(m.phoneNumber,true);
                                    editor1.apply();
                                    System.out.println(m.phoneNumber+"Blocked");
                                    //Toast.makeText(mContext, "The number has been unblocked." + v.getTag().toString(), Toast.LENGTH_LONG).show();
                                }
                            } else if (item.getItemId() == R.id.menu_call) {
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:" + m.phoneNumber));
                                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    System.out.println("NOT_ALLOWED");
                                }
                                mContext.startActivity(callIntent);
                            }
                            if (item.getItemId() == R.id.menu_save) {
                                if(searchItems.get(pos).userName.equals("?")) {String DisplayName = "Srujan Jha";
                                    if(!searchItems.get(pos).phoneNumber.equals("+919010718698"))
                                    DisplayName="Unknown";
                                    String MobileNumber = searchItems.get(pos).phoneNumber;
                                    ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

                                    ops.add(ContentProviderOperation.newInsert(
                                            ContactsContract.RawContacts.CONTENT_URI)
                                            .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                                            .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                                            .build());

                                    //------------------------------------------------------ Names
                                    if (DisplayName != null) {
                                        ops.add(ContentProviderOperation.newInsert(
                                                ContactsContract.Data.CONTENT_URI)
                                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                                .withValue(ContactsContract.Data.MIMETYPE,
                                                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                                                .withValue(
                                                        ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                                        DisplayName).build());
                                    }

                                    //------------------------------------------------------ Mobile Number
                                    if (MobileNumber != null) {
                                        ops.add(ContentProviderOperation.
                                                newInsert(ContactsContract.Data.CONTENT_URI)
                                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                                .withValue(ContactsContract.Data.MIMETYPE,
                                                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MobileNumber)
                                                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                                                .build());
                                    }
                                    // Asking the Contact provider to create a new contact
                                    try {
                                        mContext.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                                        Toast.makeText(mContext, "Contact Added:" + DisplayName, Toast.LENGTH_LONG).show();
                                        searchItems.get(i).userName = DisplayName;
                                        cardViewHolder.Name.setText(DisplayName);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(mContext, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            return true;
                        }
                    });
                    popup.show();//showing popup menu

                } else {
                }
            }
        });
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
