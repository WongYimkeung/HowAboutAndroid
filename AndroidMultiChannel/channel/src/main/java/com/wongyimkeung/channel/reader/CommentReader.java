package com.wongyimkeung.channel.reader;

import com.wongyimkeung.channel.util.ApkUtil;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

/**
 * 从文件的Comment区域读取渠道信息
 * 仅限V1签名下可用
 */
public class CommentReader {
    public static String readChannel(File file) {
        if (file == null) throw new NullPointerException("Apk file can not be null");
        if (!file.exists()) throw new IllegalArgumentException("Apk file is not found");

        RandomAccessFile accessFile = null;
        try {
            accessFile = new RandomAccessFile(file, "r");
            FileChannel fileChannel = accessFile.getChannel();
            long index = accessFile.length();

            // Read flag
            index -= ApkUtil.COMMENT_MAGIC.length();
            fileChannel.position(index);
            ByteBuffer byteBuffer = ByteBuffer.allocate(ApkUtil.COMMENT_MAGIC.length());
            fileChannel.read(byteBuffer);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            if (!new String(byteBuffer.array(), "UTF-8").equals(ApkUtil.COMMENT_MAGIC)) {
                return null;
            }

            // Read dataLen
            index -= ApkUtil.BYTE_DATA_LEN;
            fileChannel.position(index);
            byteBuffer = ByteBuffer.allocate(Short.BYTES);
            fileChannel.read(byteBuffer);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            short dataLen = byteBuffer.getShort(0);

            // Read data
            index -= dataLen;
            fileChannel.position(index);
            byteBuffer = ByteBuffer.allocate(dataLen);
            fileChannel.read(byteBuffer);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            return new String(byteBuffer.array(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (accessFile != null) {
                try {
                    accessFile.close();
                } catch (IOException e) {
                    // ignored
                }
            }
        }
        return null;
    }
}
