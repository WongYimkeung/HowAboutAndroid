package com.wongyimkeung.packagechecker.info;

import org.json.JSONException;
import org.json.JSONObject;

public class PackageInfo {
    String apkPath; // Apk文件路径
    String decompilePath;   // 反编译后文件路径
    String md5; // MD5

    // 从AndroidManifest.xml读取
    String packageName; // 包名
    String packageVersion;  // 版本

    // 从assets文件夹读取
    boolean isReactNativeBundleExist;  // React Native 框架

    // 从smali文件夹读取
    // 从MdidSdkHelper.smali文件读取
    boolean isOaidSdkExist;  // OAID SDK是否存在

    public PackageInfo(String apkPath, String decompilePath) {
        this.apkPath = apkPath;
        this.decompilePath = decompilePath;
    }

    public String getApkPath() {
        return apkPath;
    }

    public String getDecompilePath() {
        return decompilePath;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setPackageVersion(String packageVersion) {
        this.packageVersion = packageVersion;
    }

    public void setOaidSdkExist(boolean oaidSdkExist) {
        isOaidSdkExist = oaidSdkExist;
    }

    public void setReactNativeBundleExist(boolean reactNativeBundleExist) {
        isReactNativeBundleExist = reactNativeBundleExist;
    }

    public JSONObject getJSONObject() {
        JSONObject manifest = new JSONObject();
        try {
            if (null != packageName && packageName.length() > 0) {
                manifest.put("packageName", packageName);
            }
            if (null != packageVersion && packageVersion.length() > 0) {
                manifest.put("packageVersion", packageVersion);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject sdk = new JSONObject();
        try {
            sdk.put("isOaidSdkExist", isOaidSdkExist);
            sdk.put("isReactNativeBundleExist", isReactNativeBundleExist);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject apk = new JSONObject();
        try {
            apk.put("apkPath", apkPath);
            apk.put("md5", md5);

            apk.put("manifest", manifest);
            apk.put("sdk", sdk);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return apk;
    }
}
