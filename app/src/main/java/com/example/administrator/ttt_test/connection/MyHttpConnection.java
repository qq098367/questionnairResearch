package com.example.administrator.ttt_test.connection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static com.example.administrator.ttt_test.connection.MyHttpClient.getHttpClient;
import static com.example.administrator.ttt_test.connection.UrlAdress.getUrl;

/**
 * Created by Acer on 2017/4/22.
 */

public class MyHttpConnection {
    private String url;
    private List<NameValuePair> dataList;
    private Handler handler;
    private HttpClient client;
    private String data;

    public MyHttpConnection(String url, List<NameValuePair> dataList, Handler handler){
        this.url=getUrl()+url;
        this.dataList=dataList;
        this.handler=handler;
    }

    public MyHttpConnection(String url, String data, Handler handler){
        this.url=getUrl()+url;
        this.data=data;
        this.handler=handler;
    }

    public MyHttpConnection(String url){
        this.url=getUrl()+url;
    }


    public void startPostConnection(){
        HttpPost post=new HttpPost(url);
        Message msg=new Message();
        client=getHttpClient();
        try {
            post.setEntity(new UrlEncodedFormEntity(dataList, HTTP.UTF_8));
            HttpResponse response=client.execute(post);

            msg.obj= EntityUtils.toString(response.getEntity());
            handler.sendMessage(msg);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startPostConnectionNeedHead(){
        HttpPost post=new HttpPost(url);
        Message msg=new Message();
        client=getHttpClient();
         try {
             StringEntity stringEntity=new StringEntity(data,"utf-8");

             post.setHeader(new BasicHeader("Content-Type","application/json"));
            post.setEntity(stringEntity);

            HttpResponse response=client.execute(post);

            msg.obj= EntityUtils.toString(response.getEntity());
            handler.sendMessage(msg);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void startGetConnection(){
        HttpGet get=new HttpGet(url);
        Message msg=new Message();
        client=getHttpClient();
        try {
            HttpResponse response=client.execute(get);
            msg.obj=EntityUtils.toString(response.getEntity());
            handler.sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap startGetPic(){
        HttpGet get=new HttpGet(url);
        client=getHttpClient();
        Bitmap bitmap=null;
        try {
            HttpResponse response=client.execute(get);
            InputStream in=response.getEntity().getContent();
            int i=response.getStatusLine().getStatusCode();
            bitmap= BitmapFactory.decodeStream(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
