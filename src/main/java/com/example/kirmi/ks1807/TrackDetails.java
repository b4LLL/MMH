package com.example.kirmi.ks1807;

public class TrackDetails
{
    String title, artist, genre, length, spotifyTrackID, moodBefore, moodAfter;
    public TrackDetails()
    {

    }
    public TrackDetails(String spotifyID, String title, String genre, String artist, String length, String moodBefore, String moodAfter) {
        this.spotifyTrackID = spotifyID;
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.length = length;
        this.moodBefore = moodBefore;
        this.moodAfter = moodAfter;
    }
    public String getTitle()
    {
        return title;
    }
    public String getArtist()
    {
        return artist;
    }
    public String getGenre()
    {
        return genre;
    }
    public String getLength()
    {
        return length;
    }
    public String getSpotifyTrackID()
    {
        return spotifyTrackID;
    }

    public String getMoodBefore() {
        return moodBefore;
    }

    public String getMoodAfter() {
        return moodAfter;
    }
}
