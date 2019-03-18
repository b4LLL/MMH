package com.example.kirmi.ks1807;

public class TrackDetails
{
    private String title, artist, album, length, moodBefore, moodAfter, moodBeforeEmote, moodAfterEmote;
    String spotifyTrackID;
    public TrackDetails()
    {

    }
    TrackDetails(String spotifyID, String title, String album, String artist, String length, String moodBefore, String moodAfter) {
        this.spotifyTrackID = spotifyID;
        this.title = title;
        this.artist = "Artist: " + artist;
        this.album = "Album: " + album;
        this.length = "Length: " + length;
        this.moodBefore = moodAfter;
        this.moodAfter = moodBefore;
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
