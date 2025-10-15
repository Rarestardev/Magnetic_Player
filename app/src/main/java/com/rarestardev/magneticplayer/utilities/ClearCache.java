package com.rarestardev.magneticplayer.utilities;

import android.content.Context;
import android.util.Log;

import java.io.File;

public class ClearCache {

    private final Context context;

    public ClearCache(Context context) {
        this.context = context;
    }

    public void clearAppCache() {
        try {
            File cacheDir = context.getCacheDir();
            if (cacheDir != null && cacheDir.isDirectory()) {
                deleteDir(cacheDir);
            }
        } catch (Exception e) {
            Log.e(Constants.appLog,e.getMessage());
        }

    }

    private boolean deleteDir(File cacheDir) {
        if (cacheDir != null && cacheDir.isDirectory()) {
            String[] children = cacheDir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(cacheDir, child));
                if (!success) {
                    return false;
                }
            }
            return cacheDir.delete();
        } else if (cacheDir != null && cacheDir.isFile()) {
            return cacheDir.delete();
        } else {
            return false;
        }
    }

}
