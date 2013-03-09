package paksu.finbert;

import paksu.finbert.ui.utils.BitmapCache;
import android.app.ActivityManager;
import android.app.Application;

public class FinbertApplication extends Application {

    private BitmapCache bitmapCache;

    @Override
    public void onCreate() {
        super.onCreate();
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        bitmapCache = new BitmapCache((int) (manager.getMemoryClass() * Config.BITMAP_CACHE_PORTION));
    }

    public BitmapCache getDefaultBitmapCache() {
        return bitmapCache;
    }
}
