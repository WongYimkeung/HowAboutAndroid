package com.wongyimkeung.packagechecker.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LogUtil {
    public static final int DEBUG = 0;
    public static final int RELEASE = 1;

    private static int logLevel = DEBUG;
    private static BufferedWriter bufferedWriter;

    public static void setLogLevel(int logLevel) {
        LogUtil.logLevel = logLevel;
    }

    public static void d(String msg) {
        if (DEBUG >= logLevel) {
            System.out.println(msg);
        }
        localLog(msg);
    }

    public static void d(Object obj) {
        if (DEBUG >= logLevel) {
            System.out.println(obj);
        }
    }

    public static void r(String msg) {
        if (RELEASE >= logLevel) {
            System.out.println(msg);
        }
        localLog(msg);
    }

    public static void r(Object obj) {
        if (RELEASE >= logLevel) {
            System.out.println(obj);
        }
    }

    public static void e(String msg) {
        if (RELEASE >= logLevel) {
            System.out.println("Error: " + msg);
        }
        localLog(msg);
    }

    public static void e(Object obj) {
        if (RELEASE >= logLevel) {
            System.out.println("Error: " + obj);
        }
    }

    public static void start(String msg) {
        if (RELEASE >= logLevel) {
            System.out.println("\n+++++++++++++++ " + msg + " start +++++++++++++++\n");
        }
        localLog("\n+++++++++++++++ " + msg + " start +++++++++++++++\n");
    }

    public static void end(String msg) {
        if (RELEASE >= logLevel) {
            System.out.println("\n--------------- " + msg + " end ---------------\n");
        }
        localLog("\n--------------- " + msg + " end ---------------\n");
    }

    public static void localLog(String msg) {
        if (null == bufferedWriter) {
            try {
                bufferedWriter = new BufferedWriter(new FileWriter(Util.getLogFileName()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            bufferedWriter.write(msg);
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
