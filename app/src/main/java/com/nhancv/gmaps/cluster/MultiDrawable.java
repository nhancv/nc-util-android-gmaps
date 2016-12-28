package com.nhancv.gmaps.cluster;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by nhancao on 12/28/16.
 */

public class MultiDrawable extends Drawable {

    private final List<Drawable> drawables;

    public MultiDrawable(List<Drawable> drawables) {
        this.drawables = drawables;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (drawables.size() == 1) {
            drawables.get(0).draw(canvas);
            return;
        }
        int width = getBounds().width();
        int height = getBounds().height();

        canvas.save();
        canvas.clipRect(0, 0, width, height);

        if (drawables.size() == 2 || drawables.size() == 3) {
            // Paint left half
            canvas.save();
            canvas.clipRect(0, 0, width / 2, height);
            canvas.translate(-width / 4, 0);
            drawables.get(0).draw(canvas);
            canvas.restore();
        }
        if (drawables.size() == 2) {
            // Paint right half
            canvas.save();
            canvas.clipRect(width / 2, 0, width, height);
            canvas.translate(width / 4, 0);
            drawables.get(1).draw(canvas);
            canvas.restore();
        } else {
            // Paint top right
            canvas.save();
            canvas.scale(.5f, .5f);
            canvas.translate(width, 0);
            drawables.get(1).draw(canvas);

            // Paint bottom right
            canvas.translate(0, height);
            drawables.get(2).draw(canvas);
            canvas.restore();
        }

        if (drawables.size() >= 4) {
            // Paint top left
            canvas.save();
            canvas.scale(.5f, .5f);
            drawables.get(0).draw(canvas);

            // Paint bottom left
            canvas.translate(0, height);
            drawables.get(3).draw(canvas);
            canvas.restore();
        }

        canvas.restore();
    }

    @Override
    public void setAlpha(int i) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }
}
