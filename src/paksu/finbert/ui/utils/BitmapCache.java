package paksu.finbert.ui.utils;

import android.graphics.Bitmap;
import android.util.LruCache;

public class BitmapCache extends LruCache<String, Bitmap> {

    public BitmapCache(int cacheSizeInMegabytes) {
        super(cacheSizeInMegabytes * 1024 * 1024);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getByteCount();
    }
}
