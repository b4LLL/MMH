package com.example.kirmi.ks1807;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Date;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DiaryInputFragment extends Fragment {
    Date date = new Date();

    String UserID = "";
    Spinner q2Ans;
    TextView q1WhyLinkToDialog;
    String q2selectedItem;
    EditText q1Ans, q3Ans, q4Ans, q5Ans;
    Button submitInfo;
    Retrofit retrofit = RestInterface.getClient();
    RestInterface.Ks1807Client client;
    boolean updateEntry = false;
    public String currentDiaryID;
    String q1, q3, q4, q5, outcome;
    String submit = "Submit";
    String update = "Update";

    void getDiaryEntry(String UserDiaryID) {
        Call<String> response = client.GetDiaryEntry(UserDiaryID);
        response.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (!response.body().equals("-1")) {
                    updateEntry = true;     //makes sure next submission is Update instead of Set
                    String returnDiaryRaw = response.body();
                    String[] returnDiaryText = returnDiaryRaw.split("@@@");
                    q1Ans.setText(returnDiaryText[0]);
                    q3Ans.setText(returnDiaryText[1]);
                    q4Ans.setText(returnDiaryText[2]);
                    q5Ans.setText(returnDiaryText[3]);
                    if(!returnDiaryText[4].equals("NULL")){
                        if(returnDiaryText[4].equals("Negative")){
                            q2Ans.setSelection(1);
                        }else
                            q2Ans.setSelection(0);
                    }
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("onFailure", " -> getDiaryEntry t= " + t);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        UserID = Global.UserID;
        client = retrofit.create(RestInterface.Ks1807Client.class);
        Call<String> response = client.CheckDiaryDate(Global.UserID);
        response.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("CheckDiaryDate", " " + response.body() + "\n" + response.code());
                if (response.code() != 204) {
                    Log.d("CheckDiaryDate", " " + response.body());
                    currentDiaryID = response.body();
                    getDiaryEntry(currentDiaryID);
                    submitInfo.setText(update);
                } else {
                    Log.d("CheckDiaryDate ", "" + response.body() + "\nNo Diary Entry Found");
                    submitInfo.setText(submit);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("onFailure", " -> CheckDiaryDate t= " + t + " response: " + response.toString());
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.activity_diaryfrag, null);
        q1Ans = view.findViewById(R.id.editText_q1Answer);
        q3Ans = view.findViewById(R.id.editText_q3Answer);
        q4Ans = view.findViewById(R.id.editText_q4Answer);
        q5Ans = view.findViewById(R.id.editText_q5Answer);
        submitInfo = view.findViewById(R.id.btn_DiaryInfo);


        q1WhyLinkToDialog = (TextView) view.findViewById(R.id.textView_linkwhy);
        q1WhyLinkToDialog.setMovementMethod(LinkMovementMethod.getInstance());
        q1WhyLinkToDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setTitle("More Information");
                alertDialogBuilder
                        .setMessage(R.string.whyLinkDialogCap)
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                browserIntent.setData(Uri.parse(
                                        "http://www.canr.msu.edu/news/abcs_of_changing_your_thoughts_and_feelings_in_order_to_change_your_behavio"));
                                startActivity(browserIntent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        q2Ans = (Spinner) view.findViewById(R.id.DiaryQ2Spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.diaryq2options,
                R.layout.spinner_item);

        adapter.setDropDownViewResource(R.layout.spinner_item);
        q2Ans.setAdapter(adapter);
        q2Ans.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                q2selectedItem = adapterView.getItemAtPosition(position).toString();

                if (q2selectedItem.equals("Negative")) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                    alertDialogBuilder.setTitle("More Information");
                    alertDialogBuilder
                            .setMessage(R.string.DiaryQ2NegativeThoughtCap)
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                    browserIntent.setData(Uri.parse(
                                            "http://au.reachout.com/articles/how-to-challenge-negative-thoughts"));
                                    startActivity(browserIntent);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        submitInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                java.sql.Timestamp sqlDate = new java.sql.Timestamp(date.getTime());
                q1 = q1Ans.getText().toString();
                q3 = q3Ans.getText().toString();
                q4 = q4Ans.getText().toString();
                q5 = q5Ans.getText().toString();
                if (q1.equals("") || q3.equals("") || q4.equals("") || q5.equals("")) {
                    Toast.makeText(getContext(), "Answers required", Toast.LENGTH_LONG).show();
                }else
                    sendDiaryEntry();
            }
        });
        return view;
    }


    void sendDiaryEntry(){
        if (!updateEntry){ //first entry for the day
            Call<String> response = client.SetDiaryEntry(Global.UserID,q1,q3,q4,q5,q2selectedItem);
            response.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.d("RETRO", " " + response.raw());
                    if(response.code() == 404)
                        Log.d("404", " " + response.body());
                    else{
                        if(response.body().equals("-1"))
                            Log.d("response.code()", " " + response.body() + "\nresponse.code() " + response.code());
                        else{
                            Log.d("Successful response"," " + response.body());
                            updateEntry = true;
                            submitInfo.setText(update);
                            Toast.makeText(getContext(), "Answer submitted", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.d("INSERT FAILURE"," t= " + t + " response: " + response.toString());
                }
            });
        } else { //if it is only updating
            Call<String> response = client.UpdateDiaryEntry(currentDiaryID,Global.UserID,q1,q3,q4,q5,q2selectedItem);
            response.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.d("Response", " " + response.body() + "\tcode " + response.code() + "\n" + currentDiaryID);
                    Toast.makeText(getContext(), "Answers updated", Toast.LENGTH_LONG).show();
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.d("UPDATE FAILURE"," t= " + t + " response: " + response.request().body());
                }
            });
        }
    }
}