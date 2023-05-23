package com.wongyimkeung.channel.writer;

import com.wongyimkeung.channel.util.ApkUtil;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * 写入渠道信息到文件的Comment区域
 * 仅限V1签名下可用
 */
public class CommentWriter {
    public static void writeChannel(File file, String data) {
        if (file == null) throw new NullPointerException("Apk file can not be null");
        if (!file.exists()) throw new IllegalArgumentException("Apk file is not found");

        int length = data.length();
        if (length > Short.MAX_VALUE)
            throw new IllegalArgumentException("Size out of range: " + length);

        RandomAccessFile accessFile = null;
        try {
            accessFile = new RandomAccessFile(file, "rw");
            long index = accessFile.length();
            index -= 2; // 2 = FCL
            accessFile.seek(index);

            short dataLen = (short) length;
            int tempLength = dataLen + ApkUtil.BYTE_DATA_LEN + ApkUtil.COMMENT_MAGIC.length();
            if (tempLength > Short.MAX_VALUE)
                throw new IllegalArgumentException("Size out of range: " + tempLength);

            short fcl = (short) tempLength;
            // Write FCL
            ByteBuffer byteBuffer = ByteBuffer.allocate(Short.BYTES);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            byteBuffer.putShort(fcl);
            byteBuffer.flip();
            accessFile.write(byteBuffer.array());

            // Write data
            accessFile.write(data.getBytes("UTF-8"));

            // Write data len
            byteBuffer = ByteBuffer.allocate(Short.BYTES);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            byteBuffer.putShort(dataLen);
            byteBuffer.flip();
            accessFile.write(byteBuffer.array());

            // Write flag
            accessFile.write(ApkUtil.COMMENT_MAGIC.getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != accessFile) {
                try {
                    accessFile.close();
                } catch (IOException e) {
                    // ignored
                }
            }
        }
    }
}
