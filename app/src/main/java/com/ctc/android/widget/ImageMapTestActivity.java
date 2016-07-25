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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;

import com.davemorrissey.labs.subscaleview.ImageSource;

public class ImageMapTestActivity extends Activity {
    ImageMapView mImageMap;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mImageMap = (ImageMapView) findViewById(R.id.map);

        mImageMap.addRegion(new ImageMapView.Region("Region 1", "160,190,228,198,227,270,152,269"));
        mImageMap.addRegion(new ImageMapView.Region("Region 2", "231,41,294,41,299,81,230,76"));
        mImageMap.addRegion(new ImageMapView.Region("Region 4", "227,80,299,80,302,120,282,116,226,116"));
        mImageMap.addRegion(new ImageMapView.Region("Region 5", "229,35,226,87,154,81,145,86,131,69,123,21"));
        mImageMap.addRegion(new ImageMapView.Region("Region 6", "224,89,223,143,148,136,156,83"));
        mImageMap.addRegion(new ImageMapView.Region("Region 7", "316,158,353,154,382,196,378,207,373,202,329,206,327,171"));
        mImageMap.addRegion(new ImageMapView.Region("Region 8", "365,124,389,120,393,133,398,172,393,185,382,193,355,155"));
        mImageMap.addRegion(new ImageMapView.Region("Region 9", "374,227,401,222,403,284,387,288,384,280,362,282,369,263"));

        mImageMap.setImage(ImageSource.resource(R.drawable.usamap));
        mImageMap.setMaxScale(10f);
        mImageMap.setImageMapViewListener(new ImageMapView.ImageMapViewListener() {
            @Override
            public void onDrawSelectedRegion(Canvas canvas, ImageMapView.Region selectedRegion) {
                RectF bounds = new RectF();
                selectedRegion.toPath().computeBounds(bounds, false);
                PointF centerPoint = mImageMap.sourceToViewCoord(bounds.left, bounds.centerY());

                Paint paint = new Paint();
                float textSize = 60f;
                paint.setTextSize(textSize);
                paint.setColor(Color.WHITE);
                canvas.drawText(selectedRegion.id, centerPoint.x, centerPoint.y, paint);
            }

            @Override
            public void onRegionClicked(ImageMapView.Region region) {
                // Toast.makeText(ImageMapTestActivity.this, region.id + " Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

}