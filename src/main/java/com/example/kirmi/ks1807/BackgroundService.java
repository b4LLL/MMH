package com.example.kirmi.ks1807;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.app.Service;
import android.content.Intent;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.text.format.DateUtils;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.*;
import com.spotify.android.appremote.api.error.AuthenticationFailedException;
import com.spotify.android.appremote.api.error.CouldNotFindSpotifyApp;
import com.spotify.android.appremote.api.error.LoggedOutException;
import com.spotify.android.appremote.api.error.NotLoggedInException;
import com.spotify.android.appremote.api.error.OfflineModeException;
import com.spotify.android.appremote.api.error.SpotifyConnectionTerminatedException;
import com.spotify.android.appremote.api.error.SpotifyDisconnectedException;
import com.spotify.android.appremote.api.error.UnsupportedFeatureVersionException;
import com.spotify.android.appremote.api.error.UserNotAuthorizedException;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BackgroundService extends Service {
    private static final int NOTIFICATION_FOREGROUND_ID = 1;                    //ID for mandatory foreground notification
    private static final String NOTIFICATION_CHANNEL_ID = "MandatoryChannelID"; //Channel ID for mandatory foreground notification
    private final IBinder binder = new LocalBinder();
    private Context t;
    public static final String CLIENT_ID = "74447ee7e0f949029b91c1f4dc4af433";
    public static final String REDIRECT_URI = "com.example.kirmi.ks1807://callback";
    public SpotifyAppRemote mSpotifyAppRemote;
    public static boolean isRunning = false;                                    //used by activity to check if it should start the service
    public static String firstSong = "";
    public static String lastSong = "";
    public static Boolean SongStarted = false;
    String SpotifyTrackID;
    String Track;
    String Artist;
    String Genre;
    String Length;
    String TheMood;
    String BeforeMood;
    Track previousTrack = null;
    Track currentTrack = null;
    Retrofit retrofit = RestInterface.getClient();
    RestInterface.Ks1807Client client;

    //Binder implementation
    class LocalBinder extends Binder {
        BackgroundService getService() {
            return BackgroundService.this;
        }
    }

    //Binder method - gives the main application a spotifyAppRemote instance - temporary, should use Web API if possible.
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        Toast.makeText(this, "Service Killed", Toast.LENGTH_SHORT).show();
        if (mSpotifyAppRemote != null)
            SpotifyAppRemote.CONNECTOR.disconnect(mSpotifyAppRemote);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public void getTrack(String trackID) {
        if (!trackID.equals("Dummy")) {
            Toast.makeText(getApplicationContext(), trackID, Toast.LENGTH_SHORT).show();
            mSpotifyAppRemote.getPlayerApi().play(trackID);
        }
    }

    @Override
    public void onCreate() {
        if(Global.isLogged){
            client = retrofit.create(RestInterface.Ks1807Client.class);
            isRunning = true;
            t = this;
            Toast.makeText(this, "Background Service Created", Toast.LENGTH_SHORT).show();
            //Create mandatory notification for API 26+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //Create notification channel
                NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "MMH", NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("Main Notification Channel");
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
                Notification.Builder builder = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID);
                builder.setSmallIcon(R.drawable.ic_queue_music_black_24dp);
                builder.setContentTitle("MMH");
                builder.setContentText("Music for Mental Health running");
                //Make the service foreground
                startForeground(NOTIFICATION_FOREGROUND_ID, builder.build());
            }
            //Create connection parameters
            ConnectionParams connectionParams = new ConnectionParams.Builder(CLIENT_ID)
                    .setRedirectUri(REDIRECT_URI)
                    .showAuthView(true)
                    .build();
            //Try to connect to spotify

            SpotifyAppRemote.CONNECTOR.connect(this, connectionParams, new Connector.ConnectionListener() {
                @Override
                public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                    mSpotifyAppRemote = spotifyAppRemote;
                    Global.isInstalled = true;
                    connected();
                }
                @Override
                public void onFailure(Throwable error) {
                    if (error instanceof AuthenticationFailedException) {
                        Toast.makeText(t, "Authentication Failed, please try again", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof CouldNotFindSpotifyApp) {
                        Toast.makeText(t, "Spotify is not installed", Toast.LENGTH_SHORT).show();
                        Global.isInstalled = false;
                    } else if (error instanceof LoggedOutException) {
                        Toast.makeText(t, "You are not logged into Spotify", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NotLoggedInException) {
                        Toast.makeText(t, "You are not logged into Spotify", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof OfflineModeException) {
                        Toast.makeText(t, "This feature is not available in offline mode", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof SpotifyConnectionTerminatedException) {
                        Toast.makeText(t, "Spotify closed unexpectedly, please try again", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof SpotifyDisconnectedException) {
                        Toast.makeText(t, "Spotify closed unexpectedly, please try again", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof UnsupportedFeatureVersionException) {
                        Toast.makeText(t, "Sorry, this feature is not supported", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof UserNotAuthorizedException) {
                        Toast.makeText(t, "Did not get authorization from Spotify, please try again", Toast.LENGTH_SHORT).show();
                    } else
                        Log.d("Error ", " - > " + error);
                }
            });
        }
    }

    String[][] GetMoods(String MoodList) {
        //Begin code to get the scores from the Mood List.

        //Split the incoming string by comma then get its size.
        String[] AllScoresByComma = MoodList.split(",");
        int AllScoresByCommaSize = AllScoresByComma.length;

        /*The scores will only require half as many elements to store as anything that isn't a score
        will be discarded.*/
        int AllScoresSize = AllScoresByCommaSize / 2;

        String[] AllScores = new String[AllScoresSize];

        int j = 0;
        for (int i = 0; i < AllScoresByCommaSize; i++) {
            //The score will appear in the comma delimited strings in the pattern of 1,3,5, etc
            if (((i % 2) - 1 == 0)) {
                AllScores[j] = AllScoresByComma[i];
                j++;
            }
        }

        //End code to get the scores from the Mood List.

        //Begin code to get the moods and emoticons from the Mood List.

        /*First start by getting rid of the minus symbol and then all numbers after the comma.*/
        MoodList = MoodList.replace("-", "");
        MoodList = MoodList.replaceAll(",[0-9],", " ");

        //Then get each mood and emoticon line by line.
        String[] AllMoods = MoodList.split("\n");
        String[] AllEmoticons = new String[AllMoods.length];

        for (int i = 0; i < AllMoods.length; i++) {
            //Emoticons are not being decoded properly. Leaving the code here.
            String EmoticonAsString = AllMoods[i].split(" ")[0];
            String MoodName = AllMoods[i].split(" ")[1];
            int Emoticon = Integer.parseInt(EmoticonAsString, 16);

            AllMoods[i] = MoodName;
            AllEmoticons[i] = new String(Character.toChars(Emoticon));
        }
        //End code to get the moods and emoticons from the Mood List.

        String MoodListAndScore[][] = {AllMoods, AllScores, AllEmoticons};
        return MoodListAndScore;
    }

    void connected() {
        Log.d("BackgroundService", "Established connection with Spotify remote.");
        mSpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback(
            new Subscription.EventCallback<PlayerState>() {
                public void onEvent(final PlayerState playerState) {
                    firstSong = playerState.track.uri;
                    Call<String> response = client.CheckMoodEntry(Global.UserID, Global.UserPassword);  // checking if logged in - does the user want to be asked for mood
                    response.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.code() == 404) {
                                Toast.makeText(getApplicationContext(),"404 Error. Server did not return a response.",Toast.LENGTH_SHORT).show();
                            } else {
                                if (response.body().equals("Yes"))
                                    promptUser(playerState);
                            }
                        }
                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            fail_LoginNetwork();
                        }
                    });
                }
            }
        );
    }

    public void promptUser(PlayerState pState){
        final PlayerState playerState = pState;
        Call<String> response = client.GetMoodList();
        response.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() == 404) {
                    Toast.makeText(getApplicationContext(),"404 Error. Server did not return a response.",Toast.LENGTH_SHORT).show();
                } else {
                    if (!response.body().equals("")) {
                        String MoodList = response.body();
                        final String[][] FullList = GetMoods(MoodList);
                        final String[] List = FullList[0];
                        final String[] StringScoreList = FullList[1];
                        final String[] EmoticonList = FullList[2];
                        int MoodListSize = FullList[0].length;
                        final String[] MoodAndEmoticonList = new String[MoodListSize];
                        int[] ScoreList = new int[MoodListSize];
                        for (int i = 0; i < MoodListSize; i++) {
                            ScoreList[i] = Integer.parseInt(StringScoreList[i]);
                            MoodAndEmoticonList[i] = EmoticonList[i] + " " + List[i];
                        }
                        final int[] CompleteScoreList = ScoreList;
                        if (!lastSong.equals(playerState.track.uri) && !playerState.track.name.equals("null")) {
                            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getApplicationContext(), R.style.overlaytheme);
                            String DialogText;
                            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                            View mView = inflater.inflate(R.layout.overlay_spinner, null);
                            TextView title = (TextView) mView.findViewById(R.id.text_alerttitle);
                            if (!SongStarted)
                                DialogText = "How are you feeling \nat the moment?";
                            else
                                DialogText = "How are you feeling now after\n listening to last \nsong you played?";
                            title.setText(DialogText);
                            final Spinner spinner = (Spinner) mView.findViewById(R.id.spinner_over);
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, MoodAndEmoticonList);
                            adapter.setDropDownViewResource(R.layout.spinner_item);
                            spinner.setAdapter(adapter);
                            Button submit = (Button) mView.findViewById(R.id.btn_positiveoverlay);
                            builder.setView(mView);
                            final android.app.AlertDialog dialog = builder.create();
                            submit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    String selectedMood = spinner.getSelectedItem().toString();
                                    Toast.makeText(getApplicationContext(), "You selected " + selectedMood, Toast.LENGTH_SHORT).show();
                                    int i = spinner.getSelectedItemPosition();
                                    //Verify if this is before or after.
                                    if (!SongStarted) { // why is this not initiated
                                        SongStarted = true;
                                        SpotifyTrackID = playerState.track.uri;
                                        Track = playerState.track.name;
                                        Artist = playerState.track.artist.name;
                                        Genre = playerState.track.album.name;
                                        Length = String.valueOf(DateUtils.formatElapsedTime(((int) playerState.track.duration) / 1000));
                                        TheMood = List[i];
                                        //For tracking the difference of the before and after moods.
                                        BeforeMood = List[i];
                                        String UserID = Global.UserID;
                                        String UserPassword = Global.UserPassword;
                                        /*Prevents the mood from being added if the user is not logged in.*/
                                        if (!UserID.equals("") && !UserPassword.equals("")) {
                                            Call<String> response = client.TrackStarted(SpotifyTrackID, Track, Genre, Artist, Length, TheMood,UserID, UserPassword);
                                            response.enqueue(new Callback<String>() {
                                                @Override
                                                public void onResponse(Call<String> call, Response<String> response) {
                                                    Log.d("retrofitclick", "SUCCESS: " + response.raw());
                                                    if (response.code() == 404) {
                                                        Toast.makeText(getApplicationContext(),
                                                                "404 Error. Server did not return a response.",
                                                                Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        if (!response.body().equals("Incorrect UserID or Password. Query not executed.")) {
                                                            Global.MoodID = response.body();
                                                            //                                                                                        Toast.makeText(getApplicationContext(),
                                                            //                                                                                                "Mood at start of track updated with Mood ID " + Global.MoodID,
                                                            //                                                                                                Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Global.MoodID = "-1";
                                                            Toast.makeText(getApplicationContext(),
                                                                    "Error, mood at start of track failed to update",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }
                                                @Override
                                                public void onFailure(Call<String> call, Throwable t) {
                                                    //This crashes on loading.
                                                    //fail_LoginNetwork();
                                                }
                                            });
                                        }
                                    } else if (SongStarted) {
                                        TheMood = List[i];
                                        String UserID = Global.UserID;
                                        String UserPassword = Global.UserPassword;
                                        if (Global.MoodID.equals(""))
                                        {
                                            Global.MoodID = "-1";
                                        }
                                        /*Prevents the mood from being added if the user is not logged in.*/
                                        if (!UserID.equals("") && !UserPassword.equals("")) {
                                            Call<String> response = client.TrackEnded(SpotifyTrackID, Global.MoodID, TheMood, "-","-", "-", "-", "-", "-",
                                                    UserID, UserPassword);
                                            response.enqueue(new Callback<String>() {
                                                @Override
                                                public void onResponse(Call<String> call, Response<String> response) {
                                                    Log.d("retrofitclick", "SUCCESS: " + response.raw());
                                                    if (response.code() == 404) {
                                                        Toast.makeText(getApplicationContext(),"404 Error. Server did not return a response.", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        if (response.body().equals("Incorrect UserID or Password. Query not executed.")) {
                                                            Toast.makeText(getApplicationContext(),"Error, mood at end of track failed to update", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }
                                                @Override
                                                public void onFailure(Call<String> call, Throwable t) {
                                                    //This crashes on loading.
                                                    //fail_LoginNetwork();
                                                }
                                            });
                                        }
                                        SongStarted = false;
                                        CommonFunctions Common = new CommonFunctions();
                                        int ScoreIndex;
                                        ScoreIndex = Common.GetArrayIndexFromString(List, BeforeMood);
                                        int BeforeMoodScore = CompleteScoreList[ScoreIndex];
                                        ScoreIndex = Common.GetArrayIndexFromString(List, TheMood);
                                        int AfterMoodScore = CompleteScoreList[ScoreIndex];
                                        if (AfterMoodScore - BeforeMoodScore > 3 || AfterMoodScore - BeforeMoodScore < -3) {
                                            //Diary prompt - Not yet implemented.
                                        }
                                    }
                                }
                            });
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.setCancelable(false);
                            if(Build.VERSION.SDK_INT >= 26) {
                                dialog.getWindow().setType(
                                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                            }else {
                                dialog.getWindow().setType(
                                        WindowManager.LayoutParams.TYPE_PHONE);
                            }
                            dialog.show();
                            final Track track = playerState.track;
                            if (track != null) {
                                Toast.makeText(t, track.name + " by " +
                                        track.artist.name, Toast.LENGTH_SHORT).show();
                            }
                            lastSong = playerState.track.uri;
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                //This crashes on loading.
                //fail_LoginNetwork();
            }
        });
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
}