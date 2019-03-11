package com.example.kirmi.ks1807;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import sun.bob.mcalendarview.MCalendarView;
import sun.bob.mcalendarview.listeners.OnMonthChangeListener;

import static android.support.constraint.Constraints.TAG;

public class DiaryCalendarFragment extends Fragment {
    ArrayList<ReturnedDiaryEntry> returnedDiaryList = new ArrayList<>();
    RestInterface.Ks1807Client client;
    Retrofit retrofit = RestInterface.getClient();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        getCalendar(Global.UserID);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        getCalendar(Global.UserID);
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_diary_calendarfrag,null);
        MCalendarView mCalendarView = view.findViewById(R.id.calendar);
        mCalendarView.setOnMonthChangeListener(new OnMonthChangeListener() {
            @Override
            public void onMonthChange(int year, int month) {
                Log.d("onMonthChange","\nyear\t" + year + "\nmonth\t" + month);
            }
        });

        //mCalendarView.markDate(year,month,day); use this after getting all the entries for the month
        return view;
    }

    public void getCalendar(String UserID){
        client = retrofit.create(RestInterface.Ks1807Client.class);
        Call<String> response = client.LoadCalendar(UserID);
        response.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("getCalendar Called","\t" + response);
                String returnAllEntries = response.body();
                String[] returnDiaryText = returnAllEntries.split("\f");
                ArrayList<String> row;
                for(int i = 0; i < returnDiaryText.length; i++){
                    returnedDiaryList.add(new ReturnedDiaryEntry(returnDiaryText[i]));
                }
                for (ReturnedDiaryEntry x : returnedDiaryList) {
                    Log.d(TAG, " " + x.getDate());
                    Log.d(TAG, " " + x.getEntryOne());
                    Log.d(TAG, " " + x.getEntryTwo());
                    Log.d(TAG, " " + x.getEntryFour());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("getCalendar Called","\terror\t" + t.toString());
            }
        });
    }
    //retreive all the dates.

}
