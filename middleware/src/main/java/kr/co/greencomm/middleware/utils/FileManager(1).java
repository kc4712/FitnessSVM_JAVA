package kr.co.greencomm.middleware.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;

/**
 * Created by jeyang on 2016-08-30.
 */
public class FileManager {
    public static class Folder {
        public static final String firmware = "/firmware/";
        public static final String video = "/video/";
        public static final String xml = "/xml/";
    }

    public static boolean isExistFile(Context context, String name) {
        File file = new File(getMainPath(context) + name);
        return file.exists();
    }

    public static String getMainPath(Context context) {
        return context.getFilesDir().getAbsolutePath() + "/";
    }

    public static void moveFileName(String path, String ori_name, String rename) {
        File full_path = new File(path);
        File source = new File(full_path, ori_name);
        if (source.isFile()) {
            source.renameTo(new File(full_path, rename));
        }
    }

    public static void deleteAllFile(String path) {
        File full_path = new File(path);

        if (full_path.isDirectory()) {
            File[] arr = full_path.listFiles();
            for (File f : arr) {
                f.delete();
            }
        }
    }
    public static void deleteFile(String path) {
        //File full_path = new File(path);
        File source = new File(path);
        if (source.isFile() && source.exists()) {
            Log.i("FileManager","try file delete!!!"+ source );
            source.delete();
            Log.i("FileManager","file deleted!!!"+ source );


        }
    }
}
