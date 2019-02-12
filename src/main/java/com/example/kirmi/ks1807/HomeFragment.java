package com.example.kirmi.ks1807;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
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
import retrofit2.http.Path;

import static android.content.Context.BIND_AUTO_CREATE;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState)
    {
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
        adapter = new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return 0;
            }
        };
        recyclerView.setAdapter(adapter);
        listItems = new ArrayList<>();
        context = this.getContext();

        /*Call<String> response = client.GetMusicHistory(UserID, password);
        response.enqueue(new Callback<String>()
        {
            @Override
            public void onResponse(Call<String> call, Response<String> response){
                Log.d("retrofitclick", "SUCCESS: " + response.raw());
                Log.d("Response.body() -> ","is" + response.body());
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
                        }else if (!response.body().equals("")){
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
                                Log.d("MusicDetails[i]"," " + MusicDetails[i]);
                                TrackDetails list = new TrackDetails(temp[0], temp[1], temp[2], temp[3], temp[4]);    //add additional imgURI? here to give to adapter/.
                                listItems.add(list);
                            }
                            adapter = new RecyclerViewAdapter(listItems, context, mService, true);
                            recyclerView.setAdapter(adapter);
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t){
                Log.d("Throwable ","" + t);
                fail_LoginNetwork();
            }
        });*/

        return view;
    }

    public void setService(BackgroundService service){
        this.mService = service;
    }

    void fail_LoginNetwork(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Dialog_Alert);
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
