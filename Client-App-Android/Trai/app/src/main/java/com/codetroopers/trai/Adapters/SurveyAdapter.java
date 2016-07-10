package com.codetroopers.trai.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.codetroopers.trai.R;
import com.codetroopers.trai.Utils.Questions;
import com.codetroopers.trai.Utils.Response;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;

import java.net.MalformedURLException;
import java.util.ArrayList;

/**
 * Created by Srujan Jha on 7/7/2016.
 */
public class SurveyAdapter extends RecyclerView.Adapter<SurveyAdapter.CardViewHolder>{
    public static class CardViewHolder extends RecyclerView.ViewHolder{
        TextView Name;
        Button Yes,No,DKnow;
        MobileServiceClient mClient;
        CardViewHolder(View convertView) {
            super(convertView);
            try {
                mClient = new MobileServiceClient("https://trai.azure-mobile.net/","sKVAklhSawgSCrrQHfOfSDnYqVUiOR89",convertView.getContext());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Name = (TextView) convertView.findViewById(R.id.name);
            Yes = (Button) convertView.findViewById(R.id.btnYes);
            No = (Button) convertView.findViewById(R.id.btnNo);
            DKnow = (Button) convertView.findViewById(R.id.btnDntKnow);
        }
    }
    ArrayList<Questions> searchItems=new ArrayList<>();
    private static Context mContext;
    public SurveyAdapter(ArrayList<Questions> searchItems, Context context) {
        this.searchItems.clear();
        this.searchItems=searchItems;
        this.mContext=context;
    }
    private void updateAdapter() {
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return searchItems.size();
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.survey_row, viewGroup, false);
        CardViewHolder pvh = new CardViewHolder(v);
        return pvh;
    }
    Questions m=new Questions();
    public void Respond(int x,Questions q,MobileServiceClient mClient,final int i)
    {
        System.out.println(q.Question);
        String mPhoneNumber="+918464813752";
        try{TelephonyManager tMgr = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
        mPhoneNumber = tMgr.getLine1Number();
        }catch (Exception e){e.printStackTrace();}
        if(mPhoneNumber.equals(""))mPhoneNumber="+918464813752";
        Response item = new Response();
        item.Answer = x;
        item.QuestionId=q.Id;
        item.Number=mPhoneNumber;
        mClient.getTable(Response.class).insert(item, new TableOperationCallback<Response>() {
            public void onCompleted(Response entity, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    // Insert succeeded
                } else {
                    // Insert failed
                }
            }
        });
        q.Done=true;
        mClient.getTable(Questions.class).update(q, new TableOperationCallback<Questions>() {
            public void onCompleted(Questions entity, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    searchItems.remove(i);
                    updateAdapter();
                } else {
                    // Insert failed
                }
            }
        });
    }
    @Override
    public void onBindViewHolder(final CardViewHolder cardViewHolder, final int i) {
        // getting movie data for the row
        m=searchItems.get(i);
        cardViewHolder.Name.setText(m.Question);
        cardViewHolder.Yes.setText(m.Option1);
        cardViewHolder.No.setText(m.Option2);
        cardViewHolder.DKnow.setText(m.Option3);
        cardViewHolder.Yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Respond(0,searchItems.get(i),cardViewHolder.mClient,i);
            }
        });
        cardViewHolder.No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Respond(1,searchItems.get(i),cardViewHolder.mClient,i);
            }
        });
        cardViewHolder.DKnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Respond(2,searchItems.get(i),cardViewHolder.mClient,i);
            }
        });
        //searchItems.remove(i);
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
