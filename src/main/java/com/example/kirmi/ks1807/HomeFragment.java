package com.example.kirmi.ks1807;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeFragment extends Fragment
{
    String UserID = "";
    String password = "";
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    private List<TrackDetails> listItems;
    Retrofit retrofit = RestInterface.getClient();
    RestInterface.Ks1807Client client;

    Context context;
    public BackgroundService mService;
    boolean mBound;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState)
    {
        context = getContext();
        View view = inflater.inflate(R.layout.activity_homefrag, null);     //cant do much about warning since no Parent... unless set to mainAct ?
        //run service
        if(!BackgroundService.isRunning)
            new BackgroundServiceStarter().onReceive(getContext(), new Intent());
        UserID = Global.UserID;
        password = Global.UserPassword;
        client = retrofit.create(RestInterface.Ks1807Client.class);
        final LinearLayout nomusic = view.findViewById(R.id.nohistory);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listItems = new ArrayList<>();
        Call<String> response = client.GetMusicHistory(UserID, password);
        response.enqueue(new Callback<String>()
        {
            @Override
            public void onResponse(Call<String> call, Response<String> response){
                Log.d("retrofitclick", "SUCCESS: " + response.raw());
                if(response.code() == 404){
                    Toast.makeText(getContext(),"404 Error. Server did not return a response.", Toast.LENGTH_SHORT).show();
                }else{
                    if(response.body().equals("-1")){
                        Toast.makeText(getActivity(), "Failed to get details from server", Toast.LENGTH_SHORT).show();
                    }else{
                        if (response.body().equals("-,-,-,-,-,-,-")){
                            nomusic.setVisibility(View.VISIBLE);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setVisibility(View.GONE);
                        }else{
                            String musicHistory = response.body();
                            String MusicDetails[] = musicHistory.split(System.getProperty("line.separator"));
                            listItems = new ArrayList<>();
                            int length;
                            if (MusicDetails.length <= 10) {
                                length = MusicDetails.length;
                            } else {
                                length = 10;
                            }
                            for (int i = 0; i < length; i++) {
                                String temp[] = MusicDetails[i].split(",");
                                TrackDetails list = new TrackDetails(temp[0], temp[1], temp[2], temp[3], temp[4], temp[5], temp[6]);
                                listItems.add(list);
                            }


                            Intent intent = new Intent(context, BackgroundService.class);
                            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);


                            adapter = new RecyclerViewAdapter(listItems, getContext(),mService,mBound);
                            recyclerView.setAdapter(adapter);
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t){
                fail_LoginNetwork();
            }
        });

        return view;
    }

    //here we set the connection to the service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BackgroundService.LocalBinder binder = (BackgroundService.LocalBinder) service;
            Log.d("RecyclerViewAdapter","BackgroundService binding..");
            mService = binder.getService(); //set the service to mService, mBound is just a flag to state that it is bound...
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("RecyclerViewAdapter","onServiceDisconnected called");
            mService.unbindService(serviceConnection);
            mBound = false;
        }
    };

    public void unbindService() {
        mService.unbindService(serviceConnection);
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d("Pausing", "serviceConnection= " + mService);
        if(mBound) {
            Log.d("onPause called", "we are bound");
        }
        //unbindService();
    }

    void fail_LoginNetwork(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Service Error");
        alertDialogBuilder
            .setCancelable(false)
            .setPositiveButton("Ok",new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog,int id)
                {
                }
            });
        String InvalidMessage = "The service is not available at this time, please try again later " +
                "or contact support";
        alertDialogBuilder.setMessage(InvalidMessage);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
