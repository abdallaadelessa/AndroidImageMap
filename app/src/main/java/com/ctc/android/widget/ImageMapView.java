package com.ctc.android.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Abdullah.Essa on 7/25/2016.
 */
public class ImageMapView extends SubsamplingScaleImageView {
    ImageMapViewListener imageMapViewListener;
    List<Region> regions;
    GestureDetector gestureDetector;
    Region selectedRegion;
    private Paint paint;

    public ImageMapView(Context context, AttributeSet attr) {
        super(context, attr);
        init();
    }

    public ImageMapView(Context context) {
        super(context);
        init();
    }

    // ----------->

    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        regions = new ArrayList<Region>();
        gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                boolean canHandleClick = false;
                if(isReady()) {
                    PointF sCoord = viewToSourceCoord(e.getX(), e.getY());
                    for(Region region : regions) {
                        RectF rectF = new RectF();
                        region.toPath().computeBounds(rectF, true);
                        if(rectF.contains(sCoord.x, sCoord.y)) {
                            canHandleClick = true;
                            onRegionClicked(region);
                        }
                    }
                }
                return canHandleClick;
            }
        });
    }

    public void setImageMapViewListener(ImageMapViewListener imageMapViewListener) {
        this.imageMapViewListener = imageMapViewListener;
    }

    public List<Region> getRegions() {
        return regions;
    }

    public void addRegion(Region region) {
        getRegions().add(region);
    }

    public void removeRegion(Region region) {
        getRegions().remove(region);
    }

    // ----------->

    public void onDrawSelectedRegion(Canvas canvas, Region selectedRegion) {
        List<PointF> pointFs = convertSourcePointsToViewCoord(selectedRegion.points);
        canvas.drawPath(connectPoints(pointFs), paint);
        if(imageMapViewListener != null) {
            imageMapViewListener.onDrawSelectedRegion(canvas, selectedRegion);
        }
    }

    public void onRegionClicked(Region region) {
        selectedRegion = region;
        if(imageMapViewListener != null) {
            imageMapViewListener.onRegionClicked(region);
        }
        invalidate();
    }

    // ----------->

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!isReady()) {
            return;
        }
        if(selectedRegion != null) {
            onDrawSelectedRegion(canvas, selectedRegion);
        }
    }

    // ----------->

    private List<PointF> convertSourcePointsToViewCoord(List<PointF> points) {
        List<PointF> newPoints = new ArrayList<PointF>();
        for(PointF pointF : points) {
            newPoints.add(sourceToViewCoord(pointF));
        }
        return newPoints;
    }

    public static PointF[] getPathPoints(Path path) {
        int numOfPoints = 4;
        PointF[] pointArray = new PointF[numOfPoints];
        PathMeasure pm = new PathMeasure(path, false);
        float length = pm.getLength();
        float distance = 0f;
        float speed = length / numOfPoints;
        int counter = 0;
        float[] aCoordinates = new float[2];
        while((distance < length) && (counter < numOfPoints)) {
            // get point from the path
            pm.getPosTan(distance, aCoordinates, null);
            pointArray[counter] = new PointF(aCoordinates[0], aCoordinates[1]);
            counter++;
            distance = distance + speed;
        }
        return pointArray;
    }

    @NonNull
    public static Path connectPoints(List<PointF> points) {
        Path newPath = new Path();
        int i = 0;
        for(PointF pointF : points) {
            if(i == 0) newPath.moveTo(pointF.x, pointF.y);
            else newPath.lineTo(pointF.x, pointF.y);
            i++;
        }
        return newPath;
    }

    private Path convertSourcePathToViewCoord(Path path) {
        Path newPath = new Path();
        PointF[] points = getPathPoints(path);
        int i = 0;
        for(PointF pointF : points) {
            PointF newPoint = sourceToViewCoord(pointF);
            if(i == 0) newPath.moveTo(newPoint.x, newPoint.y);
            else newPath.lineTo(newPoint.x, newPoint.y);
            i++;
        }
        return newPath;
    }

    // ----------->

    public static class Region {
        public String id;
        public List<PointF> points;

        public Region(String id) {
            this.id = id;
            this.points = new LinkedList<PointF>();
        }

        public Region(String id, String commaSeparatedCoords) {
            this(id);
            addAllAsString(commaSeparatedCoords);
        }

        public void addAllAsString(String commaSeparatedCoords) {
            String[] strings = commaSeparatedCoords.split(",");
            for(int i = 0; i < strings.length - 1; i = i + 2) {
                this.points.add(new PointF(Integer.parseInt(strings[i]), Integer.parseInt(strings[i + 1])));
            }
        }

        public void add(PointF pointF) {
            this.points.add(pointF);
        }

        public void remove(PointF pointF) {
            this.points.remove(pointF);
        }

        public Path toPath() {
            return connectPoints(points);
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) return true;
            if(o == null || getClass() != o.getClass()) return false;

            Region region = (Region) o;

            return id.equals(region.id);

        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }

    public interface ImageMapViewListener {
        void onDrawSelectedRegion(Canvas canvas, Region selectedRegion);

        void onRegionClicked(Region region);
    }
}
