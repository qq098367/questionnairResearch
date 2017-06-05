package com.example.administrator.ttt_test.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.ttt_test.R;
import com.example.administrator.ttt_test.fragment.download.DownloadFileFragment;
import com.example.administrator.ttt_test.fragment.local.LocalFileFragment;
import com.example.administrator.ttt_test.fragment.main.MainFragment;
import com.example.administrator.ttt_test.util.user.MyUser;


public class MainActivity extends Activity implements View.OnClickListener {

    private MainFragment mainFragment;
    private DownloadFileFragment downloadFileFragment;
    private LocalFileFragment localFileFragment;
    private View mainLayout;
    private View unloadLayout;
    private View loadLayout;
    private ImageView mainImage;
    private ImageView unloadsImage;
    private ImageView loadImage;
    private FragmentManager fragmentManager;
    private long mExitTime=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initViews();
        fragmentManager = getFragmentManager();
        setTabSelection(0);
    }

    private void initViews() {
        mainLayout = findViewById(R.id.message_layout);
        unloadLayout = findViewById(R.id.contacts_layout);
        loadLayout=findViewById(R.id.news_layout);
        mainImage = (ImageView) findViewById(R.id.message_image);
        unloadsImage = (ImageView) findViewById(R.id.contacts_image);
        loadImage=(ImageView)findViewById(R.id.news_image);
        mainLayout.setOnClickListener(this);
        unloadLayout.setOnClickListener(this);
       loadLayout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.message_layout:
                setTabSelection(0);
                break;
            case R.id.contacts_layout:
                if(MyUser.isHavaLogin()) {
                    setTabSelection(1);
                }
                else {
                    Toast.makeText(MainActivity.this,"离线模式无法使用此功能",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.news_layout:
                setTabSelection(2);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }

    private void setTabSelection(int index) {
        clearSelection();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragments(transaction);
        switch (index) {
            case 0:
                mainImage.setImageResource(R.drawable.icon_home_blue);
                if (mainFragment == null) {
                    mainFragment = new MainFragment();
                    transaction.add(R.id.content, mainFragment);
                } else {
                    transaction.show(mainFragment);
                }
                break;
            case 1:
                unloadsImage.setImageResource(R.drawable.download_blue);
                if (downloadFileFragment == null) {
                    downloadFileFragment = new DownloadFileFragment();
                    transaction.add(R.id.content, downloadFileFragment);
                } else {
                    transaction.show(downloadFileFragment);
                }
                break;
            case 2:
               loadImage.setImageResource(R.drawable.enter_blue);
                if (localFileFragment == null) {
                    localFileFragment = new LocalFileFragment();
                    transaction.add(R.id.content, localFileFragment);
                } else {
                    transaction.show(localFileFragment);
                }
                break;
        }
        transaction.commit();
    }


    private void clearSelection() {
        mainImage.setImageResource(R.drawable.homepage_lblue);
        unloadsImage.setImageResource(R.drawable.download_lblue);
        loadImage.setImageResource(R.drawable.open_file_exit);

    }

    private void hideFragments(FragmentTransaction transaction) {
        if (mainFragment != null) {
            transaction.hide(mainFragment);
        }
        if (downloadFileFragment != null) {
            transaction.hide(downloadFileFragment);
        }
        if (localFileFragment != null) {
            transaction.hide(localFileFragment);
        }

    }
}
