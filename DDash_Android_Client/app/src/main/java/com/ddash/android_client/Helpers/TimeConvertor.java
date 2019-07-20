package com.ddash.android_client.Helpers;

public class TimeConvertor {
    public long seconds;
    public float minutes;
    public int hours;

    public TimeConvertor(long milliseconds){
        this.seconds = milliseconds/1000;
        this.minutes = seconds/60;
        if (minutes >= 60) {
            this.hours = (int) minutes / 60;
            this.minutes = minutes - 60*hours;
        }
    }
}
