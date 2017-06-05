package com.example.administrator.ttt_test.util.user;

/**
 * Created by Acer on 2017/5/2.
 */

public class MyUser {
    private static String userName;
    private static String name;
    private static boolean havaLogin;
    private static String unit;

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        MyUser.userName = userName;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        MyUser.name = name;
    }

    public static boolean isHavaLogin() {
        return havaLogin;
    }

    public static void setHavaLogin(boolean havaLogin) {
        MyUser.havaLogin = havaLogin;
    }

    public static String getUnit() {
        return unit;
    }

    public static void setUnit(String unit) {
        MyUser.unit = unit;
    }
}
