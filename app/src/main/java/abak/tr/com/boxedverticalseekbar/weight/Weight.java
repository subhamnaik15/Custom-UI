package abak.tr.com.boxedverticalseekbar.weight;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import abak.tr.com.boxedverticalseekbar.R;


public class Weight extends View {

    private float midx, midy;
    private Paint textPaint, secondaryCirclePaint, primaryCirclePaint, linePaint;
    private float currdeg = 0, deg = 3, downdeg = 0;

    private int mainCircleColor = Color.parseColor("#000000");
    private int indicatorColor = Color.parseColor("#FFA036");
    private int progressPrimaryColor = Color.parseColor("#FFA036");
    private int progressSecondaryColor = Color.parseColor("#111111");


    private float progressPrimaryStrokeWidth = 35;
    private float progressSecondaryStrokeWidth = 50;

    private float progressRadius = -1;
    private float mainCircleRadius = -1;

    private int max = 25;
    private int min = 1;

    private float indicatorWidth = 5;

    private String label = "";
    private String textDraw = "";
    private int labelSize = 40;
    private int labelColor = Color.WHITE;

    private int startOffset = 30;
    private Bitmap defaultBitmap;

    private boolean startEventSent = false;

    private RectF oval;

    private onProgressChangedListener mWeightChangeListener;
    private int innerPadding = 30;

    public interface onProgressChangedListener {
        void onProgressChanged(Weight weight, int progress);

        void onStopTrackingTouch(Weight weight);

        void onStartTrackingTouch(Weight weight);
    }


    public void setOnWeightChangeListener(onProgressChangedListener mWeightChangeListener) {
        this.mWeightChangeListener = mWeightChangeListener;
    }

    public Weight(Context context) {
        super(context);
        init();
    }

    public Weight(Context context, AttributeSet attrs) {
        super(context, attrs);
        initXMLAttrs(context, attrs);
        init();
    }

    public Weight(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initXMLAttrs(context, attrs);
        init();
    }

    private void init() {
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(labelColor);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(labelSize);
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        secondaryCirclePaint = new Paint();
        secondaryCirclePaint.setAntiAlias(true);
        secondaryCirclePaint.setColor(progressSecondaryColor);
        secondaryCirclePaint.setStrokeWidth(progressSecondaryStrokeWidth);
        secondaryCirclePaint.setStyle(Paint.Style.FILL);

        primaryCirclePaint = new Paint();
        primaryCirclePaint.setAntiAlias(true);
        primaryCirclePaint.setColor(progressPrimaryColor);
        primaryCirclePaint.setStrokeWidth(progressPrimaryStrokeWidth);
        primaryCirclePaint.setStyle(Paint.Style.FILL);

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setColor(indicatorColor);
        linePaint.setStrokeWidth(indicatorWidth);
        oval = new RectF();


    }

    private void initXMLAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Weight);

        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.Weight_progress) {
                setProgress(a.getInt(attr, 1));
            } else if (attr == R.styleable.Weight_label) {
                setLabel(a.getString(attr));
            } else if (attr == R.styleable.Weight_main_circle_color) {
                setMainCircleColor(a.getColor(attr, Color.parseColor("#000000")));
            } else if (attr == R.styleable.Weight_indicator_color) {
                setIndicatorColor(a.getColor(attr, Color.parseColor("#FFA036")));
            } else if (attr == R.styleable.Weight_progress_primary_color) {
                setProgressPrimaryColor(a.getColor(attr, Color.parseColor("#FFA036")));
            } else if (attr == R.styleable.Weight_progress_secondary_color) {
                setProgressSecondaryColor(a.getColor(attr, Color.parseColor("#111111")));
            } else if (attr == R.styleable.Weight_label_size) {
                setLabelSize(a.getInteger(attr, 40));
            } else if (attr == R.styleable.Weight_label_color) {
                setLabelColor(a.getColor(attr, Color.WHITE));
            } else if (attr == R.styleable.Weight_indicator_width) {
                setIndicatorWidth(a.getFloat(attr, 7));
            } else if (attr == R.styleable.Weight_start_offset) {
                setStartOffset(a.getInt(attr, startOffset));
            } else if (attr == R.styleable.Weight_max) {
                setMax(a.getInt(attr, 25));
            } else if (attr == R.styleable.Weight_min) {
                setMin(a.getInt(attr, 1));
                deg = min + 2;
            } else if (attr == R.styleable.Weight_padding) {
                setInnerPadding(a.getInt(attr, innerPadding));
            } else if (attr == R.styleable.Weight_progress_radius) {
                setProgressRadius(a.getFloat(attr, -1));
            } else if (attr == R.styleable.Weight_default_image) {
                defaultBitmap = ((BitmapDrawable) a.getDrawable(R.styleable.Weight_default_image)).getBitmap();
            }
        }
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int minWidth = (int) convertDpToPixel(160, getContext());
        int minHeight = (int) convertDpToPixel(160, getContext());

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(minWidth, widthSize);
        } else {
            // only in case of ScrollViews, otherwise MeasureSpec.UNSPECIFIED is never triggered
            // If width is wrap_content i.e. MeasureSpec.UNSPECIFIED, then make width equal to height
            width = heightSize;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(minHeight, heightSize);
        } else {
            // only in case of ScrollViews, otherwise MeasureSpec.UNSPECIFIED is never triggered
            // If height is wrap_content i.e. MeasureSpec.UNSPECIFIED, then make height equal to width
            height = widthSize;
        }

        if (widthMode == MeasureSpec.UNSPECIFIED && heightMode == MeasureSpec.UNSPECIFIED) {
            width = minWidth;
            height = minHeight;
        }
        progressSecondaryStrokeWidth = width / 2;
        progressPrimaryStrokeWidth = height / 2 - innerPadding;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mWeightChangeListener != null) {
            textDraw = " " + (int) (deg - 2) + " " + label;
            mWeightChangeListener.onProgressChanged(this, (int) (deg - 2));
        }

        midx = canvas.getWidth() / 2;
        midy = canvas.getHeight() / 2;

        int radius = (int) (Math.min(midx, midy) * ((float) 14.5 / 16));

        mainCircleRadius = radius * ((float) 11 / 15);

        if (progressRadius == -1) {
            progressRadius = radius;
        }

        secondaryCirclePaint.setColor(progressSecondaryColor);
        secondaryCirclePaint.setStrokeWidth(progressSecondaryStrokeWidth);
        secondaryCirclePaint.setStyle(Paint.Style.STROKE);
        primaryCirclePaint.setColor(progressPrimaryColor);
        primaryCirclePaint.setStrokeWidth(progressPrimaryStrokeWidth);
        primaryCirclePaint.setStyle(Paint.Style.STROKE);

        float deg3 = Math.min(deg, max + 2);

        oval.set(midx - progressRadius / 2, midy - progressRadius / 2, midx + progressRadius / 2, midy + progressRadius / 2);
        canvas.drawArc(oval, (float) 90 + startOffset, (float) 270, false, secondaryCirclePaint);
        canvas.drawArc(oval, (float) 90 + startOffset, ((deg3 - 2) * ((float) 270 / max)), false, primaryCirclePaint);


        drawIndicator(startOffset, max, canvas, radius);
        /*float tmp2 = ((float) startOffset / 360) + (((float) 270 / 360) * ((deg - 2) / (max)));

        float y1 = midy + (float) (radius * ((float) 2 / 3) * Math.cos(2 * Math.PI * (1.0 - tmp2)));
        float x1 = midx + (float) (radius * ((float) 2 / 3) * Math.sin(2 * Math.PI * (1.0 - tmp2)));
        float x2 = midx + (float) (radius * Math.sin(2 * Math.PI * (1.0 - tmp2)));
        float y2 = midy + (float) (radius * Math.cos(2 * Math.PI * (1.0 - tmp2)));
        canvas.drawLine(x1, y1, x2, y2, linePaint);*/

        //x2 y2 outermost circle x y coordinate
        secondaryCirclePaint.setStyle(Paint.Style.FILL);
        secondaryCirclePaint.setColor(mainCircleColor);
        //inner white circle
        canvas.drawCircle(midx, midy, progressPrimaryStrokeWidth / 2, secondaryCirclePaint);


        if (defaultBitmap != null)
            drawIcon(defaultBitmap, canvas);
        else
            canvas.drawText(textDraw, midx, midy, textPaint);//in case default image is null draw text in center

    }

    private void drawIndicator(int startOffset, int max, Canvas canvas, int radius) {
        int stepForward = 5;
        for (float deg = 2; deg < max - 3; deg = deg + stepForward) {
            float tmp2 = ((float) startOffset / 360) + (((float) 270 / 360) * ((deg - 2) / (max)));
            float x1, y1;
            if ((deg - 2) % (stepForward * 2) == 0) {
                y1 = midy + (float) (radius * ((float) 0.66) * Math.cos(2 * Math.PI * (1.0 - tmp2)));
                x1 = midx + (float) (radius * ((float) 0.66) * Math.sin(2 * Math.PI * (1.0 - tmp2)));
                canvas.drawText(""+(int)(deg - 2), x1, y1, textPaint);//in case default image is null draw text in center
                y1 = midy + (float) (radius * ((float) 0.77) * Math.cos(2 * Math.PI * (1.0 - tmp2)));
                x1 = midx + (float) (radius * ((float) 0.77) * Math.sin(2 * Math.PI * (1.0 - tmp2)));
            } else {
                y1 = midy + (float) (radius * ((float) 0.88) * Math.cos(2 * Math.PI * (1.0 - tmp2)));
                x1 = midx + (float) (radius * ((float) 0.88) * Math.sin(2 * Math.PI * (1.0 - tmp2)));

            }
            float x2 = midx + (float) (radius * Math.sin(2 * Math.PI * (1.0 - tmp2)));
            float y2 = midy + (float) (radius * Math.cos(2 * Math.PI * (1.0 - tmp2)));


            //x2 y2 outermost circle x y coordinate
            canvas.drawLine(x1, y1, x2, y2, linePaint);
        }
    }

    private void drawIcon(Bitmap bitmap, Canvas canvas) {
        bitmap = getResizedBitmap(bitmap, canvas.getWidth() / 4, canvas.getWidth() / 4);
        canvas.drawBitmap(bitmap, canvas.getWidth() / 2 - (bitmap.getWidth() / 2), canvas.getHeight() / 2 - ((bitmap.getHeight() / 2)), null);
        canvas.drawText(textDraw, canvas.getWidth() / 2, canvas.getHeight() / 2 + ((bitmap.getHeight())), textPaint);
    }

    private Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        if (getDistance(e.getX(), e.getY(), midx, midy) > Math.max(mainCircleRadius, progressRadius)) {
            if (startEventSent && mWeightChangeListener != null) {
                mWeightChangeListener.onStopTrackingTouch(this);
                startEventSent = false;
            }
            return super.onTouchEvent(e);
        }

        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            float dx = e.getX() - midx;
            float dy = e.getY() - midy;
            downdeg = (float) ((Math.atan2(dy, dx) * 180) / Math.PI);
            downdeg -= 90;
            if (downdeg < 0) {
                downdeg += 360;
            }
            downdeg = (float) Math.floor((downdeg / 360) * (max + 5));

            if (mWeightChangeListener != null) {
                mWeightChangeListener.onStartTrackingTouch(this);
                startEventSent = true;
            }

            return true;
        }
        if (e.getAction() == MotionEvent.ACTION_MOVE) {
            float dx = e.getX() - midx;
            float dy = e.getY() - midy;
            currdeg = (float) ((Math.atan2(dy, dx) * 180) / Math.PI);
            currdeg -= 90;
            if (currdeg < 0) {
                currdeg += 360;
            }
            currdeg = (float) Math.floor((currdeg / 360) * (max + 5));

            if ((currdeg / (max + 4)) > 0.75f && ((downdeg - 0) / (max + 4)) < 0.25f) {

                deg--;
                if (deg < (min + 2)) {
                    deg = (min + 2);
                }

            } else if ((downdeg / (max + 4)) > 0.75f && ((currdeg - 0) / (max + 4)) < 0.25f) {

                deg++;
                if (deg > max + 2) {
                    deg = max + 2;
                }

            } else {
                deg += (currdeg - downdeg);
                if (deg > max + 2) {
                    deg = max + 2;
                }
                if (deg < (min + 2)) {
                    deg = (min + 2);
                }
            }

            downdeg = currdeg;

            invalidate();
            return true;

        }
        if (e.getAction() == MotionEvent.ACTION_UP) {
            if (mWeightChangeListener != null) {
                mWeightChangeListener.onStopTrackingTouch(this);
                startEventSent = false;
            }
            return true;
        }
        return super.onTouchEvent(e);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (getParent() != null && event.getAction() == MotionEvent.ACTION_DOWN) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.dispatchTouchEvent(event);
    }

    public int getProgress() {
        return (int) (deg - 2);
    }

    public void setProgress(int x) {
        deg = x + 2;
        invalidate();
    }

    public void setLabel(String txt) {
        label = txt;
        invalidate();
    }

    public void setMainCircleColor(int mainCircleColor) {
        this.mainCircleColor = mainCircleColor;
        invalidate();
    }

    public int getIndicatorColor() {
        return indicatorColor;
    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        invalidate();
    }

    public void setProgressPrimaryColor(int progressPrimaryColor) {
        this.progressPrimaryColor = progressPrimaryColor;
        invalidate();
    }

    public void setProgressSecondaryColor(int progressSecondaryColor) {
        this.progressSecondaryColor = progressSecondaryColor;
        invalidate();
    }

    public void setLabelSize(int labelSize) {
        this.labelSize = labelSize;
        invalidate();
    }

    public void setLabelColor(int labelColor) {
        this.labelColor = labelColor;
        invalidate();
    }

    public void setIndicatorWidth(float indicatorWidth) {
        this.indicatorWidth = indicatorWidth;
        invalidate();
    }

    public void setProgressPrimaryStrokeWidth(float progressPrimaryStrokeWidth) {
        this.progressPrimaryStrokeWidth = progressPrimaryStrokeWidth;
        invalidate();
    }

    public void setProgressSecondaryStrokeWidth(float progressSecondaryStrokeWidth) {
        this.progressSecondaryStrokeWidth = progressSecondaryStrokeWidth;
        invalidate();
    }


    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
        invalidate();
    }

    public void setInnerPadding(int innerPadding) {
        this.innerPadding = innerPadding;
        invalidate();
    }

    public void setMax(int max) {
        if (max < min) {
            this.max = min;
        } else {
            this.max = max;
        }
        invalidate();
    }

    public void setMin(int min) {
        if (min < 0) {
            this.min = 0;
        } else if (min > max) {
            this.min = max;
        } else {
            this.min = min;
        }
        invalidate();
    }


    public void setProgressRadius(float progressRadius) {
        this.progressRadius = progressRadius;
        invalidate();
    }

    public float getDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    public float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
