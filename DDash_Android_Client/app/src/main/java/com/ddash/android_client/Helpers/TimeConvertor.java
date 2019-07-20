package com.ddash.android_client.Helpers;

import android.widget.TextView;

/**
 * converts millisecond to hours/minutes/seconds format
 */
public class TimeConvertor {
    public long seconds;
    public float minutes;
    public int hours = 0;

    public TimeConvertor(long milliseconds){
        this.seconds = milliseconds/1000;
        this.minutes = seconds/60;
        if (minutes >= 60) {
            this.hours = (int) minutes / 60;
            this.minutes = minutes - 60*hours;
        }
    }
    public void displayTime(TextView textView){
        if(hours == 0){
            textView.setText(String.format("%.0f mins until full", minutes));
        }
        else if(minutes == 0)
            textView.setText(String.format("%d hours until full"));
        else
            textView.setText(String.format("%d hours, %.0f mins until full", hours, minutes));
    }
}
