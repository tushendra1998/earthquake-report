package com.example.android.quakereport.model;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.List;

/**
 * Loads a list of earthQuake by using an AsyncTask to perform the
 * network request to the given url.
 */

public class EarthQuakeLoader extends AsyncTaskLoader<List<EarthQuake>> {
    public final String mUrl;
    public EarthQuakeLoader(@NonNull Context context,String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading()
    {
        forceLoad();
    }

    /**
     *This is an a background thread
     */

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public List<EarthQuake> loadInBackground() {
        if (mUrl==null)
        {
            return null;
        }
        // Perform the network request parse the response and extract a list of  earthQuake
        List<EarthQuake> earthQuakes = QueryUtils.fetchEarthQuakeData(mUrl);
        return earthQuakes;
    }
}
