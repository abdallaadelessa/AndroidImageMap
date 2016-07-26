/*
 * Copyright (C) 2011 Scott Lund
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ctc.android.widget;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.pixplicity.sharp.OnSvgElementListener;
import com.pixplicity.sharp.Sharp;
import com.pixplicity.sharp.SharpPicture;

import java.util.Random;

public class ImageMapTestActivity extends Activity {
    private ImageMapView mImageMap;
    private Sharp mSvg;
    private Toast toast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        toast = Toast.makeText(ImageMapTestActivity.this, " Clicked", Toast.LENGTH_SHORT);
        mImageMap = (ImageMapView) findViewById(R.id.map);
        mImageMap.setImageMapViewListener(new ImageMapView.ImageMapViewListener() {
            @Override
            public void onDrawSelectedRegion(Canvas canvas, ImageMapView.Region selectedRegion) {

            }

            @Override
            public void onRegionClicked(ImageMapView.Region region) {
                toast.setText(region.id + " Clicked");
                toast.show();
            }
        });
        mImageMap.setMaxScale(10f);
        mSvg = Sharp.loadResource(getResources(), R.raw.cartman);
        reloadSvg(false);
    }

    private void reloadSvg(final boolean changeColor) {
        mSvg.setOnElementListener(new OnSvgElementListener() {
            @Override
            public void onSvgStart(@NonNull Canvas canvas, @Nullable RectF bounds) {
            }

            @Override
            public void onSvgEnd(@NonNull Canvas canvas, @Nullable RectF bounds) {
            }

            @Override
            public <T> T onSvgElement(@Nullable String id, @NonNull T element, @Nullable RectF elementBounds, @NonNull Canvas canvas, @Nullable RectF canvasBounds, @Nullable Paint paint) {
                mImageMap.addRegion(new ImageMapView.Region(id, (Path) element));
                if(("hat".equals(id))) {
                    Random random = new Random();
                    paint.setColor(Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256)));
                }
                return element;
            }

            @Override
            public <T> void onSvgElementDrawn(@Nullable String id, @NonNull T element, @NonNull Canvas canvas, @Nullable Paint paint) {
            }

        });
        mSvg.getSharpPicture(new Sharp.PictureCallback() {
            @Override
            public void onPictureReady(SharpPicture picture) {
                Drawable drawable = picture.getDrawable(mImageMap);
                mImageMap.setImage(ImageSource.bitmap(pictureDrawableToBitmap((PictureDrawable) drawable)));
            }
        });
    }

    private Bitmap pictureDrawableToBitmap(PictureDrawable pictureDrawable) {
        Bitmap bmp = Bitmap.createBitmap(pictureDrawable.getIntrinsicWidth(), pictureDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        canvas.drawPicture(pictureDrawable.getPicture());
        return bmp;
    }
}