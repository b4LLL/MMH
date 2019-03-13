package com.example.kirmi.ks1807;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.Inflater;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import sun.bob.mcalendarview.MCalendarView;
import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.listeners.OnMonthChangeListener;
import sun.bob.mcalendarview.vo.DateData;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class DiaryCalendarFragment extends Fragment {
    RestInterface.Ks1807Client client;
    Retrofit retrofit = RestInterface.getClient();
    MCalendarView mCalendarView;
    Calendar cal;
    Date dateToday;
    DateData dateData;
    int currentYear, currentMonth, currentDay;
    String diaryData;
    Boolean successfulCalendarPoll = false; //were we able to pull the diary entries from the database.
    ArrayList<ReturnedDiaryEntry> returnedDiaryList = new ArrayList<>();

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
                currentYear = year;
                currentMonth = month;
                if(successfulCalendarPoll)
                    markCalendar(processDiaryEntry(diaryData));
            }
        });
        mCalendarView.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClick(View view, DateData date) {
                searchMarkedDates(date);
            }
        });

        return view;
    }


    public void getCalendar(String UserID){
        client = retrofit.create(RestInterface.Ks1807Client.class);
        Call<String> response = client.LoadCalendar(UserID);
        response.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if((response.body() != null) && (!response.body().equals("-1"))) {
                    successfulCalendarPoll = true;
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

        for(int i = 0; i < returnDiaryText.length; i++) {
            returnedDiaryList.add(new ReturnedDiaryEntry(returnDiaryText[i]));
        }
        return returnedDiaryList;
    }

    void markCalendar(ArrayList<ReturnedDiaryEntry> returnedDiaryList){
        for (ReturnedDiaryEntry x : returnedDiaryList) {
            if(x.getMonth() == currentMonth)
                mCalendarView.markDate(currentYear,x.getMonth(),x.getDay());
        }
    }

    void searchMarkedDates(DateData date){
        for (ReturnedDiaryEntry x : returnedDiaryList) {
            if(date.getDay() == x.getDay()) {
                Log.d("FOUND", "\nmonth\t" + x.getMonth() + "\ndate\t" + x.getDay() + "\ne1\t" + x.getEntryOne());
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.diary_recollection,null);
                PopupWindow popupWindow = new PopupWindow(popupView,400,600,true);
                popupWindow.setContentView(popupView);
                popupWindow.showAtLocation(this.getView(), Gravity.CENTER,0,0);
                Log.d("loading popup","\tsuccess\t" + popupWindow.isShowing());
            }
        }
    }
}
