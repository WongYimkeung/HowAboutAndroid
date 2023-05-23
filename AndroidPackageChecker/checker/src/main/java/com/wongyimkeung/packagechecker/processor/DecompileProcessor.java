package com.wongyimkeung.packagechecker.processor;

import com.wongyimkeung.packagechecker.Context;
import com.wongyimkeung.packagechecker.Processor;
import com.wongyimkeung.packagechecker.info.PackageInfo;
import com.wongyimkeung.packagechecker.util.LogUtil;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DecompileProcessor implements Processor {
    private static final int INT_VALUE_SUCCESS = 0;
    private final Processor processor;

    public DecompileProcessor(Processor processor) {
        this.processor = processor;
    }

    @Override
    public boolean process(Context context) {
        LogUtil.start("DecompileProcessor");

        for (PackageInfo packageInfo : context.getPackageInfoList()) {
            String md5 = calculateMD5(packageInfo.getApkPath());
            LogUtil.r("md5 " + md5);
            packageInfo.setMd5(md5);

            String decompileResult = decompile(packageInfo.getApkPath());
            LogUtil.r("decompile " + packageInfo.getApkPath() + " " + decompileResult + "\n");
        }

        LogUtil.end("DecompileProcessor");
        return processor.process(context);
    }

    public static String calculateMD5(String path) {
        BigInteger bigInteger = null;

        try {
            int len = 0;
            byte[] buffer = new byte[8192];
            MessageDigest md = MessageDigest.getInstance("MD5");

            File f = new File(path);
            FileInputStream fis = new FileInputStream(f);

            while ((len = fis.read(buffer)) != -1) {
                md.update(buffer, 0, len);
            }
            fis.close();

            byte[] b = md.digest();
            bigInteger = new BigInteger(1, b);
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }

        return bigInteger.toString(16);
    }

    public String decompile(String apkPath) {
        boolean success;
        try {
            Process process = Runtime.getRuntime().exec("java -jar apktool.jar d " + apkPath);
            success = handleProcess(process);
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        }
        return success ? "success" : "fail";
    }

    private boolean handleProcess(Process process) {
        try {
            String result;

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((result = reader.readLine()) != null) {
                LogUtil.r(result);
            }
            reader.close();

            // 休眠1s再读取exitValue，避免报异常
            // java.lang.IllegalThreadStateException: process hasn't exited
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int exitValue = process.exitValue();
            LogUtil.r("exitValue = " + exitValue);
            if (exitValue == INT_VALUE_SUCCESS) {
                process.destroy();
                return true;
            }

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((result = errorReader.readLine()) != null) {
                LogUtil.e(result);
            }
            errorReader.close();
            process.destroy();
            return false;
        } catch (IllegalThreadStateException | IOException e) {
            e.printStackTrace();
            process.destroy();
            return false;
        }
    }
}
