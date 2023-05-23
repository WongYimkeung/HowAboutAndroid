package com.wongyimkeung.packagechecker.processor;

import com.wongyimkeung.packagechecker.Constant;
import com.wongyimkeung.packagechecker.Context;
import com.wongyimkeung.packagechecker.Processor;
import com.wongyimkeung.packagechecker.info.PackageInfo;
import com.wongyimkeung.packagechecker.util.IOUtil;
import com.wongyimkeung.packagechecker.util.LogUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class SmaliProcessor implements Processor {
    private final Processor processor;

    public SmaliProcessor(Processor processor) {
        this.processor = processor;
    }

    @Override
    public boolean process(Context context) {
        LogUtil.start("SmaliProcessor");
        for (PackageInfo packageInfo : context.getPackageInfoList()) {
            processSmali(packageInfo.getDecompilePath(), packageInfo);
        }
        LogUtil.end("SmaliProcessor");
        return processor.process(context);
    }

    /**
     * 处理smali
     * <p>
     * 注意：判断sdk是否存在，一定要先判断存在然后再赋值true，不能直接根据是否存在结果赋值。
     * 因为如果存在多个smali目录（dex分包），那么处理后面smali目录时候会覆盖前面smali的判断结果
     * <p>
     * 例如：
     * // 正确方式
     * if (oaidSdkFile.exists()) {
     * packageInfo.setOaidSdkExist(true);
     * }
     * // 错误方式
     * packageInfo.setOaidSdkExist(oaidSdkFile.exists());
     *
     * @param decompilePath 反编译路径
     * @param packageInfo   packageInfo
     */
    private void processSmali(String decompilePath, PackageInfo packageInfo) {
        File decompileFile = new File(decompilePath);
        File[] files = decompileFile.listFiles();
        if (null == files && files.length == 0) {
            return;
        }

        for (File file : files) {
            if (!file.getName().startsWith("smali")) {
                continue;
            }

            File oaidSdkFile = new File(file.getPath() + File.separator + Constant.PATH_OAID_SDK);
            LogUtil.d("oaidSdkFile " + oaidSdkFile.getPath() + " " + oaidSdkFile.exists());
            if (oaidSdkFile.exists()) {
                packageInfo.setOaidSdkExist(true);
            }
        }
    }

    private String getFileVersionFromSmali(File file) {
        String matchStr = ".field private static final VERSION:Ljava/lang/String; = ";
        String version = "";

        try {
            List<String> stringList = IOUtil.read(file);
            for (String str : stringList) {
                if (str.contains(matchStr)) {
                    version = str.replace(matchStr, "").replace("\"", "");
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }
}
