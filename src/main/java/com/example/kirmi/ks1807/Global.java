package com.example.kirmi.ks1807;

public class Global
{
    //Used to pass the UserID around in some places where it might be otherwise not possible.
    public static String UserID = "";

    //Used to pass the current user password to the API.
    static String UserPassword = "";

    //Used to pass the Mood ID.
    static String MoodID = "";

    /*Used if the user backtracks on the connect page (determines if they go to the first or
    second registration page.*/
    static String UserExtraMoodQuestions = "";

    static Boolean isLogged = false;

    static Boolean isInstalled = false;

    static Boolean bgsReceiverRunning = false;
}
