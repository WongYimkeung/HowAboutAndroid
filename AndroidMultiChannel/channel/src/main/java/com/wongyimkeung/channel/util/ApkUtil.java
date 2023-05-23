package com.wongyimkeung.channel.util;

import java.io.File;

public class ApkUtil {
    /**
     * META-INF文件夹下渠道信息文件前缀
     */
    public static final String APK_META_INFO_CHANNEL_PREFIX = "META-INF" + File.separator + "bt_channel_";

    public static final String COMMENT_MAGIC = "CHANNEL";
    public static final short BYTE_DATA_LEN = 2;

    public static String getNewFileName(String fileName, String channel) {
        return fileName.replace(".apk", "") + "-" + channel + ".apk";
    }
}
