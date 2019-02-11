package com.example.kirmi.ks1807;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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


import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DiaryFragment extends Fragment
{
    Date date = new Date();

    String UserID = "";
    Spinner q2Ans;
    TextView q1WhyLinkToDialog;
    String q2selectedItem;
    EditText q1Ans, q3Ans, q4Ans, q5Ans;
    Button submitInfo;
    //rest interface needed here0
    Retrofit retrofit = RestInterface.getClient();
    RestInterface.Ks1807Client client;
    //onSaveInstanceState()
    //onRestoreInstanceState()

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.activity_diaryfrag, null);
        UserID = Global.UserID;

        client = retrofit.create(RestInterface.Ks1807Client.class);

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
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog,int id)
                            {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                browserIntent.setData(Uri.parse(
                                        "http://www.canr.msu.edu/news/abcs_of_changing_your_thoughts_and_feelings_in_order_to_change_your_behavio"));
                                startActivity(browserIntent);
                            }
                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog,int id)
                            {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        q2Ans = (Spinner)view.findViewById(R.id.DiaryQ2Spinner);
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
                if (q1Ans.getText().toString().equals("") || q3Ans.getText().toString().equals("") || q4Ans.getText().toString().equals("") || q5Ans.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Answers required", Toast.LENGTH_LONG).show();
                } else {
                    java.sql.Timestamp sqlDate = new java.sql.Timestamp(date.getTime());
                    Call<String> response = client.SetDiaryEntry(Global.UserID, sqlDate.toString(),q1Ans.getText().toString(),q3Ans.getText().toString(),
                            q4Ans.getText().toString(), q5Ans.getText().toString());
                    Log.d("Call<String>", " " + response);
                    response.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            Log.d("retrofit", "SUCCESS: " + response);
                            if(response.code() == 404)
                                Log.d("404", " " + response.body());
                            else{
                                if(response.body().equals("-1"))
                                    Log.d("response.code()", " " + response.body() + "\nresponse.code() " + response.code());
                                else{
                                    Log.d("Successful response"," " + response.body());
                                    Toast.makeText(getContext(), "Answer submitted", Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
                }
            }
        });

        return view;
    }
}
