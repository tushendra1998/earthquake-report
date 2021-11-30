package com.example.android.quakereport.model;

public class EarthQuake {
    private Double mMagnitude;
    private String mLocation;
    private String mDate;
    private long timeInMillisecond;
    private String mUrl;

    public EarthQuake(Double mMagnitude, String mLocation, long timeInMillisecond,String mUrl)
    {
        this.mMagnitude = mMagnitude;
        this.mLocation = mLocation;
        this.timeInMillisecond = timeInMillisecond;
        this.mUrl = mUrl;
    }

    public Double getMagnitude() {
        return mMagnitude;
    }

    public String getLocation() {
        return mLocation;
    }

    public String getDate() {
        return mDate;
    }

    public long getTimeInMillisecond() {
        return timeInMillisecond;
    }

    public String getmUrl() {
        return mUrl;
    }
}
