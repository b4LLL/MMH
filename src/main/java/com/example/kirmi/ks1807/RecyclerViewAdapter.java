package com.example.kirmi.ks1807;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<TrackDetails> Tracks;
    private Context context;
    private BackgroundService mService;
    private boolean mBound = false;

    RecyclerViewAdapter(List<TrackDetails> tracks, Context context) {   //constructor
        Tracks = tracks;
        this.context = context;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tracktitle, artist, genre, length, beforemood, aftermood;
        Button play;
        ViewHolder(View itemView) {
            super(itemView);
            tracktitle = (TextView)itemView.findViewById(R.id.text_tracktitle);
            artist = (TextView)itemView.findViewById(R.id.Text_artist);
            genre = (TextView)itemView.findViewById(R.id.Text_genre);
            length = (TextView)itemView.findViewById(R.id.Text_length);
            beforemood = (TextView)itemView.findViewById(R.id.Text_moodbefore);
            aftermood = (TextView)itemView.findViewById(R.id.Text_moodafter);
            play = (Button)itemView.findViewById(R.id.btn_Play);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_mostplayedtrack, parent, false);

        Intent intent = new Intent(parent.getContext(), BackgroundService.class);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        // the .unbindService needs to be called here?

        // https://developer.android.com/reference/android/content/Context#unbindService(android.content.ServiceConnection)
        return new ViewHolder(v);
        // https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter#oncreateviewholder
    }

    //this sets up each ViewHolder in the HomeFragment.RecyclerView screen
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final TrackDetails track = Tracks.get(position);
        holder.tracktitle.setText(track.getTitle());
        holder.artist.setText("Artist: " + track.getArtist());
        holder.genre.setText("Genre: " + track.getGenre());

        holder.length.setText("Length: " + track.getLength());
        holder.beforemood.setText("Your mood before listening: " + track.getMoodBefore() );
        holder.aftermood.setText("Your mood after listening: " + track.getMoodAfter());

        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound) {
                    mService.gettrack(track.spotifyTrackID);    //this will crash if no Spotify installed???
                }
            }
        });
    }
    public void killBind(){
        context.unbindService(serviceConnection);
    }

    @Override
    public int getItemCount() {
        return Tracks.size();
    }

    //here we set the connection to the service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BackgroundService.LocalBinder binder = (BackgroundService.LocalBinder) service;
            Log.d("RecyclerViewAdapter","BackgroundService binding..");
            mService = binder.getService(); //set the service to mService, mBound is just a flag to state that it is bound...
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("RecyclerViewAdapter","onServiceDisconnected called");
            //mService.unbindService(serviceConnection);
            mBound = false;
        }
    };
}
