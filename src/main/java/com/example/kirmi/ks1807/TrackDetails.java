package com.example.kirmi.ks1807;

public class TrackDetails
{
    String title, artist, genre, length, spotifyTrackID, moodBefore, moodAfter;
    public TrackDetails()
    {

    }
    TrackDetails(String spotifyID, String title, String genre, String artist, String length, String moodBefore, String moodAfter) {
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
    String getArtist()
    {
        return artist;
    }
    String getGenre()
    {
        return genre;
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
}
