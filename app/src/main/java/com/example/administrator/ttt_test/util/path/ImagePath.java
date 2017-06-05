package com.example.administrator.ttt_test.util.path;

import android.os.Environment;

/**
 * Created by Acer on 2017/4/9.
 */

public class ImagePath {
    public static String getImagePath(){
        String path = Environment.getExternalStorageDirectory()
                + "/img/";// 文件目录
        return path;
    }
}
