package com.example.kirmi.ks1807;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AccountSettings extends Fragment
{
    String UserID = "";
    Spinner alertsSpinner;
    RadioButton yes, no;
    Button submit, signout;
    Retrofit retrofit = RestInterface.getClient();
    RestInterface.Ks1807Client client;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.activity_account_settingstab, container, false);

        //Passing the user ID
        UserID = Global.UserID;

        //Adding content to the spinner to collect alert frequency
        alertsSpinner = (Spinner)view.findViewById(R.id.Spinner_AlertFrequency);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getActivity(), R.array.trackalertoptions,
                R.layout.spinner_item);

        //Adding custom style to the spinner
        adapter1.setDropDownViewResource(R.layout.spinner_item);
        alertsSpinner.setAdapter(adapter1);

        yes = (RadioButton) view.findViewById(R.id.RadioButton_SettingsYes);
        no = (RadioButton) view.findViewById(R.id.RadioButton_SettingsNo);

        //Changing the radio button look when selected
        yes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                yes.setBackgroundResource(R.drawable.settingsyesselected);
                no.setBackgroundResource(R.drawable.settingnonormal);
            }
        });

        no.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                yes.setBackgroundResource(R.drawable.settingsyesnormal);
                no.setBackgroundResource(R.drawable.settingnoselected);
            }
        });

        submit = (Button)view.findViewById(R.id.btn_SubmitSettings);
        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                UpdateSettings();
            }
        });

        client = retrofit.create(RestInterface.Ks1807Client.class);
        signout = (Button)view.findViewById(R.id.btn_signout);
        signout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setTitle("Confirm logout");
                alertDialogBuilder.setMessage("Are you sure that you want to logout?");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Global.UserID = "";
                        //BackgroundService mService = ((NavBarMain) getActivity()).getService();
                        if (Global.isBGSrunning){ //confirm this
                            Log.i("ACCOUNT", "\t\tonClick: BGS is Running");
                            Intent intent = new Intent(getContext().getApplicationContext(), BackgroundService.class);
                            Global.isLogged = false;
                            try{
                                getActivity().getApplicationContext().unbindService(((NavBarMain) getActivity()).serviceConnection); // try
                                Log.i("ACCOUNT", "\t\tonClick: Unbinding");
                            }catch(Exception e){
                                Log.e("ACCOUNT", "\t\tonClick: " + e.toString());
                            }
                            try{
                                //getActivity().getApplicationContext().stopService(intent);
                                getActivity().stopService(intent);
                                Log.i("ACCOUNT", "\t\tonClick: ACTIVITY CONTEXT");
                            }catch(Exception e){
                                Log.e("ACCOUNT", "\t\tonClick: ACTIVITY CONTEXT " + e.toString());
                            }
                            try{
                                //getActivity().getApplicationContext().stopService(intent);
                                getActivity().getApplicationContext().stopService(intent);
                                Log.i("ACCOUNT", "\t\tonClick: APPLICATION CONTEXT STOPSERVICE");
                            }catch(Exception e){
                                Log.e("ACCOUNT", "\t\tonClick: APPLICATION CONTEXT STOPSERVICE" + e.toString());
                            }
                        }
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);   //create new mainActivity as own new Task and clear the backstack.
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
        //Showing the user's current account settings
        ShowUserSettings(view);
        return view;
    }

    private void UpdateSettings()
    {
        //Convert the contents of the Radio buttons and Spinner to strings
        String InvalidMessage = "";
        String MakeRecommendation = "";
        String MoodFrequencyText = alertsSpinner.getSelectedItem().toString();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        if (yes.isChecked())
        {
            MakeRecommendation = "Yes";
        }
        else if (no.isChecked())
        {
            MakeRecommendation = "No";
        }
        else
        {
            InvalidMessage = "No recommendation selected. This error should never happen.";
            alertDialogBuilder.setMessage(InvalidMessage);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        String UserPassword = Global.UserPassword;

        Call<String> response = client.UpdateSettings(
                MakeRecommendation, MoodFrequencyText, "No", UserID, UserPassword);
        response.enqueue(new Callback<String>()
        {
            @Override
            public void onResponse(Call<String> call, Response<String> response)
            {
                Log.i("retrofitclick", "SUCCESS: " + response.raw());

                if(response.code() == 404)
                {
                    Toast.makeText(getContext(),
                            "404 Error. Server did not return a response.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(response.body().equals("Successful"))
                        Toast.makeText(getActivity(), "Settings Updated", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getActivity(), "Failed to update settings", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t)
            {
                fail_LoginNetwork();
            }
        });
    }

    private void ShowUserSettings(View view)
    {
        String UserPassword = Global.UserPassword;

        Call<String> response = client.GetUserSettings(UserID, UserPassword);
        response.enqueue(new Callback<String>()
        {
            @Override
            public void onResponse(Call<String> call, Response<String> response)
            {
                Log.i("retrofitclick", "SUCCESS: " + response.raw());

                if(response.code() == 404)
                {
                    Toast.makeText(getContext(),
                            "404 Error. Server did not return a response.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(response.body().equals("Incorrect UserID or Password. Query not executed."))
                        Toast.makeText(getActivity(), "Failed to get settings from server", Toast.LENGTH_SHORT).show();
                    else
                    {
                        String Settings = response.body();
                        String[] TheSettings = Settings.split("\n");

                        String MakeRecommendations = TheSettings[0].replace("MakeRecommendations: ", "");
                        String MoodFrequency = TheSettings[1].replace("MoodFrequency: ", "");
                        //Third String called RememberLogin is also retrieved by the API but will be ignored here.

                        if (MakeRecommendations.equals("Yes"))
                        {
                            yes.setChecked(true);
                            yes.setBackgroundResource(R.drawable.settingsyesselected);
                            no.setBackgroundResource(R.drawable.settingnonormal);
                        }
                        else if (MakeRecommendations.equals("No"))
                        {
                            no.setChecked(true);
                            yes.setBackgroundResource(R.drawable.settingsyesnormal);
                            no.setBackgroundResource(R.drawable.settingnoselected);
                        }

                        //Set the Spinner position to match the string retrieved from the database.
                        ArrayAdapter SpinnerAdapter = (ArrayAdapter) alertsSpinner.getAdapter();
                        alertsSpinner.setSelection(SpinnerAdapter.getPosition(MoodFrequency));
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
        //Blank ID means either the email or password were incorrect.
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Service Error");
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Ok",new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog,int id)
                    {
                        //No action to be taken until login issue is resolved.
                    }
                });
        String InvalidMessage = "The service is not available at this time, please try again later" +
                "or contact support";
        alertDialogBuilder.setMessage(InvalidMessage);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}