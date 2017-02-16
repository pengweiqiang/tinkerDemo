package com.shopin.tinkerdemo;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.tencent.tinker.lib.tinker.TinkerInstaller;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void loadPatch(View view){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/patch_signed.apk";
        File file = new File(path);
        boolean isExist = file.exists();
        Log.d(TAG, "loadPatch: "+isExist);
        Toast.makeText(this,"loadPatch start "+path,Toast.LENGTH_LONG).show();
        TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(),
                Environment.getExternalStorageDirectory().getAbsolutePath()+"/patch_signed_7zip.apk");
    }
}
