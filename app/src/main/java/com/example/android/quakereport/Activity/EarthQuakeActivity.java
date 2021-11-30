package com.example.android.quakereport.Activity;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.quakereport.model.EarthQuake;
import com.example.android.quakereport.Adapter.EarthQuakeAdapter;
import com.example.android.quakereport.model.EarthQuakeLoader;
import com.example.android.quakereport.R;

import java.util.ArrayList;
import java.util.List;

public class EarthQuakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<EarthQuake>> {
    // Adapter for the list of earthQuake
    private EarthQuakeAdapter mAdapter;
    private TextView mEmptyStateTextView;
    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=2&limit=40";
    private static final int EARTHQUAKE_LOADER_ID = 1;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        ListView earthquakeListView = findViewById(R.id.list);
        // Create a new adapter that take an empty list of earthQuake as input
        mAdapter = new EarthQuakeAdapter(this, new ArrayList<EarthQuake>());
        // Set the adapter on the ListView
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);
        mEmptyStateTextView = (TextView)findViewById(R.id.empty_view);
        earthquakeListView.setEmptyView(mEmptyStateTextView);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Find the current earthQuake that was clicked on
                EarthQuake currentEarthQuake = mAdapter.getItem(position);
                // convert the string URL into a URI object (to pass into the Intent constructor)
                Uri earthQuakeUri = Uri.parse(currentEarthQuake.getmUrl());
                // create a new Intent to view the earthQuake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthQuakeUri);
                // send the Intent to launch a new activity
                startActivity(websiteIntent);

            }
        });

        // Get a reference to the ConnectivityManage to check state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        // If there is a network connection , fetch data
        if (networkInfo !=null && networkInfo.isConnected())
        {
            // Get a reference to the LoaderManager in order to interact with loader
            android.app.LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null,this);
        }
        else {
            View loadingIndicate = findViewById(R.id.loading_indicator);
            loadingIndicate.setVisibility(View.GONE);
            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection); 
        }

    }



    @Override
    public Loader<List<EarthQuake>> onCreateLoader(int id, Bundle args) {
        // Create a new loader for the given URL
        return new EarthQuakeLoader(this,USGS_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(android.content.Loader<List<EarthQuake>> loader, List<EarthQuake> earthQuakes) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        // Set empty state Text to display "No earthQuake found"
        mEmptyStateTextView.setText(R.string.no_earthquakes);
        // clear the adapter of previous earthQuake data
        mAdapter.clear();

        if (earthQuakes != null && !earthQuakes.isEmpty()) {
            mAdapter.addAll(earthQuakes);
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<List<EarthQuake>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

}
