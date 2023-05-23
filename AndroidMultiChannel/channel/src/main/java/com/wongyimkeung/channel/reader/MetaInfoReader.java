package com.wongyimkeung.channel.reader;

import com.wongyimkeung.channel.util.ApkUtil;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 从应用META-INF文件夹下读取渠道信息
 */
public class MetaInfoReader {
    public static String readChannel(File file) {
        String channel = null;

        try {
            ZipFile zipFile = new ZipFile(file);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name == null
                        || name.trim().length() == 0
                        || !name.startsWith(ApkUtil.APK_META_INFO_CHANNEL_PREFIX)) {
                    continue;
                }

                channel = name.replace(ApkUtil.APK_META_INFO_CHANNEL_PREFIX, "");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return channel;
    }
}
