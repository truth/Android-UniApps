package com.elevenzon.TextInputLayout.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
/**
 * 5、Environment.getExternalStorageDirectory().getAbsolutePath() = /storage/emulated/0
 * 这个方法是获取外部存储的根路径
 * 6、Environment.getExternalStoragePublicDirectory(“”).getAbsolutePath() = /storage/emulated/0
 * 这个方法是获取外部存储的根路径
 * 7、getExternalFilesDir(“”).getAbsolutePath() = /storage/emulated/0/Android/data/packname/files
 * 这个方法是获取某个应用在外部存储中的files路径
 * 8、getExternalCacheDir().getAbsolutePath() = /storage/emulated/0/Android/data/packname/cache
 * 这个方法是获取某个应用在外部存储中的cache路径
 */

public class FileUtil {
    public static  String getRoot() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }
    public static String getAppFilesRoot(final Context context) {
        return context.getExternalFilesDir("").getAbsolutePath();
    }
    public static String getAppCacheRoot(final Context context) {
        return context.getExternalCacheDir().getAbsolutePath();
    }
    public static boolean  fileIsExists(String strFile)
    {
        try
        {
            File f=new File(strFile);
            if(!f.exists())
            {
                return false;
            }
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }
    public static boolean delete(String strFile) {
        try
        {
            File f=new File(strFile);
            if(!f.delete())
            {
                return false;
            }
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }
    public static boolean deleteDir(String strFolder) {
        try
        {
            File f=new File(strFolder);
            if(f.exists())
            {
                return f.delete();
            }
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }
}
