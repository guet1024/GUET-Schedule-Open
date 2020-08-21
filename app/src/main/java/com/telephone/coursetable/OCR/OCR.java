package com.telephone.coursetable.OCR;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.telephone.coursetable.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OCR {

    /**
     * get the brightness of a pixel of a specified image
     * @clear
     */
    private static int getBright(Bitmap bm, int w, int h){
        if(bm == null) return -1;
        int width = bm.getWidth();
        int height = bm.getHeight();
        if(w < 0 || w >= width || h < 0 || h >= height) return -1;
        int r, g, b;
        int p = bm.getPixel(w, h);
        r = (p | 0xff00ffff) >> 16 & 0x00ff;
        g = (p | 0xffff00ff) >> 8 & 0x0000ff;
        b = (p | 0xffffff00) & 0x0000ff;
        return (int)(0.299 * r + 0.587 * g + 0.114 * b);
    }

    /**
     * determine whether a pixel of the specified image is noise
     * @clear
     */
    private static boolean isNoise(Bitmap bm, int w, int h){
        if(bm == null) return false;
        int width = bm.getWidth();
        int height = bm.getHeight();
        if(w < 0 || w >= width || h < 0 || h >= height) return false;
        int brights = (getBright(bm, w, h) + getBright(bm, w, h + 1) + getBright(bm, w, h - 1) +
                getBright(bm, w + 1, h) + getBright(bm, w + 1, h + 1) + getBright(bm, w + 1, h - 1) +
                getBright(bm, w - 1, h) + getBright(bm, w - 1, h + 1) + getBright(bm, w - 1, h - 1)) / 9;
        return brights > 180;
    }

    /**
     * process a bitmap:
     *      1. remove noise
     *      2. binary
     * @clear
     */
    private static Bitmap processBitmap(Bitmap bm){
        if (bm != null){
            Bitmap res_bitmap = bm.copy(bm.getConfig(), true);
            //remove noise
            for (int w = 0; w < res_bitmap.getWidth(); w++){
                for (int h = 0; h < res_bitmap.getHeight(); h++){
                    int b = getBright(res_bitmap, w, h);
                    boolean n = isNoise(res_bitmap, w, h);
                    if (b > 100 || n){
                        res_bitmap.setPixel(w, h, Color.WHITE);
                    }
                }
            }
            //binary
            for (int w = 0; w < res_bitmap.getWidth(); w++){
                for (int h = 0; h < res_bitmap.getHeight(); h++){
                    if (res_bitmap.getPixel(w, h) != Color.WHITE){
                        res_bitmap.setPixel(w, h, Color.BLACK);
                    }
                }
            }
            return res_bitmap;
        }
        return null;
    }

    /**
     * 1. if traineddata file of specified language does not exist on sdcard, copy it from application resources
     * 2. if everything ok, return the data path of specified language to use in {@link TessBaseAPI#init(String, String)}
     * @return
     * - a data path to use in {@link TessBaseAPI#init(String, String)} : fine
     * - null : something went wrong
     * @clear
     */
    private static String prepareTesseract(Context c, String lang_code){
        try {
            File f = new File(c.getExternalFilesDir("tessdata").toString() + File.separator + lang_code + ".traineddata");
            if (!f.exists()) {
                InputStream in = c.getAssets().open(lang_code + ".traineddata");
                OutputStream out = new FileOutputStream(f);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            }
            return c.getExternalFilesDir(null).toString() + File.separator;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @non-ui
     * recognize the text on specified bitmap using specified language
     * @param bm the bitmap
     * @param lang_code the language code
     * @return
     * - String : the result
     * - null : something went wrong
     * @clear
     */
    public static String getTextFromBitmap(@NonNull Context c, @NonNull Bitmap bm, @NonNull String lang_code){
        TessBaseAPI api = new TessBaseAPI();
        String data_path = prepareTesseract(c, lang_code);
        if(data_path != null){
            api.init(data_path, lang_code);
            api.setImage(processBitmap(bm));
            String res = api.getUTF8Text();
            api.end();
            return res;
        }else {
            return null;
        }
    }
}
