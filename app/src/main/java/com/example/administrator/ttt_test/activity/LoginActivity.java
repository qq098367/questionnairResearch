package com.example.administrator.ttt_test.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.ttt_test.R;
import com.example.administrator.ttt_test.connection.MyHttpConnection;
import com.example.administrator.ttt_test.util.encryp.EncrypAES;
import com.example.administrator.ttt_test.util.user.MyUser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private CheckBox cb_remember;
    private SharedPreferences preferences;
    private EditText et_username;
    private EditText et_password;
    private EncrypAES encrypAES=new EncrypAES();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }

        et_username= (EditText) findViewById(R.id.editText_login_username);
        et_password= (EditText) findViewById(R.id.editText_login_password);
        Button bt_login= (Button) findViewById(R.id.button_login_login);
        Button bt_loginWithoutConnect= (Button) findViewById(R.id.button_login_withoutConnect);
        cb_remember= (CheckBox) findViewById(R.id.checkBox_login);

        MyUser.setHavaLogin(false);
        initData();


        bt_loginWithoutConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                intent.putExtra("status","no");
                LoginActivity.this.startActivity(intent);
                LoginActivity.this.finish();
            }
        });

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Handler handler=new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        try {
                            JSONObject jsobj=new JSONObject(msg.obj.toString());
//                            Toast.makeText(LoginActivity.this,msg.obj.toString(),Toast.LENGTH_SHORT).show();
                            if(jsobj.get("code").toString().equals("200")){
                                saveData();
                                Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                                intent.putExtra("status","yes");
                                MyUser.setHavaLogin(true);
                                LoginActivity.this.startActivity(intent);
                                LoginActivity.this.finish();
                            }
                            else {
                                Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        List<NameValuePair> dataList=new ArrayList<NameValuePair>();
                        dataList.add(new BasicNameValuePair("userTel",et_username.getText().toString()));
                        dataList.add(new BasicNameValuePair("password",et_password.getText().toString()));
                        MyHttpConnection myConnection=new MyHttpConnection("/userLoginRestful/login",dataList,handler);
                        myConnection.startPostConnection();
                    }
                }.start();

            }
        });


    }

    private void initData(){
        preferences=getSharedPreferences("user",MODE_PRIVATE);
        if(!preferences.getString("username","").isEmpty()){
            et_username.setText(preferences.getString("username",""));
            String password=preferences.getString("password","");
            et_password.setText(encrypAES.DecryptorString(password));
            cb_remember.setChecked(true);
        }
    }

    private void saveData(){
        String username="";
        String password="";
        if(cb_remember.isChecked()) {
            username=et_username.getText().toString();
            password=encrypAES.EncryptorString(et_password.getText().toString());
        }
        SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
        editor.putString("username", username);
        editor.putString("password",password);
        editor.apply();

    }

}
