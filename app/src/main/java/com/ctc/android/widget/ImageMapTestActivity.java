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
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.pixplicity.sharp.OnSvgElementListener;
import com.pixplicity.sharp.Sharp;
import com.pixplicity.sharp.SharpDrawable;
import com.pixplicity.sharp.SharpPicture;

import java.util.HashSet;
import java.util.Set;

public class ImageMapTestActivity extends Activity {
    private SubsamplingScaleImageView mImageMap;
    private GestureDetector gestureDetector;
    // ----->
    private Set<Region> regions = new HashSet<Region>();
    private SharpPicture pictureFromSVG;
    // ----->

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mImageMap = (SubsamplingScaleImageView) findViewById(R.id.map);
        mImageMap.setMaxScale(10f);
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                float x = e.getX();
                float y = e.getY();
                ImageMapTestActivity.this.onPhotoTap(x, y);
                return false;
            }
        });
        mImageMap.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return false;
            }
        });
        loadSvg();
    }

    // --------------->

    private void loadSvg() {
        Sharp mSvg = Sharp.loadResource(getResources(), R.raw.map);
        mSvg.setOnElementListener(new OnSvgElementListener() {
            @Nullable
            @Override
            public void onSvgStart(@NonNull Canvas canvas, @Nullable RectF bounds) {
            }

            @Override
            public void onSvgEnd(@NonNull Canvas canvas, @Nullable RectF bounds) {
            }

            @Override
            public <T> T onSvgElement(@Nullable String id, @NonNull T element, @Nullable RectF elementBounds, @NonNull Canvas canvas, @Nullable RectF canvasBounds, @Nullable Paint paint) {
                if(paint != null && paint.getStyle() == Paint.Style.FILL) {
                    regions.add(new Region(getId(id, elementBounds), (Path) element, new RectF(elementBounds)));
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
                ImageMapTestActivity.this.pictureFromSVG = picture;
                Bitmap bitmap = pictureDrawableToBitmap(picture.getDrawable(mImageMap));
                mImageMap.setImage(ImageSource.bitmap(bitmap), mImageMap.getState());
            }
        });
    }

    private void onPhotoTap(float x, float y) {
        PointF tappedPoint = toImageBound(x, y);
        for(Region region : regions) {
            if(region.elementBounds.contains(tappedPoint.x, tappedPoint.y)) {
                onRegionClicked(region);
            }
        }
    }

    public void onRegionClicked(Region region) {
        SharpDrawable drawable = pictureFromSVG.getDrawable(mImageMap);
        Bitmap bitmap = pictureDrawableToBitmap(drawable);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawPath(region.path, paint);
        mImageMap.setImage(ImageSource.bitmap(bitmap), mImageMap.getState());
    }

    // --------------->

    private String getId(@Nullable String id, RectF elementBounds) {
        return !TextUtils.isEmpty(id) ? id : elementBounds.toString().trim();
    }

    private Bitmap pictureDrawableToBitmap(PictureDrawable pictureDrawable) {
        Bitmap bmp = Bitmap.createBitmap(pictureDrawable.getIntrinsicWidth(), pictureDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        canvas.drawPicture(pictureDrawable.getPicture());
        return bmp;
    }

    @NonNull
    private PointF toImageBound(float x, float y) {
        return mImageMap.viewToSourceCoord(x, y);
    }

    // --------------->

    public static class Region {
        public String id;
        public RectF elementBounds;
        public Path path;

        public Region(String id, Path path, RectF elementBounds) {
            this.id = id;
            this.path = path;
            this.elementBounds = elementBounds;
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) return true;
            if(o == null || getClass() != o.getClass()) return false;

            Region region = (Region) o;

            return id != null ? id.equals(region.id) : region.id == null;

        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }
    }

}