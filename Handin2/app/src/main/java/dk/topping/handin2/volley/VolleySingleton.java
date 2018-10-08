package dk.topping.handin2.volley;


import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

// https://developer.android.com/training/volley/requestqueue.html
// Apparently it's really bad to place "Android context classes in static fields"....
// But the android documentation says it's ok. so... ¯\_(ツ)_/¯
public class VolleySingleton {
    private static VolleySingleton instance;
    private static ImageLoader.ImageCache bitmapCache;
    private static RequestQueue requestQueue;
    private static Context context;
    private ImageLoader imageLoader;

    private VolleySingleton(Context context) {

        this.context = context;
        requestQueue = getRequestQueue();
        bitmapCache = new BitmapLruImageCache(100000);
        this.imageLoader = new ImageLoader(requestQueue,  bitmapCache);
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if(instance == null) {
           instance = new VolleySingleton(context);
        }

        return instance;
    }

    private RequestQueue getRequestQueue() {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }

        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}
