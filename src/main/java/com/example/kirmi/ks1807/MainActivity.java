package com.example.kirmi.ks1807;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity
{
    private final Context context = this;
    final CommonFunctions PasswordFunctions = new CommonFunctions();
    Retrofit retrofit = RestInterface.getClient();  // sets up the interface implementation
    RestInterface.Ks1807Client client;              // client is an <interface> and implemented below
    EditText EmailAddress;
    EditText Password;
    String TheEmailAddress;
    String ThePassword;
    String UserID = "";

    // write a function to check if the background service is running
    // also check if Spotify is already installed -> the function should be called on the "Login" button click
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   //checking here if the App has permission to write over other apps
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }
        // onCreate code called
        setContentView(R.layout.activity_main);                     // needs to be called inside onCreate - activity_main.xml [R.layout.* => /res/layout/]
        EmailAddress = findViewById(R.id.EditText_UserName);
        Password = findViewById(R.id.EditText_Password);
        Log.d("onCreate", "Called");
        client = retrofit.create(RestInterface.Ks1807Client.class); // an implementation of the interface

        Password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && Password.getText().toString().isEmpty()) {
                    Password.setHint("");
                    Password.setText("");
                }
            }
        });
        EmailAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && EmailAddress.getText().toString().isEmpty()) {
                    EmailAddress.setHint("");
                    EmailAddress.setText("");
                }
            }
        });
    }

    public void button_Login(View view){
        if(ValidateLogin()){
            String EncryptedPassword = PasswordFunctions.EncryptPassword(ThePassword);
            Global.UserPassword = EncryptedPassword;
            Call<String> response = client.VerifyLogin(TheEmailAddress, EncryptedPassword);     //calls a
            response.enqueue(new Callback<String>()
            {
                @Override
                public void onResponse(Call<String> call, Response<String> response)
                {
                    Log.d("retrofit", "SUCCESS: " + response);
                    if(response.code() == 404)  //is the code actually an integer and not a string?
                        Toast.makeText(getApplicationContext(),"404 Error. Server did not return a response.", Toast.LENGTH_SHORT).show();
                    else{
                        if(response.body().equals("-1"))
                            showAlert(3);
                        else{
                            Global.UserID = response.body();
                            Global.isLogged = true;             //declare user as logged in
                            success_Login();
                        }
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t)
                {
                    showAlert(4);
                }
            });
        }
    }

    @Override
    public void onResume(){
        if(Global.isLogged){
            success_Login();
        }
        super.onResume();
    }

    private boolean ValidateLogin(){
        TheEmailAddress = EmailAddress.getText().toString();
        ThePassword = Password.getText().toString();
        if(TheEmailAddress.contentEquals("") && ThePassword.contentEquals("")){
            showAlert(2);
            return false;
        }else if(TheEmailAddress.contentEquals("")) {
            showAlert(1);
            return false;
        }else if(ThePassword.contentEquals("")) {
            showAlert(0);
            return false;
        }else
            return true;
    }

    void success_Login(){
        Intent intent = new Intent(this, NavBarMain.class);
        startActivity(intent);
        this.finish();
    }

    public void button_Register(View view){
        Intent intent = new Intent(MainActivity.this, Register.class);
        startActivity(intent);
    }

    private void showAlert(int i){
        String Title;
        String Message;
        switch(i){
            case 0:
                //missing password
                Title = "Missing Credential";
                Message = "Password is missing.";
                break;
            case 1:
                //missing email
                Title = "Missing Credential";
                Message = "Email is missing.";
                break;
            case 2:
                //both missing
                Title = "Missing Credentials";
                Message = "Email and password have not been entered.";
                break;
            case 3:
                //failed login
                Title = "Invalid Credentials";
                Message = "Your login was unsuccessful. Please check that your Email Address and Password have been typed in correctly.";
                break;
            case 4:
                //network failure
                Title = "Service Error";
                Message = "The service is not available at this time, please try again later";
                break;
            default:
                //success
                Title = "Success";
                Message = "Logging in now";
                break;
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(Title);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Ok",new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog,int id)
                    {
                    }
                });
        alertDialogBuilder.setMessage(Message);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
