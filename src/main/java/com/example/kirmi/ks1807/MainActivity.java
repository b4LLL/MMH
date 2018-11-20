package com.example.kirmi.ks1807;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity
{
    private final Context context = this;
    final CommonFunctions PasswordFunctions = new CommonFunctions();
    Retrofit retrofit = RestInterface.getClient();
    RestInterface.Ks1807Client client;
    TextView EmailAddress;
    TextView Password;
    String TheEmailAddress;
    String ThePassword;
    String UserID = "";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);     // spotify API code
        setContentView(R.layout.activity_main); // spotify API code
        EmailAddress = (TextView)findViewById(R.id.EditText_UserName);
        Password = (TextView)findViewById(R.id.EditText_Password);
        client = retrofit.create(RestInterface.Ks1807Client.class);

        //Ensures that password hint disappears when user focuses the text box.
        final EditText PasswordTextBox = (EditText) findViewById(R.id.EditText_Password);
        PasswordTextBox.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                String TextBoxString = PasswordTextBox.getText().toString();
                if (hasFocus)
                    PasswordTextBox.setText("");
                /*If user unfocuses the text box but hasn't typed anything yet, repopulate the
                field. But if they have started typing just leave it as it is.*/
                else if (!hasFocus && TextBoxString.equals(""))
                    PasswordTextBox.setText("password");
            }
        });
    }

    public void button_Login(View view)
    {
        if(ValidateLogin()) //if validation was successful
        {
            String EncryptedPassword = PasswordFunctions.EncryptPassword(ThePassword);
            Global.UserPassword = EncryptedPassword;
            Call<String> response = client.VerifyLogin(TheEmailAddress, EncryptedPassword);
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
                        if(response.body().equals("-1"))
                        {
                            fail_Login();
                        }
                        else
                        {
                            Global.UserID = response.body();
                            success_Login();
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
    }

    void success_Login()
    {
        Intent intent = new Intent(MainActivity.this, NavBarMain.class);
        startActivity(intent);
    }

    void fail_Login()
    {
        //Blank ID means either the email or password were incorrect.
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Invalid Credentials");
        alertDialogBuilder
            .setCancelable(false)
            .setPositiveButton("Ok",new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog,int id)
                {
                    //No action to be taken until login issue is resolved.
                }
            });
        String InvalidMessage = "Your login was unsuccessful. Please check that your Email Address" +
                " and Password have been typed in correctly.";
        alertDialogBuilder.setMessage(InvalidMessage);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    void fail_LoginNetwork()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
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

    public void button_Register(View view)
    {
        Intent intent = new Intent(MainActivity.this, Register.class);
        startActivity(intent);
    }

    private boolean ValidateLogin()
    {
        boolean ValidationSuccessful = true;
        String InvalidMessage = "";
        //Convert the contents of the text boxes to strings
        TheEmailAddress = EmailAddress.getText().toString();
        ThePassword = Password.getText().toString();
        //Validation dialogue
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Invalid Credentials");
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Ok",new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog,int id)
                    {
                        //No action to be taken until login issue is resolved.
                    }
                });
        if (TheEmailAddress.equals(""))
        {
            ValidationSuccessful = false;
            InvalidMessage = "You need to enter an Email Address to login.";
            alertDialogBuilder.setMessage(InvalidMessage);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        if (ThePassword.equals("") && ValidationSuccessful)
        {
            ValidationSuccessful = false;
            InvalidMessage = "You need to enter your password to login.";
            alertDialogBuilder.setMessage(InvalidMessage);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        return ValidationSuccessful;
    }
}
