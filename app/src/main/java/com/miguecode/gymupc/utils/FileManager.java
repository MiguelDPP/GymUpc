package com.miguecode.gymupc.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;

public class FileManager {
    public static boolean fileExists(String fileName, String[] files) {
        for (String file : files) {
            if (file.equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    public static String getInformation(Context context,String path) {
        try(InputStreamReader file = new InputStreamReader(context.openFileInput(path)); BufferedReader br = new BufferedReader(file);) {
            String line = br.readLine();
            return line;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean deleteFile(Context context, String path) {
        try {
            context.deleteFile(path);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean saveInformation(Context context, String path, String number) {
        try(OutputStreamWriter file = new OutputStreamWriter(context.openFileOutput(path, context.MODE_PRIVATE));) {
            file.write(number);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
