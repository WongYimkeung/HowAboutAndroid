package com.wongyimkeung.packagechecker;

import com.wongyimkeung.packagechecker.info.PackageInfo;

import java.util.ArrayList;
import java.util.List;

public class Context {
    List<PackageInfo> packageInfoList = new ArrayList<>();

    public List<PackageInfo> getPackageInfoList() {
        return packageInfoList;
    }

    public void setPackageInfoList(List<PackageInfo> packageInfoList) {
        this.packageInfoList = packageInfoList;
    }
}
