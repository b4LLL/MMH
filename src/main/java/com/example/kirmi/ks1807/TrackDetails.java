package com.example.kirmi.ks1807;
import android.util.Log;
import com.spotify.protocol.types.ImageUri;

public class TrackDetails
{
    String spotifyTrackID;
    private String title, artist, album, length, moodBefore, moodAfter, moodBeforeEmote, moodAfterEmote;
    private ImageUri spotifyURI;

    TrackDetails(String spotifyID, String spotifyStringURI, String title, String album,     //change spotifyURI to drawable
                 String artist, String length, String moodBefore, String moodAfter) {
        this.spotifyTrackID = spotifyID;
        this.title =  "Title:  " + title;
        this.artist = "Artist: " + artist;
        this.album =  "Album:  " + album;
        this.length = "Length: " + length;
        this.moodBefore = moodAfter;
        this.moodAfter = moodBefore;
        if(!(spotifyStringURI.equals("null"))){
            this.spotifyURI = new ImageUri(spotifyStringURI);
        }else
            this.spotifyURI = null;
    }

    ImageUri getStringURI(){
        return spotifyURI;
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


}