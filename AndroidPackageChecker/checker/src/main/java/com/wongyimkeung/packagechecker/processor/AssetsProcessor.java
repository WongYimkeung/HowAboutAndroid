package com.wongyimkeung.packagechecker.processor;

import com.wongyimkeung.packagechecker.Constant;
import com.wongyimkeung.packagechecker.Context;
import com.wongyimkeung.packagechecker.Processor;
import com.wongyimkeung.packagechecker.info.PackageInfo;
import com.wongyimkeung.packagechecker.util.LogUtil;

import java.io.File;

public class AssetsProcessor implements Processor {
    private final Processor processor;

    public AssetsProcessor(Processor processor) {
        this.processor = processor;
    }

    @Override
    public boolean process(Context context) {
        LogUtil.start("AssetsProcessor");

        for (PackageInfo packageInfo : context.getPackageInfoList()) {
            processAssets(packageInfo);
        }

        LogUtil.end("AssetsProcessor");
        return processor.process(context);
    }

    public void processAssets(PackageInfo packageInfo) {
        File reactNativeBundleFile = new File(packageInfo.getDecompilePath() + File.separator
                + Constant.PATH_ASSETS_REACT_NATIVE_BUNDLE);
        LogUtil.d("reactNativeBundleFile " + reactNativeBundleFile.getPath() + " " + reactNativeBundleFile.exists());
        if (reactNativeBundleFile.exists()) {
            packageInfo.setReactNativeBundleExist(true);
        }
    }
}
