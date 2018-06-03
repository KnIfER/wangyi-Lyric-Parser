package com.knizha.wangYiLP;

import android.support.annotation.NonNull;

import java.io.File;
import java.net.URI;

public class Filemy extends File {

    public Filemy(@NonNull String pathname) {
        super(pathname);
    }

    public Filemy(String parent, @NonNull String child) {
        super(parent, child);
    }

    public Filemy(File parent, @NonNull String child) {
        super(parent, child);
    }

    public Filemy(@NonNull URI uri) {
        super(uri);
    }

    //倒序
    @Override
    public File[] listFiles() {
        String[] ss = list();
        if (ss == null) return null;
        int n = ss.length;
        File[] fs = new File[n];
        //倒序
        for (int i = 0; i <n; i++) {
            fs[n-i-1] = new File(this.getAbsolutePath(),ss[i]);
        }
        //for (int i = 0; i <n; i++) {
        //    fs[i] = new File(this.getAbsolutePath(),ss[i]);
        //}
        return fs;
    }
}