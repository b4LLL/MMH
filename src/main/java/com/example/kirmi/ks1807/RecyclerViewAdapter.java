package com.example.kirmi.ks1807;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.spotify.protocol.client.CallResult;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements AsyncInterface {

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

    @Override
    public void processedTrackList(List<TrackDetails> listItems) {

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tracktitle, artist, album, length, beforemood, aftermood, moodBeforeEmote, moodAfterEmote;
        ImageButton play;
        ViewHolder(View itemView) {
            super(itemView);
            tracktitle = itemView.findViewById(R.id.textTrackTitle);
            artist = itemView.findViewById(R.id.textArtist);
            album = itemView.findViewById(R.id.textAlbum);
            length = itemView.findViewById(R.id.textLength);
            beforemood = itemView.findViewById(R.id.textMoodBefore);
            aftermood = itemView.findViewById(R.id.textMoodAfter);
            moodBeforeEmote = itemView.findViewById(R.id.moodBeforeEmote);
            moodAfterEmote = itemView.findViewById(R.id.moodAfterEmote);
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
        holder.artist.setText(track.getArtist());
        holder.album.setText(track.getAlbum());
        holder.length.setText(track.getLength());
        holder.beforemood.setText("Mood before: ");
        holder.aftermood.setText("Mood after: ");
        holder.moodBeforeEmote.setText(track.getMoodBeforeEmote());
        holder.moodAfterEmote.setText(track.getMoodAfterEmote());
        if(mBound && Global.isRunning && (track.getStringURI() != null)){
            Global.mSpotifyAppRemote.getImagesApi().getImage(track.getStringURI()).setResultCallback(new CallResult.ResultCallback<Bitmap>() {

                @Override
                public void onResult(Bitmap bitmap) {
                    Drawable d = new BitmapDrawable(context.getResources(), bitmap);
                    holder.play.setBackground(d);
                }
            });
        }

        holder.play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mBound && Global.isRunning){
                        mService.getTrack(track.spotifyTrackID);
                        Log.d("onClick","\nmBound:\t" + mBound + "\tGlobal.isRunning:\t" + Global.isRunning);
                    }
                    else
                        Log.d("onClick","\tUnbound\nmBound:\t" + mBound + "\tGlobal.isRunning:\t" + Global.isRunning);
                }
            });
        }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return Tracks.size();
    }
}

/*    */