package com.example.kirmi.ks1807;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterSecondPage extends AppCompatActivity
{
    private final Context context = this;
    String UserID = "";
    Spinner s1, s2, s3, s4;

    Retrofit retrofit = RestInterface.getClient();
    RestInterface.Ks1807Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_second_page);
        client = retrofit.create(RestInterface.Ks1807Client.class);

        //Get the UserID for this login session.
        UserID = Global.UserID;

        s1 = (Spinner) findViewById(R.id.spinner1);
        s2 = (Spinner) findViewById(R.id.spinner2);
        s3 = (Spinner) findViewById(R.id.spinner3);
        s4 = (Spinner) findViewById(R.id.spinner4);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.spinner1ques,
                R.layout.spinner_item);

        adapter1.setDropDownViewResource(R.layout.spinner_item);
        s1.setAdapter(adapter1);
        s2.setAdapter(adapter1);
        s3.setAdapter(adapter1);
        s4.setAdapter(adapter1);

        GetUserAnswers();
    }

    //Confirm if the user wants to go back if the button is pressed.
    public void button_Back(View view)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Confirm going back to previous page");
        alertDialogBuilder
                .setMessage("Are you sure you wish to go back? All changes on this page will be discarded.")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog,int id)
                    {
                        Intent intent = new Intent(RegisterSecondPage.this, Register.class);
                        startActivity(intent);
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

    public void button_RegisterNow2(View view)
    {
        UpdateSecondPage();
    }

    private void GetUserAnswers()
    {
        String UserPassword = Global.UserPassword;

        Call<String> response = client.GetUserRegistrationQuestions(UserID, UserPassword);
        response.enqueue(new Callback<String>()
        {
            @Override
            public void onResponse(Call<String> call, Response<String> response)
            {
                Log.d("retrofitclick", "SUCCESS: " + response.raw());

                if(response.code() == 404)
                {
                    Toast.makeText(getApplicationContext(),
                            "404 Error. Server did not return a response.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(response.body().equals("Incorrect UserID or Password. Query not executed."))
                        Toast.makeText(context, "Failed to get registration questions from server", Toast.LENGTH_SHORT).show();
                    else
                    {
                        String Questions = response.body();
                        String[] TheQuestions = Questions.split("\n");

                        String MusicQuestionOne = TheQuestions[0].replace("MusicQuestionOne: ", "");
                        String MusicQuestionTwo = TheQuestions[1].replace("MusicQuestionTwo: ", "");
                        String MusicQuestionThree = TheQuestions[2].replace("MusicQuestionThree: ", "");
                        String MusicQuestionFour = TheQuestions[3].replace("MusicQuestionFour: ", "");

                    /*If any of these questions have not been answered then do not attempt to set
                    the spinner positions for any of them. Leave as default.*/
                        if (MusicQuestionOne.equals("Not Answered") ||
                                MusicQuestionTwo.equals("Not Answered") ||
                                MusicQuestionThree.equals("Not Answered") ||
                                MusicQuestionFour.equals("Not Answered"))
                        {
                            return;
                        }
                        else
                        {
                            //Set the Spinners positions to match the string retrieved from the database.
                            ArrayAdapter SpinnerAdapterOne = (ArrayAdapter) s1.getAdapter();
                            s1.setSelection(SpinnerAdapterOne.getPosition(MusicQuestionOne));

                            ArrayAdapter SpinnerAdapterTwo = (ArrayAdapter) s2.getAdapter();
                            s2.setSelection(SpinnerAdapterTwo.getPosition(MusicQuestionTwo));

                            ArrayAdapter SpinnerAdapterThree = (ArrayAdapter) s3.getAdapter();
                            s3.setSelection(SpinnerAdapterThree.getPosition(MusicQuestionThree));

                            ArrayAdapter SpinnerAdapterFour = (ArrayAdapter) s4.getAdapter();
                            s4.setSelection(SpinnerAdapterFour.getPosition(MusicQuestionFour));
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t)
            {
                fail_LoginNetwork();
            }
        });
    }

    private void UpdateSecondPage()
    {
        String TheMusicQuestionOne = "";
        String TheMusicQuestionTwo = "";
        String TheMusicQuestionThree = "";
        String TheMusicQuestionFour = "";

        if (s1.getSelectedItem() != null)
        {
            TheMusicQuestionOne = s1.getSelectedItem().toString();
        }
        if (s2.getSelectedItem() != null)
        {
            TheMusicQuestionTwo = s2.getSelectedItem().toString();
        }
        if (s3.getSelectedItem() != null)
        {
            TheMusicQuestionThree = s3.getSelectedItem().toString();
        }
        if (s4.getSelectedItem() != null)
        {
            TheMusicQuestionFour = s4.getSelectedItem().toString();
        }

        String UserPassword = Global.UserPassword;

        Call<String> response = client.UpdateUserSecondPage(TheMusicQuestionOne,
                TheMusicQuestionTwo, TheMusicQuestionThree, TheMusicQuestionFour, UserID,
                UserPassword);
        response.enqueue(new Callback<String>()
        {
             @Override
             public void onResponse(Call<String> call, Response<String> response)
             {
                 Log.d("retrofitclick", "SUCCESS: " + response.raw());

                 if(response.code() == 404)
                 {
                     Toast.makeText(getApplicationContext(),
                             "404 Error. Server did not return a response.", Toast.LENGTH_SHORT).show();
                 }
                 else
                 {
                     if (!response.body().equals("Successful"))
                         Toast.makeText(context, "Failed to update user with registration questions",
                                 Toast.LENGTH_SHORT).show();
                     else
                     {
                         Intent intent = new Intent(RegisterSecondPage.this, OtherPlatforms.class);
                         startActivity(intent);
                     }
                 }
             }
             @Override
             public void onFailure(Call<String> call, Throwable t)
             {
                 fail_LoginNetwork();
             }
        });
    }

    void fail_LoginNetwork()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
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
