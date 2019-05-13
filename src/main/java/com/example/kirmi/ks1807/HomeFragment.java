package com.example.kirmi.ks1807;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeFragment extends Fragment implements AsyncInterface
{
    String UserID = "";
    String password = "";
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    Retrofit retrofit = RestInterface.getClient();
    RestInterface.Ks1807Client client;
    Context context;
    public BackgroundService mService;
    TrackAsync asyncTask = new TrackAsync();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mService = ((NavBarMain)getActivity()).getService();
    }

    @Override
    public void processedTrackList(List<TrackDetails> listItems) {
        mService = ((NavBarMain)getActivity()).getService();
        if(this.mService!=null){
            adapter = new RecyclerViewAdapter(listItems, context, mService, true);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }else {
            Log.i("delegate.PTL", "\tmSERVICE NULL");
            Toast.makeText(getContext(), "\"ERROR: delegate.PTL: mSERVICE NULL", Toast.LENGTH_LONG).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.activity_homefrag, null);     //cant do much about warning since no Parent... unless set to mainAct ?
        Log.i("HomeFrag","\tonCreateView\t!");
        //run service
        UserID = Global.UserID;
        password = Global.UserPassword;
        client = retrofit.create(RestInterface.Ks1807Client.class);
        final LinearLayout nomusic = view.findViewById(R.id.nohistory);
        context = this.getContext();
        adapter = new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return null;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

            }

            @Override
            public int getItemCount() {
                return 0;
            }
        };
        asyncTask.delegate = this;
        Call<String> response = client.GetMusicHistory(UserID, password);
        response.enqueue(new Callback<String>()
        {
            @Override
            public void onResponse(Call<String> call, Response<String> response){
                Log.i("retrofitclick", "SUCCESS: " + response.raw());
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
                            String musicHistory = response.body(); //pass musicHistory to BackgroundLoader
                            asyncTask.execute(musicHistory);
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t){
                Log.i("Throwable ","" + t);
                fail_LoginNetwork();
            }
        });

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.context));
        recyclerView.setAdapter(adapter);
        setRetainInstance(true);
        return view;
    }

    @Override
    public void onResume() {
        Log.i("HomeFrag","\tresumed\t!");
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
