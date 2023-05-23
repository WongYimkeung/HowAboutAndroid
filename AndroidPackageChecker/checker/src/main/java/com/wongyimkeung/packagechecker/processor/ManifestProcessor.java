package com.wongyimkeung.packagechecker.processor;

import com.wongyimkeung.packagechecker.Constant;
import com.wongyimkeung.packagechecker.Context;
import com.wongyimkeung.packagechecker.Processor;
import com.wongyimkeung.packagechecker.info.PackageInfo;
import com.wongyimkeung.packagechecker.util.LogUtil;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

import java.io.File;

public class ManifestProcessor implements Processor {
    private final Processor processor;

    public ManifestProcessor(Processor processor) {
        this.processor = processor;
    }

    @Override
    public boolean process(Context context) {
        LogUtil.start("ManifestProcessor");

        for (PackageInfo packageInfo : context.getPackageInfoList()) {
            processManifest(packageInfo.getDecompilePath() + File.separator
                    + Constant.PATH_ANDROID_MANIFEST, packageInfo);
        }

        LogUtil.end("ManifestProcessor");
        return processor.process(context);
    }

    private void processManifest(String manifestPath, PackageInfo packageInfo) {
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(new File(manifestPath));
            Element root = document.getRootElement();
            packageInfo.setPackageName(root.attributeValue("package"));
            packageInfo.setPackageVersion(root.attributeValue("android:versionName"));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}
