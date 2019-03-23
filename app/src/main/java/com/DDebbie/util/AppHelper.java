package com.DDebbie.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class AppHelper {

    /**
     * Turn drawable resource into byte array.
     *
     * @param context parent context
     * @param id      drawable resource id
     * @return byte array
     */
    public static byte[] getFileDataFromDrawable(Context context, int id) {
        Drawable drawable = ContextCompat.getDrawable(context, id);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Turn drawable into byte array.
     *
     * @param drawable data
     * @return byte array
     */
    public static byte[] getFileDataFromDrawable(Context context, Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG,80, byteArrayOutputStream);
//        return byteArrayOutputStream.toByteArray();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    /**
     * @param context
     * @param path
     * @return
     */
    public static byte[] getFileDataFromDrawable(Context context, String path) {
        ImageUtils imageUtils = ImageUtils.getInstant();
        Bitmap bitmap = imageUtils.getCompressedBitmap(path);
        // bitmap = Bitmap.createScaledBitmap(bitmap, Constant.IMAGE_WIDTH, Constant.IMAGE_HEIGHT, false);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        byte[] byteArray = stream.toByteArray();
        Log.d("test", "getFileDataFromDrawable" + bitmap.getWidth() + " updatedBitmap" + bitmap.getHeight());
        return byteArray;

    }

    public static byte[] getFileDataFromDrawable(Context context, String path, int angle) {
        ImageUtils imageUtils = ImageUtils.getInstant();
        Bitmap bitmap = imageUtils.getCompressedBitmap(path);

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        bitmap= Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        // bitmap = Bitmap.createScaledBitmap(bitmap, Constant.IMAGE_WIDTH, Constant.IMAGE_HEIGHT, false);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);




        byte[] byteArray = stream.toByteArray();
        Log.d("test", "getFileDataFromDrawable" + bitmap.getWidth() + " updatedBitmap" + bitmap.getHeight());
        return byteArray;

    }


    public static byte[] getFileDataFromDrawable(Context context, Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Log.d("test", "getFileDataFromDrawable" + bitmap.getWidth() + " updatedBitmap" + bitmap.getHeight());
        return byteArray;

    }

}