package com.wongyimkeung.channel.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ConfigUtil {
    public static List<String> getChannelListByFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return Collections.emptyList();
        }
        return getChannelListByFile(file);
    }

    public static List<String> getChannelListByFile(File file) {
        List<String> channelList = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String channel = null;
            while ((channel = bufferedReader.readLine()) != null) {
                channelList.add(channel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return channelList;
    }

    public static List<String> getChannelListByString(String channels) {
        return Arrays.asList(channels.split(","));
    }
}
