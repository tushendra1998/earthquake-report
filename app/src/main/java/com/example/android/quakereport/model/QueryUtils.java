package com.example.android.quakereport.model;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    // Query the USGS dataSet and return a list of earthQuake object
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static List<EarthQuake> fetchEarthQuakeData(String requestUrl)
    {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "problem making http request", e);
        }
        // Extract relevant fields from the JSON response and create an {@link Event} object
        List<EarthQuake> earthQuakes = extractFeatureFromJson(jsonResponse);
        return earthQuakes;
    }

    /**
     * Return new URL object from the given string URL
     */

    private static URL createUrl(String stringUrl)
    {
        URL url = null;
        try {
            url = new URL(stringUrl);

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG,"problem building the url",e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return the string as a response
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link EarthQuake} objects that has been built up from
     * parsing a JSON response.
     */
    private static List<EarthQuake> extractFeatureFromJson(String earthQuakeJSON) {
        // if the JSON string is empty or null then return early
        if (TextUtils.isEmpty(earthQuakeJSON))
        {
            return null;
        }

        // Create an empty List that we can start adding earthquakes to
        List<EarthQuake> earthquakes = new ArrayList<>();

        // Try to parse the JSON response string . If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // create a JSON object from the JSON response string
            JSONObject baseJasonResponse = new JSONObject(earthQuakeJSON);
            // Extract the JSONArray associated with the key called "feature"
            // which represent a list of feature (or earthQuake)
            JSONArray earthQuakeArray = baseJasonResponse.getJSONArray("features");

            // for each earthQuake in the earthQuakeArray , create an earthQuake object
            for (int i = 0; i < earthQuakeArray.length(); i++) {
                // Get a single earthQuake at position i within the list of earthQuake
                JSONObject currentEarthQuake = earthQuakeArray.getJSONObject(i);
                JSONObject properties = currentEarthQuake.getJSONObject("properties");
                // Extract the value for the key called "mag"
                Double magnitude = properties.getDouble("mag");
                // Extract the value for the key called "place"
                String location = properties.getString("place");
                // Extract the value for the key called "time"
                long time = properties.getLong("time");
                // Extract the value for the key called "url"
                String url = properties.getString("url");

                EarthQuake earthQuake = new EarthQuake(magnitude, location, time,url);
                earthquakes.add(earthQuake);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return earthquakes;
    }

}