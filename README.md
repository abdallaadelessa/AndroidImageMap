#Screenshot
![alt tag](https://github.com/abdallaadelessa/AndroidImageMap/blob/master/screen%20shot.gif)
![alt tag](https://github.com/abdallaadelessa/AndroidImageMap/blob/master/screen%20shot2.gif)

An implementation of an HTML map like element in an Android View:

- Supports images as drawable or bitmap or SVG in layout
- Allows for a list of area tags in xml
- Enables use of cut and paste HTML area tags to a resource xml  (ie, the ability to take an HTML map - and image and use it with minimal editing)
- Supports panning if the image is larger than the device screen
- Supports pinch-zoom
- Supports callbacks when an area is tapped.
- Supports showing annotations as bubble text and provide callback if the bubble is tapped

# Xml
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:ctc="http://schemas.android.com/apk/res-auto"
    android:orientation="vertical"
    android:layout_width="fill_parent"
    android:layout_height="fill_parent">
    <com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
        android:id="@+id/map"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>

</LinearLayout>

```

# Here is a sample code

```
     
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

```
