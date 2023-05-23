package com.wongyimkeung.packagechecker;

import com.wongyimkeung.packagechecker.util.Util;
import com.wongyimkeung.packagechecker.util.LogUtil;
import com.wongyimkeung.packagechecker.processor.*;

public class CheckerStarter {
    private static CheckerStarter checkerStarter;
    private boolean isProcessing = false;

    private CheckerStarter() {

    }

    public static CheckerStarter getInstance() {
        if (null == checkerStarter) {
            synchronized (CheckerStarter.class) {
                if (null == checkerStarter) {
                    checkerStarter = new CheckerStarter();
                }
            }
        }
        return checkerStarter;
    }

    /**
     * 开始检测，检测流程如下：
     * 1、处理配置
     * 2、反编译APK
     * 3、处理AndroidManifest.xml
     * 4、处理assets文件夹
     * 5、处理Smali文件夹
     * 6、输出结果
     *
     * @param args 传入参数
     */
    public synchronized void startProcess(String[] args) {
        if (isProcessing) {
            LogUtil.e("is processing, can't start again");
            return;
        }

        isProcessing = true;
        long startAnalyzeTime = System.currentTimeMillis();
        LogUtil.start("Checker");

        Context context = new Context();
        ResultProcessor resultProcessor
                = new ResultProcessor();
        CheckProcessor checkProcessor
                = new CheckProcessor(resultProcessor);
        SmaliProcessor smaliProcessor
                = new SmaliProcessor(checkProcessor);
        AssetsProcessor assetsProcessor
                = new AssetsProcessor(smaliProcessor);
        ManifestProcessor manifestProcessor
                = new ManifestProcessor(assetsProcessor);
        DecompileProcessor decompileProcessor
                = new DecompileProcessor(manifestProcessor);
        ArgumentsProcessor argumentsProcessor
                = new ArgumentsProcessor(decompileProcessor);

        boolean result = argumentsProcessor.process(context);
        long stopAnalyzeTime = System.currentTimeMillis();
        LogUtil.r("\nCheck result is " + (result ? "success" : "fail"));
        LogUtil.r("takes " + Util.getCostTime(startAnalyzeTime, stopAnalyzeTime));

        LogUtil.end("Checker");
        isProcessing = false;
    }
}
