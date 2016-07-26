#Screenshot
![alt tag](https://github.com/abdallaadelessa/AndroidImageMap/blob/workaround/screen%20shot.gif)

An implementation of an HTML map like element in an Android View:

- Supports images as drawable or bitmap in layout
- Allows for a list of area tags in xml
- Enables use of cut and paste HTML area tags to a resource xml  (ie, the ability to take an HTML map - and image and use it with minimal editing)
- Supports panning if the image is larger than the device screen
- Supports pinch-zoom
- Supports callbacks when an area is tapped.
- Supports showing annotations as bubble text and provide callback if the bubble is tapped


New in this version:
By default, the initial image is resized to fit the view dimensions with no regard to maintaining aspect ratio.  This appears to be the most common use.  

To have the aspect ratio kept:
change this (ImageView.java line 54) to from true to false
private boolean mFitImageToScreen=true;  

Xml
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:ctc="http://schemas.android.com/apk/res-auto"
    android:orientation="vertical"
    android:layout_width="fill_parent"
    android:layout_height="fill_parent">
    <com.ctc.android.widget.ImageMapView
        android:id="@+id/map"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>

</LinearLayout>
```
The image itself is placed in res/drawable-nodpi so that the system will not attempt to fit the image to the device based on dpi. This way we are guaranteed that our area coordinates will map properly to the displayed image.  If you want to use different density drawables, you will have to make changes in the code based on the DisplayMetrics.density.

Here is a sample code
```
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
```
Don't hesitate to ask if you have any other questions.
