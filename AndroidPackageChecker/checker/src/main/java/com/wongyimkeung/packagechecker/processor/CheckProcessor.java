package com.wongyimkeung.packagechecker.processor;

import com.wongyimkeung.packagechecker.Context;
import com.wongyimkeung.packagechecker.Processor;
import com.wongyimkeung.packagechecker.util.IOUtil;
import com.wongyimkeung.packagechecker.util.LogUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CheckProcessor implements Processor {
    private final Processor processor;

    public CheckProcessor(Processor processor) {
        this.processor = processor;
    }

    @Override
    public boolean process(Context context) {
        LogUtil.start("CheckProcessor");

//        JSONArray packageJSONArray = loadJSONArray();
//        int packageLength = packageJSONArray.length();
//
//        for (PackageInfo packageInfo : context.getPackageInfoList()) {
//            JSONObject targetJSONObject = packageInfo.getJSONObject();
//
//            for (int i = 0; i < packageLength; i++) {
//                try {
//                    JSONObject comparedJSONObject = packageJSONArray.getJSONObject(i);
//                    String targetPackageName = targetJSONObject.getJSONObject("manifest").getString("packageName");
//                    String comparedPackageName = comparedJSONObject.getJSONObject("manifest").getString("packageName");
//
//                    if (targetPackageName.equals(comparedPackageName)) {
//                        JSONObject checkResult = compareJSONObject(targetJSONObject, comparedJSONObject);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

        LogUtil.end("CheckProcessor");
        return processor.process(context);
    }

    private JSONArray loadJSONArray() {
        JSONArray jsonArray = new JSONArray();
        List<String> stringList = null;
        try {
            stringList = IOUtil.read("package.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (null == stringList || stringList.size() <= 0) {
            LogUtil.d("loadJSONArray: stringList is empty");
            return jsonArray;
        }

        StringBuilder jsonArrayString = new StringBuilder();
        for (String str : stringList) {
            jsonArrayString.append(str);
        }
        try {
            jsonArray = new JSONArray(jsonArrayString.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    /**
     * 比较两个JSON对象
     *
     * @param target   需要检查的包体参数
     * @param compared package.json 文件中的参数，正确参数
     * @return 比较结果
     */
    private JSONObject compareJSONObject(JSONObject target, JSONObject compared) {
        JSONObject checkResult = new JSONObject();

        try {
            if (compared.has("oaid")) {
                checkResult.put("oaidCheck",
                        compareJSONObject(target, compared, "oaid"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return checkResult;
    }

    private String compareJSONObject(JSONObject target, JSONObject compared, String jsonObjectName) {
        List<String> checkResults = new ArrayList<>();

        try {
            JSONObject comparedConfig = compared.getJSONObject(jsonObjectName);
            JSONObject targetConfig = target.getJSONObject(jsonObjectName);
            Iterator<String> configKeys = comparedConfig.keys();

            while (configKeys.hasNext()) {
                String key = configKeys.next();

                if (!targetConfig.has(key)) {
                    checkResults.add(key + " not configured");
                    continue;
                }
                if (!targetConfig.get(key).equals(comparedConfig.get(key))) {
                    checkResults.add(key + " not matched");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return e.toString();
        }

        return checkResults.size() == 0 ? "pass" : checkResults.toString();
    }
}
