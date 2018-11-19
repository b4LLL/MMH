package com.example.kirmi.ks1807;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProfileSettings extends Fragment
{
    String UserID = "", CurrentEmailAddress = "";
    final CommonFunctions Common = new CommonFunctions();

    private RadioGroup gender;
    private RadioButton genderMale, genderFemale, genderOther;
    private EditText firstN, lastN, editemail, editdob, oldpass, newpass, newpassagain;
    private Button options, editback, changepassback, btnUpdate, signout, changePassword;
    private LinearLayout userdetails, updatepass;
    private String[] UserDetails;
    Retrofit retrofit = RestInterface.getClient();
    RestInterface.Ks1807Client client;

    boolean IsEmailUnique = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.activity_profile_settingstab, container, false);

        UserID = Global.UserID;
        userdetails = (LinearLayout)view.findViewById(R.id.user_details);

        firstN = (EditText)view.findViewById(R.id.editText_EditFirstName);
        lastN = (EditText)view.findViewById(R.id.editText_EditLastName);
        editemail = (EditText)view.findViewById(R.id.editText_EditEmail);
        editdob = (EditText)view.findViewById(R.id.editText_EditDateOfBirth);

        gender = (RadioGroup)view.findViewById(R.id.RadioGroup_EditGenderSelect);
        genderMale = (RadioButton)view.findViewById(R.id.RadioButton_EditMale);
        genderFemale = (RadioButton)view.findViewById(R.id.RadioButton_EditFemale);
        genderOther = (RadioButton)view.findViewById(R.id.RadioButton_EditOther);

        gender.setEnabled(false);
        editback = (Button)view.findViewById(R.id.btn_profileeditsettingback);
        btnUpdate = (Button)view.findViewById(R.id.btn_updateprofile);

        updatepass = (LinearLayout)view.findViewById(R.id.changepasscontent);

        oldpass = (EditText) view.findViewById(R.id.EditText_OldPassword);
        newpass = (EditText) view.findViewById(R.id.EditText_NewPassword);
        newpassagain = (EditText)view.findViewById(R.id.EditTextNewPasswordAgain);

        changepassback = (Button)view.findViewById(R.id.btn_profilechangepasssettingback);
        changePassword = (Button) view.findViewById(R.id.btnchangepass);

        client = retrofit.create(RestInterface.Ks1807Client.class);

        String UserPassword = Global.UserPassword;

        //Check that the email is unique or not before the main validation is run.
        final EditText EmailTextBoxValidate = editemail;
        EmailTextBoxValidate.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                String TheEmail = EmailTextBoxValidate.getText().toString();
                if (!hasFocus && !TheEmail.equals(""))
                {
                    CheckEmailAddress(TheEmail);
                }
            }
        });

        Call<String> response = client.GetUserDetails(UserID, UserPassword);
        response.enqueue(new Callback<String>()
        {
            @Override
            public void onResponse(Call<String> call, Response<String> response)
            {
                Log.d("retrofitclick", "SUCCESS: " + response.raw());

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
                        String UsersInformation = response.body();
                        UserDetails = UsersInformation.split("\n");
                        UserDetails[0] = UserDetails[0].replace("FirstName: ", "");
                        UserDetails[1] = UserDetails[1].replace("LastName: ", "");
                        UserDetails[2] = UserDetails[2].replace("EmailAddress: ", "");
                        UserDetails[3] = UserDetails[3].replace("DateOfBirth: ", "");
                        UserDetails[4] = UserDetails[4].replace("Gender: ", "");

                        //Function to show all the details within respective text element.
                        DisplayUserDetails(UserDetails);
                    }
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t)
            {
                fail_LoginNetwork();
            }
        });

        if (userdetails.getVisibility() != View.VISIBLE)
        {
            userdetails.setVisibility(View.VISIBLE);
            updatepass.setVisibility(View.GONE);
        }

        //Getting the selection from the options dropdown.
        options = (Button)view.findViewById(R.id.btn_Options);
        options.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Showing the popup menu with the two different options edit and change password
                Context wrapper = new ContextThemeWrapper(getActivity(), R.style.popUpOptions);
                PopupMenu popup = new PopupMenu(wrapper, options);
                popup.getMenuInflater().inflate(R.menu.profileoptions, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem)
                    {
                        //Once a menu item is selected, doing relevant tasks to the menu item.
                        switch (menuItem.getItemId())
                        {
                            case R.id.option_edit:
                                /*Setting some elements not relevant to the edit section to be
                                invisible and some relevant section to be visible*/
                                options.setVisibility(View.INVISIBLE);
                                signout.setVisibility(View.INVISIBLE);
                                updatepass.setVisibility(View.GONE);
                                editback.setVisibility(View.VISIBLE);
                                btnUpdate.setVisibility(View.VISIBLE);

                                /*Since the text fields have been disabled at the start of the page,
                                fields should be enabled for the user to edit.*/
                                enableAllFields();

                                //Changing the image according the screen design once user is on edit page
                                if (genderMale.isChecked())
                                {
                                    genderMale.setBackgroundResource(R.drawable.settingseditmaleselected);
                                    genderFemale.setBackgroundResource(R.drawable.settingseditfemalenormal);
                                    genderOther.setBackgroundResource(R.drawable.settingseditothernormal);
                                }

                                if (genderFemale.isChecked())
                                {
                                    genderMale.setBackgroundResource(R.drawable.settingseditmalenormal);
                                    genderFemale.setBackgroundResource(R.drawable.settingseditfemaleselected);
                                    genderOther.setBackgroundResource(R.drawable.settingseditothernormal);
                                }

                                if (genderOther.isChecked())
                                {
                                    genderMale.setBackgroundResource(R.drawable.settingseditmalenormal);
                                    genderFemale.setBackgroundResource(R.drawable.settingseditfemalenormal);
                                    genderOther.setBackgroundResource(R.drawable.settingseditotherselected);
                                }

                                //Setting the correct images when a button is selected
                                genderMale.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        genderMale.setBackgroundResource(R.drawable.settingseditmaleselected);
                                        genderFemale.setBackgroundResource(R.drawable.settingseditfemalenormal);
                                        genderOther.setBackgroundResource(R.drawable.settingseditothernormal);
                                    }
                                });
                                genderFemale.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        genderMale.setBackgroundResource(R.drawable.settingseditmalenormal);
                                        genderFemale.setBackgroundResource(R.drawable.settingseditfemaleselected);
                                        genderOther.setBackgroundResource(R.drawable.settingseditothernormal);
                                    }
                                });
                                genderOther.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        genderMale.setBackgroundResource(R.drawable.settingseditmalenormal);
                                        genderFemale.setBackgroundResource(R.drawable.settingseditfemalenormal);
                                        genderOther.setBackgroundResource(R.drawable.settingseditotherselected);
                                    }
                                });

                                /*On click of the update button, validate the inputs from the user
                                and update details in the database.*/
                                btnUpdate.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        if (ValidateForm())
                                        {
                                            String UserPassword = Global.UserPassword;

                                            Call<String> response = client.GetUserDetails(UserID, UserPassword);
                                            response.enqueue(new Callback<String>()
                                            {
                                                @Override
                                                public void onResponse(Call<String> call, Response<String> response)
                                                {
                                                    Log.d("retrofitclick", "SUCCESS: " + response.raw());

                                                    if(response.code() == 404)
                                                    {
                                                        Toast.makeText(getContext(),
                                                                "404 Error. Server did not return a response.", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else
                                                    {
                                                        if(response.body().equals("Incorrect UserID or Password. Query not executed."))
                                                            Toast.makeText(getActivity(), "Failed to get details from server", Toast.LENGTH_SHORT).show();
                                                        else
                                                        {
                                                            String UsersInformation = response.body();
                                                            UserDetails = UsersInformation.split("\n");
                                                            UserDetails[0] = UserDetails[0].replace("FirstName: ", "");
                                                            UserDetails[1] = UserDetails[1].replace("LastName: ", "");
                                                            UserDetails[2] = UserDetails[2].replace("EmailAddress: ", "");
                                                            UserDetails[3] = UserDetails[3].replace("DateOfBirth: ", "");
                                                            UserDetails[4] = UserDetails[4].replace("Gender: ", "");

                                                            //Function to show all the details within respective text element.
                                                            DisplayUserDetails(UserDetails);
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
                                });

                                /*If the back button is selected then all the fields are disabled,
                                the fields are updated to what is in the database since the fields
                                are the same ones being used as the normal view of the activity*/
                                editback.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view)
                                    {

                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                                        alertDialogBuilder.setTitle("Confirm exit");
                                        alertDialogBuilder
                                                .setMessage("Are you sure you wish to go back? All changes will be discarded.")
                                                .setCancelable(false)
                                                .setPositiveButton("Yes",new DialogInterface.OnClickListener()
                                                {
                                                    public void onClick(DialogInterface dialog,int id)
                                                    {
                                                        setProfileBackFromEdit();
                                                        String UserPassword = Global.UserPassword;

                                                        Call<String> response = client.GetUserDetails(UserID, UserPassword);
                                                        response.enqueue(new Callback<String>()
                                                        {
                                                            @Override
                                                            public void onResponse(Call<String> call, Response<String> response)
                                                            {
                                                                Log.d("retrofitclick", "SUCCESS: " + response.raw());

                                                                if(response.code() == 404)
                                                                {
                                                                    Toast.makeText(getContext(),
                                                                            "404 Error. Server did not return a response.", Toast.LENGTH_SHORT).show();
                                                                }
                                                                else
                                                                {
                                                                    if(response.body().equals("Incorrect UserID or Password. Query not executed."))
                                                                        Toast.makeText(getActivity(), "Failed to get details from server",
                                                                                Toast.LENGTH_SHORT).show();
                                                                    else
                                                                    {
                                                                        String UsersInformation = response.body();
                                                                        UserDetails = UsersInformation.split("\n");
                                                                        UserDetails[0] = UserDetails[0].replace("FirstName: ", "");
                                                                        UserDetails[1] = UserDetails[1].replace("LastName: ", "");
                                                                        UserDetails[2] = UserDetails[2].replace("EmailAddress: ", "");
                                                                        UserDetails[3] = UserDetails[3].replace("DateOfBirth: ", "");
                                                                        UserDetails[4] = UserDetails[4].replace("Gender: ", "");

                                                                        //Function to show all the details within respective text element.
                                                                        DisplayUserDetails(UserDetails);
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
                                return true;

                                //The following is run if the change password menu item is selected.
                                case R.id.option_changepass:
                                userdetails.setVisibility(View.GONE);
                                options.setVisibility(View.INVISIBLE);

                                //Setting the fields and buttons relevant to change user password.
                                changepassback.setVisibility(View.VISIBLE);
                                updatepass.setVisibility(View.VISIBLE);

                                //If the back button is selected then display the content of the normal profile page.
                                changepassback.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view) {
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                                        alertDialogBuilder.setTitle("Confirm exit");
                                        alertDialogBuilder
                                                .setMessage("Are you sure you wish to go back? All changes will be discarded.")
                                                .setCancelable(false)
                                                .setPositiveButton("Yes",new DialogInterface.OnClickListener()
                                                {
                                                    public void onClick(DialogInterface dialog,int id)
                                                    {
                                                        setProfileBackFromChangePass();
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

                                /*Validating the user input to change password once the button is
                                clicked and taking the user back to the main profile*/
                                changePassword.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        if (ValidateChangePasswordForm())
                                        {
                                            Toast.makeText(getActivity(),
                                                    "Successfully changed password", Toast.LENGTH_SHORT).show();
                                            setProfileBackFromChangePass();
                                        }

                                    }
                                });
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //Used to actually show the menu items with the popup.
                popup.show();
            }
        });

        /*If the user chooses to sign out then this takes them to the login page and sets the user
        ID as empty as the user is no longer logged in*/
        signout = (Button)view.findViewById(R.id.btn_signout);
        signout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setTitle("Confirm logout");
                alertDialogBuilder
                        .setMessage("Are you sure that you want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog,int id)
                            {
                                Global.UserID = "";
                                if(BackgroundService.isRunning)
                                {
                                    new BackgroundServiceStarter().onEnd(getContext(), new Intent());
                                }
                                Intent intent = new Intent(getActivity(), MainActivity.class);
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
        });
        return view;
    }

    //Assigning the fields in the profile with the information found from the database about the user.
    public void DisplayUserDetails(String[] UserDetails)
    {
        String TheDateOfBirth = UserDetails[3];
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

        firstN.setText(UserDetails[0]);
        lastN.setText(UserDetails[1]);
        editemail.setText(UserDetails[2]);
        CurrentEmailAddress = UserDetails[2];
        editdob.setText(TheDateOfBirth);
        String Gender = UserDetails[4];

        /*Make sure that the buttons have their image and checked status set to what the user
        put in the database*/
        if (Gender.equals("Male"))
        {
            genderMale.setBackgroundResource(R.drawable.settingsmaleselected);
            genderFemale.setBackgroundResource(R.drawable.settingsfemaleunselected);
            genderOther.setBackgroundResource(R.drawable.settingsotherunselected);
            genderMale.setChecked(true);
        }
        else if (Gender.equals("Female"))
        {
            genderMale.setBackgroundResource(R.drawable.settingsmaleunselected);
            genderFemale.setBackgroundResource(R.drawable.settingsfemaleselected);
            genderOther.setBackgroundResource(R.drawable.settingsotherunselected);
            genderFemale.setChecked(true);
        }
        else if (Gender.equals("Other"))
        {
            genderMale.setBackgroundResource(R.drawable.settingsmaleunselected);
            genderFemale.setBackgroundResource(R.drawable.settingsfemaleunselected);
            genderOther.setBackgroundResource(R.drawable.settingsotherselected);
            genderOther.setChecked(true);
        }
    }

    //Function used to set the profile back to normal after coming back from editing the user details.
    public void setProfileBackFromEdit()
    {
        options.setVisibility(View.VISIBLE);
        signout.setVisibility(View.VISIBLE);
        editback.setVisibility(View.INVISIBLE);
        btnUpdate.setVisibility(View.INVISIBLE);
        disableAllFields();
    }

    //Function used to set the profile back to normal after coming back from changing password.
    public void setProfileBackFromChangePass()
    {
        options.setVisibility(View.VISIBLE);
        updatepass.setVisibility(View.GONE);
        changepassback.setVisibility(View.INVISIBLE);
        userdetails.setVisibility(View.VISIBLE);
        disableAllFields();
    }

    //Function that enables all fields and radio buttons for the user to edit.
    public void enableAllFields()
    {
        firstN.setEnabled(true);
        firstN.setTextColor(Color.WHITE);
        firstN.setHint("First Name");

        lastN.setEnabled(true);
        lastN.setTextColor(Color.WHITE);
        lastN.setHint("Last Name");

        editemail.setEnabled(true);
        editemail.setTextColor(Color.WHITE);
        editemail.setHint("Email");

        editdob.setEnabled(true);
        editdob.setTextColor(Color.WHITE);
        editdob.setHint("DD/MM/YYYY");

        gender.setEnabled(true);
        genderFemale.setEnabled(true);
        genderMale.setEnabled(true);
        genderOther.setEnabled(true);
    }

    //Function that disables all fields and radio buttons so that the user cannot edit and only view the details
    public void disableAllFields()
    {
        firstN.setEnabled(false);
        firstN.setTextColor(getResources().getColor(R.color.darkgrey));
        firstN.setHint("");

        lastN.setEnabled(false);
        lastN.setTextColor(getResources().getColor(R.color.darkgrey));
        lastN.setHint("");

        editemail.setEnabled(false);
        editemail.setTextColor(getResources().getColor(R.color.darkgrey));
        editemail.setHint("");

        editdob.setEnabled(false);
        editdob.setTextColor(getResources().getColor(R.color.darkgrey));
        editdob.setHint("");

        gender.setEnabled(false);
        genderFemale.setEnabled(false);
        genderMale.setEnabled(false);
        genderOther.setEnabled(false);
    }

    //Validating edited user details and updating it in the database
    private boolean ValidateForm()
    {
        boolean ValidationSuccessful = true;
        String InvalidMessage = "";

        String FName = firstN.getText().toString();
        String LName = lastN.getText().toString();
        String TheEmail = editemail.getText().toString();
        String TheDateOfBirth = editdob.getText().toString();

        //Validation dialogue
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
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
        the user is not changing their address. Note that the check for email uniqueness is made
        when the user unfocuses the email text box as otherwise the check is done AFTER the
        update.*/
        if (!IsEmailUnique && !TheEmail.toLowerCase().equals(CurrentEmailAddress.toLowerCase())
                && ValidationSuccessful)
        {
            ValidationSuccessful = false;
            InvalidMessage = "Email Address is already in use. Please pick another one.";
            alertDialogBuilder.setMessage(InvalidMessage);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        if (!TheDateOfBirth.equals("") && ValidationSuccessful)
        {
            CommonFunctions Common = new CommonFunctions();
            try
            {
                //Checking if the entered date of birth is in past or not
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

        String TheGender = "";
        if (genderMale.isChecked())
        {
            TheGender = "Male";
        }
        else if(genderFemale.isChecked())
        {
            TheGender = "Female";
        }
        else if(genderOther.isChecked())
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

        if(ValidationSuccessful)
        {
            String UserPassword = Global.UserPassword;

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

            //Used to force the display change to use the user's new details.
            final String DisplayedFirstName = FName;
            final String DisplayedLastName = LName;
            final String DisplayedEmail = TheEmail;
            final String DisplayedDOB = TheDateOfBirth;
            final String DisplayedGender = TheGender;

            Call<String> response = client.UpdateUser(
                    FName, LName, TheEmail, TheDateOfBirth, TheGender, UserID, UserPassword);
            response.enqueue(new Callback<String>()
            {
                @Override
                public void onResponse(Call<String> call, Response<String> response)
                {
                    Log.d("retrofitclick", "SUCCESS: " + response.raw());

                    if(response.code() == 404)
                    {
                        Toast.makeText(getContext(),
                                "404 Error. Server did not return a response.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if(response.body().equals("Incorrect UserID or Password. Query not executed."))
                            Toast.makeText(getActivity(), "Failed to update your details", Toast.LENGTH_SHORT).show();
                        else
                        {
                            Toast.makeText(getActivity(), "Successfully updated your details", Toast.LENGTH_SHORT).show();
                            setProfileBackFromEdit();

                        /*Code below forces an update of user changes as the retrieval of user
                        details actually takes place BEFORE the update.*/
                            firstN.setText(DisplayedFirstName);
                            lastN.setText(DisplayedLastName);
                            editemail.setText(DisplayedEmail);

                            //Format the date so it appears correctly after the update.
                            String DOB = DisplayedDOB;
                            if (!DOB.equals(""))
                            {
                                try
                                {
                                    final CommonFunctions Common = new CommonFunctions();
                                    Date DateOfBirthDate = Common.DateFromStringFromSQLToAustraliaFormat(DOB);
                                    SimpleDateFormat DateOfBirthFormat = new SimpleDateFormat("dd-MM-yyyy");
                                    DOB = DateOfBirthFormat.format(DateOfBirthDate);
                                    DOB = DOB.replace("-", "/");
                                }
                                catch (ParseException e)
                                {
                                    e.printStackTrace();
                                    DOB = "";
                                }
                            }

                            editdob.setText(DOB);

                        /*Make sure that the buttons have their image and checked status set to
                        what the user chose.*/
                            if (DisplayedGender.equals("Male"))
                            {
                                genderMale.setBackgroundResource(R.drawable.settingsmaleselected);
                                genderFemale.setBackgroundResource(R.drawable.settingsfemaleunselected);
                                genderOther.setBackgroundResource(R.drawable.settingsotherunselected);
                                genderMale.setChecked(true);
                            }
                            else if (DisplayedGender.equals("Female"))
                            {
                                genderMale.setBackgroundResource(R.drawable.settingsmaleunselected);
                                genderFemale.setBackgroundResource(R.drawable.settingsfemaleselected);
                                genderOther.setBackgroundResource(R.drawable.settingsotherunselected);
                                genderFemale.setChecked(true);
                            }
                            else if (DisplayedGender.equals("Other"))
                            {
                                genderMale.setBackgroundResource(R.drawable.settingsmaleunselected);
                                genderFemale.setBackgroundResource(R.drawable.settingsfemaleunselected);
                                genderOther.setBackgroundResource(R.drawable.settingsotherselected);
                                genderOther.setChecked(true);
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
        return ValidationSuccessful;
    }

    //Validating the password input from the user to update their password
    private boolean ValidateChangePasswordForm()
    {
        boolean ValidationSuccessful = true;

        String InvalidMessage = "";

        //Convert the contents of the text boxes to strings
        String OldPass = oldpass.getText().toString();
        String NewPass = newpass.getText().toString();
        String NewPassRepeat = newpassagain.getText().toString();

        //Validation dialogue
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Invalid Password");
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Ok",new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog,int id)
                    {
                        //No action to be taken until validation issue is resolved.
                    }
                });

        //Checks if password fields are empty. Display only the first error we find to the user.
        if (OldPass.equals(""))
        {
            ValidationSuccessful = false;
            InvalidMessage = "You need to enter your old password.";
            alertDialogBuilder.setMessage(InvalidMessage);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        if (NewPass.equals("") && ValidationSuccessful)
        {
            ValidationSuccessful = false;
            InvalidMessage = "You need to enter your new password.";
            alertDialogBuilder.setMessage(InvalidMessage);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        if (NewPassRepeat.equals("") && ValidationSuccessful)
        {
            ValidationSuccessful = false;
            InvalidMessage = "You need to enter your new password again.";
            alertDialogBuilder.setMessage(InvalidMessage);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        //Check if both new passwords match.
        if (!NewPass.equals(NewPassRepeat) && ValidationSuccessful)
        {
            ValidationSuccessful = false;
            InvalidMessage = "The new password you entered does not match what is in confirm password field.";
            alertDialogBuilder.setMessage(InvalidMessage);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        //Checking if the password is strong.
        if (!Common.ValidPassword(NewPass) && ValidationSuccessful)
        {
            ValidationSuccessful = false;
            InvalidMessage = "Password must have at least 8 characters, have at least" +
                    " one upper case, lower case letter, a number and a special character.";
            alertDialogBuilder.setMessage(InvalidMessage);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        String TestPassword = Common.EncryptPassword(OldPass);

        //Checking ID the old password entered here is the same the user's current password.
        if (!TestPassword.equals(Global.UserPassword) && ValidationSuccessful)
        {
            ValidationSuccessful = false;
            InvalidMessage = "The old password you have specified does not match your current password.";
            alertDialogBuilder.setMessage(InvalidMessage);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        if (OldPass.equals(NewPass) && ValidationSuccessful)
        {
            ValidationSuccessful = false;
            InvalidMessage = "The new password is exactly the same as your old password! Please use a different password.";
            alertDialogBuilder.setMessage(InvalidMessage);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        //If all the validations above are successful, then encrypt the password and update the database.
        if(ValidationSuccessful)
        {
            final String EncryptedNewPass = Common.EncryptPassword(NewPass);
            String UserPassword = Global.UserPassword;

            Call<String> response = client.UpdatePassword(EncryptedNewPass, UserID, UserPassword);
            response.enqueue(new Callback<String>()
            {
                @Override
                public void onResponse(Call<String> call, Response<String> response)
                {
                    Log.d("retrofitclick", "SUCCESS: " + response.raw());
                    if(response.body().equals("Incorrect UserID or Password. Query not executed."))
                        Toast.makeText(getActivity(), "Failed to update the server", Toast.LENGTH_SHORT).show();
                    else
                    {
                        //Update the new global password
                        Global.UserPassword = EncryptedNewPass;

                        //Used to clear the password fields after completing the change.
                        oldpass.setText("");
                        newpass.setText("");
                        newpassagain.setText("");
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t)
                {
                    fail_LoginNetwork();
                }
            });
        }
        return ValidationSuccessful;
    }

    void fail_LoginNetwork()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
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
                    Toast.makeText(getContext(),
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
