package id.go.pekalongankab.halobupati.Util;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by ERIK on 28-Jan-18.
 */

public class AppController extends Application {
    private static final String TAG = AppController.class.getSimpleName();
    private static AppController instance;
    RequestQueue mRequestQueque;
    private ImageLoader mImageLoader;
    LruBitmapCache mLruBitmapCache;


    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

    }

    public static synchronized AppController getInstance(){
        return instance;
    }

    private RequestQueue getmRequestQueque(){
        if (mRequestQueque == null){
            mRequestQueque = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueque;
    }

    public ImageLoader getImageLoader() {
        getmRequestQueque();
        if (mImageLoader == null) {
            getLruBitmapCache();
            mImageLoader = new ImageLoader(this.mRequestQueque, mLruBitmapCache);
        }

        return this.mImageLoader;
    }

    public LruBitmapCache getLruBitmapCache() {
        if (mLruBitmapCache == null)
            mLruBitmapCache = new LruBitmapCache();
        return this.mLruBitmapCache;
    }

    public <T> void addToRequestQueue (Request<T> req, String tag){
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getmRequestQueque().add(req);
    }

    public <T> void addToRequestQueue (Request<T> req){
        req.setTag(TAG);
        getmRequestQueque().add(req);
    }

    public void cancelAllRequest(Object req){
        if (mRequestQueque != null){
            mRequestQueque.cancelAll(req);
        }
    }
}
