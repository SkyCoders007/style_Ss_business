package mxi.com.styleswiperbusiness.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RelativeLayout;

import mxi.com.styleswiperbusiness.Network.CommanClass;
import mxi.com.styleswiperbusiness.Network.Constants;
import mxi.com.styleswiperbusiness.R;

public class Splash extends AppCompatActivity {

    CommanClass cc;
    RelativeLayout rlControler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        cc = new CommanClass(Splash.this);
        rlControler = (RelativeLayout) findViewById(R.id.rl_controller_splash);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // only for marshmallow and newer versions
            checkReadWritePermission();
        } else {
            new CountDownTimer(Constants.SplashScreen.countdownTime, Constants.SplashScreen.countdownIntervalTime) {

                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    if(cc.loadPrefBoolean("isLoggedIn")){
                        startActivity(new Intent(Splash.this, SelectGender.class));
                    } else {
                        startActivity(new Intent(Splash.this, Login.class));
                    }
                    finish();
                }
            }.start();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void checkReadWritePermission(){
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                return;
            }
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            return;
        } else {
            new CountDownTimer(Constants.SplashScreen.countdownTime, Constants.SplashScreen.countdownIntervalTime) {

                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    if(cc.loadPrefBoolean("isLoggedIn")){
                        startActivity(new Intent(Splash.this, SelectGender.class));
                    } else {
                        startActivity(new Intent(Splash.this, Login.class));
                    }
                    finish();
                }
            }.start();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new CountDownTimer(Constants.SplashScreen.countdownTime, Constants.SplashScreen.countdownIntervalTime) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            if(cc.loadPrefBoolean("isLoggedIn")){
                                startActivity(new Intent(Splash.this, SelectGender.class));
                            } else {
                                startActivity(new Intent(Splash.this, Login.class));
                            }
                            finish();
                        }
                    }.start();
                } else {
                    // Permission Denied
                    cc.showSnackbar(rlControler, "Please allow the permission for better performance");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}