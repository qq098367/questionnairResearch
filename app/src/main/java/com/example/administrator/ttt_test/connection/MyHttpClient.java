package com.example.administrator.ttt_test.connection;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Created by Acer on 2017/4/22.
 */

public class MyHttpClient {
    private static HttpClient client;

    public static HttpClient getHttpClient(){
        if(client==null){
//            ClientConnectionManager manager=new ThreadSafeClientConnManager();
            client=new DefaultHttpClient();
        }
        return client;
    }
}
