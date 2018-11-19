package com.example.kirmi.ks1807;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class RestInterface
{
    //static final String BASE_URL = "localhost:4567/";
    static final String BASE_URL = "http://pe-ks1807.scem.westernsydney.edu.au/MMH_API/webresources/";
    //static final String BASE_URL = "http://pe-ks1807.scem.uws.edu.au:8080/MMH_API/webresources/";

    OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public static Retrofit retrofit = null;
    public static Retrofit getClient()
    {
        if (retrofit==null)
        {
            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create());
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            retrofit = builder.client(httpClient.build()).build();
        }
        return retrofit;
    }

    public interface Ks1807Client
    {
        @GET("mmhpackage.useraccount/GetMusicHistory/{id}/{password}")
        Call<String> GetMusicHistory(@Path("id") String id, @Path("password") String password);

        @GET("mmhpackage.useraccount/GetUserDetailsRegistration/{id}/{password}")
        Call<String> GetUserDetailsRegistration(@Path("id") String id, @Path("password") String password);

        @GET("mmhpackage.useraccount/GetUserDetails/{id}/{password}")
        Call<String> GetUserDetails(@Path("id") String id, @Path("password") String password);

        @GET("mmhpackage.useraccount/GetUserRegistrationQuestions/{id}/{password}")
        Call<String> GetUserRegistrationQuestions(@Path("id") String id, @Path("password") String password);

        @GET("mmhpackage.useraccount/GetUserID/{email}")
        Call<String> GetUserID(@Path("email") String email);

        @GET("mmhpackage.useraccount/GetUserSettings/{id}/{password}")
        Call<String> GetUserSettings(@Path("id") String id, @Path("password") String password);

        @GET("mmhpackage.useraccount/InsertNewUser/{firstname}/{lastname}/{email}/{date}/{gender}/{ethicsAgreed}/{password}/")
        Call<String> InsertNewUser(
                @Path("firstname") String firstname, @Path("lastname") String lastname,
                @Path("email") String email, @Path("date") String date,
                @Path("gender") String gender, @Path("ethicsAgreed") String ethicsAgreed,
                @Path("password") String password);

        @GET("mmhpackage.useraccount/UpdateNewUser/{firstname}/{lastname}/{email}/{date}/{gender}/{ethicsAgreed}/{UserID}/{password}/")
        Call<String> UpdateNewUser(
                @Path("firstname") String firstname, @Path("lastname") String lastname,
                @Path("email") String email, @Path("date") String date,
                @Path("gender") String gender, @Path("ethicsAgreed") String ethicsAgreed,
                @Path("UserID") String UserID,
                @Path("password") String password);

        @GET("mmhpackage.useraccount/UpdateUser/{firstname}/{lastname}/{email}/{date}/{gender}/{id}/{password}/")
        Call<String> UpdateUser(
                @Path("firstname") String firstname,
                @Path("lastname") String lastname, @Path("email") String email,
                @Path("date") String date, @Path("gender") String gender,
                @Path("id") String id, @Path("password") String password);

        @GET("mmhpackage.useraccount/UpdateUserSecondPage/{q1}/{q2}/{q3}/{q4}/{id}/{password}")
        Call<String> UpdateUserSecondPage(@Path("q1") String q1,
                                          @Path("q2") String q2, @Path("q3") String q3,
                                          @Path("q4") String q4, @Path("id") String id,
                                          @Path("password") String password);

        @GET("mmhpackage.useraccount/IsEmailAddressUnique/{email}")
        Call<String> IsEmailAddressUnique(@Path("email") String email);

        @GET("mmhpackage.useraccount/VerifyLogin/{email}/{password}")
        Call<String> VerifyLogin(@Path("email") String email, @Path("password") String password);

        @GET("mmhpackage.useraccount/UpdatePassword/{newpassword}/{id}/{currentpassword}")
        Call<String> UpdatePassword(@Path("newpassword") String newpassword, @Path("id") String id,
                                    @Path("currentpassword") String currpassword);

        @GET("mmhpackage.useraccount/UpdateSettings/{MakeRecommendations}/{MoodFrequency}/{RememberLogin}/{id}/{password}")
        Call<String> UpdateSettings(@Path("MakeRecommendations") String MakeRecommendations, @Path("MoodFrequency") String frequency,
                                    @Path("RememberLogin") String RememberLogin, @Path("id") String id, @Path("password") String password);

        @GET("mmhpackage.useraccount/VerifyPassword/{id}/{password}")
        Call<String> VerifyPassword(@Path("id") String id, @Path("password") String password);

        @GET("mmhpackage.musictrack/TrackStarted/{SpotifyTrackID}/{track}/{genre}/{artist}/{duration}/{moodBefore}/{id}/{password}")
        Call<String> TrackStarted(@Path("SpotifyTrackID") String SpotifyTrackID,
                                  @Path("track") String track,
                                  @Path("genre") String genre, @Path("artist") String artist,
                                  @Path("duration") String duration, @Path("moodBefore") String moodBefore,
                                  @Path("id") String id, @Path("password") String password);

        @GET("mmhpackage.musictrack/TrackEnded/{SpotifyTrackID}/{moodID}/{moodAfter}/{userLiked}/" +
                "{entry1}/{entry2}/{entry3}/{entry4}/{entry5}/{id}/{password}")
        Call<String> TrackEnded(@Path("SpotifyTrackID") String SpotifyTrackID,
                                @Path("moodID") String moodID, @Path("moodAfter") String moodAfter,
                                @Path("userLiked") String userLiked,
                                @Path("entry1") String entry1, @Path("entry2") String entry2,
                                @Path("entry3") String entry3, @Path("entry4") String entry4,
                                @Path("entry5") String entry5, @Path("id") String id,
                                @Path("password") String password);

        @GET("mmhpackage.moodscore/GetMoodList")
        Call<String> GetMoodList();

        @GET("mmhpackage.musictrack/GetRecommendedTracksUser/{id}/{password}")
        Call<String> GetRecommendedTracksUser(@Path("id") String id, @Path("password") String password);

        @GET("mmhpackage.musictrack/GetRecommendedTracksSystem/{id}/{password}")
        Call<String> GetRecommendedTracksSystem(@Path("id") String id, @Path("password") String password);

        @GET("mmhpackage.musictrack/CheckMoodEntry/{id}/{password}")
        Call<String> CheckMoodEntry(@Path("id") String id, @Path("password") String password);
    }

    //Data Structures
    public class User
    {
        String firstName;
        String lastName;
        String email;
        String dob;
        String gender;
    }

    public class Settings
    {
        String makeRecommendations;
        String moodFrequency;
        String rememberLogin;
    }

    public static User getUserFromResult(String body)
    {
        User item = new RestInterface().new User();
        String temp[] = body.split(",");
        item.firstName = temp[0];
        item.lastName = temp[1];
        item.email = temp[2];
        item.dob = temp[3];
        item.gender = temp[4];
        return item;
    }

    public static Settings getSettingsFromResult(String body)
    {
        Settings item = new RestInterface().new Settings();
        String temp[] = body.split(",");
        item.makeRecommendations = temp[0];
        item.moodFrequency = temp[1];
        item.rememberLogin = temp[2];
        return item;
    }

    //Use on GetMusicHistory, GetRecommendedTracksUser and GetRecommendedTracksSystem return values
    // Separating the results into different columns
//    public static List<TrackDetails> getTrackFromResult(String body)
//    {
//        ArrayList<TrackDetails> list = new ArrayList<>();
//        String result[] = body.split(System.getProperty("line.separator"));
//        for(int i = 0; i < result.length; i++)
//        {
//            TrackDetails item = new RestInterface().new TrackDetails();
//            String temp[] = result[i].split(",");
//            item.aftermood = temp[0];
//            item.title = temp[1];
//            item.genre = temp[2];
//            item.artist = temp[3];
//            item.length = temp[4];
//            list.add(item);
//        }
//        return list;
//    }
}
