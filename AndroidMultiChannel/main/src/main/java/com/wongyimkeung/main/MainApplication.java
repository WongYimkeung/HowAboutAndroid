package com.wongyimkeung.main;

import com.wongyimkeung.channel.ChannelUtil;
import com.wongyimkeung.channel.util.ConfigUtil;

import java.io.File;
import java.util.List;

public class MainApplication {
    public static void main(String[] args) {
        System.out.println("MainApplication");

        List<String> channels = null;
        String sourcePath = null;

        for (String arg : args) {
            System.out.println(arg);

            if (arg.startsWith("-c=")) {
                channels = ConfigUtil.getChannelListByString(arg.replaceFirst("-c=", ""));
            } else if (arg.startsWith("-f=")) {
                channels = ConfigUtil.getChannelListByFile(arg.replaceFirst("-f=", ""));
            } else if (arg.startsWith("-apk=")) {
                sourcePath = arg.replaceFirst("-apk=", "");
            }
        }

        if (null == sourcePath) {
            System.out.println("No apk is configured");
            return;
        }

        File apk = new File(sourcePath);
        System.out.println("\n\nsourcePath = " + sourcePath);

        String channel = ChannelUtil.readChannel(apk);
        if (null != channel) {
            System.out.println("\nread channel = " + channel);
        }

        if (null == channels || channels.isEmpty()) {
            return;
        }

        System.out.println("\n\n==========start build channel package==========\n");
        for (String channelString : channels) {
            System.out.println("write channel = " + channelString);
            ChannelUtil.writeChannel(new File(sourcePath), channelString);
        }
        System.out.println("\n==========build channel package finished==========\n\n");
    }
}