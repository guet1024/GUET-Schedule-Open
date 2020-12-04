package com.telephone.coursetable.Clock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class DateTime {

    public static TimeZone timezone_GMT8 = TimeZone.getTimeZone("GMT+08:00");

    private TimeZone tz;
    private Calendar cld;

    private int year;
    private int month;
    private int day;
    private int weekday;

    public int hour_24;
    public int minute;
    public int second;

    public static DateTime getGMT8_Instance(long timestamp){
        return new DateTime(DateTime.timezone_GMT8, timestamp);
    }

    public static DateTime getDefault_Instance(long timestamp){
        return new DateTime(TimeZone.getDefault(), timestamp);
    }

    public DateTime(TimeZone timeZone, long timestamp){
        this.tz = timeZone;
        this.cld = Calendar.getInstance(this.tz);

        cld.setTime(new Date(timestamp));
        this.year = cld.get(Calendar.YEAR);
        this.month = cld.get(Calendar.MONTH) + 1;
        this.day = cld.get(Calendar.DAY_OF_MONTH);
        this.weekday = cld.get(Calendar.DAY_OF_WEEK) - 1;
        if (this.weekday == 0){
            this.weekday = 7;
        }
        this.hour_24 = cld.get(Calendar.HOUR_OF_DAY);
        this.minute = cld.get(Calendar.MINUTE);
        this.second = cld.get(Calendar.SECOND);
    }

    public DateTime(DateTime src, @NonNull String time_string, @NonNull String delimiter) {
        this.tz = src.tz;
        this.cld = Calendar.getInstance(this.tz);

        this.year = src.year;
        this.month = src.month;
        this.day = src.day;
        this.weekday = src.weekday;
        Scanner scanner = new Scanner(time_string);
        scanner.useDelimiter(delimiter);
        this.hour_24 = scanner.nextInt();
        this.minute = scanner.nextInt();
        if (scanner.hasNext()) {
            this.second = scanner.nextInt();
        } else {
            this.second = 0;
        }
    }

    public long getTime(){
        cld.set(
                this.year,
                this.month - 1,
                this.day,
                this.hour_24,
                this.minute,
                this.second
        );
        return cld.getTime().getTime();
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getWeekday() {
        return weekday;
    }

    public boolean equals(@NonNull DateTime dateTime) {
        return this.getTime() == dateTime.getTime();
    }

    public boolean before(@NonNull DateTime dateTime){
        return this.getTime() < dateTime.getTime();
    }

    public boolean after(@NonNull DateTime dateTime){
        return this.getTime() > dateTime.getTime();
    }

    public boolean before_or_equals(@NonNull DateTime dateTime){
        return this.getTime() < dateTime.getTime() || this.getTime() == dateTime.getTime();
    }

    public boolean after_or_equals(@NonNull DateTime dateTime){
        return this.getTime() > dateTime.getTime() || this.getTime() == dateTime.getTime();
    }

    @Override
    public String toString() {
        return "DateTime{" +
                "year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", weekday=" + weekday +
                ", hour_24=" + hour_24 +
                ", minute=" + minute +
                ", second=" + second +
                ", timestamp=" + this.getTime() +
                '}';
    }
}
