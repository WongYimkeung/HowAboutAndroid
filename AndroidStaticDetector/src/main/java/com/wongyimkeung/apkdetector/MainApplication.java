package com.wongyimkeung.apkdetector;

import com.wongyimkeung.apkdetector.util.LogUtil;

public class MainApplication {
    public static void main(String[] args) {
        LogUtil.setLogLevel(LogUtil.DEBUG);
        AnalyzeStarter.getInstance().startProcess(args);
    }
}
