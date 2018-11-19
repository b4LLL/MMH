package com.example.kirmi.ks1807;

public class Global
{
    //Used to pass the UserID around in some places where it might be otherwise not possible.
    public static String UserID = "";

    //Used to pass the current user password to the API.
    public static String UserPassword = "";

    //Used to pass the Mood ID.
    public static String MoodID = "";

    /*Used if the user backtracks on the connect page (determines if they go to the first or
    second registration page.*/
    public static String UserExtraMoodQuestions = "";
}
