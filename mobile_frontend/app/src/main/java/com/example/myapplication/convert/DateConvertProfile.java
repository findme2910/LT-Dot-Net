package com.example.myapplication.convert;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConvertProfile {
    public static String convertToString(long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd '/' MM '/' yyyy");
        return dateFormat.format(new Date(date));
    }
}
