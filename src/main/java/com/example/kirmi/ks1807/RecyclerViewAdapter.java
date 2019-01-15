package com.example.kirmi.ks1807;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<TrackDetails> Tracks;
    private Context context;
    private BackgroundService mService;
    private boolean mBound;

    RecyclerViewAdapter(List<TrackDetails> tracks, Context context, BackgroundService service, boolean isBound) {   //constructor
        this.Tracks = tracks;
        this.context = context;
        this.mService = service;
        this.mBound = isBound;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tracktitle, artist, album, length, beforemood, aftermood;
        ImageButton play;
        ViewHolder(View itemView) {
            super(itemView);
            tracktitle = itemView.findViewById(R.id.textTrackTitle);
            artist = itemView.findViewById(R.id.textArtist);
            album = itemView.findViewById(R.id.textAlbum);
            length = itemView.findViewById(R.id.textLength);
            beforemood = itemView.findViewById(R.id.textMoodBefore);
            aftermood = itemView.findViewById(R.id.textMoodAfter);
            play = itemView.findViewById(R.id.buttonPlay);
        }
    }   //object holding track information

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //returns the ViewHolder Object
        View v = LayoutInflater.from(context).inflate(R.layout.activity_mostplayedtrack, parent, false); //create the viewholder
        return new ViewHolder(v);
    }

    //this sets up each ViewHolder in the HomeFragment.RecyclerView screen
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final TrackDetails track = Tracks.get(position);
        holder.tracktitle.setText(track.getTitle());
        holder.artist.setText("Artist: " + track.getArtist());
        holder.album.setText("Album: " + track.getAlbum());
        holder.length.setText("Length: " + track.getLength());
        holder.beforemood.setText("Your mood before listening: " + track.getMoodBefore() );
        holder.aftermood.setText("Your mood after listening: " + track.getMoodAfter());
        //holder.play.setImageURI(); ???
        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound && Global.isRunning) {
                    mService.getTrack(track.spotifyTrackID);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return Tracks.size();
    }
}