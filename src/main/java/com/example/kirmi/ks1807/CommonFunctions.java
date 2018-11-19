package com.example.kirmi.ks1807;

import java.util.regex.Pattern;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;

//Functions used by the entire application that are not class specific.
public class CommonFunctions
{
    public Date DateFromStringAustraliaFormat(String DateString) throws ParseException
    {
        SimpleDateFormat SQLServerDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SQLServerDateFormat.setLenient(false);
        try
        {
            Date FormattedDate = SQLServerDateFormat.parse(DateString);
            return FormattedDate;
        } catch (ParseException e)
        {
            e.printStackTrace();
            throw new ParseException("Invalid Date Australian Format", -1);
        }
    }

    public Date DateFromStringFromSQLToAustraliaFormat(String DateString) throws ParseException
    {
        SimpleDateFormat SQLServerDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SQLServerDateFormat.setLenient(false);
        try
        {
            Date FormattedDate = SQLServerDateFormat.parse(DateString);
            return FormattedDate;
        } catch (ParseException e)
        {
            e.printStackTrace();
            throw new ParseException("Invalid Date SQL Format", -1);
        }
    }

    /*Code for this algorithm derived from:
    https://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
    */
    public String EncryptPassword(String Password)
    {
        String EncryptedPassword = "";
        try
        {
            //Using MD5 Message-Digest Algorithm to encrypt the password.
            MessageDigest Digest = MessageDigest.getInstance("MD5");

            //Add password bytes to digest.
            Digest.update(Password.getBytes());

            //Get the hash's bytes.
            byte[] Bytes = Digest.digest();

            //Convert these decimal bytes to hexadecimal format.
            StringBuilder StringToBuild = new StringBuilder();

            for(int i=0; i< Bytes.length ;i++)
            {
                StringToBuild.append(Integer.toString((Bytes[i] & 0xff) +
                        0x100, 16).substring(1));
            }

            EncryptedPassword = StringToBuild.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return EncryptedPassword;
    }

    public int GetArrayIndexFromString(String[] Array, String SearchString)
    {
        int Index = 0;
        for(int i = 0; i < Array.length; i++)
        {
            if(Array[i].equals(SearchString))
            {
                Index = i;
                break;
            }
        }
        return Index;
    }

    public boolean isNumeric(String TheString)
    {
        try
        {
            int Number = Integer.parseInt(TheString);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    //Code adapted from https://www.geeksforgeeks.org/check-email-address-valid-not-java/
    public boolean IsEmailValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
        {
            return false;
        }
        return pat.matcher(email).matches();
    }

    /*Password must be at least 8 characters, have at least one upper and lower case letter
    and a special character.
    Code adapted from https://stackoverflow.com/questions/36574183/how-to-validate-password-field-in-android*/
    public boolean ValidPassword(String password)
    {
        if (password.length() < 8)
        {
            return false;
        }

        Pattern pat;
        String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[/@#$%^&+=!])(?=\\S+$).{4,}$";
        pat = Pattern.compile(PASSWORD_REGEX);

        return pat.matcher(password).matches();
    }
}
