package com.codetroopers.trai.Activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.codetroopers.trai.Adapters.SurveyAdapter;
import com.codetroopers.trai.R;
import com.codetroopers.trai.Utils.Questions;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SurveyActivity extends MainActivity {
    private static MobileServiceClient mClient;
    private RecyclerView mRecyclerView;
    private SurveyAdapter adapter;
    private static Context mAppContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_survey, frameLayout);
        getSupportActionBar().setTitle("Survey");
        mAppContext=this;
        try {
            mClient = new MobileServiceClient(
                    "https://trai.azure-mobile.net/",
                    "sKVAklhSawgSCrrQHfOfSDnYqVUiOR89",
                    this
            );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        mRecyclerView = (RecyclerView)findViewById(R.id.rvCategoriesCard);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.card_spacing);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        getSurveys();
    }
    private void getSurveys()
    {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    URL url = new URL("https://trai.azure-mobile.net/tables/Questions");

                    // Build a request object to connect to Azure Mobile Services
                    HttpURLConnection urlRequest = (HttpURLConnection) url.openConnection();

                    // Reading data so the http verb is "GET"
                    urlRequest.setRequestMethod("GET");

                    // Start building up the request header
                    // (1) The data is JSON format
                    // (2) We need to pass the service app id (we get this from the Azure Portal)
                    urlRequest.addRequestProperty("Content-Type", "application/json");
                    urlRequest.addRequestProperty("ACCEPT", "application/json");
                    urlRequest.addRequestProperty("X-ZUMO-APPLICATION",mClient.getAppKey());

                    // We hold the json results
                    JSONObject[] todos = null;
                    try
                    {
                        InputStream is = null;
                        try {
                            is = urlRequest.getInputStream();
                            int ch;
                            StringBuffer sb = new StringBuffer();
                            while ((ch = is.read()) != -1) {
                                sb.append((char) ch);
                            }
                            System.out.println(sb.toString());
                            JSONArray jsonArray = new JSONArray(sb.toString());
                            // Will hold an array of JSON objects
                            todos = new JSONObject[jsonArray.length()];
                            // values is very important. It is the string array that will
                            // get assigned to our ListView control.
                            questions=new ArrayList<>();
                            // Loop through the objects. The ultimate goal is to have
                            // an array of strings called "values"
                            for (int i = 0; i < jsonArray.length(); i++)
                            {
                                Questions q=new Questions();
                                todos[i] = jsonArray.getJSONObject(i);
                                //if(todos[i].get("done").toString().equals("false")){
                                q.Id=todos[i].get("id").toString();
                                q.Question = todos[i].get("question").toString();
                                q.Option1=todos[i].get("option1").toString();
                                q.Option2=todos[i].get("option2").toString();
                                q.Option3=todos[i].get("option3").toString();
                                questions.add(q);//}
                            }
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    adapter = new SurveyAdapter(MainActivity.questions,SurveyActivity.this);
                                    mRecyclerView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            });

                        } catch (IOException e) {
                            throw e;
                        } finally {
                            if (is != null) {
                                is.close();
                            }
                        }
                    } catch (Exception ex) {
                        Log.e("MainActivity Failure", "Error getting JSON from Server: ");
                        ex.printStackTrace();
                    } finally {
                        urlRequest.disconnect();
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return null;
            }
        }.execute();
    }
}
