package com.example.kirmi.ks1807;

import com.spotify.protocol.types.ImageUri;
import com.spotify.protocol.types.Uri;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

class RestInterface{
    private static Retrofit retrofit = null;
    static Retrofit getClient(){ //getClient definition
        //"http://pe-ks1807.scem.westernsydney.edu.au/MMH_API/webresources/"
        //"http://10.0.2.2:8080/MMH_API/webresources/";
        //http://137.154.147.62/MMH_API/webresources/
        final String BASE_URL = "http://58.173.109.195:8080/MMH_API/webresources/";     // URL for the API endpoints -> glassfish -->
            if (retrofit == null){                                              // simple straight forward instantiation of an object
            Retrofit.Builder builder = new Retrofit.Builder()                   // Retrofit.Builder is a class -> Builder() is the constructor
                .baseUrl(BASE_URL)                                              // sets the baseUrl (API base URL)
                .addConverterFactory(ScalarsConverterFactory.create());         // adds a converter factory for serial/deserialization of objects
                OkHttpClient.Builder httpClient = new OkHttpClient.Builder();   // Builders are like constructors, prepping object with default values,
            retrofit = builder.client(httpClient.build()).build();              // Build simply executes the return from each builder/constructor
        }               // Retrofit.Builder() => builder;
                        // builder.client(okhttp3.OkHttpClient client)
                        // This is the HTTP client used for requests.
                        // retrofit uses OkHttp library for HTTP requests
        return retrofit;
    }

    public interface Ks1807Client{

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

        @GET("mmhpackage.musictrack/TrackStarted/{SpotifyTrackID}/{SpotifyImageID}/{track}/{genre}/{artist}/{duration}/{moodBefore}/{id}/{password}")
        Call<String> TrackStarted(@Path("SpotifyTrackID") String SpotifyTrackID,
                                  @Path("SpotifyImageID") String SpotifyImageID,
                                  @Path("track") String track,
                                  @Path("genre") String genre,
                                  @Path("artist") String artist,
                                  @Path("duration") String duration,
                                  @Path("moodBefore") String moodBefore,
                                  @Path("id") String id,
                                  @Path("password") String password);

        @GET("mmhpackage.musictrack/TrackEnded/{SpotifyTrackID}/{moodID}/{moodAfter}/{userLiked}/{entry1}/{entry2}/{entry3}/{id}/{password}")
        Call<String> TrackEnded(@Path("SpotifyTrackID") String SpotifyTrackID,
                                @Path("moodID") String moodID,
                                @Path("moodAfter") String moodAfter,
                                @Path("userLiked") String userLiked,
                                @Path("entry1") String entry1,
                                @Path("entry2") String entry2,
                                @Path("entry3") String entry3,
                                @Path("id") String id,
                                @Path("password") String password);

        @GET("mmhpackage.musictrack/GetRecommendedTracksUser/{id}/{password}")
        Call<String> GetRecommendedTracksUser(@Path("id") String id, @Path("password") String password);

        @GET("mmhpackage.musictrack/GetRecommendedTracksSystem/{id}/{password}")
        Call<String> GetRecommendedTracksSystem(@Path("id") String id, @Path("password") String password);

        @GET("mmhpackage.musictrack/CheckMoodEntry/{id}/{password}")
        Call<String> CheckMoodEntry(@Path("id") String id, @Path("password") String password);

        @GET("mmhpackage.moodscore/GetMoodList")
        Call<String> GetMoodList();

        @GET("mmhpackage.userdiary/SetDiaryEntry/{id}/{diaryentryone}/{diaryentrytwo}/{diaryentrythree}/{diaryentryfour}/{outcome}")
        Call<String> SetDiaryEntry(@Path("id") String id,
                                   @Path("diaryentryone") String diarytextone,
                                   @Path("diaryentrytwo") String diarytexttwo,
                                   @Path("diaryentrythree") String diarytextthree,
                                   @Path("diaryentryfour") String diarytextfour,
                                   @Path("outcome") String outcome);

        @GET("mmhpackage.userdiary/UpdateDiaryEntry/{userdiaryid}/{id}/{diaryentryone}/{diaryentrytwo}/{diaryentrythree}/{diaryentryfour}/{outcome}")
        Call<String> UpdateDiaryEntry(@Path("userdiaryid") String userdiaryid,
                                      @Path("id") String id,
                                      @Path("diaryentryone") String diarytextone,
                                      @Path("diaryentrytwo") String diarytexttwo,
                                      @Path("diaryentrythree") String diarytextthree,
                                      @Path("diaryentryfour") String diarytextfour,
                                      @Path("outcome") String outcome);

        @GET("mmhpackage.userdiary/GetDiaryEntry/{userdiaryid}")
        Call<String> GetDiaryEntry(@Path("userdiaryid") String userdiaryid);

        @GET("mmhpackage.userdiary/LoadCalendar/{id}")
        Call<String> LoadCalendar(@Path("id") String id);

        @GET("mmhpackage.userdiary/CheckDiaryDate/{id}")
        Call<String> CheckDiaryDate(@Path("id") String id);
    }
}
