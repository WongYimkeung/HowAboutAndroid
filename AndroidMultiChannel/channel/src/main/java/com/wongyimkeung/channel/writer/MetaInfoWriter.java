package com.wongyimkeung.channel.writer;

import com.wongyimkeung.channel.util.ApkUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * 写入渠道信息到META-INF文件夹下
 */
public class MetaInfoWriter {
    public static void writeChannel(File file, String channel) {
        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(
                    ApkUtil.getNewFileName(file.getName(), channel)));

            ZipFile jarFile = new ZipFile(file);
            Enumeration<? extends ZipEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                zipOutputStream.putNextEntry(new ZipEntry(zipEntry.getName()));
                InputStream inputStream = jarFile.getInputStream(zipEntry);
                byte[] buffer = new byte[2048];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    zipOutputStream.write(buffer, 0, len);
                }
                zipOutputStream.closeEntry();
            }

            zipOutputStream.putNextEntry(new ZipEntry(ApkUtil.APK_META_INFO_CHANNEL_PREFIX + channel));
            zipOutputStream.closeEntry();
            zipOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
