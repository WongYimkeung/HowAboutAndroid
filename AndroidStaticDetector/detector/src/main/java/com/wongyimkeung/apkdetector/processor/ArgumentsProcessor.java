package com.wongyimkeung.apkdetector.processor;

import com.wongyimkeung.apkdetector.Context;
import com.wongyimkeung.apkdetector.Processor;
import com.wongyimkeung.apkdetector.util.LogUtil;
import com.wongyimkeung.apkdetector.Constant;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ArgumentsProcessor implements Processor {
    private final Processor processor;
    private final String[] args;
    private Context context;

    public ArgumentsProcessor(Processor processor, String[] args) {
        this.processor = processor;
        this.args = args;
    }

    @Override
    public boolean process(Context context) {
        this.context = context;
        if (handleArguments(args)) {
            return processor.process(this.context);
        }
        return false;
    }

    public boolean handleArguments(String[] args) {
        if (null == args || args.length == 0) {
            throw new IllegalArgumentException("no argument found");
        }

        if (args.length == 1 && args[0].startsWith(Constant.KEY_CONFIG_FILE)) {
            String[] splitString = args[0].split("=");
            return handleConfigFile(splitString[1]);
        }

        boolean result = true;
        for (String arg : args) {
            LogUtil.d(arg);

            String[] splitString = arg.split("=");
            if (splitString.length < 2) {
                LogUtil.e("arg " + arg + "'s format is wrong");
                continue;
            }

            switch (splitString[0]) {
                case Constant.KEY_APK_PATH:
                    result = handleApkPath(splitString[1]);
                    break;
                case Constant.KEY_EXCLUDE_DIR:
                    handleExcludeDir(splitString[1]);
                    break;
                case Constant.KEY_INTERFACE_FILE:
                    handleInterfaceFile(splitString[1]);
                    break;
                case Constant.KEY_SOURCE_FILE:
                    handleSourceFile(splitString[1]);
                    break;
                case Constant.KEY_PERMISSION_FILE:
                    handlePermissionFile(splitString[1]);
                    break;
                case Constant.KEY_CONFIG_FILE:
                    handleConfigFile(splitString[1]);
                    break;
                default:
                    LogUtil.e("unknown arg " + splitString[0]);
                    break;
            }

            // 如果configFile存在，其他参数无效
            if (Constant.KEY_CONFIG_FILE.equals(splitString[0])) {
                break;
            }
        }
        return result;
    }

    private boolean handleConfigFile(String configFile) {
        if (null == configFile || configFile.length() == 0) {
            LogUtil.e("configFile is null");
            return false;
        }

        boolean result = true;
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(configFile));
            if (properties.containsKey(Constant.KEY_APK_PATH)) {
                result = handleApkPath(properties.getProperty(Constant.KEY_APK_PATH));
            }
            if (properties.containsKey(Constant.KEY_EXCLUDE_DIR)) {
                handleExcludeDir(properties.getProperty(Constant.KEY_EXCLUDE_DIR));
            }
            if (properties.containsKey(Constant.KEY_INTERFACE_FILE)) {
                handleInterfaceFile(properties.getProperty(Constant.KEY_INTERFACE_FILE));
            }
            if (properties.containsKey(Constant.KEY_SOURCE_FILE)) {
                handleSourceFile(properties.getProperty(Constant.KEY_SOURCE_FILE));
            }
            if (properties.containsKey(Constant.KEY_PERMISSION_FILE)) {
                handlePermissionFile(properties.getProperty(Constant.KEY_PERMISSION_FILE));
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    private boolean handleApkPath(String apkPath) {
        if (null == apkPath || apkPath.length() == 0
                || !apkPath.endsWith(Constant.STRING_APK_SUFFIX)) {
            LogUtil.e("apkPath is illegal");
            return false;
        }

        File file = new File(apkPath);
        if (!file.exists()) {
            LogUtil.e("apkPath " + apkPath + " is not exists");
            return false;
        }

        context.setApkPath(apkPath);
        context.setDirName(file.getName().replace(Constant.STRING_APK_SUFFIX, ""));
        LogUtil.r("handleApkPath apkPath = " + context.getApkPath());
        LogUtil.r("handleApkPath dirName = " + context.getDirName());

        return true;
    }

    private void handleExcludeDir(String excludeDir) {
        String[] splitExcludeDir = excludeDir.split(",");
        for (String dir : splitExcludeDir) {
            context.getExcludeDirList().add(dir);
        }
        LogUtil.r("handleExcludeDir " + context.getExcludeDirList());
    }

    private void handleInterfaceFile(String interfaceFile) {
        context.setInterfaceFile(interfaceFile);
        LogUtil.r("handleInterfaceFile interfaceFile = " + context.getInterfaceFile());
    }

    private void handleSourceFile(String sourceFile) {
        context.setSourceFile(sourceFile);
        LogUtil.r("handleSourceFile sourceFile = " + context.getSourceFile());
    }

    private void handlePermissionFile(String permissionFile) {
        context.setPermissionFile(permissionFile);
        LogUtil.r("handlePermissionFile permissionFile = " + context.getPermissionFile());
    }
}
