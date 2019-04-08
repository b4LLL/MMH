package com.example.kirmi.ks1807;

import android.graphics.Bitmap;

import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.ImageUri;
import com.spotify.protocol.types.Uri;

public class TrackDetails
{
    private String spotifyTrackID, title, artist, album, length, moodBefore, moodAfter, moodBeforeEmote, moodAfterEmote;
    private Uri spotifyImageID;
    Bitmap trackImage;

    TrackDetails(String spotifyID, String spotifyImageID, String title, String album, String artist, String length, String moodBefore, String moodAfter) {
        this.spotifyTrackID = spotifyID;
        //this.spotifyImageID = ImageUri
        this.title =  "Title:  " + title;
        this.artist = "Artist: " + artist;
        this.album =  "Album:  " + album;
        this.length = "Length: " + length;
        this.moodBefore = moodAfter;
        this.moodAfter = moodBefore;

/*        if(Global.mSpotifyAppRemote != null){
            Global.mSpotifyAppRemote.getImagesApi().getImage(this.spotifyImageID).setResultCallback(new CallResult.ResultCallback<Bitmap>() {
                @Override
                public void onResult(Bitmap bitmap) {
                    trackImage = bitmap;
                }
            });
        }*/
    }

    String getTitle()
    {
        return title;
    }
    String getArtist()
    {
        return artist;
    }
    String getAlbum()
    {
        return album;
    }
    String getLength()
    {
        return length;
    }

    /*if (track != null) { mSpotifyAppRemote.getImagesApi().getImage(track.imageUri).setResultCallback(new CallResult.ResultCallback<Bitmap>() { @Override public void onResult(Bitmap bitmap) { albumImageView.setImageBitmap(bitmap); } });
*/

    public String getSpotifyTrackID()
    {
        return spotifyTrackID;
    }
    String getMoodBefore() {
        return moodBefore;
    }
    String getMoodAfter() {
        return moodAfter;
    }

    String getMoodBeforeEmote() {
        if(Global.moodList != null){
            for(int i = 0; i < Global.moodList.length; i++){
                if(moodBefore.equals(Global.moodList[i])){
                    return Global.emoticonList[i];
                }
            }
        }
        return moodBeforeEmote;
    }

    String getMoodAfterEmote() {
        if(Global.moodList != null){
            for(int i = 0; i < Global.moodList.length; i++){
                if(moodAfter.equals(Global.moodList[i])){
                    return Global.emoticonList[i];
                }
            }
        }
        return moodAfterEmote;
    }
}
