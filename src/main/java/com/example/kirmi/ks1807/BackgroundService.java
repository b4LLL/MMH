package com.example.kirmi.ks1807;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.app.Service;
import android.content.Intent;
import android.os.Message;
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
import com.spotify.protocol.client.ErrorCallback;
import com.spotify.protocol.types.PlayerState;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BackgroundService extends Service{
    private static final int NOTIFICATION_FOREGROUND_ID = 1;                    //ID for mandatory foreground notification
    private static final String NOTIFICATION_CHANNEL_ID = "MandatoryChannelID"; //Channel ID for mandatory foreground notification
    private final IBinder binder = new LocalBinder();
    private Context t;
    public static final String CLIENT_ID = "9b0634404b05419397a2541e4a3080fe";
    public static final String REDIRECT_URI = "com.example.kirmi.ks1807://callback";
    public SpotifyAppRemote mSpotifyAppRemote = null;
    public boolean isBGSrunning = false;                                    //used by activity to check if it should start the service
    String TheMood;
    String BeforeMood;
    public String[] moodEmoticonList;
    Retrofit retrofit = RestInterface.getClient();
    RestInterface.Ks1807Client client;
    ConnectionParams connectionParams;
    Queue<QueueItem> trackQueue = new LinkedList<>();
    static LinkedBlockingQueue<Dialog> dialogsToShow = new LinkedBlockingQueue<>(3);
    public BroadcastReceiver spotifyReceiver;
    QueueHandler messageHandler = new QueueHandler();

    class QueueItem{
        Boolean pre,post;
        PlayerState playerState;

        QueueItem(Boolean pre, Boolean post, PlayerState playerState){
            this.pre = pre;
            this.post = post;
            this.playerState = playerState;
        }
        Boolean getPre(){return this.pre;}
        Boolean getPost(){return this.post;}
        PlayerState getPlayerState(){return this.playerState;}
        void setPre(Boolean pre){this.pre = pre;}
        void setPost(Boolean post){this.post = post;}
        void setPlayerState(PlayerState playerState){this.playerState = playerState;}
    }
    private static class QueueHandler extends Handler{
        public void handleMessage(Message message){
            super.handleMessage(message);
            Bundle bundle = message.getData();
            switch(bundle.getInt("key")){
                case 1:{
                    break;
                }
                case 2:{
                    Dialog d = dialogsToShow.peek();
                    d.show();
                    d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            dialogsToShow.remove();
                        }
                    });
                    break;
                }
                case 3:{
                    Dialog d = dialogsToShow.peek();
                    d.show();
                    d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            dialogsToShow.remove();
                        }
                    });
                    break;
                }
            }
        }
    }
    class LocalBinder extends Binder {
        BackgroundService getService() {
            return BackgroundService.this;
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try{
                while(Global.isLogged){
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    //Log.i("THREAD","\ntrackQueue.size\t" + trackQueue.size() + "\tdialoguesToShow.size()\t"+dialogsToShow.size());
                    bundle.putInt("key",dialogsToShow.size());
                    msg.setData(bundle);
                    messageHandler.sendMessage(msg);
                    Thread.sleep(1000);
                }
            }catch  (InterruptedException e){
                Thread.currentThread().interrupt();
                Log.e("BGS", "run: " + e);

            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("BGS", "\t\tonCreate: ");
        if (Build.VERSION.SDK_INT >= 26) {
            //Create mandatory notification for API 26+
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
            Log.i("BGS", "onCreate:\t BGS created.");
        }
        client = retrofit.create(RestInterface.Ks1807Client.class);
        isBGSrunning = true;
        Global.isBGSrunning = true;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("BGS", "onStartCommand: ");
        t = this;
        Toast.makeText(this, "Background Service Created", Toast.LENGTH_SHORT).show();
        connectionParams = new ConnectionParams.Builder(CLIENT_ID)
                .setRedirectUri(REDIRECT_URI)
                .showAuthView(true)
                .build();
        connectSpotify(connectionParams);

        spotifyReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.i("TAG","\tAction:\t"+action);
                if(Global.mSpotifyAppRemote != null){
                    PlayerApi playerApi = Global.mSpotifyAppRemote.getPlayerApi();
                    pollPlayerState(playerApi);
                }
            }
        };
        IntentFilter filter = new IntentFilter("com.spotify.music.metadatachanged");
        registerReceiver(spotifyReceiver,filter);

        this.loadMoodArray();

        new Thread(runnable).start();
        return START_STICKY;
    }


    void connectSpotify(ConnectionParams connectionParams){
        SpotifyAppRemote.connect(this, connectionParams, new Connector.ConnectionListener() {
            @Override
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                mSpotifyAppRemote = spotifyAppRemote;
                Global.mSpotifyAppRemote = spotifyAppRemote;
                Global.isRunning = true;
                Log.i("BackgroundService", "Established connection with Spotify remote.");
                sendBroadcast(new Intent("spotifyConnected"));
            }
            @Override
            public void onFailure(Throwable error) {
                if (error instanceof AuthenticationFailedException) {
                    Toast.makeText(t, "Authentication Failed, please try again", Toast.LENGTH_SHORT).show();
                    Log.i("New ERROR :) ", "\n" + error);
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
                    Log.i("Error ", " - > " + error);
            }
        });
    }
    @Override
    public void onDestroy() {
        Log.i("BGS","\tonDestroy: ");
        isBGSrunning = false;
        Global.isBGSrunning = false;
        Global.isLogged = false;
        if (mSpotifyAppRemote.isConnected()) {
            Log.i("onDestroy","disconnecting spotify");
            SpotifyAppRemote.disconnect(mSpotifyAppRemote);
            unregisterReceiver(spotifyReceiver);
            Global.isRunning = false;
            Global.spotifyConnected = null;
        }
        stopForeground(true);
        stopSelf();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i("BGS", "\tonTaskRemoved: ");
        onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    public void getTrack(String trackID) {
        Log.i("getTrack", "getTrack: \t"+trackID);
        mSpotifyAppRemote.getPlayerApi().play(trackID);
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
        Log.i("Network Error","\nt =" + t.toString());
    }
    void processQueue(PlayerState playerState){
        if(dialogsToShow.size() <= 1){
            trackQueue.add(new QueueItem(true,true, playerState));
            dialogsToShow.add(primaryPrompt(trackQueue.peek().getPlayerState()));
            trackQueue.peek().setPre(false);
            dialogsToShow.add(finalPrompt(trackQueue.remove().getPlayerState()));
        }else
            Toast.makeText(this, "Dialog queue is full.", Toast.LENGTH_LONG).show();

    }
    void pollPlayerState(PlayerApi playerApi){
        if(playerApi.getPlayerState() == null) {
            Log.i("Something went wrong", "playerApi.getPlayerState() == null");
        } else {
            playerApi.getPlayerState()
                    .setResultCallback(new CallResult.ResultCallback<PlayerState>() {
                        @Override
                        public void onResult(PlayerState playerState) {
                            if (playerState.track.name == null) {    //error with spotify API
                                Log.e("ERROR", "onResult: TRACK NAME IS NULL. URI:\t" + playerState.track.uri);
                                if(playerState.track.uri != null){
                                    getTrack(playerState.track.uri);//test to see if getTrack can reload URI object.
                                }
                            } else {
                                Log.i("BS\t", " signal received" + "\nTrack.name\t\t" + playerState.track.name + "\nTrack.Artists\t" + playerState.track.artist.name);
                                Call<String> response = client.CheckMoodEntry(Global.UserID, Global.UserPassword);
                                response.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        if (response.code() == 404) {
                                            Toast.makeText(getApplicationContext(), "404 Error. Server did not return a response.", Toast.LENGTH_SHORT).show();
                                        } else if (response.body().equals("Yes")) {
                                            processQueue(playerState);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        networkLoginFail(t);
                                        Log.i("Error", " " + t);
                                    }
                                });
                            }
                        }
                    })
                    .setErrorCallback(new ErrorCallback() {
                        @Override
                        public void onError(Throwable throwable) {
                            Log.i("ERROR", "onError: \t" + throwable);
                        }
                    });
        }
    }
    Dialog primaryPrompt(PlayerState playerState){
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
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        if(Build.VERSION.SDK_INT >= 26) {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        }else {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        }
        dialog.show();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedMood = spinner.getSelectedItem().toString();
                Toast.makeText(getApplicationContext(), "You selected " + selectedMood, Toast.LENGTH_SHORT).show();
                int i = spinner.getSelectedItemPosition();
                BeforeMood = Global.moodList[i];
                Call<String> response = client.TrackStarted(playerState.track.uri, playerState.track.imageUri.raw, playerState.track.name, playerState.track.album.name, playerState.track.artist.name,
                        String.valueOf(DateUtils.formatElapsedTime(((int) playerState.track.duration) / 1000)), Global.moodList[i], Global.UserID, Global.UserPassword);
                response.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.i("retrofitclick", "SUCCESS: " + response.raw());
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
                dialog.dismiss();
            }
        });
        return dialog;
    }
    Dialog finalPrompt(PlayerState playerState){
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
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        if(Build.VERSION.SDK_INT >= 26) {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        }else {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        }
        //dialog.show();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                        Log.i("Response ", "\nRaw\t" + response.raw());
                        if (response.body() == null)
                            Log.i("ERROR", " TrackEnded return code\t" + response.code());
                        else
                            Log.i("TrackEnded :"," " + response.body());
                        if (response.code() == 404) {
                            Toast.makeText(getApplicationContext(),"404 Error. Server did not return a response.", Toast.LENGTH_SHORT).show();
                        } else if (response.body().equals("Incorrect UserID or Password. Query not executed.")) {
                            Toast.makeText(getApplicationContext(),"Error, mood at end of track failed to update", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.i("Call"," call error\t" + call.toString());
                        networkLoginFail(t);
                    }
                });
                setUpDiaryPrompt(Global.moodList, BeforeMood, TheMood, Global.CompleteScoreList);
                dialog.dismiss();
            }
        });
        return dialog;
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

}