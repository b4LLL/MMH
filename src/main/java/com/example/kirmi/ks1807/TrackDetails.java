package com.example.kirmi.ks1807;

public class TrackDetails
{
    String title, artist, album, length, spotifyTrackID, moodBefore, moodAfter;
    public TrackDetails()
    {

    }
    TrackDetails(String spotifyID, String title, String album, String artist, String length, String moodBefore, String moodAfter) {
        this.spotifyTrackID = spotifyID;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.length = length;
        this.moodBefore = moodBefore;
        this.moodAfter = moodAfter;
        // pull/load img url here..
        // this.trackImage = trackImage;
    }
    public String getTitle()
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
    public String getSpotifyTrackID()
    {
        return spotifyTrackID;
    }

    String getMoodBefore() {
        if(Global.moodList != null){
            for(int i = 0; i < Global.moodList.length; i++){
                if(moodBefore.equals(Global.moodList[i])){
                    return Global.emoticonList[i];
                }
            }
        }
        return moodBefore;
    }

    String getMoodAfter() {
        if(Global.moodList != null){
            for(int i = 0; i < Global.moodList.length; i++){
                if(moodAfter.equals(Global.moodList[i])){
                    return Global.emoticonList[i];
                }
            }
        }
        return moodAfter;
    }
}
