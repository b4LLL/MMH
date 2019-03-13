package com.example.kirmi.ks1807;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import sun.bob.mcalendarview.MCalendarView;
import sun.bob.mcalendarview.listeners.OnMonthChangeListener;
import sun.bob.mcalendarview.vo.DateData;

public class DiaryCalendarFragment extends Fragment {
    RestInterface.Ks1807Client client;
    Retrofit retrofit = RestInterface.getClient();
    MCalendarView mCalendarView;
    Calendar cal;
    Date dateToday;
    DateData dateData;
    int currentYear, currentMonth, currentDay;
    String diaryData;
    Boolean calendarPoll = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        dateToday = new Date();
        cal = Calendar.getInstance();
        cal.setTime(dateToday);
        dateData = new DateData(currentYear = cal.get(Calendar.YEAR),currentMonth = cal.get(Calendar.MONTH) + 1,currentDay = cal.get(Calendar.DAY_OF_MONTH));
        getCalendar(Global.UserID);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_diary_calendarfrag,null);
        mCalendarView = view.findViewById(R.id.calendar);
        mCalendarView.travelTo(dateData);
        mCalendarView.setOnMonthChangeListener(new OnMonthChangeListener() {
            @Override
            public void onMonthChange(int year, int month) {
                Log.d("onMonthChange","\nyear\t" + year + "\nmonth\t" + month);
                currentYear = year;
                currentMonth = month;
                if(calendarPoll)
                    markCalendar(processDiaryEntry(diaryData));
            }
        });

        return view;
    }

    //mCalendarView.markDate(year,month,day); use this after getting all the entries for the month

    public void getCalendar(String UserID){
        client = retrofit.create(RestInterface.Ks1807Client.class);
        Call<String> response = client.LoadCalendar(UserID);
        response.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(!response.body().equals("-1")) {
                    calendarPoll = true;
                    diaryData = response.body();
                    markCalendar(processDiaryEntry(diaryData));
                }else
                    Toast.makeText(getContext(),"No diary entries found", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("getCalendar Called","\terror\t" + t.toString());
            }
        });
    }

    ArrayList<ReturnedDiaryEntry> processDiaryEntry(String returnAllEntries){
        String[] returnDiaryText = returnAllEntries.split("\f");
        ArrayList<ReturnedDiaryEntry> returnedDiaryList = new ArrayList<>();
        for(int i = 0; i < returnDiaryText.length; i++) {
            returnedDiaryList.add(new ReturnedDiaryEntry(returnDiaryText[i]));
        }
        return returnedDiaryList;
    }

    void markCalendar(ArrayList<ReturnedDiaryEntry> returnedDiaryList){
        for (ReturnedDiaryEntry x : returnedDiaryList) {
            if(x.getMonth() == currentMonth){
                mCalendarView.markDate(currentYear,x.getMonth(),x.getDay());
            }
        }
    }
}
