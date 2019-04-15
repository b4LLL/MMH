package com.example.kirmi.ks1807;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.app.Service;
import android.content.Intent;
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
import com.spotify.android.appremote.api.PlayerApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.android.appremote.api.error.AuthenticationFailedException;
import com.spotify.android.appremote.api.error.CouldNotFindSpotifyApp;
import com.spotify.android.appremote.api.error.LoggedOutException;
import com.spotify.android.appremote.api.error.NotLoggedInException;
import com.spotify.android.appremote.api.error.OfflineModeException;
import com.spotify.android.appremote.api.error.SpotifyConnectionTerminatedException;
import com.spotify.android.appremote.api.error.SpotifyDisconnectedException;
import com.spotify.android.appremote.api.error.UnsupportedFeatureVersionException;
import com.spotify.android.appremote.api.error.UserNotAuthorizedException;

import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.ImageUri;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Uri;

import java.util.ArrayList;

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
    String TheMood;
    String BeforeMood;
    public String[] moodEmoticonList;
    ArrayList<PlayerState> processingQueue = new ArrayList<>();
    Retrofit retrofit = RestInterface.getClient();
    RestInterface.Ks1807Client client;
    ConnectionParams connectionParams;
    Bitmap bm = null;
    class LocalBinder extends Binder {
        BackgroundService getService() {
            return BackgroundService.this;
        }
    }

    int[] loadScoreList(String moodList){
        String[][] fullList = getMoods(moodList);
        Global.moodList = fullList[0];
        String[] stringScoreList = fullList[1];
        Global.emoticonList = fullList[2];
        int moodListSize = fullList[0].length;
        moodEmoticonList = new String[moodListSize];
        int[] intScoreList = new int[moodListSize];
        for (int i = 0; i < moodListSize; i++) {
            intScoreList[i] = Integer.parseInt(stringScoreList[i]);
            moodEmoticonList[i] = Global.emoticonList[i] + " " + Global.moodList[i];
        }
        return intScoreList;
    }

    void loadMoodArray(){
        Call<String> response = client.GetMoodList();
        response.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if ((response.code() != 400) && (response.body() != null)) {
                    Global.CompleteScoreList = loadScoreList(response.body());
                } else if (response.code() == 404) {
                    Toast.makeText(getApplicationContext(), "404 Error. Server did not return a response.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                networkLoginFail(t);
            }
        });
    }

    void setUpDiaryPrompt(String[] List, String BeforeMood, String TheMood, int[] CompleteScoreList){
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

    void networkLoginFail(Throwable t){
        Log.d("Network Error","\nt =" + t.toString());
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    String[][] getMoods(String MoodList) {
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

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        Toast.makeText(this, "Service Killed", Toast.LENGTH_SHORT).show();
        if (mSpotifyAppRemote != null)
            SpotifyAppRemote.disconnect(mSpotifyAppRemote);
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
            connectionParams = new ConnectionParams.Builder(CLIENT_ID)
                .setRedirectUri(REDIRECT_URI)
                .showAuthView(true)
                .build();
            //Try to connect to spotify
            this.connectSpotify(connectionParams);

            BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d("message received"," " + intent.getAction());
                    if(Global.mSpotifyAppRemote != null){
                        PlayerApi playerApi = Global.mSpotifyAppRemote.getPlayerApi();
                        if(playerApi != null){
                            try{
                                pollPlayerState(playerApi);
                            }catch(Exception e){
                                Log.d("Exception " , "\t" + e.getMessage());
                            }
                        }
                    }
                }
            };
            registerReceiver(broadcastReceiver, new IntentFilter("playerStateChange"));
            this.loadMoodArray();
        }
    }

    void connectSpotify(ConnectionParams connectionParams){
        SpotifyAppRemote.connect(this, connectionParams, new Connector.ConnectionListener() {
            @Override
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                mSpotifyAppRemote = spotifyAppRemote;
                Global.mSpotifyAppRemote = spotifyAppRemote;
                Global.isRunning= true;
                Log.d("BackgroundService", "Established connection with Spotify remote.");
            }
            @Override
            public void onFailure(Throwable error) {
                if (error instanceof AuthenticationFailedException) {
                    Toast.makeText(t, "Authentication Failed, please try again", Toast.LENGTH_SHORT).show();
                    Log.d("New ERROR :) ", "\n" + error);
                } else if (error instanceof CouldNotFindSpotifyApp) {
                    Toast.makeText(t, "Spotify is not installed", Toast.LENGTH_SHORT).show();
                    Global.isRunning = false;
                } else if (error instanceof LoggedOutException) {
                    Toast.makeText(t, "You are not logged into Spotify", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NotLoggedInException) {
                    Toast.makeText(t, "You are not logged into Spotify", Toast.LENGTH_SHORT).show();
                } else if (error instanceof OfflineModeException) {
                    Toast.makeText(t, "This feature is not available in offline mode", Toast.LENGTH_SHORT).show();
                } else if (error instanceof SpotifyConnectionTerminatedException) {
                    Toast.makeText(t, "Spotify closed in the background.. attempting to reload.", Toast.LENGTH_SHORT).show();
                    connectSpotify(connectionParams);
                } else if (error instanceof SpotifyDisconnectedException) {
                    Toast.makeText(t, "Spotify disconnected.. attempting to reload.", Toast.LENGTH_SHORT).show();
                } else if (error instanceof UnsupportedFeatureVersionException) {
                    Toast.makeText(t, "Sorry, this feature is not supported", Toast.LENGTH_SHORT).show();
                } else if (error instanceof UserNotAuthorizedException) {
                    Toast.makeText(t, "Did not get authorization from Spotify, please try again", Toast.LENGTH_SHORT).show();
                } else
                    Log.d("Error ", " - > " + error);
            }
        });
    }

    void pollPlayerState(PlayerApi playerApi){
        if(playerApi.getPlayerState() == null) {
            Log.d("Something went wrong", "playerApi.getPlayerState() == null");
        } else {
            playerApi.getPlayerState()
                .setResultCallback(playerState -> {
                    Log.d("BSS\t", " signal received" + "\nTrack.name\t\t" + playerState.track.name + "\nTrack.Artists\t" + playerState.track.artist.name);
                    Call<String> response = client.CheckMoodEntry(Global.UserID, Global.UserPassword);
                    response.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.code() == 404) {
                                Toast.makeText(getApplicationContext(), "404 Error. Server did not return a response.", Toast.LENGTH_SHORT).show();
                            } else if (response.body().equals("Yes")) {
                                if (processingQueue.size() > 0) {
                                    finalPrompt(processingQueue.get(0));
                                    Log.d("FINAL Prompt ", ":\t" + processingQueue.get(0).track.name);
                                    processingQueue.remove(0);
                                    processingQueue.add(playerState);
                                    primaryPrompt(processingQueue.get(0));
                                    Log.d("START Prompt ", ":\t" + processingQueue.get(0).track.name);
                                } else {
                                    processingQueue.add(playerState);
                                    primaryPrompt(playerState);
                                    Log.d("INITIAL Prompt ", ":\t" + processingQueue.get(0).track.name);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            networkLoginFail(t);
                            Log.d("Error", " " + t);
                        }
                    });
                })
                .setErrorCallback(throwable -> {
                    Log.d("Error", " throwable from pollPlayerState" + throwable);
                });
        }
    }

    public void primaryPrompt(PlayerState playerState){
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getApplicationContext(), R.style.overlaytheme);
        String DialogText;
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.overlay_spinner, null);
        TextView title = mView.findViewById(R.id.text_alerttitle);
        DialogText = "How are you feeling before listening to\n\n" + playerState.track.name + "?";
        title.setText(DialogText);
        final Spinner spinner = mView.findViewById(R.id.spinner_over);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, moodEmoticonList);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);
        Button submit = mView.findViewById(R.id.btn_positiveoverlay);
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
                //For tracking the difference of the before and after moods.
                BeforeMood = Global.moodList[i];
                Call<String> response = client.TrackStarted(playerState.track.uri, playerState.track.imageUri.raw, playerState.track.name, playerState.track.album.name, playerState.track.artist.name,
                        String.valueOf(DateUtils.formatElapsedTime(((int) playerState.track.duration) / 1000)), Global.moodList[i], Global.UserID, Global.UserPassword);
                response.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.d("retrofitclick", "SUCCESS: " + response.raw());
                        if (response.code() == 404) {
                            Toast.makeText(getApplicationContext(),"404 Error. Server did not return a response.",Toast.LENGTH_SHORT).show();
                        } else {
                            if (!response.body().equals("Incorrect UserID or Password. Query not executed.")) {
                                Global.MoodID = response.body();
                            } else {
                                Global.MoodID = "-1";
                                Toast.makeText(getApplicationContext(),"Error, mood at start of track failed to update",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        networkLoginFail(t);
                    }
                });
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        if(Build.VERSION.SDK_INT >= 26) {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        }else {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        }
        dialog.show();
    }

    public void finalPrompt(PlayerState playerState){
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getApplicationContext(), R.style.overlaytheme);
        String DialogText;
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.overlay_spinner, null);
        TextView title = mView.findViewById(R.id.text_alerttitle);
        DialogText = "How are you feeling now after\n listening to the last \nsong you played:\n" + playerState.track.name + "?";
        title.setText(DialogText);
        final Spinner spinner = mView.findViewById(R.id.spinner_over);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, moodEmoticonList);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);
        Button submit = mView.findViewById(R.id.btn_positiveoverlay);
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
                TheMood = Global.moodList[i];
                if (Global.MoodID.equals(""))
                    Global.MoodID = "-1";
                /*Prevents the mood from being added if the user is not logged in.*/
                Call<String> response = client.TrackEnded(playerState.track.uri, Global.MoodID, TheMood, "-","-", "-", "-",
                        Global.UserID, Global.UserPassword);
                response.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.d("Response ", "\nRaw\t" + response.raw());
                        if (response.body() == null)
                            Log.d("ERROR", " TrackEnded return code\t" + response.code());
                        else
                            Log.d("TrackEnded :"," " + response.body());
                        if (response.code() == 404) {
                            Toast.makeText(getApplicationContext(),"404 Error. Server did not return a response.", Toast.LENGTH_SHORT).show();
                        } else if (response.body().equals("Incorrect UserID or Password. Query not executed.")) {
                            Toast.makeText(getApplicationContext(),"Error, mood at end of track failed to update", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("Call"," call error\t" + call.toString());
                        networkLoginFail(t);
                    }
                });
                setUpDiaryPrompt(Global.moodList, BeforeMood, TheMood, Global.CompleteScoreList);
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        if(Build.VERSION.SDK_INT >= 26) {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        }else {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        }
        dialog.show();
    }
}