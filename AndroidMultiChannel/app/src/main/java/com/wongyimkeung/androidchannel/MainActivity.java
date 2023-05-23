package com.wongyimkeung.androidchannel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.wongyimkeung.channel.ChannelUtil;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    TextView mTvChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvChannel = findViewById(R.id.tv_channel);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: getChannelFromManifest = " + getChannelFromManifest(this));

        File sourceDirFile = getSourceDirFile(this);
        if (null != sourceDirFile) {
            String channel = "Channel: " + ChannelUtil.readChannel(sourceDirFile);
            Log.d(TAG, "onResume: channel = " + channel);
            mTvChannel.setText(channel);
        }
    }

    private File getSourceDirFile(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        if (null == applicationInfo) {
            return null;
        }

        String sourceDir = applicationInfo.sourceDir;
        if (null == sourceDir) {
            return null;
        }
        Log.d(TAG, "getSourceDirFile: sourceDir = " + sourceDir);

        return new File(sourceDir);
    }

    /**
     * 从AndroidManifest文件中获取渠道信息
     *
     * @param context Android上下文
     * @return 渠道信息
     */
    private static String getChannelFromManifest(Context context) {
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (null == applicationInfo) {
            return null;
        }

        Bundle metaDataBundle = applicationInfo.metaData;
        if (null == metaDataBundle) {
            return null;
        }

        String channel = metaDataBundle.getString("channel");
        if (TextUtils.isEmpty(channel)) {
            return null;
        }

        return channel;
    }
}