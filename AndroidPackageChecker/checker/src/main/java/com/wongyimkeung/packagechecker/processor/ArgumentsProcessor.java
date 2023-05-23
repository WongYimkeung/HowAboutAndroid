package com.wongyimkeung.packagechecker.processor;

import com.wongyimkeung.packagechecker.Constant;
import com.wongyimkeung.packagechecker.Context;
import com.wongyimkeung.packagechecker.Processor;
import com.wongyimkeung.packagechecker.info.PackageInfo;
import com.wongyimkeung.packagechecker.util.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ArgumentsProcessor implements Processor {
    private final Processor processor;

    public ArgumentsProcessor(Processor processor) {
        this.processor = processor;
    }

    @Override
    public boolean process(Context context) {
        LogUtil.start("ArgumentsProcessor");

        List<PackageInfo> packageInfoList = new ArrayList<>();
        File currentFile = new File(".");

        File[] files = currentFile.listFiles();
        if (null != files && files.length > 0) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(Constant.FILE_SUFFIX_APK)) {
                    packageInfoList.add(parsePackageInfo(file));
                }
            }
        }

        context.setPackageInfoList(packageInfoList);

        LogUtil.end("ArgumentsProcessor");
        return processor.process(context);
    }

    private PackageInfo parsePackageInfo(File file) {
        String apkPath = file.getName();
        String decompilePath = file.getName().replace(Constant.FILE_SUFFIX_APK, "");
        LogUtil.d("apkPath:" + apkPath + ", decompilePath:" + decompilePath);
        return new PackageInfo(apkPath, decompilePath);
    }
}
