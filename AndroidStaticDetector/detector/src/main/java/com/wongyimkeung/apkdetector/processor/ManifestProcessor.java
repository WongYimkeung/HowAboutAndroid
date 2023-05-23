package com.wongyimkeung.apkdetector.processor;

import com.wongyimkeung.apkdetector.Context;
import com.wongyimkeung.apkdetector.Processor;
import com.wongyimkeung.apkdetector.info.PermissionInfo;
import com.wongyimkeung.apkdetector.util.LogUtil;
import com.wongyimkeung.apkdetector.Constant;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.Iterator;

public class ManifestProcessor implements Processor {
    private final Processor processor;
    private Context context;

    public ManifestProcessor(Processor processor) {
        this.processor = processor;
    }

    @Override
    public boolean process(Context context) {
        this.context = context;
        processManifest(context.getAndroidManifestPath());
        return processor.process(context);
    }

    private void processManifest(String manifestPath) {
        LogUtil.start("ManifestProcessor");
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(new File(manifestPath));
            Element root = document.getRootElement();

            Iterator<Element> iterator = root.elementIterator();
            while (iterator.hasNext()) {
                Element element = iterator.next();

                if (element.getName().equals(Constant.STRING_MANIFEST_USES_PERMISSION)) {
                    processPermission(element.attributeValue("name"));
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        LogUtil.end("ManifestProcessor");
    }

    private void processPermission(String permission) {
        boolean isPermissionContain = false;

        for (PermissionInfo permissionInfo : context.getPermissionInfoList()) {
            if (permissionInfo.getName().equals(permission)) {
                permissionInfo.setDeclared(true);
                isPermissionContain = true;
                break;
            }
        }

        if (!isPermissionContain) {
            context.getPermissionInfoList().add(new PermissionInfo(
                    permission, "", "", true));
        }
    }
}
