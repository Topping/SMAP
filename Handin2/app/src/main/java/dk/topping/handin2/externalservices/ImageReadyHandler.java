package dk.topping.handin2.externalservices;

import android.graphics.Bitmap;

@FunctionalInterface
public interface ImageReadyHandler {
    void setImageView(Bitmap bitmap);
}
