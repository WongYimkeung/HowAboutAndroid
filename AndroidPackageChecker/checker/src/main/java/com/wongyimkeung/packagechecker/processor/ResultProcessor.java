package com.wongyimkeung.packagechecker.processor;

import com.wongyimkeung.packagechecker.Context;
import com.wongyimkeung.packagechecker.Processor;
import com.wongyimkeung.packagechecker.info.PackageInfo;
import com.wongyimkeung.packagechecker.util.IOUtil;
import com.wongyimkeung.packagechecker.util.LogUtil;
import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ResultProcessor implements Processor {
    @Override
    public boolean process(Context context) {
        LogUtil.start("ResultProcessor");

        JSONArray resultArray = new JSONArray();
        for (PackageInfo packageInfo : context.getPackageInfoList()) {
            resultArray.put(packageInfo.getJSONObject());
        }
        try {
            LogUtil.d(resultArray.toString(4));
            IOUtil.write(getResultFileName(), resultArray.toString(4));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LogUtil.end("ResultProcessor");
        return true;
    }

    public static String getResultFileName() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return "CheckResult" + "-" + simpleDateFormat.format(new Date()) + ".txt";
    }
}
