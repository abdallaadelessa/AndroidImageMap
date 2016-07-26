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
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.pixplicity.sharp.OnSvgElementListener;
import com.pixplicity.sharp.Sharp;
import com.pixplicity.sharp.SharpDrawable;
import com.pixplicity.sharp.SharpPicture;

import java.util.HashSet;
import java.util.Set;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageMapTestActivity extends Activity {
    private ImageView mImageMap;
    private Sharp mSvg;
    private Toast toast;
    private RectF canvasBounds;
    private PhotoViewAttacher mAttacher;
    Set<Region> regions = new HashSet<Region>();
    private String selectedId;
    private SharpPicture picture;
    // --------------->

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        toast = Toast.makeText(ImageMapTestActivity.this, "", Toast.LENGTH_SHORT);
        mImageMap = (ImageView) findViewById(R.id.map);
        mAttacher = new PhotoViewAttacher(mImageMap);
        mAttacher.setMaximumScale(10f);
        mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                PointF tappedPoint = toImageBound(x, y);
                for(Region region : regions) {
                    RectF rectF = new RectF();
                    region.path.computeBounds(rectF, true);
                    boolean regionContainPoint = region.elementBounds.contains(tappedPoint.x, tappedPoint.y);
                    if(regionContainPoint) {
                        selectedId = region.id;
                        onRegionClicked(tappedPoint, region);
                    }
                }
            }
        });
        mSvg = Sharp.loadResource(getResources(), R.raw.map);
        loadSvg();
    }

    // --------------->

    private void loadSvg() {
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
                    ImageMapTestActivity.this.canvasBounds = canvasBounds;
                    regions.add(new Region(getId(id, elementBounds), (Path) element, new RectF(elementBounds)));
                    if(selectedId != null && selectedId.equals(id)) {
                        paint.setColor(Color.WHITE);
                    }
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
                ImageMapTestActivity.this.picture = picture;
                mImageMap.setImageDrawable(picture.getDrawable(mImageMap));
            }
        });
    }

    public void onRegionClicked(PointF tappedPoint, Region region) {
        SharpDrawable drawable = picture.getDrawable(mImageMap);
        Bitmap bitmap = pictureDrawableToBitmap(drawable);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawPath(region.path, paint);
        mImageMap.setImageBitmap(bitmap);
    }

    // --------------->

    private String getId(@Nullable String id, RectF elementBounds) {
        if(!TextUtils.isEmpty(id)) {
            return id;
        }
        else {
            return elementBounds.toString().trim();
        }
    }

    private Bitmap pictureDrawableToBitmap(PictureDrawable pictureDrawable) {
        Bitmap bmp = Bitmap.createBitmap(pictureDrawable.getIntrinsicWidth(), pictureDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        canvas.drawPicture(pictureDrawable.getPicture());
        return bmp;
    }

    @NonNull
    private PointF toImageBound(float x, float y) {
        PointF pointF = new PointF(x * canvasBounds.right, y * canvasBounds.bottom);
        return pointF;
    }

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