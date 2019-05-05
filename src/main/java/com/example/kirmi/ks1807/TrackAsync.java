package com.example.kirmi.ks1807;


import android.os.AsyncTask;
import java.util.ArrayList;
import java.util.List;
        /*<Params, Progress, Result>
            Paramas:    [Object]that gets passed to the AsyncTask eg: new BackgroundLoader().execute(object)
            Progress:   Type that gets passed to the onProgressUpdate()
            Result:     Type returns from doInBackground()
        */

public class TrackAsync extends AsyncTask<String, Integer, List<TrackDetails>> {

    AsyncInterface delegate = null;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //show progress bar - runs in UI thread
    }

    //this runs in the background
    @Override
    protected List<TrackDetails> doInBackground(String... strings) {
        String musicHistory = strings[0];
        String[] MusicDetails = musicHistory.split(System.getProperty("line.separator"));
        List<TrackDetails> listItems = new ArrayList<>();
        int length;
        if (MusicDetails.length <= 10) {
            length = MusicDetails.length;
        } else {
            length = 10;
        }
        for (int i = 0; i < length; i++) {
            String[] temp = MusicDetails[i].split(",");
            TrackDetails list = new TrackDetails(temp[0], temp[1], temp[2], temp[3], temp[4], temp[5], temp[6], temp[7]);    //add additional imgURI? here to give to adapter/.
            listItems.add(list);
        }
        return listItems;
    }

    @Override
    protected void onProgressUpdate(Integer... integer) {
        super.onProgressUpdate(integer);
        //update progress bar
    }

    @Override
    protected void onPostExecute(List<TrackDetails> listItems) {
        //super.onPostExecute(listItems);
        delegate.processedTrackList(listItems);
    }



}

