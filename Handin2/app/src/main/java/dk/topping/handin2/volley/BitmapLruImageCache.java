package dk.topping.handin2.volley;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

// https://github.com/rdrobinson3/VolleyImageCacheExample
public class BitmapLruImageCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache {

    private final String TAG = this.getClass().getSimpleName();

    public BitmapLruImageCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight();
    }

    @Override
    public Bitmap getBitmap(String url) {
        Log.d(TAG, "Loaded image from cache: " + url);
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        Log.d(TAG, "Put image in cache: " + url);
        put(url, bitmap);
    }
}
