package com.example.myapplication.convert;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;

public class ImageConvert {
    public static Bitmap base64ToBitMap(String base64){
        byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
        // Chuyển mảng byte thành hình ảnh Bitmap
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return bitmap;
    }
}