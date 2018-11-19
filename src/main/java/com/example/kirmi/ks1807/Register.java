package com.example.kirmi.ks1807;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioButton;
import android.widget.Toast;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Register extends AppCompatActivity
{
    private final Context context = this;

    String UserID = "-1";
    String BackUserID = "";
    String CurrentEmailAddress = "";
    String EthicsAgreement = "No";
    boolean IsEmailUnique = false;

    Retrofit retrofit = RestInterface.getClient();
    RestInterface.Ks1807Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        client = retrofit.create(RestInterface.Ks1807Client.class);

        //Get the UserID for this login session if user went back from second page.
        BackUserID = Global.UserID;

        if (BackUserID != null && !BackUserID.equals(""))
        {
            ShowUserDetails();
        }

        //Check that the email is unique or not before the main validation is run.
        final EditText EmailTextBox = (EditText) findViewById(R.id.EditText_Email);
        EmailTextBox.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                String TheEmail = EmailTextBox.getText().toString();
                if (!hasFocus && !TheEmail.equals(""))
                {
                    CheckEmailAddress(TheEmail);
                }
            }
        });
    }

    //Confirm if the user wants to go back if the button is pressed.
    public void button_Back(View view)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Confirm exit");
        alertDialogBuilder
                .setMessage("Are you sure you wish to go back? All changes will be discarded.")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog,int id)
                    {
                        Intent intent = new Intent(Register.this, MainActivity.class);
                        Global.UserID = "";
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

    public void button_Next(View view)
    {
        String ButtonName = view.getResources().getResourceEntryName(view.getId());
        ValidateForm(ButtonName);
    }

    public void button_RegisterNow(View view)
    {
        String ButtonName = view.getResources().getResourceEntryName(view.getId());
        ValidateForm(ButtonName);
    }

    //Buttons that switch the graphics of the gender radio buttons to indicate what is selected.
    public void RadioButtonMale(View view)
    {
        RadioButton GenderMale = (RadioButton) findViewById(R.id.RadioButton_Male);
        RadioButton GenderFemale = (RadioButton) findViewById(R.id.RadioButton_Female);
        RadioButton GenderOther = (RadioButton) findViewById(R.id.RadioButton_Other);

        GenderMale.setBackgroundResource(R.drawable.maleselected);
        GenderFemale.setBackgroundResource(R.drawable.femaleunselected);
        GenderOther.setBackgroundResource(R.drawable.otherunselected);
    }

    public void RadioButtonYesResearch(View view)
    {
        RadioButton yes1 = (RadioButton) findViewById(R.id.RadioButton_Yes1);
        RadioButton no1 = (RadioButton) findViewById(R.id.RadioButton_No1);

        yes1.setBackgroundResource(R.drawable.yesselected);
        no1.setBackgroundResource(R.drawable.nounselected);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Ethics Statement");
        alertDialogBuilder
                .setMessage("...\n\nDo you agree to comply with the above ethical statement?")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog,int id)
                    {
                        EthicsAgreement = "Yes";
                        Toast.makeText(context, "You have agreed.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog,int id)
                    {
                        EthicsAgreement = "No";
                        dialog.cancel();

                        RadioButton yes1 = (RadioButton) findViewById(R.id.RadioButton_Yes1);
                        RadioButton no1 = (RadioButton) findViewById(R.id.RadioButton_No1);

                        yes1.setBackgroundResource(R.drawable.yesunselected);
                        no1.setBackgroundResource(R.drawable.noselected);

                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void RadioButtonNoResearch(View view)
    {
        RadioButton yes1 = (RadioButton) findViewById(R.id.RadioButton_Yes1);
        RadioButton no1 = (RadioButton) findViewById(R.id.RadioButton_No1);

        yes1.setBackgroundResource(R.drawable.yesunselected);
        no1.setBackgroundResource(R.drawable.noselected);
    }

    public void RadioButtonYesMoreQues(View view)
    {
        RadioButton yes = (RadioButton) findViewById(R.id.RadioButton_Yes);
        RadioButton no = (RadioButton) findViewById(R.id.RadioButton_No);
        Button register = (Button) findViewById(R.id.btn_Submit);
        Button next = (Button) findViewById(R.id.btn_Next);

        yes.setBackgroundResource(R.drawable.yesselected);
        no.setBackgroundResource(R.drawable.nounselected);

        next.setVisibility(view.VISIBLE);
        register.setVisibility(view.INVISIBLE);

        //Used in case the user backtracks to this page we want the user's choice preselected.
        Global.UserExtraMoodQuestions = "Yes";
    }

    public void RadioButtonNoMoreQues(View view)
    {
        RadioButton yes = (RadioButton) findViewById(R.id.RadioButton_Yes);
        RadioButton no = (RadioButton) findViewById(R.id.RadioButton_No);

        Button register = (Button) findViewById(R.id.btn_Submit);
        Button next = (Button) findViewById(R.id.btn_Next);

        yes.setBackgroundResource(R.drawable.yesunselected);
        no.setBackgroundResource(R.drawable.noselected);

        next.setVisibility(view.INVISIBLE);
        register.setVisibility(view.VISIBLE);

        //Used in case the user backtracks to this page we want the user's choice preselected.
        Global.UserExtraMoodQuestions = "No";
    }

    public void RadioButtonFemale(View view)
    {
        RadioButton GenderMale = (RadioButton) findViewById(R.id.RadioButton_Male);
        RadioButton GenderFemale = (RadioButton) findViewById(R.id.RadioButton_Female);
        RadioButton GenderOther = (RadioButton) findViewById(R.id.RadioButton_Other);

        GenderMale.setBackgroundResource(R.drawable.maleunselected);
        GenderFemale.setBackgroundResource(R.drawable.femaleselected);
        GenderOther.setBackgroundResource(R.drawable.otherunselected);
    }

    public void RadioButtonOther(View view)
    {
        RadioButton GenderMale = (RadioButton) findViewById(R.id.RadioButton_Male);
        RadioButton GenderFemale = (RadioButton) findViewById(R.id.RadioButton_Female);
        RadioButton GenderOther = (RadioButton) findViewById(R.id.RadioButton_Other);

        GenderMale.setBackgroundResource(R.drawable.maleunselected);
        GenderFemale.setBackgroundResource(R.drawable.femaleunselected);
        GenderOther.setBackgroundResource(R.drawable.otherselected);
    }

    //Repopulate the fields if user has gone back.
    private void ShowUserDetails()
    {
        String UserPassword = Global.UserPassword;

        Call<String> response = client.GetUserDetailsRegistration(BackUserID, UserPassword);
        response.enqueue(new Callback<String>()
        {
            @Override
            public void onResponse(Call<String> call, Response<String> response)
            {
                Log.d("retrofitclick", "SUCCESS: " + response.raw());
                if(response.body().equals("Incorrect UserID or Password. Query not executed."))
                    Toast.makeText(context, "Failed to get details from server",
                            Toast.LENGTH_SHORT).show();
                else
                {
                    String UsersInformation = response.body();
                    String [] UserDetails = UsersInformation.split("\n");
                    String TheFirstName = UserDetails[0].replace("FirstName: ", "");
                    String TheLastName = UserDetails[1].replace("LastName: ", "");
                    String TheEmail = UserDetails[2].replace("EmailAddress: ", "");
                    String TheDateOfBirth = UserDetails[3].replace("DateOfBirth: ", "");
                    String TheGender = UserDetails[4].replace("Gender: ", "");
                    String AcceptedEthicsStatement = UserDetails[5].replace("AcceptedEthicsStatement: ", "");

                    //Populate all the fields with the database data.
                    TextView FirstName = (TextView)findViewById(R.id.EditText_FirstName);
                    FirstName.setText(TheFirstName);

                    TextView LastName = (TextView)findViewById(R.id.EditText_LastName);
                    LastName.setText(TheLastName);

                    TextView Email = (TextView)findViewById(R.id.EditText_Email);
                    Email.setText(TheEmail);
                    CurrentEmailAddress = TheEmail;

                    if (!TheDateOfBirth.equals(""))
                    {
                        try
                        {
                            final CommonFunctions Common = new CommonFunctions();
                            Date DateOfBirthDate = Common.DateFromStringFromSQLToAustraliaFormat(TheDateOfBirth);
                            SimpleDateFormat DateOfBirthFormat = new SimpleDateFormat("dd-MM-yyyy");
                            TheDateOfBirth = DateOfBirthFormat.format(DateOfBirthDate);
                            TheDateOfBirth = TheDateOfBirth.replace("-", "/");
                        }
                        catch (ParseException e)
                        {
                            e.printStackTrace();
                            TheDateOfBirth = "";
                        }
                    }

                    TextView DateOfBirth = (TextView)findViewById(R.id.EditText_DateOfBirth);
                    DateOfBirth.setText(TheDateOfBirth);

                    final RadioButton GenderMale = (RadioButton) findViewById(R.id.RadioButton_Male);
                    final RadioButton GenderFemale = (RadioButton) findViewById(R.id.RadioButton_Female);
                    final RadioButton GenderOther = (RadioButton) findViewById(R.id.RadioButton_Other);

                    /*Make sure that the buttons have their image and checked status set to what
                    the user put in the database*/
                    if (TheGender.equals("Male"))
                    {
                        GenderMale.setBackgroundResource(R.drawable.maleselected);
                        GenderFemale.setBackgroundResource(R.drawable.femaleunselected);
                        GenderOther.setBackgroundResource(R.drawable.otherunselected);
                        GenderMale.setChecked(true);
                    }
                    else if (TheGender.equals("Female"))
                    {
                        GenderMale.setBackgroundResource(R.drawable.maleunselected);
                        GenderFemale.setBackgroundResource(R.drawable.femaleselected);
                        GenderOther.setBackgroundResource(R.drawable.otherunselected);
                        GenderFemale.setChecked(true);
                    }
                    else if (TheGender.equals("Other"))
                    {
                        GenderMale.setBackgroundResource(R.drawable.maleunselected);
                        GenderFemale.setBackgroundResource(R.drawable.femaleunselected);
                        GenderOther.setBackgroundResource(R.drawable.otherselected);
                        GenderOther.setChecked(true);
                    }

                    //Set the research buttons based on the user's previous choice.
                    final RadioButton YesResearch = (RadioButton) findViewById(R.id.RadioButton_Yes1);
                    final RadioButton NoResearch = (RadioButton) findViewById(R.id.RadioButton_No1);

                    if (AcceptedEthicsStatement.equals("Yes"))
                    {
                        YesResearch.setBackgroundResource(R.drawable.yesselected);
                        NoResearch.setBackgroundResource(R.drawable.nounselected);
                        YesResearch.setChecked(true);
                    }
                    else
                    {
                        YesResearch.setBackgroundResource(R.drawable.yesunselected);
                        NoResearch.setBackgroundResource(R.drawable.noselected);
                        NoResearch.setChecked(true);
                    }

                    //Set the mood buttons based on the user's previous choice.
                    final RadioButton YesMoodQuestions = (RadioButton) findViewById(R.id.RadioButton_Yes);
                    final RadioButton NoMoodQuestions = (RadioButton) findViewById(R.id.RadioButton_No);

                    Button register = (Button) findViewById(R.id.btn_Submit);
                    Button next = (Button) findViewById(R.id.btn_Next);

                    if (Global.UserExtraMoodQuestions.equals("Yes"))
                    {
                        next.setVisibility(View.VISIBLE);
                        register.setVisibility(View.INVISIBLE);
                        YesMoodQuestions.setBackgroundResource(R.drawable.yesselected);
                        NoMoodQuestions.setBackgroundResource(R.drawable.nounselected);
                        YesMoodQuestions.setChecked(true);
                    }
                    else
                    {
                        next.setVisibility(View.INVISIBLE);
                        register.setVisibility(View.VISIBLE);
                        YesMoodQuestions.setBackgroundResource(R.drawable.yesunselected);
                        NoMoodQuestions.setBackgroundResource(R.drawable.noselected);
                        NoMoodQuestions.setChecked(true);
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

    private void ValidateForm(String ButtonName)
    {
        final String TheButton = ButtonName;
        boolean ValidationSuccessful = true;
        final CommonFunctions Common = new CommonFunctions();
        String InvalidMessage = "";

        //Convert the contents of the text boxes to strings.
        TextView FirstName = (TextView)findViewById(R.id.EditText_FirstName);
        TextView LastName = (TextView)findViewById(R.id.EditText_LastName);
        TextView Email = (TextView)findViewById(R.id.EditText_Email);
        TextView DateOfBirth = (TextView)findViewById(R.id.EditText_DateOfBirth);
        TextView NewPassword = (TextView)findViewById(R.id.EditText_Password);
        TextView NewPasswordRepeat = (TextView)findViewById(R.id.EditText_ConfirmPassword);

        String FName = FirstName.getText().toString();
        String LName = LastName.getText().toString();
        String TheEmail = Email.getText().toString();
        String TheDateOfBirth = DateOfBirth.getText().toString();
        String NewPass = NewPassword.getText().toString();
        String NewPassRepeat = NewPasswordRepeat.getText().toString();

        //Get the gender.
        RadioButton GenderFemale = (RadioButton)findViewById(R.id.RadioButton_Female);
        RadioButton GenderMale = (RadioButton)findViewById(R.id.RadioButton_Male);
        RadioButton GenderOther = (RadioButton)findViewById(R.id.RadioButton_Other);

        //Validation dialogue.
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Invalid Edits");
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Ok",new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog,int id)
                    {
                        //No action to be taken until validation issue is resolved.
                    }
                });

        if (FName.equals("") && LName.equals(""))
        {
            ValidationSuccessful = false;
            InvalidMessage = "You need to enter either a First Name or a Last Name.";
            alertDialogBuilder.setMessage(InvalidMessage);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        if (TheEmail.equals("") && ValidationSuccessful)
        {
            ValidationSuccessful = false;
            InvalidMessage = "You must enter an Email Address.";
            alertDialogBuilder.setMessage(InvalidMessage);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        if (!Common.IsEmailValid(TheEmail) && !TheEmail.equals("") && ValidationSuccessful)
        {
            ValidationSuccessful = false;
            InvalidMessage = "Email Address is invalid.";
            alertDialogBuilder.setMessage(InvalidMessage);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        /*Check if the email address is used by another user and also don't trigger validation if
        the user is not changing their email address (if they already set this before).
        Note actual checking of Email Uniqueness is actually done as soon as the user enters
        something into the email text box, this is due to the fact that the API call is made
        AFTER all the other code is run in this validation function.*/
        if (!IsEmailUnique && !TheEmail.toLowerCase().equals(CurrentEmailAddress.toLowerCase())
                && ValidationSuccessful)
        {
            ValidationSuccessful = false;
            InvalidMessage = "Email Address is already in use. Please pick another one.";
            alertDialogBuilder.setMessage(InvalidMessage);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        if (NewPass.equals("") && ValidationSuccessful)
        {
            ValidationSuccessful = false;
            InvalidMessage = "You need to enter your password.";
            alertDialogBuilder.setMessage(InvalidMessage);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        if (NewPassRepeat.equals("") && ValidationSuccessful)
        {
            ValidationSuccessful = false;
            InvalidMessage = "You need to enter your password again.";
            alertDialogBuilder.setMessage(InvalidMessage);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        if (!Common.ValidPassword(NewPass) && ValidationSuccessful)
        {
            ValidationSuccessful = false;
            InvalidMessage = "Password must have at least 8 characters, have at least" +
                    " one upper case, lower case letter, a number and a special character.";
            alertDialogBuilder.setMessage(InvalidMessage);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        //Check if both new passwords match.
        if (!NewPass.equals(NewPassRepeat) && ValidationSuccessful)
        {
            ValidationSuccessful = false;
            InvalidMessage = "The password you entered does not match what is in confirm password field.";
            alertDialogBuilder.setMessage(InvalidMessage);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        if (!TheDateOfBirth.equals("") && ValidationSuccessful)
        {
            try
            {
                Date DOBTest = Common.DateFromStringAustraliaFormat(TheDateOfBirth);
                if (DOBTest.after(new Date()))
                {
                    ValidationSuccessful = false;
                    InvalidMessage = "Date of Birth is invalid as it is in the future.";
                    alertDialogBuilder.setMessage(InvalidMessage);
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
                else
                {
                    SimpleDateFormat DateOfBirthFormat = new SimpleDateFormat("yyyy-MM-dd");
                    TheDateOfBirth = DateOfBirthFormat.format(DOBTest);
                }
            }
            catch (ParseException e)
            {
                e.printStackTrace();
                ValidationSuccessful = false;
                InvalidMessage = "Date of Birth must be a valid date in the format of " +
                        "Day, Month and Year (DD/MM/YYYY).";
                alertDialogBuilder.setMessage(InvalidMessage);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }

        RadioGroup researchques = (RadioGroup) findViewById(R.id.RadioGroup_MoreResearch);
        RadioGroup moremood = (RadioGroup) findViewById(R.id.RadioGroup_MoreMood);

        /*Checking if the questions regarding the research and the extra mood questions in checked
        for it to continue on*/
        if (ValidationSuccessful && (researchques.getCheckedRadioButtonId() == -1))
        {
            ValidationSuccessful = false;
            InvalidMessage = "All fields must be filled and/or selected";
            alertDialogBuilder.setMessage(InvalidMessage);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        if (ValidationSuccessful && (moremood.getCheckedRadioButtonId() == -1))
        {
            ValidationSuccessful = false;
            InvalidMessage = "All fields must be filled and/or selected";
            alertDialogBuilder.setMessage(InvalidMessage);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        String TheGender = "";
        if (GenderMale.isChecked())
        {
            TheGender = "Male";
        }
        else if(GenderFemale.isChecked())
        {
            TheGender = "Female";
        }
        else if(GenderOther.isChecked())
        {
            TheGender = "Other";
        }
        else
        {
            ValidationSuccessful = false;
            InvalidMessage = "No gender. This error should never happen.";
            alertDialogBuilder.setMessage(InvalidMessage);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        //Change blank strings to - so they can be passed in the URL.
        if (FName.equals(""))
        {
            FName = "-";
        }
        if (LName.equals(""))
        {
            LName = "-";
        }
        if (TheEmail.equals(""))
        {
            TheEmail = "-";
        }
        if (TheDateOfBirth.equals(""))
        {
            TheDateOfBirth = "-";
        }

        /*Insert if this is the first time the user is on this page, otherwise just update what is
        already there.*/
        if (ValidationSuccessful && (BackUserID.equals("") || BackUserID == null))
        {
            final String FinalNewPass = Common.EncryptPassword(NewPass);
            Call<String> response = client.InsertNewUser(FName, LName, TheEmail, TheDateOfBirth,
                    TheGender, EthicsAgreement, FinalNewPass);
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
                            Toast.makeText(context, "Failed to insert new user", Toast.LENGTH_SHORT).show();
                        else
                        {
                            //Storing the new created user ID as global so that it can be used throughout the application.
                            UserID = response.body();
                            Global.UserID = UserID;
                            BackUserID = UserID;
                            Global.UserPassword = FinalNewPass;

                            if (TheButton.contains("btn_Next"))
                            {
                                Intent intent = new Intent(Register.this, RegisterSecondPage.class);
                                startActivity(intent);
                            }
                            else if (TheButton.contains("btn_Submit"))
                            {
                                Intent intent = new Intent(Register.this, OtherPlatforms.class);
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(context, "Invalid Button! This error should never occur!", Toast.LENGTH_SHORT).show();
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
        else if(ValidationSuccessful)
        {
            NewPass = Common.EncryptPassword(NewPass);

            Call<String> response = client.UpdateNewUser(FName, LName, TheEmail, TheDateOfBirth,
                    TheGender, EthicsAgreement, BackUserID, NewPass);
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
                        if(!response.body().equals("Successful"))
                            Toast.makeText(context, "Failed to update user", Toast.LENGTH_SHORT).show();
                        else
                        {
                            if (TheButton.contains("btn_Next"))
                            {
                                Intent intent = new Intent(Register.this, RegisterSecondPage.class);
                                startActivity(intent);
                            }
                            else if (TheButton.contains("btn_Submit"))
                            {
                                Intent intent = new Intent(Register.this, OtherPlatforms.class);
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(context, "Invalid Button! This error should never occur!", Toast.LENGTH_SHORT).show();
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

    void CheckEmailAddress(String EmailAddress)
    {
        Call<String> response = client.IsEmailAddressUnique(EmailAddress);
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
                    if(response.body().equals("YES"))
                        IsEmailUnique = true;
                    else
                        IsEmailUnique = false;
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
